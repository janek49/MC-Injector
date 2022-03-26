package pl.janek49.iniektor.agent;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.hotswap.HotswapperThread;
import pl.janek49.iniektor.agent.patcher.ApplyPatchTransformer;
import pl.janek49.iniektor.agent.patcher.LaunchWrapperPatcher;
import pl.janek49.iniektor.client.gui.GuiScreenIniektorMain;
import pl.janek49.iniektor.client.hook.IniektorHooks;
import pl.janek49.iniektor.mapper.ForgeMapper;
import pl.janek49.iniektor.mapper.Mapper;
import pl.janek49.iniektor.mapper.Pre17Mapper;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.regex.Pattern;

public class AgentMain {
    private static String TIME = System.currentTimeMillis() + "";
    public static URL JARFILE;

    public static Mapper MAPPER;
    public static boolean WasInjected = false;
    public static boolean IS_LAUNCHWRAPPER = false;
    public static Version MCP_VERSION;

    public static boolean USE_ASM_503 = false;
    public static boolean IS_FORGE = false;

    public static void agentmain(String agentArgs, Instrumentation inst) {
        try {

            Logger.log("*************************");
            Logger.log("Iniektor v0.1 by janek49");
            Logger.log("*************************");
            Logger.log("Agent main executed");
            Logger.log("Arguments:", agentArgs);
            Logger.log("Classloader:", AgentMain.class.getClassLoader());

            JARFILE = AgentMain.class.getProtectionDomain().getCodeSource().getLocation();
            Logger.log("Agent JarFile:", JARFILE.getFile());

            if (WasInjected) {
                Logger.log("Agent was already injected into this JVM.");
                return;
            }

            WasInjected = true;

            String versionString = Util.getLastPartOfArray(agentArgs.contains("/") ? agentArgs.split("/") : agentArgs.split(Pattern.quote("\\")));
            MCP_VERSION = Version.valueOf("MC" + versionString.replace(".", "_"));

            try {
                Logger.log("Checking for Forge Modloader");
                Class.forName("net.minecraftforge.fml.common.launcher.FMLTweaker");
                USE_ASM_503 = true;
                IS_FORGE = true;
                Logger.log("Found Forge Modloader");
            } catch (ClassNotFoundException ex) {
                Logger.log("Forge Modloader not found");
            }

            if (MCP_VERSION.ordinal() < Version.MC1_7_10.ordinal()) {
                MAPPER = new Pre17Mapper(agentArgs);
            } else {
                MAPPER = IS_FORGE ? new ForgeMapper(agentArgs) : new Mapper(agentArgs);
            }

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
            ApplyPatchTransformer apt = new ApplyPatchTransformer();
            inst.addTransformer(apt, true);
            apt.ApplyPatches(inst);

            new HotswapperThread(inst).start();

            if (!IS_LAUNCHWRAPPER) {
                inst.retransformClasses(IniektorHooks.class);
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }


}
