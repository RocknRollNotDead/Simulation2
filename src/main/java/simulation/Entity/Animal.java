package simulation.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.backend.Position;
import simulation.backend.Simulation;
import simulation.other.Randomizer;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class Animal extends Entity{

    private static final Logger log = LoggerFactory.getLogger(Animal.class);
    private static final Randomizer random = new Randomizer();
    private static final int COUNT_LIFES_FROM_EAT = 5;

    private int id;     // в идеале по-другому сделать - указать в конструкторе Animals ещё и counter в качестве параметра -> (читать далее)            а также использовать отдельный класс для создания объектов, тогда можно будет сделать это поле final, но у меня есть как есть, и я слишком много времени потратил на этот проект, чтобы на столько доводить его до идеала
    protected Simulation simulation = null;
    private int lifes = getCountLifesFirstTime();
    private boolean isDead;

    public Animal(int x, int y) {
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
            isDead = true;
            log.info(" " + this.getClass().getSimpleName() + getId() + " DEAD!X!X! ");
        }

        log.trace(" " + this.getClass().getSimpleName() + getId() + " " + newPosition + "  lifes: " + lifes);

        return newPosition;
    }


    protected Position searchMove(Simulation simulation) {
        Position newPos;
        Position position = getPosition();
        Set<Position> freePositions = calculateFreePositions(simulation, position);

        Position dangPos = searchDanger(position, simulation.getObjsMap());
        Position eatPos = searchEat(position, simulation.getNewObjsMap());
        int countLifesToDeadWithSimultaneousVisionDangerousAndEat = 30;

        if (dangPos != null) {
            if (lifes > countLifesToDeadWithSimultaneousVisionDangerousAndEat){
                newPos = leavingFromDanger(freePositions, dangPos, simulation);
            } else {
                newPos = leavingFromDangerAndSearchEat(freePositions, dangPos, eatPos);
            }
        } else {
            newPos = searchPosition(freePositions, eatPos);
        }

        // ветка2
        // commit2
        if (newPos == null) {
            return position;
        }
        return newPos;
    }



    private Position searchDanger(Position position, Map<Position, Entity> objsMap) {

        return objsMap.entrySet().stream()
                .filter(e-> Math.abs(e.getKey().x() - position.x()) <= 2 &&
                        Math.abs(e.getKey().y() - position.y()) <= 2)
                .filter(e ->  isEntityCanEatMe(e.getValue()))
                .min(Comparator.comparingInt(e ->
                        Math.max(
                                Math.abs(e.getKey().x() - position.x()),
                                Math.abs(e.getKey().y() - position.y()))
                ))
                .map(Map.Entry::getKey)
                .orElse(null);
    }


    private Position leavingFromDanger(Set<Position> setPos, Position predPos, Simulation simulation) {
        return setPos.stream()
                .max(Comparator
                        .comparingInt((Position pos) ->
                                Math.max(
                                        Math.abs(predPos.x() - pos.x()),
                                        Math.abs(predPos.y() - pos.y())
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
                                        Math.abs(predPos.x() - pos.x()), // расстояние Чебышёва
                                        Math.abs(predPos.y() - pos.y())
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
                                Math.abs(e.getKey().x() - animPos.x()),
                                Math.abs(e.getKey().y() - animPos.y()))
                ))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private void eating(Simulation simulation, Entity entity){
        simulation.addInSetForEating(entity);
        log.trace("eating  " + entity.getPosition().x() + " " + entity.getPosition().y());
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

        int x = position.x();
        int y = position.y();

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
                Math.abs(pos.x() - targetPos.x()), // Расстояние Чебышёва
                Math.abs(pos.y() - targetPos.y()));
    }

    private Position searchNearestPos(Set<Position> setPos, Position targetPos){
        if (setPos == null || targetPos == null) {
            return null;
        }

        return setPos.stream()
                .min(Comparator
                        .comparingInt((Position pos) ->
                                Math.max(
                                        Math.abs(targetPos.x() - pos.x()),
                                        Math.abs(targetPos.y() - pos.y())
                                ))
                        .thenComparingInt(pos -> Math.min(
                                Math.abs(targetPos.x() - pos.x()),
                                Math.abs(targetPos.y() - pos.y())
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
