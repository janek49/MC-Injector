package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.AsmUtil;
import pl.janek49.iniektor.api.IniektorHooks;


public class PatchGuiIngameForge extends IPatch {

    public PatchGuiIngameForge() {
        super("net/minecraftforge/client/GuiIngameForge");
    }

    @Override
    public byte[] PatchClassImpl(String className, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {
        String rgoSignature = AgentMain.MCP_VERSION == Version.MC1_7_10 ? "(FZII)V" : "(F)V";

        String[] rgoMethodObf = AgentMain.MAPPER.getObfMethodNameWithoutClass("net/minecraft/client/gui/GuiIngame/renderGameOverlay", rgoSignature);

        CtMethod renderGameOverlay = ctClass.getMethod(rgoMethodObf[0], rgoMethodObf[1]);

        pool.importPackage(AsmUtil.getPackage(IniektorHooks.class));
        Logger.log("Patching method body:", renderGameOverlay.getLongName());
        renderGameOverlay.insertAfter("{ IniektorHooks.HookRenderInGameOverlay(this); }");
        return ctClass.toBytecode();
    }


}
