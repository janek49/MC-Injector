package pl.janek49.iniektor.agent;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.patcher.LaunchWrapperPatcher;
import pl.janek49.iniektor.agent.patcher.PatchGuiIngame;
import pl.janek49.iniektor.agent.patcher.PatchMinecraft;
import pl.janek49.iniektor.mapper.Mapper;

import java.lang.instrument.Instrumentation;
import java.util.regex.Pattern;

public class AgentMain {
    private static String TIME = System.currentTimeMillis() + "";

    public static Mapper MAPPER;
    public static boolean WasInjected = false;
    public static boolean IS_LAUNCHWRAPPER = false;
    public static Version MCP_VERSION;

    public static void agentmain(String agentArgs, Instrumentation inst) {
        try {
            Logger.log("Agent main executed");
            Logger.log("Arguments:", agentArgs);
            Logger.log("Classloader:", AgentMain.class.getClassLoader());
            if (WasInjected) {
                Logger.log("Agent was already injected into this JVM.");
                return;
            }

            WasInjected = true;

            String versionString = Util.getLastPartOfArray(agentArgs.contains("/") ? agentArgs.split("/") : agentArgs.split(Pattern.quote("\\")));
            switch (versionString){
                case "1.7.10":
                    MCP_VERSION = Version.MC1_7_10;
                    break;
                case "1.8.8":
                    MCP_VERSION = Version.MC1_8_8;
                    break;
                case "1.9.4":
                    MCP_VERSION = Version.MC1_9_4;
                    break;
            }


            MAPPER = new Mapper(agentArgs);
            MAPPER.init();

            Logger.log("Registering transformers");
            inst.addTransformer(new IniektorTransformer(), true);


            try {
                Logger.log("Checking for LaunchWrapper");
                //jeśli nie ma błędu, to znaczy że jest klasa LaunchWrapper
                Class.forName("net.minecraft.launchwrapper.LaunchClassLoader");
                IS_LAUNCHWRAPPER = true;

                Logger.log("Patching LaunchWrapper");
                LaunchWrapperPatcher.ApplyPatch(inst);
            } catch (ClassNotFoundException e) {
                Logger.log("LaunchWrapper not found");
            }



            Logger.log("Setting up Reflector");
            ReflectorHelper.TransformNames();

            Logger.log("Applying Patches");
            PatchMinecraft.ApplyPatch(inst);
            PatchGuiIngame.ApplyPatch(inst);


        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }


}
