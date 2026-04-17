package simulation.Entity;

import simulation.backend.Position;
import simulation.backend.Simulation;
import simulation.util.Config;

public class Iwe extends EnvironmentObject {


    public static final int MAX_COUNT = 4;
    public static final int MAX_LEVELS_OUT = 4;
    public static final int MAX_LEVEL_LIFE = Config.getMaxLevel(Iwe.class);

    private static final String reset = "\u001B[39m"; // сброс только буквы
    private static final String gray = "\u001B[97m";

    public static final String symbol = gray + "⦿" + reset;

    public Iwe(int x, int y) {
        super(x, y);
    }

    @Override
    protected boolean isDead() {
        return MAX_LEVEL_LIFE <= getCountLevels();
    }
    @Override
    public String getSymbol() {
        return symbol;
    }

}