package pl.janek49.iniektor.agent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static void log(Object... args) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = "[Iniektor/" + formatter.format(date) + "]:";
        String text = "";
        if (args == null)
            text = " null";
        else
            for (Object o : args)
                text += " " + o.toString();
        System.out.println(time + text);
    }
}
