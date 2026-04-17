package simulation.Entity;

import simulation.backend.Position;
import simulation.backend.Simulation;

import java.util.*;

public class Hare extends Animals{

    private static final String SYMBOL = "\uD83D\uDC30"; // 🐰

    public Hare(int x, int y) {
        super(x, y);
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

        do{

            /*for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    posAroundX = getPosition().getX() + i;
                    posAroundY = getPosition().getY() + j;

                    if(objsMap.containsKey(new Position(posAroundX, posAroundY)) &&
                            objsMap.get(new Position(posAroundX, posAroundY)).getClass() == Berries.class){
                        eat = true;
                        moveX = i;
                        moveY = j;
                    }
                }
            }*/



            Position berPos = searchEat(getPosition(), simulation.getObjsMap());
            Position newPos = berPos;
            /*if(!simulation.getEatingList().contains(simulation.getObjsMap().get(newPos))){

            }else{

            }*/
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


            /*if((simulation.getNewObjsMap().containsKey(new Position(position.getX() + moveX,
                    position.getY() + moveY)) ||
                    simulation.getObjsMap().containsKey(new Position(position.getX() + moveX,
                            position.getY() + moveY)) &&
                            simulation.getObjsMap().get(
                                    new Position
                                            (position.getX() + moveX,
                                                    position.getY() + moveY)
                            ).getClass() != Hare.class) &&
                    simulation.getNewObjsMap().get(
                            new Position
                                    (position.getX() + moveX,
                                            position.getY() + moveY)
                    ).getClass() != Berries.class){
                System.out.println("col!");
            }
*/
            while /*(simulation.getNewObjsMap().containsKey(new Position(position.getX() + moveX,
                    position.getY() + moveY)) &&
            simulation.getNewObjsMap().get(
                    new Position
                            (position.getX() + moveX,
                    position.getY() + moveY)
            ).getClass() != Berries.class)*/
            ((simulation.getNewObjsMap().containsKey(new Position(position.getX() + moveX,
                    position.getY() + moveY)) &&
                    simulation.getNewObjsMap().get(
                            new Position(position.getX() + moveX,
                                    position.getY() + moveY)
                    ).getClass() != Berries.class
                    ||
                    simulation.getObjsMap().containsKey(new Position(position.getX() + moveX,
                            position.getY() + moveY)) &&
                            simulation.getObjsMap().get(
                                    new Position(position.getX() + moveX,
                                                    position.getY() + moveY)
                            ).getClass() != Hare.class &&
                            simulation.getObjsMap().get(
                                    new Position(position.getX() + moveX,
                                            position.getY() + moveY)
                            ).getClass() != Berries.class
                    ))


            {
                System.out.println("collisea! " + position);

                /*raznicaX = raznicaX + random.getMove()[0];
                raznicaY = raznicaY + random.getMove()[1];

                moveX = Integer.compare(raznicaX, 0);
                moveY = Integer.compare(raznicaY, 0);*/
                moveX = random.getMove()[0];
                moveY = random.getMove()[1];

                System.out.println(position.getX() + moveX + " " + position.getY() + moveY);
            }



            x = position.getX() + moveX;
            y = position.getY() + moveY;
            if(objsMap.containsKey(new Position(x, y)) && a<6){
                System.out.println(x + " " + y);
                a++;
            }


        } while(simulation.getNewObjsMap().containsKey(new Position(x, y)) && objsMap.get(new Position(x, y)).getClass()!= Berries.class
                || x >= simulation.getWidth() || y >= simulation.getHeigh() || x < 0 || y < 0);

        Entity entity = objsMap.get(new Position(x, y));

        if (entity != null && entity.getClass() == Berries.class){
            eating(simulation, entity);
            System.out.println("eating  " + entity.getPosition().getX() + " " + entity.getPosition().getY());
        }

        return new Position(x, y);
    }

    @Override
    public Position doMove(Simulation simulation) {

        Position newPosition = searchMove(simulation);

        return newPosition;
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
//                .forEach(e -> System.out.println(e.getKey() + " = " + e.getValue()));


        return pos;
    }




    public void eating(Simulation simulation, Entity entity){
        simulation.addInMapQue(entity);
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
