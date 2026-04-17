package simulation.util;

import simulation.Entity.Entity;
import simulation.backend.Position;

import java.util.Map;

public class Creator {
    private final Map<Position, Entity> objects;
    EntityCounter counter;
    private int width;
    private int heigh;


    public Creator(Map<Position, Entity> objects, EntityCounter counter, int width, int heigh){
        this.objects = objects;
        this.counter = counter;
        this.width = width;
        this.heigh = heigh;
    }

    public Entity execute(Class<? extends Entity> clazz){

        Entity entity = null;
        Randomizer random = new Randomizer();
        int x;
        int y;

        do{

//            x = random.getX();
//            y = random.getY();
            x = random.get(width);
            y = random.get(heigh);


            if(counter.getCount(clazz)<counter.getMaxCount(clazz) & (objects.get(new Position(x, y)) == null)){
                try {
                    entity = clazz.getDeclaredConstructor(int.class, int.class).newInstance(x, y);
                    System.out.println("object was created: " /*+ clazz.getSimpleName()*/ + entity.getSymbol() + " " +  entity.getPosition().getX() + " " + entity.getPosition().getY());
                } catch (Exception e){
                    System.out.println("object not was created: " + clazz.getSimpleName());
                }

            }
        } while (entity == null);
        return entity;
    }
}

