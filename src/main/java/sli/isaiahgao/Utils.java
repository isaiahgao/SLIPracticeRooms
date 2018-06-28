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
    
    public static String format(String msg) {
        return format(msg, 16, "verdana");
    }
    //<span style='font-size:20px'
    public static String format(String msg, int fontSize, String fontStyle) {
        return "<html><font face=\"" + fontStyle + "\"><span style='font-size:" + fontSize + "px'>" + msg.replace("\\n", "<br>") + "</span></font></html>";
    }

}
