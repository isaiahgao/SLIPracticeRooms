package sli.isaiahgao;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Date;

public class Utils {
    
    private static final DecimalFormat DF = new DecimalFormat("00");

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
    
    public static String capitalizeFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    public static String getTime(Date d) {
        String suffix = " AM";
        int hours = d.getHours();
        int mins = d.getMinutes();
        int secs = d.getSeconds();
        if (hours > 12) {
            suffix = " PM";
            hours -= 12;
        } else if (hours == 0) {
            hours = 12;
        }
        
        return hours + ":" + DF.format(mins) + ":" + DF.format(secs) + suffix;
    }

}
