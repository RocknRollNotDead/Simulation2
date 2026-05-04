package simulation.Entity;

import simulation.other.Config;

public class Iwe extends EnvironmentObject {

    public static final int MAX_LEVEL_LIFE = Config.getMaxLevel(Iwe.class);

    public static final String symbol =  "⦿";

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