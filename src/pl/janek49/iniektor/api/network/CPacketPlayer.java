package pl.janek49.iniektor.api.network;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.reflection.ClassImitator;
import pl.janek49.iniektor.api.reflection.ConstructorDefinition;
import pl.janek49.iniektor.api.reflection.ResolveConstructor;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/network/protocol/game/ServerboundMovePlayerPacket")
@ClassImitator.ResolveClass(version = Version.MC1_9_4, andAbove = true, value = "net/minecraft/network/play/client/CPacketPlayer")
@ClassImitator.ResolveClass(version = Version.MC1_7_10, andAbove = true, value = "net/minecraft/network/play/client/C03PacketPlayer")
@ClassImitator.ResolveClass(version = Version.MC1_6_4, andAbove = true, value = "net/minecraft/src/Packet10Flying")
public class CPacketPlayer extends Packet {

    public static ClassInformation target;

    @ResolveConstructor(params = "Z")
    public static ConstructorDefinition constructor;

    public CPacketPlayer(Object instance) {
        super(instance);
    }

    public CPacketPlayer(boolean onGround){
        this(constructor.newInstance(onGround));
    }

    private CPacketPlayer(){super(null);}
}
