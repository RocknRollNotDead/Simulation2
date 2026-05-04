package simulation.backend;

import simulation.Entity.Entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EntityMap {
    private Map<Position, Entity> entities = new HashMap<>();
    private final int width;
    private final int height;

    public EntityMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Map<Position, Entity> getMap() {
        return  Collections.unmodifiableMap(entities);
    }

    public void update(Position position, Entity entity){
        entities.put(position, entity);
    }

    public boolean isPositionExist(Position position){
        return position.x() >= 0 && position.x() < width
                && position.y() >= 0 && position.y() < height;
    }


}
