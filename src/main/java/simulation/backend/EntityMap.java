package simulation.backend;

import simulation.Entity.Entity;

import java.util.*;

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

    public boolean isPosBusy(Position position){
        boolean result = false;
        if (entities.containsKey(position)){
            result = true;
        }
        return result;
    }

    private Set<Position> calculateFreePositions(Position position){
        Set<Position> setPos = new LinkedHashSet<>();

        int x = position.x();
        int y = position.y();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i != 0 || j != 0){
                    Position pos = new Position(x+j, y+i);
                    if (!isPosBusy(pos) && isPositionExist(pos)){
                        setPos.add(pos);
                    }
                }
            }
        }
        return setPos;
    }



}
