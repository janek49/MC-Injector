package pl.janek49.iniektor.api;

import pl.janek49.iniektor.agent.Version;

@ClassImitator.ResolveClass(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/network/play/server/SPacketEntityVelocity")
@ClassImitator.ResolveClass(version = Version.MC1_8_8, name = "net/minecraft/network/play/server/S12PacketEntityVelocity")
@ClassImitator.ResolveClass(version = Version.MC1_6_4, andAbove = true, name = "net/minecraft/src/Packet28EntityVelocity")
public class WrapperSPacketVelocity extends ClassImitator {

    public static ClassInformation target;

    @ResolveConstructor(params = {"I", "D", "D", "D"})
    public static ConstructorDefinition defaultConstructor;

    @ResolveField(version = Version.MC1_6_4, name = "entityId")
    @ResolveField(name = "entityID")
    public static FieldDefinition entityID;

    @ResolveField(name = "motionX")
    public static FieldDefinition motionX;

    @ResolveField(name = "motionY")
    public static FieldDefinition motionY;

    @ResolveField(name = "motionZ")
    public static FieldDefinition motionZ;

}
