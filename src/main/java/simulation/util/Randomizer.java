package simulation.util;

import simulation.backend.Position;

import java.util.Random;
import java.util.Set;


public class Randomizer {
    Random random = new Random();

    public Randomizer(){
    }

    public int get(int max){
        return random.nextInt(max);
    }

    public Position getMove(){

        int znakX = random.nextInt(2);
        int znakY = random.nextInt(2);
        int randomX = random.nextInt(2);
        int randomY = random.nextInt(2);

        if (znakX==0){
            randomX = randomX*(-1);
        }
        if (znakY==0){
            randomY = randomY*(-1);
        }
        return new Position(randomX, randomY);
    }

    public Position getMove(Set<Position> setPos){
        if (setPos.isEmpty()){
            return null;
        } else{
            Position[] arrPos = setPos.toArray(new Position[0]);
            return arrPos[random.nextInt(arrPos.length)];
        }
    }
}
