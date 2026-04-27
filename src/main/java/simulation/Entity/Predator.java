package simulation.Entity;

public abstract class Predator extends Animals{
    private final int id;

    public Predator(int x, int y) {
        super(x, y);
        simulation.getCounter().incId(this.getClass());
        id = simulation.getCounter().getId(this.getClass());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    protected boolean isEntityCanEatMe(Entity entity){
        return false;
    }
}
