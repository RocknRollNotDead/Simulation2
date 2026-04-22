package simulation.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.backend.Simulation;

public class Config {
    private static final Dotenv env = Dotenv.configure().ignoreIfMissing().load();
    private static final Logger log = LoggerFactory.getLogger(Config.class);
    public static int getWidth(){
        try {
            return Integer.parseInt(env.get("WIDTH"));
        } catch (RuntimeException e) {
            log.error("Не удалось найти width");
            return 0;
        }

    }

    public static int getHeigh(){
        try{
            return Integer.parseInt(env.get("HEIGH"));
        }catch (RuntimeException e) {
            log.error("Не удалось найти heigh");
            return 0;
        }

    }
    public static int getMaxElements(Class<?> clazz){
        String stringName = clazz.getSimpleName().toUpperCase() + "_MAX_COUNT";
        try{
            return Integer.parseInt(env.get(stringName));
        }catch (RuntimeException e) {
            log.error("Не удалось найти {}", stringName);
            return 0;
        }

    }
    public static int getMaxLevel(Class<?> clazz){
        String stringName = clazz.getSimpleName().toUpperCase() + "_MAX_LEVEL";
        try{
            return Integer.parseInt(env.get(stringName));
        }catch (RuntimeException e) {
            log.error("Не удалось найти {}", stringName);
            return 0;
        }

    }
    public static int getMaxLevelsOut(Class<?> clazz){
        String stringName = clazz.getSimpleName().toUpperCase() + "_MAX_LEVELS_OUT";
        try{
            return Integer.parseInt(env.get(stringName));
        }catch (RuntimeException e) {
            log.error("Не удалось найти {}", stringName);
            return 0;
        }
    }







}
