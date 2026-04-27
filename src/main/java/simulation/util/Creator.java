package simulation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.Entity.Entity;
import simulation.backend.Position;

import java.util.Map;

public class Creator {
    private static final Logger log = LoggerFactory.getLogger(Creator.class);
    private final EntityCounter counter;
    private final int width;
    private final int heigh;


    public Creator(EntityCounter counter, int width, int heigh){
        this.counter = counter;
        this.width = width;
        this.heigh = heigh;
    }

    public Entity execute(Class<? extends Entity> clazz, Map<Position, Entity> objects){


        Entity entity = null;
        Randomizer random = new Randomizer();
        int x;
        int y;
        int countAttempts = 0;
        do{
            x = random.get(width);
            y = random.get(heigh);

            if(counter.getCount(clazz) < counter.getMaxCount(clazz) && (objects.get(new Position(x, y)) == null)){
                try {
                    entity = clazz.getDeclaredConstructor(int.class, int.class).newInstance(x, y);
                    log.info("object was created: "/* + clazz.getSimpleName()*/ + entity.getSymbol() + " " +
                            entity.getPosition().getX() + " " + entity.getPosition().getY() + " count "
                            + counter.getCount(clazz) + "  maxCount " + counter.getMaxCount(clazz));
                } catch (Exception e){
                    log.debug("object not was created: " + clazz.getSimpleName());
                }

            }
            if (countAttempts > 20) {
                log.error("Не удалось создать объект. Возможно, карта заполнена.");
                return null;

            }
            countAttempts++;
        } while (entity == null);
        return entity;
    }
}

