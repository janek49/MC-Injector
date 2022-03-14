package pl.janek49.iniektor.agent.patcher;

import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.McClassPatcher;
import pl.janek49.iniektor.client.hook.Hooks;

import java.lang.instrument.Instrumentation;

public class PatchGuiIngame {
    public static void ApplyPatch(Instrumentation inst) {
        new McClassPatcher() {
            @Override
            public byte[] patchClass(CtClass ctClass, String deobfName, String obfName) throws Exception {
                String[] rgoMethodObf = AgentMain.MAPPER.getObfMethodNameWithoutClass("net/minecraft/client/gui/GuiIngame/renderGameOverlay", "(F)V");
                CtMethod renderGameOverlay = ctClass.getMethod(rgoMethodObf[0], rgoMethodObf[1]);
                renderGameOverlay.insertAfter("{ " + Hooks.class.getName() + ".HookRenderInGameOverlay(this); }");
                return ctClass.toBytecode();
            }
        }.applyPatches(inst, "net/minecraft/client/gui/GuiIngame");
    }

}
