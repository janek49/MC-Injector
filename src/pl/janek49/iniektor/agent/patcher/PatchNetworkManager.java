package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.IniektorHooks;

public class PatchNetworkManager extends IPatch {
    public PatchNetworkManager() {
        addFirst(new PatchTarget(Version.MC1_14_4, Version.Compare.OR_HIGHER,
                "net/minecraft/network/Connection", "channelRead0", "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V"));

        addFirst(new PatchTarget(Version.DEFAULT, Version.Compare.EQUAL,
                "net/minecraft/network/NetworkManager", "channelRead0", "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V"));
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {
        pool.importPackage(IniektorHooks.class.getPackage().getName());

        PatchTarget pt = getFirstPatchTarget();

        Logger.log("Patching method body:", pt);
        pt.findMethodInClass(ctClass).insertBefore("{ if (IniektorHooks.HookCancelReceivedPacket($2)) return; }");

        return ctClass.toBytecode();
    }
}
