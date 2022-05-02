package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundTakeItemEntityPacket implements Packet {
   private int itemId;
   private int playerId;
   private int amount;

   public ClientboundTakeItemEntityPacket() {
   }

   public ClientboundTakeItemEntityPacket(int itemId, int playerId, int amount) {
      this.itemId = itemId;
      this.playerId = playerId;
      this.amount = amount;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.itemId = friendlyByteBuf.readVarInt();
      this.playerId = friendlyByteBuf.readVarInt();
      this.amount = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.itemId);
      friendlyByteBuf.writeVarInt(this.playerId);
      friendlyByteBuf.writeVarInt(this.amount);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleTakeItemEntity(this);
   }

   public int getItemId() {
      return this.itemId;
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public int getAmount() {
      return this.amount;
   }
}
