package pl.janek49.iniektor.api.network;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.client.IniektorClient;

public class WrapperPacket implements IWrapper {

    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/client/network/NetHandlerPlayClient/sendPacket", descriptor = "(Lnet/minecraft/network/Packet;)V")
    @ResolveMethod(version = Version.MC1_7_10, andAbove = true, name = "net/minecraft/client/network/NetHandlerPlayClient/addToSendQueue", descriptor = "(Lnet/minecraft/network/Packet;)V")
    @ResolveMethod(version = Version.MC1_6_4, name = "net/minecraft/src/NetClientHandler/addToSendQueue", descriptor = "(Lnet/minecraft/src/Packet;)V")
    public static MethodDefinition _nethandler_addToSendQueue;

    @ResolveField(version = Version.MC1_9_4, andAbove = true, value = "net/minecraft/client/entity/EntityPlayerSP/connection")
    @ResolveField(version = Version.MC1_8_8, value = "net/minecraft/client/entity/EntityPlayerSP/sendQueue")
    public static FieldDefinition _sendQueue;

    @ResolveMethod(version = Version.MC1_7_10, name = "net/minecraft/client/Minecraft/getNetHandler", descriptor = "()Lnet/minecraft/client/network/NetHandlerPlayClient;")
    @ResolveMethod(version = Version.MC1_6_4, name = "net/minecraft/src/Minecraft/getNetHandler", descriptor = "()Lnet/minecraft/src/NetClientHandler;")
    public static MethodDefinition _mc_getNetHandler;

    @ResolveConstructor(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/network/play/client/CPacketPlayer", params = "Z")
    @ResolveConstructor(version = Version.MC1_8_8, name = "net/minecraft/network/play/client/C03PacketPlayer", params = "Z")
    @ResolveConstructor(version = Version.MC1_6_4, andAbove = true, name = "net/minecraft/src/Packet10Flying", params = "Z")
    public static ConstructorDefinition CPacketPlayer;

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/network/protocol/Packet/handle", descriptor = "(Lnet/minecraft/network/PacketListener;)V")
    @ResolveMethod(version = Version.MC1_6_4, name = "net/minecraft/src/Packet/processPacket", descriptor = "(Lnet/minecraft/src/NetHandler;)V")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/network/Packet/processPacket", descriptor = "(Lnet/minecraft/network/INetHandler;)V")
    public static MethodDefinition _Packet_processPacket;

    public static void sendPacket(Object packet) {
        if (Reflector.isOnOrBlwVersion(Version.MC1_7_10)) {
            Invoker.fromObj(Reflector.MINECRAFT.getInstanceBehind()).method(_mc_getNetHandler).exec().method(_nethandler_addToSendQueue).exec(packet);
        } else {
            Invoker.fromObj(Minecraft.thePlayer.get()).field(_sendQueue).get().method(_nethandler_addToSendQueue).exec(packet);
        }
    }

    public static Object getNetHandler() {
        if (Reflector.isOnOrBlwVersion(Version.MC1_7_10)) {
            return Invoker.fromObj(Reflector.MINECRAFT.getInstanceBehind()).method(_mc_getNetHandler).exec().getValue();
        } else {
            return Invoker.fromObj(Minecraft.thePlayer.get()).field(_sendQueue).getType();
        }
    }

    public static void fakeReceivePacket(Object packet) {
        IniektorClient.INSTANCE.eventManager.skipPackets.add(packet);
        _Packet_processPacket.invokeSilent(packet, getNetHandler());
    }

    @Override
    public void initWrapper() {

    }

    @Override
    public Object getInstanceBehind() {
        return null;
    }
}
