package net.minecraft.network.protocol.login;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;

public class ServerboundCustomQueryPacket implements Packet {
   private int transactionId;
   private FriendlyByteBuf data;

   public ServerboundCustomQueryPacket() {
   }

   public ServerboundCustomQueryPacket(int transactionId, @Nullable FriendlyByteBuf data) {
      this.transactionId = transactionId;
      this.data = data;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.transactionId = friendlyByteBuf.readVarInt();
      if(friendlyByteBuf.readBoolean()) {
         int var2 = friendlyByteBuf.readableBytes();
         if(var2 < 0 || var2 > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
         }

         this.data = new FriendlyByteBuf(friendlyByteBuf.readBytes(var2));
      } else {
         this.data = null;
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.transactionId);
      if(this.data != null) {
         friendlyByteBuf.writeBoolean(true);
         friendlyByteBuf.writeBytes(this.data.copy());
      } else {
         friendlyByteBuf.writeBoolean(false);
      }

   }

   public void handle(ServerLoginPacketListener serverLoginPacketListener) {
      serverLoginPacketListener.handleCustomQueryPacket(this);
   }
}
