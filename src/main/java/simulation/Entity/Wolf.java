package simulation.Entity;

import simulation.backend.Position;
import simulation.backend.Simulation;

public class Wolf extends Predator{
    public static final String symbol = "🐺"; // 🐺 "\uD83D\uDC3A"
    private static final int COUNT_LIFES_FIRST_TIME = 30;
    private static final int MAX_COUNT_LIFES = COUNT_LIFES_FIRST_TIME + 20;

    public Wolf(int x, int y) {
        super(x, y);
    }



    @Override
    protected boolean isEntityEdible(Entity entity) {
        return entity instanceof PeacefulAnimal;
    }

    @Override
    protected int getCountLifesFirstTime() {
        return COUNT_LIFES_FIRST_TIME;
    }

    @Override
    protected int getMaxCountLifes() {
        return MAX_COUNT_LIFES;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }


}
