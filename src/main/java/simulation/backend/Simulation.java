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

    private final List<String> eatingEvents = new ArrayList<>();
    /**
     * forDelete - туда попадают сущности, которые надо удалить, чтобы удалять их не сразу, а один ход = одно удаление
     * у каждого класса
     * Счётчик следит за тем, сколько сущностей конкретного класса
     * eatingList - лист тех, кого сьедают. Управляется из классов <? наслед Animals>
     * objsMap хранит основную полноценную карту обьектов.
     * *
     * По objsMap мы иттерируемся, но записываем все объекты в newObsMap, и после итерации тупо записываем
     * newObjsMap в objsMap.    (как раз из-за objsMap = newObjsMap, objsMap не final)
     * eatingEvents создал ИИ помощник для того, чтобы прописывать события съедения (а также смерти от голода) в веб-версии
     * */


    private final int width, height;
    private long cycle;
    private final Creator creator;

    {
        types.add(Iwe.class);
        types.add(Tree.class);
        types.add(Hare.class);
        types.add(Wolf.class);
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

        List<Map.Entry<Position, Entity>> entries = new ArrayList<>(objsMap.entrySet());
        entries.sort(Comparator.comparingInt(this::getNumPriority));

        for (Map.Entry<Position, Entity> entry : entries) {
            Entity entity = entry.getValue();

            entity.setPosition(entry.getKey());
            position = entity.doMove(this);
            entity.setPosition(position);

            if (!eatingList.contains(entity)) {
                newObjsMap.put(position, entity);
            } else {
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

    private int getNumPriority(Map.Entry<Position, Entity> e) {
        return switch (e.getValue()) {
            case EnvironmentObject environmentObject -> 1;
            case PeacefulAnimal peacefulAnimal -> 2;
            case Predator predator -> 3;
            case null, default -> 0;
        };
    }

    public boolean isPosExist(Position position){
        int x = position.getX();
        int y = position.getY();

        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void addInSetForEating(Entity entity){
        this.eatingList.add(entity);
    }
    
    public void addEatingEvent(Entity eater, Entity eaten){
        String event = eater.getSymbol() + " eat " + eaten.getSymbol();
        eatingEvents.add(event);
        log.info(event);
    }
    
    public void addDeathEvent(Entity entity){
        String event = entity.toString() + " DEAD!X!X!";
        eatingEvents.add(event);
        log.info(event);
    }

    public EntityCounter getCounter(){
        return counter;
    }

    public void incIdToCounter(Class<?> clazz){
        counter.incId(clazz);
    }


    public List<String> getEatingEvents(){
        return new ArrayList<>(eatingEvents);
    }
    
    public void clearEatingEvents(){
        eatingEvents.clear();
    }
    
    public void addInQueueDel(Entity entity){
        this.forDelete.add(entity);
    }

    public long getCycle(){
        return this.cycle;
    }

    public Map<Position, Entity> getObjsMap() {
        return  Collections.unmodifiableMap(objsMap);
    }

    public Map<Position, Entity> getNewObjsMap() {
        return Collections.unmodifiableMap(newObjsMap);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
