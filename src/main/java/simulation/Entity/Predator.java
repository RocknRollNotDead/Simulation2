package simulation.Entity;

public abstract class Predator extends Animal {


    public Predator(int x, int y) {
        super(x, y);
    }


    @Override
    protected boolean isEntityEdible(Entity entity) {
        return entity instanceof PeacefulAnimal;
    }
    @Override
    protected boolean isEntityCanEatMe(Entity entity){
        return false;
    }
}
