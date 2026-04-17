package simulation.util;

import java.util.Random;


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

    /*public int[] getCoordinates(){
        int x = random.nextInt(this.area.getLenght()[0]);
        int y = random.nextInt(this.area.getLenght()[1]);
        return new int[]{x, y};
    }*/
}
