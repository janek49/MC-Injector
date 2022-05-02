package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetChunkCacheCenterPacket implements Packet {
   private int x;
   private int z;

   public ClientboundSetChunkCacheCenterPacket() {
   }

   public ClientboundSetChunkCacheCenterPacket(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.x = friendlyByteBuf.readVarInt();
      this.z = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.x);
      friendlyByteBuf.writeVarInt(this.z);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetChunkCacheCenter(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }
}
