package pl.janek49.iniektor.api;

import pl.janek49.iniektor.agent.Version;

public class WrapperPacket implements IWrapper {

    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/client/network/NetHandlerPlayClient/sendPacket", descriptor = "(Lnet/minecraft/network/Packet;)V")
    @ResolveMethod(version = Version.MC1_7_10, andAbove = true, name = "net/minecraft/client/network/NetHandlerPlayClient/addToSendQueue", descriptor = "(Lnet/minecraft/network/Packet;)V")
    @ResolveMethod(version = Version.MC1_6_4, name = "net/minecraft/src/NetClientHandler/addToSendQueue", descriptor = "(Lnet/minecraft/src/Packet;)V")
    public static MethodDefinition _nethandler_addToSendQueue;

    @ResolveField(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/client/entity/EntityPlayerSP/connection")
    @ResolveField(version = Version.MC1_8_8, name = "net/minecraft/client/entity/EntityPlayerSP/sendQueue")
    public static FieldDefinition _sendQueue;

    @ResolveMethod(version = Version.MC1_7_10, name = "net/minecraft/client/Minecraft/getNetHandler", descriptor = "()Lnet/minecraft/client/network/NetHandlerPlayClient;")
    @ResolveMethod(version = Version.MC1_6_4, name = "net/minecraft/src/Minecraft/getNetHandler", descriptor = "()Lnet/minecraft/src/NetClientHandler;")
    public static MethodDefinition _mc_getNetHandler;

    @ResolveConstructor(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/network/play/client/CPacketPlayer", params = "Z")
    @ResolveConstructor(version = Version.MC1_8_8, name = "net/minecraft/network/play/client/C03PacketPlayer", params = "Z")
    @ResolveConstructor(version = Version.MC1_6_4, andAbove = true, name = "net/minecraft/src/Packet10Flying", params = "Z")
    public static ConstructorDefinition CPacketPlayer;



    public static void sendPacket(Object packet) {
        if (Reflector.isOnOrBlwVersion(Version.MC1_7_10)) {
            Invoker.fromObj(Reflector.MINECRAFT.getInstance()).method(_mc_getNetHandler).exec().method(_nethandler_addToSendQueue).exec(packet);
        } else {
            Invoker.fromObj(Reflector.PLAYER.getInstance()).field(_sendQueue).get().method(_nethandler_addToSendQueue).exec(packet);
        }
    }

    @Override
    public void initWrapper() {

    }

    @Override
    public Object getInstance() {
        return null;
    }
}
