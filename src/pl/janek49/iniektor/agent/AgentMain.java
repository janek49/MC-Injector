package pl.janek49.iniektor.agent;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.asm.AsmUtil;
import pl.janek49.iniektor.agent.patcher.ApplyPatchTransformer;
import pl.janek49.iniektor.agent.patcher.LaunchWrapperPatcher;
import pl.janek49.iniektor.api.IniektorHooks;
import pl.janek49.iniektor.client.gui.IniektorGuiScreen;
import pl.janek49.iniektor.mapper.*;

import javax.swing.*;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.regex.Pattern;

public class AgentMain {
    private static String TIME = System.currentTimeMillis() + "";
    public static URL JARFILE;

    public static SeargeMapper MAPPER;
    public static boolean WasInjected = false;
    public static boolean IS_LAUNCHWRAPPER = false;
    public static Version MCP_VERSION;

    public static boolean USE_ASM_503 = false;
    public static boolean IS_FORGE = false;

    public static Instrumentation INSTR;

    public static int REMAPPER_ERRORS = 0;
    public static String REMAPPER_CURRENT_CLASS = "";

    public static Object guiWindow;

    public static void agentmain(String agentArgs, Instrumentation inst) {
        try {
            if (WasInjected) {
                Logger.log("Agent was already injected into this JVM.");
                return;
            }

            guiWindow = new AgentGui();
            AgentGui.SetVisible(guiWindow, true);

            Logger.log("*************************");
            Logger.log("Iniektor v0.1 by janek49");
            Logger.log("*************************");
            Logger.log("Agent main executed");
            Logger.log("Arguments:", agentArgs);
            Logger.log("Classloader:", AgentMain.class.getClassLoader());

            JARFILE = AgentMain.class.getProtectionDomain().getCodeSource().getLocation();
            Logger.log("Agent JarFile:", JARFILE.getFile());

            WasInjected = true;
            INSTR = inst;

            String versionString = Util.getLastPartOfArray(agentArgs.contains("/") ? agentArgs.split("/") : agentArgs.split(Pattern.quote("\\")));
            MCP_VERSION = Version.valueOf("MC" + versionString.replace(".", "_"));

            Logger.log("Checking for Forge Modloader");

            if (AsmUtil.doesClassExist("cpw.mods.fml.common.launcher.FMLTweaker") ||
                    AsmUtil.doesClassExist("net.minecraftforge.fml.common.launcher.FMLTweaker")) {
                Logger.log("Found Forge Modloader");
                USE_ASM_503 = true;
                IS_FORGE = true;
            } else {
                Logger.log("Forge Modloader not found");
            }

            MAPPER = createMapper(MCP_VERSION, IS_FORGE, agentArgs);
            Logger.log("Obfuscation mapper:", MAPPER.getClass().getName());
            MAPPER.init();

            Logger.log("Registering transformers");
            inst.addTransformer(new IniektorTransformer(), true);
            ApplyPatchTransformer apt = new ApplyPatchTransformer();
            inst.addTransformer(apt, true);


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
            ClassLoaderBridge.SetReflectorFields();

            Logger.log("Applying Patches");
            apt.ApplyPatches(inst);

            if (!IS_LAUNCHWRAPPER) {
                inst.retransformClasses(IniektorHooks.class);
                IniektorGuiScreen.class.getName();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public static SeargeMapper createMapper(Version forVersion, boolean isForge, String path) {
        boolean pre17 = forVersion.ordinal() < Version.MC1_7_10.ordinal();
        boolean useMojangMapping = forVersion.ordinal() >= Version.MC1_14_4.ordinal();

        if (pre17) {
            if (isForge) {
                return new ForgePre17Mapper(path);
            } else {
                return new Pre17Mapper(path);
            }
        } else if (isForge) {
            return new ForgeMapper(path);
        } else if (useMojangMapping) {
            return new MojangMapper(path);
        } else {
            return new McpMapper(path);
        }
    }

}
