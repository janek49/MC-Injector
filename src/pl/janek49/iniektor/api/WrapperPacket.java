package pl.janek49.iniektor.api;

import pl.janek49.iniektor.agent.Version;

public class WrapperPacket implements IWrapper {

    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/client/network/NetHandlerPlayClient/sendPacket", descriptor = "(Lnet/minecraft/network/Packet;)V")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/client/network/NetHandlerPlayClient/addToSendQueue", descriptor = "(Lnet/minecraft/network/Packet;)V")
    public static MethodDefinition _addToSendQueue;

    @ResolveField(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/client/entity/EntityPlayerSP/connection")
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/entity/EntityPlayerSP/sendQueue")
    public static FieldDefinition _sendQueue;

    @ResolveConstructor(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/network/play/client/CPacketPlayer", params = "Z")
    @ResolveConstructor(version = Version.DEFAULT, name = "net/minecraft/network/play/client/C03PacketPlayer", params = "Z")
    public static ConstructorDefinition CPacketPlayer;

    public static void sendPacket(Object packet) {
        _addToSendQueue.invoke(_sendQueue.get(Reflector.PLAYER.getDefaultInstance()), packet);
    }

    @Override
    public void initWrapper() {

    }

    @Override
    public Object getDefaultInstance() {
        return null;
    }
}
