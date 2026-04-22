package simulation.util;

import simulation.Entity.Entity;

public class EntityCompare {

    public static boolean isCountFill(Class<? extends Entity> clazz, EntityCounter counter){
        return counter.getCount(clazz) >= counter.getMaxCount(clazz);
    }

    public static boolean isNeedToBeCreated(Class<? extends Entity> clazz, EntityCounter counter){
        return counter.getLevelsOut(clazz) >= counter.getMaxLevelsOut(clazz);
    }


}
