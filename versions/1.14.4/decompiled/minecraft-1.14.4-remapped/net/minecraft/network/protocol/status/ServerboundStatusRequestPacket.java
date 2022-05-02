package net.minecraft.network.protocol.status;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;

public class ServerboundStatusRequestPacket implements Packet {
   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
   }

   public void handle(ServerStatusPacketListener serverStatusPacketListener) {
      serverStatusPacketListener.handleStatusRequest(this);
   }
}
