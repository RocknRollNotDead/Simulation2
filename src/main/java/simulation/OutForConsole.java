package simulation;

import simulation.Entity.Entity;
import simulation.backend.Position;
import simulation.backend.Simulation;

import java.util.Map;

public class OutForConsole {

    public static final String resetColor = "\u001B[0m";
    public static final String backgroundColor = "\u001B[40m";

    public OutForConsole (){

    }

    public void print(Simulation simulation) {
        Map<Position, Entity> map = simulation.getObjsMap();
        System.out.println("Цикл " + simulation.getCycle());
        String symbol;
        for (int i = 0; i < simulation.getHeight(); i++) {
            for (int j = 0; j < simulation.getWidth(); j++) {
                if (map.get(new Position(j, i))==null){
                    symbol = "  ";
                }else{
                    symbol = map.get(new Position(j, i)).getSymbol();
                }

                System.out.printf("%s|%s|", backgroundColor, symbol);
            }
            System.out.println(resetColor);
        }
        System.out.println();
    }



}
