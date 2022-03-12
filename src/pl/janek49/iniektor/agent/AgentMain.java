package pl.janek49.iniektor.agent;

import javassist.ClassPool;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import pl.janek49.iniektor.mapper.Mapper;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.List;

public class AgentMain {
    private static String TIME = System.currentTimeMillis() + "";

    public static Mapper MAPPER;
    public static boolean WasInjected = false;

    public static void agentmain(String agentArgs, Instrumentation inst) {
        try {
            Logger.log(inst);
            Logger.log("Agent main executed");

            if (WasInjected) {
                Logger.log("Agent was already injected into this JVM.");
                return;
            }

            WasInjected = true;

            MAPPER = new Mapper();
            MAPPER.init();

            inst.addTransformer(new IniektorTransformer(), true);


            try {
                Logger.log("Preparing to patch OptiFine");
                //jeśli nie ma błędu, to jest klasa Optifine
                Class.forName("optifine.OptiFineClassTransformer");
                OptiFineTransformer.ApplyPatchOptifine(inst);
                LaunchWrapperPatcher.ApplyPatchLaunchClassLoader(inst);
                LaunchWrapperPatcher.ClearCacheInLaunchClassLoader();
            } catch (ClassNotFoundException t) {
                Logger.log("OptiFine not found");
            }

            MinecraftPatcher.init(inst);

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }


}
