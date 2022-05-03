package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.AsmUtil;
import pl.janek49.iniektor.api.IniektorHooks;

public class Patch_LegacySendChatMessageHook extends IPatch {

    public Patch_LegacySendChatMessageHook() {
        patchTargets.add(new PatchTarget(Version.MC1_7_10, Version.Compare.EQUAL,
                "net/minecraft/client/gui/GuiChat", "func_146403_a", "(Ljava/lang/String;)V"));

        patchTargets.add(new PatchTarget(Version.MC1_6_4, Version.Compare.EQUAL,
                "net/minecraft/client/src/EntityClientPlayerMP", "sendChatMessage", "(Ljava/lang/String;)V"));
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {
        PatchTarget pt = getFirstPatchTarget();
        CtMethod ctMethod = pt.findMethodInClass(ctClass);

        pool.importPackage(AsmUtil.getPackage(IniektorHooks.class));

        Logger.log("Patching method body:", pt);

        ctMethod.insertBefore("{ if (IniektorHooks.GuiChatHook($1,true)) return; }");
        return ctClass.toBytecode();
    }
}
