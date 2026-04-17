package simulation;

import simulation.Entity.Entity;
import simulation.backend.Position;
import simulation.backend.Simulation;

import java.util.Map;

public class PlayingArea {
    private final int width, heigh;
    public static final String reset = "\u001B[0m";
    public static final String colorFon = "\u001B[40m";

    PlayingArea(int x, int y){
        this.width = x;
        this.heigh = y;}

    public void print(Simulation simulation) {
        Map<Position, Entity> map = simulation.getObjsMap();
        System.out.println("Цикл " + simulation.getCycle());
        String symbol;
        for (int i = 0; i < this.heigh; i++) {
            for (int j = 0; j < this.width; j++) {
                if (map.get(new Position(j, i))==null){
                    symbol = " ";
                }else{
                    symbol = map.get(new Position(j, i)).getSymbol();
                }

                System.out.printf("%s|%s|", colorFon, symbol);
            }
            System.out.println(reset);
        }
        System.out.println();
    }

    public int[] getLenght(){
        return new int[]{this.width, this.heigh};
    }


}
