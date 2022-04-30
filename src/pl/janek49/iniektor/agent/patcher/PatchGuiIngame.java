package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.IniektorHooks;


public class PatchGuiIngame extends IPatch {

    public PatchGuiIngame() {
        addFirst(new PatchTarget(Version.MC1_14_4, Version.Compare.OR_HIGHER, "net/minecraft/client/gui/Gui", "render", "(F)V"));
        addFirst(new PatchTarget(Version.MC1_8_8, Version.Compare.OR_HIGHER, "net/minecraft/client/gui/GuiIngame", "renderGameOverlay", "(F)V"));
        addFirst(new PatchTarget(Version.MC1_7_10, Version.Compare.OR_LOWER, "net/minecraft/client/gui/GuiIngame", "renderGameOverlay", "(FZII)V"));
    }


    @Override
    public byte[] PatchClassImpl(String className, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {

        PatchTarget pt = getFirstPatchTarget();

        CtMethod renderGameOverlay = pt.findMethodInClass(ctClass);

        pool.importPackage(IniektorHooks.class.getPackage().getName());
        Logger.log("Patching method body:", pt);
        renderGameOverlay.insertAfter("{ IniektorHooks.HookRenderInGameOverlay(this); }");
        return ctClass.toBytecode();
    }


}
