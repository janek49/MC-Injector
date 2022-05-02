package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetCarriedItemPacket implements Packet {
   private int slot;

   public ClientboundSetCarriedItemPacket() {
   }

   public ClientboundSetCarriedItemPacket(int slot) {
      this.slot = slot;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.slot = friendlyByteBuf.readByte();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.slot);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetCarriedItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
