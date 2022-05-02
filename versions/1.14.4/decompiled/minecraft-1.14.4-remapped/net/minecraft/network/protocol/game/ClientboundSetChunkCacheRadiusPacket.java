package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetChunkCacheRadiusPacket implements Packet {
   private int radius;

   public ClientboundSetChunkCacheRadiusPacket() {
   }

   public ClientboundSetChunkCacheRadiusPacket(int radius) {
      this.radius = radius;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.radius = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.radius);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetChunkCacheRadius(this);
   }

   public int getRadius() {
      return this.radius;
   }
}
