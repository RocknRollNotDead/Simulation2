package simulation.Entity;

import simulation.backend.Position;
import simulation.backend.Simulation;

public class Wolf extends Predator{
    public static final String symbol = "🐺"; // 🐺 "\uD83D\uDC3A"

    public Wolf(int x, int y) {
        super(x, y);
    }

    @Override
    protected Position searchMove(Simulation simulation) {
        return null;
    }

    @Override
    public Position doMove(Simulation simulation) {
        return null;
    }

    @Override
    protected boolean isDead() {
        return false;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
