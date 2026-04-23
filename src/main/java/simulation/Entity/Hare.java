package simulation.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.backend.Position;
import simulation.backend.Simulation;

import java.util.*;

public class Hare extends PeacefulAnimal{

    private static final String SYMBOL = "\uD83D\uDC30"; // 🐰
    private static final Logger log = LoggerFactory.getLogger(Hare.class);
    private static final int COUNT_LIFES_FROM_BERRY = 5;
    private static final int COUNT_LIFES_FIRST_TIME = 30;
    private static final int MAX_COUNT_LIFES = 50;
    private static int count;

    private final int id;
    private int lifes = COUNT_LIFES_FIRST_TIME;
    private boolean isDead;

    public Hare(int x, int y) {
        super(x, y);
        count++;
        id = count;
    }



    @Override
    public Position doMove(Simulation simulation) {

        Position newPosition = searchMove(simulation);

        if(checkEating(simulation, newPosition)){
            Entity entity = simulation.getObjsMap().get(newPosition);
            eating(simulation, entity);
        }

        lifes--;

        if (lifes <= 0){
            simulation.addInQueueDel(this);
            isDead = true;
            log.info(" " + this.getClass().getSimpleName() + id + " DEAD!X!X! ");
        }

        log.trace(" " + this.getClass().getSimpleName() + id + " " + newPosition + "  lifes: " + lifes);

        return newPosition;
    }

    @Override
    protected Position searchMove(Simulation simulation) {

        Position position = getPosition();
        Position berPos = searchEat(position, simulation.getObjsMap());

        Set<Position> setPos = calculateFreePositions(simulation, position);
        Position newPos = searchPosition(setPos, berPos);
        if (newPos == null) {
            return position;
        }
        return newPos;
    }


    private Position searchEat(Position animPos, Map<Position, Entity> objsMap){

        //pos = в objsMap ищется ближайшая ягода к animPos

        Position pos = objsMap.entrySet().stream()
                .filter(e -> e.getValue() instanceof Edible)
                .min(Comparator.comparingInt(e ->
                        Math.max(
                                Math.abs(e.getKey().getX() - animPos.getX()),
                                Math.abs(e.getKey().getY() - animPos.getY()))
                ))
                .map(Map.Entry::getKey)
                .orElse(null);

        return pos;
    }

    private void eating(Simulation simulation, Entity entity){
        simulation.addInSetForEating(entity);
        log.trace("eating  " + entity.getPosition().getX() + " " + entity.getPosition().getY());
        if (lifes < MAX_COUNT_LIFES){
            lifes = lifes + COUNT_LIFES_FROM_BERRY;
        }

    }

    private boolean checkEating(Simulation simulation, Position newPosition){
        Entity entity = simulation.getObjsMap().get(newPosition);
        return (entity instanceof Edible);

    }

    private boolean isPosBusy(Position position, Simulation simulation){
        boolean result = false;
        if (simulation.getObjsMap().containsKey(position)){
            if (simulation.getObjsMap().get(position).getClass() != Hare.class &&
                    !(simulation.getObjsMap().get(position) instanceof Edible)/*.getClass() != Berries.class*/){
                result = true;
            }

        }
        if(simulation.getNewObjsMap().containsKey(position) &&
                !(simulation.getNewObjsMap().get(position) instanceof Edible)/*.getClass() != Berries.class*/){
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
    public String getSymbol() {
        return SYMBOL;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + getPosition();
    }
}
