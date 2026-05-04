package simulation.other;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Config {
    private static final Dotenv env = Dotenv.configure().ignoreIfMissing().load();
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    private Config(){
    }

    public static int getWidth(){
        try {
            return Integer.parseInt(env.get("WIDTH"));
        } catch (NumberFormatException e) {
            log.error("Не удалось найти width");
            throw new RuntimeException("WIDTH not found!");
        } // здесь ещё по хорошему надо ловить все исключения, которые могут попасться, например если не удалось найти строку WIDTH, но опустим поиск таких исключений, главное что я суть понял
        // а также вместо RunTime должно быть исключение которое больше подходит под эту ситуацию, но я его не нашёл

    }
    // представьте что дальше тоже ловятся только NumberFormatException и NotFound Exception

    public static int getHeigh(){
        try{
            return Integer.parseInt(env.get("HEIGHT"));
        }catch (RuntimeException e) {
            log.error("Не удалось найти height");
            return 0;
        }
    }

    private static int getValueFromEnv(String nameValue, Class<?> clazz){
        String nameString = clazz.getSimpleName().toUpperCase() + "_" + nameValue;
        try{
            return Integer.parseInt(env.get(nameString));
        }catch (RuntimeException e) {
            log.error("Не удалось найти {}", nameString);
            return 0;
        }
    }

    public static int getMaxElements(Class<?> clazz){
        return getValueFromEnv("MAX_COUNT", clazz);

    }
    public static int getMaxLevel(Class<?> clazz){
        return getValueFromEnv("MAX_LEVEL", clazz);

    }
    public static int getMaxLevelsOut(Class<?> clazz){
        return getValueFromEnv("MAX_LEVELS_OUT", clazz);
    }
}
