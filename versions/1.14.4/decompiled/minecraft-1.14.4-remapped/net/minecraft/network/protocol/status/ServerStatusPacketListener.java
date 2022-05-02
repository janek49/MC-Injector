package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;

public interface ServerStatusPacketListener extends PacketListener {
   void handlePingRequest(ServerboundPingRequestPacket var1);

   void handleStatusRequest(ServerboundStatusRequestPacket var1);
}
