package pl.janek49.iniektor.agent;

import pl.janek49.iniektor.mapper.Mapper;

import java.lang.instrument.Instrumentation;

public class AgentMain {
    private static String TIME = System.currentTimeMillis() + "";

    public static Mapper MAPPER;
    public static boolean WasInjected = false;

    public static void agentmain(String agentArgs, Instrumentation inst) {
        try {
            Logger.log("Agent main executed");

            if(WasInjected){
                Logger.log("Agent was already injected into this JVM.");
                return;
            }

            WasInjected = true;

            MAPPER = new Mapper();
            MAPPER.init();

            inst.addTransformer(new IniektorTransformer());
            MinecraftPatcher.init(inst);

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }


}
