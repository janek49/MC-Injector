package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.AsmUtil;
import pl.janek49.iniektor.api.IniektorHooks;

public class PatchMinecraft extends IPatch {

    public PatchMinecraft() {
        patchTargets.add(new PatchTarget(Version.MC1_14_4, Version.Compare.OR_HIGHER, "net/minecraft/client/Minecraft", "runTick", "(Z)V"));
        patchTargets.add(new PatchTarget(Version.MC1_12_2, Version.Compare.OR_LOWER, "net/minecraft/client/Minecraft", "runGameLoop", "()V"));
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {
        pool.importPackage(AsmUtil.getPackage(IniektorHooks.class));

        for(PatchTarget pt : getApplicableTargets()){
            CtMethod runGameLoop = getApplicableTargets().get(0).findMethodInClass(ctClass);
            Logger.log("Patching method body:", pt);

            runGameLoop.insertBefore("{ IniektorHooks.HookGameLoop(); }");
        }

        return ctClass.toBytecode();
    }
}
