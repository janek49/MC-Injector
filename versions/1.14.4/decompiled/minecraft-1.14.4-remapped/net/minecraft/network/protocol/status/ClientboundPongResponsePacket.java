package net.minecraft.network.protocol.status;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;

public class ClientboundPongResponsePacket implements Packet {
   private long time;

   public ClientboundPongResponsePacket() {
   }

   public ClientboundPongResponsePacket(long time) {
      this.time = time;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.time = friendlyByteBuf.readLong();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeLong(this.time);
   }

   public void handle(ClientStatusPacketListener clientStatusPacketListener) {
      clientStatusPacketListener.handlePongResponse(this);
   }
}
