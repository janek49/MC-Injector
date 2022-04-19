package pl.janek49.iniektor.agent;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Logger {
    public static void log(Object... args) {
        print(false, args);
    }

    public static void err(Object... args) {
        print(true, args);
    }

    public static void print(boolean err, Object... args) {
        if (!err && showOnlyErrors)
            return;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = "[Iniektor/" + formatter.format(date) + "]:";
        String text = "";
        if (args == null)
            text = " null";
        else
            for (Object o : args)
                text += " " + (o == null ? "null" : o.getClass().isArray() ? Arrays.toString(getArray(o)) : o.toString());
        if (err)
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
