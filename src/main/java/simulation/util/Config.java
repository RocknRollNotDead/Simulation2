package simulation.util;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv env = Dotenv.configure().ignoreIfMissing().load();

    public static int getWidth(){
        return Integer.parseInt(env.get("WIDTH"));
    }

    public static int getHeigh(){
        return Integer.parseInt(env.get("HEIGH"));
    }
    public static int getMaxElements(Class<?> clazz){
        String stringName = clazz.getSimpleName().toUpperCase() + "_MAX_COUNT";
        return Integer.parseInt(env.get(stringName));
    }
    public static int getMaxLevel(Class<?> clazz){
        String stringName = clazz.getSimpleName().toUpperCase() + "_MAX_LEVEL";
        return Integer.parseInt(env.get(stringName));
    }
    public static int getMaxLevelsOut(Class<?> clazz){
        String stringName = clazz.getSimpleName().toUpperCase() + "_MAX_LEVELS_OUT";
        return Integer.parseInt(env.get(stringName));
    }







}
