package pl.janek49.iniektor.agent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static void log(Object... args) {
        print(false, args);
    }

    public static void err(Object... args) {
        print(true, args);
    }

    public static void print(boolean err, Object... args) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = "[Iniektor/" + formatter.format(date) + "]:";
        String text = "";
        if (args == null)
            text = " null";
        else
            for (Object o : args)
                text += " " + (o == null ? "null" : o.toString());
        if (err)
            System.err.println("!!!!! " + time + text);
        else
            System.out.println(time + text);
    }
}
