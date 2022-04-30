package pl.janek49.iniektor.api.network;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.client.IniektorClient;

public class PacketHelper implements IWrapper {

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/client/multiplayer/ClientPacketListener/send", descriptor = "(Lnet/minecraft/network/protocol/Packet;)V")
    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/client/network/NetHandlerPlayClient/sendPacket", descriptor = "(Lnet/minecraft/network/Packet;)V")
    @ResolveMethod(version = Version.MC1_7_10, andAbove = true, name = "net/minecraft/client/network/NetHandlerPlayClient/addToSendQueue", descriptor = "(Lnet/minecraft/network/Packet;)V")
    @ResolveMethod(version = Version.MC1_6_4, name = "net/minecraft/src/NetClientHandler/addToSendQueue", descriptor = "(Lnet/minecraft/src/Packet;)V")
    public static MethodDefinition _nethandler_addToSendQueue;

    @ResolveMethod(version = Version.MC1_7_10, name = "net/minecraft/client/Minecraft/getNetHandler", descriptor = "()Lnet/minecraft/client/network/NetHandlerPlayClient;")
    @ResolveMethod(version = Version.MC1_6_4, name = "net/minecraft/src/Minecraft/getNetHandler", descriptor = "()Lnet/minecraft/src/NetClientHandler;")
    public static MethodDefinition _mc_getNetHandler;


    public static void sendPacket(Object packet) {
        if (Reflector.isOnOrBlwVersion(Version.MC1_7_10)) {
            Invoker.fromObj(Reflector.MINECRAFT.getInstanceBehind()).method(_mc_getNetHandler).exec().method(_nethandler_addToSendQueue).exec(packet);
        } else {
            Invoker.fromObj(Minecraft.getPlayer().getConnection()).method(_nethandler_addToSendQueue).exec(packet);
        }
    }

    public static void sendPacket(Packet packet){
        sendPacket(packet.getInstanceBehind());
    }

    public static Object getNetHandler() {
        if (Reflector.isOnOrBlwVersion(Version.MC1_7_10)) {
            return Invoker.fromObj(Reflector.MINECRAFT.getInstanceBehind()).method(_mc_getNetHandler).exec().getValue();
        } else {
            return Invoker.fromObj(Minecraft.getPlayer().getConnection());
        }
    }

    public static void fakeReceivePacket(Object packet) {
        IniektorClient.INSTANCE.eventManager.skipPackets.add(packet);
        Packet.processPacket.invokeSilent(packet, getNetHandler());
    }

    public static void fakeReceivePacket(Packet packet) {
        IniektorClient.INSTANCE.eventManager.skipPackets.add(packet.getInstanceBehind());
        Packet.processPacket.invokeSilent(packet.getInstanceBehind(), getNetHandler());
    }

    @Override
    public void initWrapper() {

    }

    @Override
    public Object getInstanceBehind() {
        return null;
    }
}
