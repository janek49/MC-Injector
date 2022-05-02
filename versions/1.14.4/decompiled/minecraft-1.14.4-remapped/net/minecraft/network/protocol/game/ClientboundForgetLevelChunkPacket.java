package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundForgetLevelChunkPacket implements Packet {
   private int x;
   private int z;

   public ClientboundForgetLevelChunkPacket() {
   }

   public ClientboundForgetLevelChunkPacket(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.x = friendlyByteBuf.readInt();
      this.z = friendlyByteBuf.readInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeInt(this.x);
      friendlyByteBuf.writeInt(this.z);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleForgetLevelChunk(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }
}
