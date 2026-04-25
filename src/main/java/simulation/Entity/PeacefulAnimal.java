package simulation.Entity;

public abstract class PeacefulAnimal extends Animals{
    private final int id;
    private static int count;

    public PeacefulAnimal(int x, int y) {
        super(x, y);
        count++;
        id = count;
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
