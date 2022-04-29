package pl.janek49.iniektor.api.network;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;

@ClassImitator.ResolveClass(version = Version.MC1_9_4, andAbove = true, value = "net/minecraft/network/play/server/SPacketEntityVelocity")
@ClassImitator.ResolveClass(version = Version.MC1_8_8, value = "net/minecraft/network/play/server/S12PacketEntityVelocity")
@ClassImitator.ResolveClass(version = Version.MC1_6_4, andAbove = true, value = "net/minecraft/src/Packet28EntityVelocity")
public class WrapperSPacketVelocity extends ClassImitator {

    public static ClassInformation target;

    @ResolveConstructor(params = {"I", "D", "D", "D"})
    public static ConstructorDefinition defaultConstructor;

    @ResolveField(version = Version.MC1_6_4, value = "entityId")
    @ResolveField(value = "entityID")
    public static FieldDefinition entityID;

    @ResolveField(value = "motionX")
    public static FieldDefinition motionX;

    @ResolveField(value = "motionY")
    public static FieldDefinition motionY;

    @ResolveField(value = "motionZ")
    public static FieldDefinition motionZ;

}
