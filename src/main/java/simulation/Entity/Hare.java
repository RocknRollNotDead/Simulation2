package simulation.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.backend.Position;
import simulation.backend.Simulation;

import java.util.*;

public class Hare extends Animals{

    private static final String SYMBOL = "\uD83D\uDC30"; // 🐰
    private static final Logger log = LoggerFactory.getLogger(Hare.class);
    private static int count;

    private final int id;
    private int lifes = 30;
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
            log.info(" Hare" + id + " DEAD!X!X! ");
        }
        log.trace(" Hare" + id + " " + newPosition + "  lifes: " + lifes);

        return newPosition;
    }

    @Override
    protected Position searchMove(Simulation simulation) {
        int x;
        int y;
//        Map<Position, Entity> objsMap = simulation.getObjsMap();
        int aDeb = 0;
        Position position = getPosition();

        int moveX;
        int moveY;

        do{
            Position berPos = searchEat(getPosition(), simulation.getObjsMap());
            int differenceInX;
            int differenceInY;
            if (berPos != null){
                differenceInX = berPos.getX() - getPosition().getX();
                differenceInY = berPos.getY() - getPosition().getY();
                moveX = Integer.compare(differenceInX, 0);
                moveY = Integer.compare(differenceInY, 0);
            }else{
                int[] move = random.getMove();
                moveX = move[0];
                moveY = move[1];
            }

            x = position.getX() + moveX;
            y = position.getY() + moveY;

            while
            ((simulation.getNewObjsMap().containsKey(new Position(x, y)) &&
                    simulation.getNewObjsMap().get(
                            new Position(x, y)).getClass() != Berries.class
                    ||
                    simulation.getObjsMap().containsKey(new Position(x, y)) &&
                            simulation.getObjsMap().get(new Position(x, y)).getClass() != Hare.class &&
                            simulation.getObjsMap().get(new Position(x, y)).getClass() != Berries.class
                    ) || ( moveX == 0 && moveY == 0))
            {
                moveX = random.getMove()[0];
                moveY = random.getMove()[1];

                x = position.getX() + moveX;
                y = position.getY() + moveY;
                log.debug("cycles in in while id {}", id);
//                System.out.println("collisea! " + position);
            }
            aDeb++;
            if (aDeb>5 && aDeb<8){
                log.debug("cycles in 1 while id: {}", id);
            }
        } while(simulation.getNewObjsMap().containsKey(new Position(x, y)) &&
                simulation.getNewObjsMap().get(new Position(x, y)).getClass() != Berries.class
                || x >= simulation.getWidth() || y >= simulation.getHeigh() || x < 0 || y < 0);

        return new Position(x, y);
    }


    private Position searchEat(Position animPos, Map<Position, Entity> objsMap){

        //pos = в objsMap ищется ближайшая ягода к animPos

        Position pos = objsMap.entrySet().stream()
                .filter(e -> e.getValue().getClass() == Berries.class)
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
        if (lifes < 50){
            lifes = lifes + 5;
        }

    }

    private boolean checkEating(Simulation simulation, Position newPosition){
        Entity entity = simulation.getObjsMap().get(newPosition);
        return (entity != null && entity.getClass() == Berries.class);

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
        return "Hare " + getPosition();
    }
}
