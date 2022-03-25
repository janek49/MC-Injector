package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.client.hook.IniektorHooks;


public class PatchGuiIngame extends IPatch {

    public PatchGuiIngame() {
        super("net/minecraft/client/gui/GuiIngame");
    }

    @Override
    public byte[] PatchClassImpl(String className, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {
        String rgoSignature = AgentMain.MCP_VERSION.ordinal() <= Version.MC1_7_10.ordinal() ? "(FZII)V" : "(F)V";

        String[] rgoMethodObf = AgentMain.MAPPER.getObfMethodNameWithoutClass(deobfNameToPatch + "/renderGameOverlay", rgoSignature);

        CtMethod renderGameOverlay = ctClass.getMethod(rgoMethodObf[0], rgoMethodObf[1]);

        pool.importPackage(IniektorHooks.class.getPackage().getName());
        Logger.log("Patching method body:", renderGameOverlay.getLongName());
        renderGameOverlay.insertAfter("{ IniektorHooks.HookRenderInGameOverlay(this); }");
        return ctClass.toBytecode();
    }


}
