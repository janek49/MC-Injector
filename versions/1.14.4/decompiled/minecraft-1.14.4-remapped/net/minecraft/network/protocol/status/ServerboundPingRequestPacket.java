package net.minecraft.network.protocol.status;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;

public class ServerboundPingRequestPacket implements Packet {
   private long time;

   public ServerboundPingRequestPacket() {
   }

   public ServerboundPingRequestPacket(long time) {
      this.time = time;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.time = friendlyByteBuf.readLong();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeLong(this.time);
   }

   public void handle(ServerStatusPacketListener serverStatusPacketListener) {
      serverStatusPacketListener.handlePingRequest(this);
   }

   public long getTime() {
      return this.time;
   }
}
