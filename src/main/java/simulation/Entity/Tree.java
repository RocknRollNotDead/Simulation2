package simulation.Entity;

import simulation.util.Config;
import simulation.backend.Position;
import simulation.backend.Simulation;

public class Tree extends EnvironmentObject{

    public final static int MAX_COUNT = 2;
    public final static int MAX_LEVELS_OUT = 5;
    public final static int MAX_LEVEL_LIFE = Config.getMaxLevel(Tree.class);
    public final static String symbol = "\uD83C\uDF33";

    public Tree(int x, int y){
        super(x, y);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    protected boolean isDead() {
        return MAX_LEVEL_LIFE <= getCountLevels();
    }
}
