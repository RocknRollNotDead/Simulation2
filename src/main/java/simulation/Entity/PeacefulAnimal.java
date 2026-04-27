package simulation.Entity;

public abstract class PeacefulAnimal extends Animals{
    private final int id;

    public PeacefulAnimal(int x, int y) {
        super(x, y);
        simulation.getCounter().incId(this.getClass());
        id = simulation.getCounter().getId(this.getClass());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    protected boolean isEntityCanEatMe(Entity entity) {
        return entity instanceof Predator;
    }
}
