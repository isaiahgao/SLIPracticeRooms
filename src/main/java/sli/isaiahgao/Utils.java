package sli.isaiahgao;

import java.lang.reflect.Field;

public class Utils {

    public static Object getField(Class<?> clazz, Object o, String field) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(o);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
