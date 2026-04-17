package simulation.Entity;

import simulation.backend.Position;
import simulation.backend.Simulation;

public abstract class Entity {
    private Position position;

    public Entity (int x, int y){
        this.position = new Position(x, y);
    }

    public abstract Position doMove(Simulation simulation);
    protected abstract boolean isDead();
    public abstract String getSymbol();


    public Position getPosition(){
        return position;
    }
    public void setCoordinates(int x, int y){
        this.position = new Position(x, y);
    }
    public void setPosition(Position position){
        this.position = position;
    }

    @Override
    public String toString() {
        return "E " + position;
    }
}
