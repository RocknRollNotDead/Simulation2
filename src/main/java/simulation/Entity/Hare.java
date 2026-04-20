package simulation.Entity;

import simulation.backend.Position;
import simulation.backend.Simulation;
import simulation.util.Config;

import java.util.*;

public class Hare extends Animals{

    private static final String SYMBOL = "\uD83D\uDC30"; // 🐰
    private int lifes = 30;
    private static int count;
    private final int id;

    public Hare(int x, int y) {
        super(x, y);
        count++;
        id = count;
    }

    @Override
    public Position doMove(Simulation simulation) {

        Position newPosition = searchMove(simulation);

        lifes--;

        if (lifes <= 0){
            simulation.addInQueue(this);
        }
        System.out.println(" Hare " + id + " " + newPosition + "  lifes: " + lifes);

        return newPosition;
    }

    @Override
    protected Position searchMove(Simulation simulation) {
        int x;
        int y;
        int a = 0;
        Map<Position, Entity> objsMap = simulation.getObjsMap();

        Position position = getPosition();
        int posAroundX;
        int posAroundY;

        int moveX = 0;
        int moveY = 0;

        int bDebug = 0;
        do{
            Position berPos = searchEat(getPosition(), simulation.getObjsMap());
            Position newPos = berPos;
            int raznicaX = 0;
            int raznicaY = 0;
            if (berPos != null){
                raznicaX = berPos.getX() - getPosition().getX();
                raznicaY = berPos.getY() - getPosition().getY();
                moveX = Integer.compare(raznicaX, 0);
                moveY = Integer.compare(raznicaY, 0);
            }else{
                moveX = random.getMove()[0];
                moveY = random.getMove()[1];
            }

            x = position.getX() + moveX;
            y = position.getY() + moveY;

            int aDebug=0;
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
                System.out.println("new moves  " + moveX + " " + moveY);
                if (aDebug>5){
                    System.out.println("Cycles in search");

                }
                aDebug++;

                x = position.getX() + moveX;
                y = position.getY() + moveY;
//                System.out.println("collisea! " + position);
//                System.out.println((position.getX() + moveX) + " " + (position.getY() + moveY));
            }



//            x = position.getX() + moveX;
//            y = position.getY() + moveY;

            if (bDebug>5){
                System.out.println("Cycles in confirm move");
                if (simulation.getNewObjsMap().containsKey(new Position(x, y))){
                    System.out.println("class " + simulation.getNewObjsMap().get(new Position(x, y)).getClass().getSimpleName());
                    System.out.println("x and y " + x + " " + y);
                }
            }

            bDebug++;
        } while(//(simulation.getObjsMap().containsKey(new Position(x, y)) && objsMap.get(new Position(x, y)).getClass() != Berries.class) &&
                //(simulation.getNewObjsMap().containsKey(new Position(x, y)) && simulation.getObjsMap().get(new Position(x, y)).getClass() != Berries.class)
                (simulation.getNewObjsMap().containsKey(new Position(x, y)) && simulation.getNewObjsMap().get(new Position(x, y)).getClass() != Berries.class)
                || x >= simulation.getWidth() || y >= simulation.getHeigh() || x < 0 || y < 0);

        Entity entity = objsMap.get(new Position(x, y));

        if (entity != null && entity.getClass() == Berries.class){
            eating(simulation, entity);
            System.out.println("eating  " + entity.getPosition().getX() + " " + entity.getPosition().getY());
        }

        return new Position(x, y);
    }


    private Position searchEat(Position animPos, Map<Position, Entity> objsMap){

        //berPos = в objsMap ищется ближайшая ягода к animPos

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




    public void eating(Simulation simulation, Entity entity){
        simulation.addInMapQue(entity);
        lifes = lifes + 5;
    }

    @Override
    protected boolean isDead() {
        return false;
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
