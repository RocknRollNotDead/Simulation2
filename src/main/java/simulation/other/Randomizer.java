package simulation.other;

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

    public Position getMove(Set<Position> setPos){
        if (setPos.isEmpty()){
            return null;
        } else{
            Position[] arrPos = setPos.toArray(new Position[0]);
            return arrPos[random.nextInt(arrPos.length)];
        }
    }
}
