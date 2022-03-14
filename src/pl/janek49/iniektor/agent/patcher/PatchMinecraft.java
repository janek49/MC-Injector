package pl.janek49.iniektor.agent.patcher;

import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.McClassPatcher;
import pl.janek49.iniektor.client.hook.Hooks;

import java.lang.instrument.Instrumentation;

public class PatchMinecraft {
    public static void ApplyPatch(Instrumentation inst) {
        new McClassPatcher() {
            @Override
            public byte[] patchClass(CtClass ctClass, String deobfName, String obfName) throws Exception {
                String[] rgoMethodObf = AgentMain.MAPPER.getObfMethodNameWithoutClass(deobfName + "/runGameLoop", "()V");
                CtMethod runGameLoop = ctClass.getMethod(rgoMethodObf[0], rgoMethodObf[1]);

                Logger.log("Patching method body:", runGameLoop.getLongName());
                runGameLoop.insertBefore("{" + Hooks.class.getName() + ".HookGameLoop();}");
                return ctClass.toBytecode();
            }
        }.applyPatches(inst, "net/minecraft/client/Minecraft");
    }
}
