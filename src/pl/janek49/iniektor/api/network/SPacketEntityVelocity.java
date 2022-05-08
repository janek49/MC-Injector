package pl.janek49.iniektor.api.network;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.client.Vec3;
import pl.janek49.iniektor.api.reflection.*;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/network/protocol/game/ClientboundSetEntityMotionPacket")
@ClassImitator.ResolveClass(version = Version.MC1_9_4, andAbove = true, value = "net/minecraft/network/play/server/SPacketEntityVelocity")
@ClassImitator.ResolveClass(version = Version.MC1_7_10, andAbove = true, value = "net/minecraft/network/play/server/S12PacketEntityVelocity")
@ClassImitator.ResolveClass(version = Version.MC1_6_4, andAbove = true, value = "net/minecraft/src/Packet28EntityVelocity")
public class SPacketEntityVelocity extends Packet {

    public static ClassInformation target;

    @ResolveConstructor(version = Version.MC1_14_4, andAbove = true, params = {"I", "net/minecraft/world/phys/Vec3"})
    @ResolveConstructor(params = {"I", "D", "D", "D"})
    public static ConstructorDefinition defaultConstructor;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "id")
    @ResolveField(version = Version.MC1_7_10, value = "field_149417_a")
    @ResolveField(version = Version.MC1_6_4, value = "entityId")
    @ResolveField(value = "entityID")
    public static FieldDefinition entityID;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "xa")
    @ResolveField(version = Version.MC1_7_10, value = "field_149415_b")
    @ResolveField(value = "motionX")
    public static FieldDefinition motionX;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "ya")
    @ResolveField(version = Version.MC1_7_10, value = "field_149416_c")
    @ResolveField(value = "motionY")
    public static FieldDefinition motionY;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "za")
    @ResolveField(version = Version.MC1_7_10, value = "field_149414_d")
    @ResolveField(value = "motionZ")
    public static FieldDefinition motionZ;

    public SPacketEntityVelocity(int id, double x, double y, double z) {
        super(generateObj(id, x, y, z));
    }

    public static Object generateObj(int id, double x, double y, double z) {
        if(Reflector.USE_NEW_API){
            return defaultConstructor.newInstance(id, new Vec3(x, y, z).getInstanceBehind());
        }else{
            return defaultConstructor.newInstance(id, x, y, z);
        }
    }

    public SPacketEntityVelocity(Object instance) {
        super(instance);
    }

    private SPacketEntityVelocity() {
        super(null);
    }
}
