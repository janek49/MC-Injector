package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundSelectTradePacket implements Packet {
   private int item;

   public ServerboundSelectTradePacket() {
   }

   public ServerboundSelectTradePacket(int item) {
      this.item = item;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.item = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.item);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleSelectTrade(this);
   }

   public int getItem() {
      return this.item;
   }
}
