package simulation.backend;


import simulation.Entity.*;
import simulation.util.Config;
import simulation.util.Creator;
import simulation.util.EntityCompare;
import simulation.util.EntityCounter;

import java.util.*;

public class Simulation {
    private final Queue<Entity> forDelete = new LinkedList<>();
    private final EntityCounter counter = new EntityCounter();
    List<Class<? extends Entity>> types = new ArrayList<>();
    private Map<Position, Entity> objsMap = new HashMap<>();
    private final List<Entity> eatingList = new ArrayList<>();
    private final Map<Position, Entity> newObjsMap = new HashMap<>();
    private final Set<Entity> objsSet = new HashSet<>();
//    private boolean colliseum = false;


    private final int width, heigh;
    private long cycle;
    private Creator creator;

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
        this.width = Config.getWidth();
        this.heigh = Config.getHeigh();

    }

    public Simulation(int width, int heigh){

        this.width = width;
        this.heigh = heigh;
    }

    public void doMove(){

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
    //                it.remove(); // безопасное удаление
                    newObjsMap.remove(position);
                    forDelete.remove();
                    objsSet.remove(entity);
                    counter.decrementCount(entity.getClass());
                }
            }
        }
        while (!newObjsMap.values().containsAll(objsSet));


        objsMap = new HashMap<>(newObjsMap);

        this.cycle++;

        createEnts();


    }

    private void createEnts(){

        this.creator = new Creator(objsMap, counter, width, heigh);

        for (int i = 0; i < types.size(); i++) {
            Class<? extends Entity> clazz = types.get(i);

            if(EntityCompare.isCountLess(clazz, counter)){
                counter.clearLevels(clazz);
            } else if(!EntityCompare.isLevelsOutLess(clazz, counter)){
                Entity entity = creator.execute(clazz);
                objsMap.put(entity.getPosition(), entity);

                // пофиксить что после создания третьего обьекта не идет ожидание перед четвертым
                counter.incCount(clazz);

                counter.incLevelsOut(clazz);
            } else{
                counter.incLevelsOut(clazz);
            }


            if(EntityCompare.isCountLess(clazz, counter)){
                counter.clearLevels(clazz);
            }

//            System.out.println(EntityCounter.getCount(clazz));
        }

    }

    public void addInMapQue(Entity entity){
        this.eatingList.add(entity);
    }

    public List<Entity> getEatingList() {
        return eatingList;
    }

    public long getCycle(){
        return this.cycle;
    }

    public void addInQueue(Entity entity){
        this.forDelete.add(entity);
    }

    public Map<Position, Entity> getObjsMap() {
        return objsMap;
    }

    public Map<Position, Entity> getNewObjsMap() {
        return newObjsMap;
    }


    public int getWidth() {
        return width;
    }

    public int getHeigh() {
        return heigh;
    }
}
