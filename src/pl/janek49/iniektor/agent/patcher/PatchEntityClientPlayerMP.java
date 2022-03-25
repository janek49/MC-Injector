package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.client.hook.IniektorHooks;

public class PatchEntityClientPlayerMP extends IPatch{
    public PatchEntityClientPlayerMP() {
        super("net/minecraft/client/src/EntityClientPlayerMP");
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {
        String[] sig = AgentMain.MAPPER.getObfMethodNameWithoutClass(deobfNameToPatch + "/sendChatMessage", "(Ljava/lang/String;)V");

        CtMethod ctMethod = ctClass.getMethod(sig[0], sig[1]);

        pool.importPackage(IniektorHooks.class.getPackage().getName());

        Logger.log("Patching method body:", ctMethod.getLongName());

        ctMethod.insertBefore("{ if (IniektorHooks.GuiChatHook($1,true)) return; }");

        return ctClass.toBytecode();
    }
}
