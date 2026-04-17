package simulation.Entity;

import simulation.util.Config;

public class Berries extends Edible{
    public static final int MAX_LEVEL_LIFE = Config.getMaxLevel(Berries.class);
    private static final String SYMBOL = "\uD83C\uDF52";//🍒 Я хотел чернику, но он чернику показывает как 🫐

    public Berries(int x, int y) {
        super(x, y);
    }

    @Override
    protected boolean isDead() {
        return MAX_LEVEL_LIFE <= getCountLevels();
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

    @Override
    public String toString() {
        return "B " + getPosition();
    }
}
