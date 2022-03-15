package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.McClassPatcher;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.client.hook.Hooks;

import java.lang.instrument.Instrumentation;

public class PatchGuiIngame {
    public static void ApplyPatch(Instrumentation inst) {
        new McClassPatcher() {
            @Override
            public byte[] patchClass(ClassPool pool, CtClass ctClass, String deobfName, String obfName) throws Exception {

                String rgoSignature = AgentMain.MCP_VERSION == Version.MC1_7_10 ? "(FZII)V" : "(F)V" ;

                String[] rgoMethodObf = AgentMain.MAPPER.getObfMethodNameWithoutClass(deobfName + "/renderGameOverlay", rgoSignature);

                CtMethod renderGameOverlay = ctClass.getMethod(rgoMethodObf[0], rgoMethodObf[1]);

                pool.importPackage(Hooks.class.getPackage().getName());
                Logger.log("Patching method body:", renderGameOverlay.getLongName());
                renderGameOverlay.insertAfter("{ Hooks.HookRenderInGameOverlay(this); }");
                return ctClass.toBytecode();
            }
        }.applyPatches(inst, "net/minecraft/client/gui/GuiIngame");
    }

}
