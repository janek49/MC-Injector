package pl.janek49.iniektor;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class Util {
    public static String getLastPartOfArray(String[] array) {
        return array[array.length - 1];
    }

    public static String repeatString(String repeat, int times) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < times; i++) {
            str.append(repeat);
        }
        return str.toString();
    }

    public static int countStringinString(String target, String search) {
        String temp = target.replace(search, "");
        return (target.length() - temp.length()) / search.length();
    }

    public static String printException(Throwable t){
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}
