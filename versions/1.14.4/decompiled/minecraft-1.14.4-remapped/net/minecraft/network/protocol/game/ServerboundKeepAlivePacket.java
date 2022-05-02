package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundKeepAlivePacket implements Packet {
   private long id;

   public ServerboundKeepAlivePacket() {
   }

   public ServerboundKeepAlivePacket(long id) {
      this.id = id;
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleKeepAlive(this);
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
