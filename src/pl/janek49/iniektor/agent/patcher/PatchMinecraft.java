package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.client.hook.IniektorHooks;

public class PatchMinecraft extends IPatch {

    public PatchMinecraft() {
        super("net/minecraft/client/Minecraft");
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {
        String[] rgoMethodObf = AgentMain.MAPPER.getObfMethodNameWithoutClass(deobfNameToPatch + "/runGameLoop", "()V");
        CtMethod runGameLoop = ctClass.getMethod(rgoMethodObf[0], rgoMethodObf[1]);

        pool.importPackage(IniektorHooks.class.getPackage().getName());
        Logger.log("Patching method body:", runGameLoop.getLongName());
        runGameLoop.insertBefore("{ IniektorHooks.HookGameLoop(); }");
        return ctClass.toBytecode();
    }
}
