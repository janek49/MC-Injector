package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundPickItemPacket implements Packet {
   private int slot;

   public ServerboundPickItemPacket() {
   }

   public ServerboundPickItemPacket(int slot) {
      this.slot = slot;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.slot = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.slot);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handlePickItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
