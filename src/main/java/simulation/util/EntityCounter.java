package simulation.util;


import java.util.HashMap;
import java.util.Map;

public class EntityCounter {
    private final Map<Class<?>, Integer> countMap = new HashMap<>();
    private final Map<Class<?>, Integer> levelsOutMap = new HashMap<>();
    private final Map<Class<?>, Integer> maxCountMap = new HashMap<>();
    private final Map<Class<?>, Integer> maxLevelsOut = new HashMap<>();
    private final Map<Class<?>, Integer> countIds = new HashMap<>();

    public int getCount(Class<?> clazz) {
        return countMap.getOrDefault(clazz, 0);
    }

    public int getLevelsOut(Class<?> clazz) {
        return levelsOutMap.getOrDefault(clazz, 0);
    }

    public int getMaxCount(Class<?> clazz) {
        return maxCountMap.getOrDefault(clazz, 0);
    }

    public int getMaxLevelsOut(Class<?> clazz) {
        return maxLevelsOut.getOrDefault(clazz, 0);
    }

    public int getId(Class<?> clazz){
        return countIds.get(clazz);
    }

    public void setMaxCount(Class<?> clazz, int maxCount) {
        maxCountMap.put(clazz, maxCount);
    }

    public void setMaxLevelsOut(Class<?> clazz, int maxCount) {
        maxLevelsOut.put(clazz, maxCount);
    }

    public void incCount(Class<?> clazz) {
        countMap.put(clazz, getCount(clazz) + 1);
    }

    public void incLevelsOut(Class<?> clazz) {
        levelsOutMap.put(clazz, getLevelsOut(clazz) + 1);
    }

    public void incId(Class<?> clazz) {
        countIds.put(clazz, getId(clazz) + 1);
    }

    public void clearLevels(Class<?> clazz) {
        levelsOutMap.put(clazz, 0);
    }
    public void decrementCount(Class<?> clazz) {
        if(getCount(clazz) > 0){
            countMap.put(clazz, getCount(clazz) - 1);
        }
    }

    public void addClass(Class<?> clazz){
        setMaxCount(clazz, Config.getMaxElements(clazz));
        setMaxLevelsOut(clazz, Config.getMaxLevelsOut(clazz));
        levelsOutMap.put(clazz, getMaxLevelsOut(clazz));
        countIds.put(clazz.getSuperclass(), 0);
    }
}
