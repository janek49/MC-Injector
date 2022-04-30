package pl.janek49.iniektor.api.network;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.ClassImitator;
import pl.janek49.iniektor.api.MethodDefinition;
import pl.janek49.iniektor.api.ResolveMethod;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/network/protocol/Packet")
@ClassImitator.ResolveClass(version = Version.MC1_6_4, value = "net/minecraft/src/Packet/processPacket")
@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/network/Packet/processPacket")
public class Packet extends ClassImitator {

    public Packet(Object instance){
        super(instance);
    }

    private Packet(){}

    public static ClassInformation target;

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "handle", descriptor = "(Lnet/minecraft/network/PacketListener;)V")
    @ResolveMethod(version = Version.MC1_6_4, name = "processPacket", descriptor = "(Lnet/minecraft/src/NetHandler;)V")
    @ResolveMethod(version = Version.DEFAULT, name = "processPacket", descriptor = "(Lnet/minecraft/network/INetHandler;)V")
    public static MethodDefinition processPacket;

}
