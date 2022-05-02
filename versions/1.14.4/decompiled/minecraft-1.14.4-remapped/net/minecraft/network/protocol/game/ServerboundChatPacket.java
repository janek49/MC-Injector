package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundChatPacket implements Packet {
   private String message;

   public ServerboundChatPacket() {
   }

   public ServerboundChatPacket(String message) {
      if(message.length() > 256) {
         message = message.substring(0, 256);
      }

      this.message = message;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.message = friendlyByteBuf.readUtf(256);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeUtf(this.message);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleChat(this);
   }

   public String getMessage() {
      return this.message;
   }
}
