package pl.janek49.injector.agent;

import javax.swing.*;
import java.lang.instrument.Instrumentation;

public class AgentMain {
    private static String TIME = System.currentTimeMillis()+"";

    public static void agentmain(String agentArgs, Instrumentation inst) {
        JOptionPane.showMessageDialog(null, "Java Injector by janek49\n" + TIME);
    }
}
