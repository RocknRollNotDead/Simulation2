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
        // cначала вычисляется ближайшая ягода
        // потом если можно пойти напрямую идем напрямую
        // если нельзя - рандомный ход (вообще по-хорошему придумать как обходить препятствия, но это слишком сложно и мне лень)
        // или - составляем список свободных позиций и из них выбираем
        // либо рандомно, либо составляем путь

        // Position berPos = searchEat(getPosition(), simulation.getObjsMap());
        //

        int x;
        int y;
        int moveX;
        int moveY;

        Position position = getPosition();

        //do{
            Position berPos = searchEat(position, simulation.getObjsMap());
//            int differenceInX;
//            int differenceInY;
            /*if (berPos != null){
//                differenceInX = berPos.getX() - getPosition().getX();
//                differenceInY = berPos.getY() - getPosition().getY();
//                moveX = Integer.compare(differenceInX, 0);
//                moveY = Integer.compare(differenceInY, 0);
                moveX = Integer.compare(getPosition().getX(), berPos.getX());
                moveY = Integer.compare(getPosition().getY(), berPos.getY());
            }else{
                int[] move = random.getMove();
                moveX = move[0];
                moveY = move[1];
            }

            x = position.getX() + moveX;
            y = position.getY() + moveY;
            Position newPos = new Position(x, y);

            if(isPosBusy(newPos, simulation)){*/
                Set<Position> setPos = calculateFreePos(simulation, position);
                Position newPos = searchPosition(setPos, berPos);
            //}
        /**
         * или можно сразу вычислять свободные позиции
         *
         *
         */

            /*while
            ((simulation.getNewObjsMap().containsKey(newPos) &&
                    simulation.getNewObjsMap().get(
                            new Position(x, y)).getClass() != Berries.class
                    ||
                    simulation.getObjsMap().containsKey(newPos) &&
                            simulation.getObjsMap().get(newPos).getClass() != Hare.class &&
                            simulation.getObjsMap().get(newPos).getClass() != Berries.class
                    ) || ( moveX == 0 && moveY == 0) || x < 0 || y < 0 || x >= simulation.getWidth() ||
                    y >= simulation.getHeigh() )
            {
                moveX = random.getMove()[0];
                moveY = random.getMove()[1];

                x = position.getX() + moveX;
                y = position.getY() + moveY;
                newPos = new Position(x, y);
                log.debug("cycles in in while id {}", id);
//                System.out.println("collisea! " + position);
            }*/




        /*} while(simulation.getNewObjsMap().containsKey(new Position(x, y)) &&
                simulation.getNewObjsMap().get(new Position(x, y)).getClass() != Berries.class
                || x >= simulation.getWidth() || y >= simulation.getHeigh() || x < 0 || y < 0);*/

//        return new Position(x, y);
        return newPos;
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

    private boolean isPosBusy(Position position, Simulation simulation){
        boolean result = false;
        if (simulation.getObjsMap().containsKey(position)){
            if (simulation.getObjsMap().get(position).getClass() != Hare.class &&
                    simulation.getObjsMap().get(position).getClass() != Berries.class){
                result = true;
            }

        }
        if(simulation.getNewObjsMap().containsKey(position) &&
                simulation.getNewObjsMap().get(position).getClass() != Berries.class){
            result = true;
        }
        return result;
    }

    private Set<Position> calculateFreePos(Simulation simulation, Position position){
        Set<Position> setPos = new LinkedHashSet<>();

        int x = position.getX();
        int y = position.getY();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Position pos = new Position(x+j, y+i);
                if (!isPosBusy(pos, simulation)){
                    setPos.add(pos);
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
        return setPos.stream()
                .min(Comparator.comparingInt(pos ->
                        Math.max(
                                Math.abs(targetPos.getX() - pos.getX()),
                                Math.abs(targetPos.getY() - pos.getY())
                        )
                ))
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
