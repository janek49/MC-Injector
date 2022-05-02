package net.minecraft.network.protocol.login;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;

public class ClientboundLoginDisconnectPacket implements Packet {
   private Component reason;

   public ClientboundLoginDisconnectPacket() {
   }

   public ClientboundLoginDisconnectPacket(Component reason) {
      this.reason = reason;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.reason = Component.Serializer.fromJsonLenient(friendlyByteBuf.readUtf(262144));
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeComponent(this.reason);
   }

   public void handle(ClientLoginPacketListener clientLoginPacketListener) {
      clientLoginPacketListener.handleDisconnect(this);
   }

   public Component getReason() {
      return this.reason;
   }
}
