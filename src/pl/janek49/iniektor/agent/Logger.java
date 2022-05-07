package pl.janek49.iniektor.agent;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Logger {
    public static void log(Object... args) {
        print(0, args);
    }

    public static void err(Object... args) {
        print(2, args);
    }

    public static void warn(Object... args) {
        print(1, args);
    }

    public static void print(int lvl, Object... args) {
        if (lvl != 2 && showOnlyErrors)
            return;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = "[" + formatter.format(date) + "] [Iniektor]" + (lvl == 2 ? " [SEVERE]" : lvl == 1 ? " [WARNING]" : "");
        String text = "";
        if (args == null)
            text = " null";
        else
            for (Object o : args)
                text += " " + (o == null ? "null" : o.getClass().isArray() ? Arrays.toString(getArray(o)) : o.toString());
        if (lvl == 2)
            System.err.println(time + text);
        else
            System.out.println(time + text);
    }

    private static Object[] getArray(Object val) {
        int arrlength = Array.getLength(val);
        Object[] outputArray = new Object[arrlength];
        for (int i = 0; i < arrlength; ++i) {
            outputArray[i] = Array.get(val, i);
        }
        return outputArray;
    }

    public static boolean showOnlyErrors = false;

}
