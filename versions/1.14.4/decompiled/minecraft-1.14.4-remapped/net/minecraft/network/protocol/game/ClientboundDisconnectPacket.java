package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundDisconnectPacket implements Packet {
   private Component reason;

   public ClientboundDisconnectPacket() {
   }

   public ClientboundDisconnectPacket(Component reason) {
      this.reason = reason;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.reason = friendlyByteBuf.readComponent();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeComponent(this.reason);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleDisconnect(this);
   }

   public Component getReason() {
      return this.reason;
   }
}
