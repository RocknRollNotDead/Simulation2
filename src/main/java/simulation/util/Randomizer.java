package simulation.util;

import simulation.backend.Position;

import java.util.Random;
import java.util.Set;


public class Randomizer {
    Random random = new Random();
//    PlayingArea area;

    public Randomizer(){
    }

    public int get(int max){
        return random.nextInt(max);
    }

    public int[] getMove(){

        int znakX = random.nextInt(2);
        int znakY = random.nextInt(2);

//        System.out.println("znak " + znakX);
//        System.out.println("znak " + znakY);

        int randomX = random.nextInt(2);
        int randomY = random.nextInt(2);

        if (znakX==0){
            randomX = randomX*(-1);
        }
        if (znakY==0){
            randomY = randomY*(-1);
        }
        return new int[]{randomX, randomY};
    }

    public Position getMove(Set<Position> setPos){
        if (setPos.isEmpty()){
            return null;
        } else{
            Position[] arrPos = setPos.toArray(new Position[0]);
            return arrPos[random.nextInt(arrPos.length)];
        }
    }

    /*public int[] getCoordinates(){
        int x = random.nextInt(this.area.getLenght()[0]);
        int y = random.nextInt(this.area.getLenght()[1]);
        return new int[]{x, y};
    }*/
}
