package simulation.backend;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.Entity.*;
import simulation.util.Config;
import simulation.util.Creator;
import simulation.util.EntityCompare;
import simulation.util.EntityCounter;

import java.util.*;

public class Simulation {
    private static final Logger log = LoggerFactory.getLogger(Simulation.class);

    private final Queue<Entity> forDelete = new LinkedList<>();
    private final EntityCounter counter = new EntityCounter();
    private final List<Class<? extends Entity>> types = new ArrayList<>();
    private final Set<Entity> eatingList = new HashSet<>();
    private final Map<Position, Entity> newObjsMap = new HashMap<>();
    private Map<Position, Entity> objsMap = new HashMap<>();
    /**
     * forDelete - туда попадают сущности, которые надо удалить, чтобы удалять их не сразу, а один ход = одно удаление
     * у каждого класса
     * Счётчик следит за тем, сколько сущностей конкретного класса
     * eatingList - лист тех, кого сьедают. Управляется из классов <? наслед Animals>
     * objsMap хранит основную полноценную карту обьектов.
     * *
     * По objsMap мы иттерируемся, но записываем все объекты в newObsMap, и после итерации тупо записываем
     * newObjsMap в objsMap.    (как раз из-за objsMap = newObjsMap, objsMap не final)
     * */


    private final int width, height;
    private long cycle;
    private final Creator creator;

    {
        types.add(Iwe.class);
        types.add(Tree.class);
        types.add(Hare.class);
        types.add(Berries.class);

        // добавить еще классов когда они будут готовы

        for(Class<?> clazz : types){
            counter.addClass(clazz);
        }
    }


    public Simulation() {
        this(Config.getWidth(), Config.getHeigh());
    }

    public Simulation(int width, int heigh){
        this.width = width;
        this.height = heigh;
        this.creator = new Creator(counter, width, heigh);
    }

    public void doMove(){

        Position position;
        newObjsMap.clear();
        Iterator<Map.Entry<Position, Entity>> it = objsMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Position, Entity> entry = it.next();
            Entity entity = entry.getValue();

            entity.setPosition(entry.getKey());
            position = entity.doMove(this);
            entity.setPosition(position);

            /***
             * итерируемся по нашей мапе, в идеале чтобы сначала прошлись по зайцу,
             * и когда дойдём до ягоды в этом же цикле проверяли бы не съели ли эту ягоду.
             * но зачастую по зайцу проходятся после прохождения по ягоде, и ягода остаётся в мапе до следующего цикла
             *
             * я пытался решить циклом вайл, чтобы ни в коем случае в следующий тур не переходить с устаревшей мапой,
             * но мне подсказали, что такие циклы вайл лучше не делать. Поэтому они закомментированы, и в следующий цикл всё-таки попадает устаревшая мапа
             * На выходящеё картинке это сказаться не должно, но я не 100% уверен в этом.
             */
            if(!eatingList.contains(entity)){
                newObjsMap.put(position, entity);
            }else{
                boolean resRemove = deletingEntFromMap(entity, newObjsMap);
//                log.info("res remove {} {}", resRemove, entity);
                eatingList.remove(entity);
            }
        }
        objsMap = new HashMap<>(newObjsMap);

        //log.info("Map {}  eatlist {}", newObjsMap.values(), eatingList);

        this.cycle++;
        createEnts();
        deleteLostInEatingList();
        deleteObjectsQueue();
        log.info("cycle {}", cycle);
    }

    private void createEnts(){

        for (Class<? extends Entity> clazz : types) {
            if (EntityCompare.isCountFill(clazz, counter)) {
                counter.clearLevels(clazz);
            } else if (EntityCompare.isNeedToBeCreated(clazz, counter)) {
                Entity entity = creator.execute(clazz, objsMap);
                if (entity != null){
                    objsMap.put(entity.getPosition(), entity);

                    // пофиксить что после создания третьего обьекта не идет ожидание перед четвертым
                    counter.incCount(clazz);
                } else {
                    log.debug("не удалось создать объект {}", objsMap);
                }
                counter.incLevelsOut(clazz);
            } else {
                counter.incLevelsOut(clazz);
            }

            if (EntityCompare.isCountFill(clazz, counter)) {
                counter.clearLevels(clazz);
            }
        }

    }

    private boolean deleteObjectsQueue(){
        boolean result = false;

        for(Entity entity : forDelete){
            boolean res = deletingEntFromMap(entity, objsMap);
            result = true;
//            log.info("удаление {}  res {}", entity, res);
        }
        forDelete.clear();
        return result;
    }

    private boolean deletingEntFromMap(Entity entity, Map<Position, Entity> map){
        boolean resRemove = map.remove(entity.getPosition(), entity);
//        objsMap.remove(entity.getPosition(), entity);
        counter.decrementCount(entity.getClass());
        return resRemove;

    }

    private boolean deleteLostInEatingList(){
        boolean result = false;
        for(Entity entity : eatingList){
            counter.decrementCount(entity.getClass());
            result = true;
        }
        eatingList.clear();
        return result;
    }

    public boolean isPosExist(Position position){
        int x = position.getX();
        int y = position.getY();

        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void addInSetForEating(Entity entity){
        this.eatingList.add(entity);
    }
    public void addInQueueDel(Entity entity){
        this.forDelete.add(entity);
    }

    public long getCycle(){
        long a = this.cycle;
        return a;
    }

    public Map<Position, Entity> getObjsMap() {
        return new HashMap<>(objsMap);
    }

    public Map<Position, Entity> getNewObjsMap() {
        return new HashMap<>(newObjsMap);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
