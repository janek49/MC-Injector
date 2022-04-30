package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.IniektorHooks;

public class PatchGuiScreen extends IPatch {

    public PatchGuiScreen() {
        patchTargets.add(new PatchTarget(Version.MC1_14_4, Version.Compare.OR_HIGHER,
                "net/minecraft/client/gui/screens/Screen", "sendMessage", "(Ljava/lang/String;Z)V"));

        patchTargets.add(new PatchTarget.VersionRanged(Version.MC1_12_2, Version.MC1_8_8,
                "net/minecraft/client/gui/GuiScreen", "sendChatMessage", "(Ljava/lang/String;Z)V"));
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {
        pool.importPackage(IniektorHooks.class.getPackage().getName());

        for (PatchTarget pt : getApplicableTargets()) {
            CtMethod ctMethod = pt.findMethodInClass(ctClass);
            Logger.log("Patching method body:", pt);
            ctMethod.insertBefore("{ if (IniektorHooks.GuiChatHook($1,$2)) return; }");
        }

        return ctClass.toBytecode();
    }

}
