package pl.janek49.iniektor.agent;

import net.minecraft.launchwrapper.Launch;
import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.patcher.LaunchWrapperPatcher;
import pl.janek49.iniektor.agent.patcher.PatchGuiIngame;
import pl.janek49.iniektor.agent.patcher.PatchMinecraft;
import pl.janek49.iniektor.mapper.Mapper;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

public class AgentMain {
    private static String TIME = System.currentTimeMillis() + "";

    public static Mapper MAPPER;
    public static boolean WasInjected = false;
    public static boolean IS_LAUNCHWRAPPER = false;

    public static void agentmain(String agentArgs, Instrumentation inst) {
        try {
            Logger.log("Agent main executed");

            if (WasInjected) {
                Logger.log("Agent was already injected into this JVM.");
                return;
            }
            WasInjected = true;

            MAPPER = new Mapper();
            MAPPER.init();

            Logger.log("Registering transformers");
            inst.addTransformer(new IniektorTransformer(), true);


            try {
                Logger.log("Checking for LaunchWrapper");
                //jeśli nie ma błędu, to znaczy że jest klasa LaunchWrapper
                Class.forName("net.minecraft.launchwrapper.LaunchClassLoader");

                Logger.log("Patching LaunchWrapper");
                LaunchWrapperPatcher.ApplyPatch(inst);
                IS_LAUNCHWRAPPER = true;
            } catch (ClassNotFoundException e) {
                Logger.log("LaunchWrapper not found");
            }


            Logger.log("Setting LaunchClassLoader for current Thread");
            if (IS_LAUNCHWRAPPER)
                Thread.currentThread().setContextClassLoader(Launch.classLoader);
            //------------- wszystko odtąd odbywa się na classloaderze optifina

            Logger.log("Applying Patches");
            PatchMinecraft.ApplyPatch(inst);
            PatchGuiIngame.ApplyPatch(inst);


            Class reflector = AsmUtil.getLaunchClassLoader().findClass("pl.janek49.iniektor.client.hook.Reflector");
            Field fd = reflector.getDeclaredField("FIELD_MC_TIMER");
            String val = (String) fd.get(null);
            fd.set(null, Util.getLastPartOfArray(MAPPER.getObfFieldName(val).split("/")));

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }


}
