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
    private final List<Class<? extends Entity>> types = new ArrayList<>(); // тут был arrayList
    private final Set<Entity> eatingList = new HashSet<>();
    private final Set<Entity> objsSet = new HashSet<>();
    private final Map<Position, Entity> newObjsMap = new HashMap<>();
    private Map<Position, Entity> objsMap = new HashMap<>();
    /**
     * forDelete - туда попадают сущности, которые надо удалить, чтобы удалять их не сразу, а один ход = одно удаление
     * у каждого класса
     * Счётчик следит за тем, сколько сущностей конкретного класса
     * eatingList - лист тех, кого сьедают. Управляется из классов <? наслед Animals>
     * objSet - список всех объектов карты
     * objsMap хранит основную полноценную карту обьектов.
     * *
     * По objsMap мы иттерируемся, но записываем все объекты в newObsMap, и после итерации тупо записываем
     * newObjsMap в objsMap.    (как раз из-за objsMap = newObjsMap, objsMap не final)
     * */


    private final int width, heigh;
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
        this.heigh = heigh;
        this.creator = new Creator(counter, width, heigh);
    }

    public void doMove(){

        int aDebug = 0;
        do{
            newObjsMap.clear();
            Iterator<Map.Entry<Position, Entity>> it = objsMap.entrySet().iterator();
            Position position;

            while (it.hasNext()) {
                Map.Entry<Position, Entity> entry = it.next();
                Entity entity = entry.getValue();


                entity.setPosition(entry.getKey());
                position = entity.doMove(this);
                entity.setPosition(position);


                if(!eatingList.contains(entity)){

                    newObjsMap.put(position, entity);
                    objsSet.add(entity);

                }else{

                    boolean resRemove = newObjsMap.remove(entity.getPosition(), entity);
                    objsSet.remove(entity);

                    counter.decrementCount(entity.getClass());
                    eatingList.remove(entity);
                }


                if (!forDelete.isEmpty() && entity.equals(forDelete.element())) {
                    it.remove(); // безопасное удаление
                    newObjsMap.remove(position);
                    forDelete.remove();
                    objsSet.remove(entity);
                    counter.decrementCount(entity.getClass());
                }
            }
            aDebug++;
            if(!newObjsMap.values().containsAll(objsSet)){
                log.error("{}    {}", newObjsMap.values(), objsSet);
            }
        }
        while (!newObjsMap.values().containsAll(objsSet) && aDebug < 20);

        objsMap = new HashMap<>(newObjsMap);


        this.cycle++;
        createEnts();
        log.info("cycle {}", cycle);
    }

    private void createEnts(){


        for (Class<? extends Entity> clazz : types) {

            if (EntityCompare.isCountFill(clazz, counter)) {
                counter.clearLevels(clazz);
            } else if (EntityCompare.isNeedToBeCreated(clazz, counter)) {
                Entity entity = creator.execute(clazz, objsMap);
                objsMap.put(entity.getPosition(), entity);

                // пофиксить что после создания третьего обьекта не идет ожидание перед четвертым
                counter.incCount(clazz);

                counter.incLevelsOut(clazz);
            } else {
                counter.incLevelsOut(clazz);
            }

            if (EntityCompare.isCountFill(clazz, counter)) {
                counter.clearLevels(clazz);
            }
        }

    }

    public void addInSetForEating(Entity entity){
        this.eatingList.add(entity);
    }
    public void addInQueueDel(Entity entity){
        this.forDelete.add(entity);
    }

    public long getCycle(){
        return this.cycle;
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

    public int getHeigh() {
        return heigh;
    }
}
