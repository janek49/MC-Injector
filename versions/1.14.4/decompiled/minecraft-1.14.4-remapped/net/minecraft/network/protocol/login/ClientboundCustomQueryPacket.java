package net.minecraft.network.protocol.login;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCustomQueryPacket implements Packet {
   private int transactionId;
   private ResourceLocation identifier;
   private FriendlyByteBuf data;

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.transactionId = friendlyByteBuf.readVarInt();
      this.identifier = friendlyByteBuf.readResourceLocation();
      int var2 = friendlyByteBuf.readableBytes();
      if(var2 >= 0 && var2 <= 1048576) {
         this.data = new FriendlyByteBuf(friendlyByteBuf.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.transactionId);
      friendlyByteBuf.writeResourceLocation(this.identifier);
      friendlyByteBuf.writeBytes(this.data.copy());
   }

   public void handle(ClientLoginPacketListener clientLoginPacketListener) {
      clientLoginPacketListener.handleCustomQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }
}
