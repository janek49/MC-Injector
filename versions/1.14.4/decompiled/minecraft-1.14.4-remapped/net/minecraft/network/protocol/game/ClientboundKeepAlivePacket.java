package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundKeepAlivePacket implements Packet {
   private long id;

   public ClientboundKeepAlivePacket() {
   }

   public ClientboundKeepAlivePacket(long id) {
      this.id = id;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleKeepAlive(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readLong();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeLong(this.id);
   }

   public long getId() {
      return this.id;
   }
}
