package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;

public interface ClientStatusPacketListener extends PacketListener {
   void handleStatusResponse(ClientboundStatusResponsePacket var1);

   void handlePongResponse(ClientboundPongResponsePacket var1);
}
