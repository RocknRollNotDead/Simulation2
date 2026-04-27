package simulation.Entity;

public abstract class PeacefulAnimal extends Animals{

    public PeacefulAnimal(int x, int y) {
        super(x, y);
    }

    @Override
    protected boolean isEntityEdible(Entity entity) {
        return entity instanceof Edible;
    }

    @Override
    protected boolean isEntityCanEatMe(Entity entity) {
        return entity instanceof Predator;
    }
}
