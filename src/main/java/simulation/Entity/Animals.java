package simulation.Entity;

import simulation.backend.Position;
import simulation.backend.Simulation;
import simulation.util.Randomizer;

import java.util.Map;

public abstract class Animals extends Entity{

    private int countLevels = 0;

    protected Randomizer random = new Randomizer();

    public Animals(int x, int y) {
        super(x, y);
    }

    public int getCountLevels() {
        return countLevels;
    }

    protected abstract Position searchMove(Simulation simulation);

}
