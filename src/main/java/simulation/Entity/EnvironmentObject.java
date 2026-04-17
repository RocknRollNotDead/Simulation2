package simulation.Entity;

import simulation.backend.Position;
import simulation.backend.Simulation;

public abstract class EnvironmentObject extends Entity{
    private int countLevels = 0;

    public EnvironmentObject(int x, int y) {
        super(x, y);
    }

    public Position doMove(Simulation simulation) {
        this.countLevels++;
        if(isDead()){
            simulation.addInQueue(this);
        }
        return this.getPosition();
    }
    public int getCountLevels() {
        return countLevels;
    }


}
