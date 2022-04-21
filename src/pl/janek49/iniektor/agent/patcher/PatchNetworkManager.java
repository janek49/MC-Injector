package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.api.IniektorHooks;

public class PatchNetworkManager extends IPatch{
    public PatchNetworkManager() {
        super("net/minecraft/network/NetworkManager");
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {
        String[] sig = AgentMain.MAPPER.getObfMethodNameWithoutClass(deobfNameToPatch + "/channelRead0",
                "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V");

        CtMethod ctMethod = ctClass.getMethod(sig[0], sig[1]);

        pool.importPackage(IniektorHooks.class.getPackage().getName());

        Logger.log("Patching method body:", ctMethod.getLongName());

        ctMethod.insertBefore("{ if (IniektorHooks.HookCancelReceivedPacket($2)) return; }");

        return ctClass.toBytecode();
    }
}
