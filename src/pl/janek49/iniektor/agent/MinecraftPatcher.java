package pl.janek49.iniektor.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.scopedpool.ScopedClassPoolFactory;
import net.minecraft.client.gui.GuiIngame;
import pl.janek49.iniektor.client.Hooks;

import java.io.File;
import java.io.FileWriter;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;

public class MinecraftPatcher {


    public static void init(Instrumentation inst) {
        // ApplyPatchGuiIngame(inst);
       // ApplyPatchEntityRenderer(inst);
        ApplyPatchMinecraft(inst);
    }

    private static void ApplyPatchGuiIngame(Instrumentation inst) {
        try {
            Logger.log("Patching GuiIngame.class");

            ClassPool classPool = ClassPool.getDefault();
            CtClass guiIngame = classPool.get(AgentMain.MAPPER.getObfClassName("net/minecraft/client/gui/GuiIngame"));
            //guiIngame.stopPruning(true);

            if (guiIngame.isFrozen())
                guiIngame.defrost();

            String[] rgoMethodObf = AgentMain.MAPPER.getObfMethodNameWithoutClass("net/minecraft/client/gui/GuiIngame/renderGameOverlay", "(F)V");
            CtMethod renderGameOverlay = guiIngame.getMethod(rgoMethodObf[0], rgoMethodObf[1]);
            renderGameOverlay.insertAfter("{ " + Hooks.GetFullClassNameForJA() + ".HookRenderInGameOverlay(); }");

            byte[] bytecode = guiIngame.toBytecode();

            ClassDefinition cd = new ClassDefinition(Class.forName(AgentMain.MAPPER.getObfClassName("net/minecraft/client/gui/GuiIngame")), bytecode);

            inst.redefineClasses(cd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void ApplyPatchEntityRenderer(Instrumentation inst) {
        try {
            Logger.log("Patching EntityRenderer.class");
            String classname = "net/minecraft/client/renderer/EntityRenderer";
            String obfName =AgentMain.MAPPER.getObfClassName(classname);

            ClassLoader.getSystemClassLoader().loadClass(obfName);
            ClassLoader.getSystemClassLoader().loadClass(obfName);

            ClassPool classPool = ClassPool.getDefault();
            CtClass guiIngame = classPool.get(obfName);
            //guiIngame.stopPruning(true);

            if (guiIngame.isFrozen())
                guiIngame.defrost();

            String[] rgoMethodObf = AgentMain.MAPPER.getObfMethodNameWithoutClass(classname + "/func_181560_a", "(FJ)V");
            CtMethod renderGameOverlay = guiIngame.getMethod(rgoMethodObf[0], rgoMethodObf[1]);
            renderGameOverlay.insertAfter("{ System.exit(0); }");

            byte[] bytecode = guiIngame.toBytecode();

            Class clz = Class.forName(AgentMain.MAPPER.getObfClassName(classname));
            ClassDefinition cd = new ClassDefinition(clz, bytecode);

            inst.redefineClasses(cd);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void ApplyPatchMinecraft(Instrumentation inst) {
        try {
            Logger.log("Patching Minecraft.class");
            String classname = "net/minecraft/client/Minecraft";
            String obfName = AgentMain.MAPPER.getObfClassName(classname);

            ClassPool classPool = ClassPool.getDefault();
            CtClass guiIngame = classPool.get(obfName);
            //guiIngame.stopPruning(true);

            if (guiIngame.isFrozen())
                guiIngame.defrost();

            String[] rgoMethodObf = AgentMain.MAPPER.getObfMethodNameWithoutClass(classname + "/runGameLoop", "()V");
            CtMethod renderGameOverlay = guiIngame.getMethod(rgoMethodObf[0], rgoMethodObf[1]);
            renderGameOverlay.insertAfter("{ System.exit(0); }");

            byte[] bytecode = guiIngame.toBytecode();

            Class clz = Class.forName(AgentMain.MAPPER.getObfClassName(classname));
            ClassDefinition cd = new ClassDefinition(clz, bytecode);

            inst.redefineClasses(cd);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
