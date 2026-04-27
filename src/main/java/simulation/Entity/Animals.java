package simulation.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.backend.Position;
import simulation.backend.Simulation;
import simulation.util.Randomizer;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class Animals extends Entity{

    private static final Logger log = LoggerFactory.getLogger(Animals.class);
    private static final Randomizer random = new Randomizer();
    private static final int COUNT_LIFES_FROM_EAT = 5;

    private int id;     // в идеале по-другому сделать - указать в конструкторе Animals ещё и counter в качестве параметра -> (читать далее)            а также использовать отдельный класс для создания объектов, тогда можно будет сделать это поле final, но у меня есть как есть, и я слишком много времени потратил на этот проект, чтобы на столько доводить его до идеала
    protected Simulation simulation = null;
    private int lifes = getCountLifesFirstTime();
    private boolean isDead;

    public Animals(int x, int y) {
        super(x, y);

    }



    @Override
    public Position doMove(Simulation simulation) {
        if(this.simulation == null){
            this.simulation = simulation;
            this.simulation.incIdToCounter(this.getClass().getSuperclass());
            id = simulation.getCounter().getId(this.getClass().getSuperclass());
        }

        Position newPosition = searchMove(simulation);


        if(checkEating(simulation, newPosition)){
            Entity entity = simulation.getNewObjsMap().get(newPosition);
            eating(simulation, entity);
        }

        lifes--;

        if (lifes <= 0){
            simulation.addInQueueDel(this);
            simulation.addDeathEvent(this);
            isDead = true;
            log.info(" " + this.getClass().getSimpleName() + getId() + " DEAD!X!X! ");
        }

        log.trace(" " + this.getClass().getSimpleName() + getId() + " " + newPosition + "  lifes: " + lifes);

        return newPosition;
    }


    protected Position searchMove(Simulation simulation) {
        Position newPos;
        Position position = getPosition();
        Set<Position> setPos = calculateFreePositions(simulation, position);

        Position dangPos = searchDanger(position, simulation.getObjsMap());
        Position eatPos = searchEat(position, simulation.getObjsMap());
        int countLifesToDeadWithSimultaneousVisionDangerousAndEat = 5;

        if (dangPos != null) {
            if (lifes > countLifesToDeadWithSimultaneousVisionDangerousAndEat){
                newPos = leavingFromDanger(setPos, dangPos, simulation);
            } else {
                newPos = leavingFromDangerAndSearchEat(setPos, dangPos, eatPos);
            }
        } else {
            newPos = searchPosition(setPos, eatPos);
        }

        if (newPos == null) {
            return position;
        }
        return newPos;
    }



    private Position searchDanger(Position position, Map<Position, Entity> objsMap) {

        return objsMap.entrySet().stream()
                .filter(e-> Math.abs(e.getKey().getX() - position.getX()) <= 2 &&
                        Math.abs(e.getKey().getY() - position.getY()) <= 2)
                .filter(e ->  isEntityCanEatMe(e.getValue()))
                .min(Comparator.comparingInt(e ->
                        Math.max(
                                Math.abs(e.getKey().getX() - position.getX()),
                                Math.abs(e.getKey().getY() - position.getY()))
                ))
                .map(Map.Entry::getKey)
                .orElse(null);
    }


    private Position leavingFromDanger(Set<Position> setPos, Position predPos, Simulation simulation) {
        return setPos.stream()
                .max(Comparator
                        .comparingInt((Position pos) ->
                                Math.max(
                                        Math.abs(predPos.getX() - pos.getX()),
                                        Math.abs(predPos.getY() - pos.getY())
                                ))
                        .thenComparingInt(pos -> isEntityEdible(simulation.getNewObjsMap().get(pos)) ? 1 : 0)
                )
                .orElse(null);
    }

    private Position leavingFromDangerAndSearchEat(Set<Position> setPos, Position predPos, Position eatPos) {
        return setPos.stream()
                .max(Comparator
                        .comparingInt((Position pos) ->
                                Math.max(
                                        Math.abs(predPos.getX() - pos.getX()), // расстояние Чебышёва
                                        Math.abs(predPos.getY() - pos.getY())
                                ))
                        .thenComparingInt(pos -> eatPos != null ? getCountMovesFromPos1toPos2(pos, eatPos) : 0)
                )
                .orElse(null);
    }


    private Position searchEat(Position animPos, Map<Position, Entity> objsMap){

        return objsMap.entrySet().stream()
                .filter(e ->  isEntityEdible(e.getValue()))
                .min(Comparator.comparingInt(e ->
                        Math.max(
                                Math.abs(e.getKey().getX() - animPos.getX()),
                                Math.abs(e.getKey().getY() - animPos.getY()))
                ))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private void eating(Simulation simulation, Entity entity){
        simulation.addInSetForEating(entity);
        simulation.addEatingEvent(this, entity);
        log.trace("eating  " + entity.getPosition().getX() + " " + entity.getPosition().getY());
        if (lifes < getMaxCountLifes()){
            lifes = lifes + COUNT_LIFES_FROM_EAT;
        }

    }

    protected boolean checkEating(Simulation simulation, Position newPosition){
        Entity entity = simulation.getNewObjsMap().get(newPosition);
        return (isEntityEdible(entity));

    }

    private boolean isPosBusy(Position position, Simulation simulation){
        boolean result = false;
        if (simulation.getObjsMap().containsKey(position)){
            if (simulation.getObjsMap().get(position).getClass() != this.getClass() &&
                    !isEntityEdible(simulation.getObjsMap().get(position))){
                result = true;
            }

        }
        if(simulation.getNewObjsMap().containsKey(position) &&
                !isEntityEdible(simulation.getNewObjsMap().get(position))){
            result = true;
        }
        return result;
    }

    private Set<Position> calculateFreePositions(Simulation simulation, Position position){
        Set<Position> setPos = new LinkedHashSet<>();

        int x = position.getX();
        int y = position.getY();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i != 0 || j != 0){
                    Position pos = new Position(x+j, y+i);
                    if (!isPosBusy(pos, simulation) && simulation.isPosExist(pos)){
                        setPos.add(pos);
                    }
                }
            }
        }
        return setPos;
    }

    private Position searchPosition(Set<Position> setPos, Position berPos){
        if(berPos != null){
            return searchNearestPos(setPos, berPos);
        } else {
            return random.getMove(setPos);
        }
    }

    private int getCountMovesFromPos1toPos2(Position pos, Position targetPos){
        return Math.max(
                Math.abs(pos.getX() - targetPos.getX()), // Расстояние Чебышёва
                Math.abs(pos.getY() - targetPos.getY()));
    }

    private Position searchNearestPos(Set<Position> setPos, Position targetPos){
        if (setPos == null || targetPos == null) {
            return null;
        }

        return setPos.stream()
                .min(Comparator
                        .comparingInt((Position pos) ->
                                Math.max(
                                        Math.abs(targetPos.getX() - pos.getX()),
                                        Math.abs(targetPos.getY() - pos.getY())
                                ))
                        .thenComparingInt(pos -> Math.min(
                                Math.abs(targetPos.getX() - pos.getX()),
                                Math.abs(targetPos.getY() - pos.getY())
                        ))

                )
                .orElse(null);
    }



    @Override
    protected boolean isDead() {
        return isDead;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + getId() + " " + getPosition();
    }

    protected abstract boolean isEntityCanEatMe(Entity entity);

    protected abstract boolean isEntityEdible(Entity entity);

    protected abstract int getCountLifesFirstTime();

    protected abstract int getMaxCountLifes();

    protected  int getId(){
        return this.id;
    }

}
