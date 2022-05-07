package pl.janek49.iniektor.agent;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Logger {
    public static void log(Object... args) {
        print(AgentGui.Level.DEFAULT, args);
    }

    public static void err(Object... args) {
        print(AgentGui.Level.ERROR, args);
    }

    public static void warn(Object... args) {
        print(AgentGui.Level.WARNING, args);
    }

    public static void print(AgentGui.Level level, Object... args) {
        if (level != AgentGui.Level.ERROR && showOnlyErrors)
            return;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = "[" + formatter.format(date) + "] [Iniektor]" + (level == AgentGui.Level.ERROR ? " [SEVERE]" : level == AgentGui.Level.WARNING ? " [WARNING]" : "");
        String text = "";
        if (args == null)
            text = " null";
        else
            for (Object o : args)
                text += " " + (o == null ? "null" : o.getClass().isArray() ? Arrays.toString(getArray(o)) : o.toString());

        if (AgentMain.guiWindow != null) {
            AgentGui.AppendText(AgentMain.guiWindow,time + text, level.toString());
        } else {
            if (level == AgentGui.Level.ERROR)
                System.err.println(time + text);
            else
                System.out.println(time + text);
        }


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
