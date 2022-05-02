package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundSetCarriedItemPacket implements Packet {
   private int slot;

   public ServerboundSetCarriedItemPacket() {
   }

   public ServerboundSetCarriedItemPacket(int slot) {
      this.slot = slot;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.slot = friendlyByteBuf.readShort();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeShort(this.slot);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleSetCarriedItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
