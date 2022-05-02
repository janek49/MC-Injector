package net.minecraft.network.protocol.login;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;

public class ClientboundLoginCompressionPacket implements Packet {
   private int compressionThreshold;

   public ClientboundLoginCompressionPacket() {
   }

   public ClientboundLoginCompressionPacket(int compressionThreshold) {
      this.compressionThreshold = compressionThreshold;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.compressionThreshold = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.compressionThreshold);
   }

   public void handle(ClientLoginPacketListener clientLoginPacketListener) {
      clientLoginPacketListener.handleCompression(this);
   }

   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }
}
