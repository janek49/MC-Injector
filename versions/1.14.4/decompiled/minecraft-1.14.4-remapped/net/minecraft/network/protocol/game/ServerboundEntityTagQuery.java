package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundEntityTagQuery implements Packet {
   private int transactionId;
   private int entityId;

   public ServerboundEntityTagQuery() {
   }

   public ServerboundEntityTagQuery(int transactionId, int entityId) {
      this.transactionId = transactionId;
      this.entityId = entityId;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.transactionId = friendlyByteBuf.readVarInt();
      this.entityId = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.transactionId);
      friendlyByteBuf.writeVarInt(this.entityId);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleEntityTagQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public int getEntityId() {
      return this.entityId;
   }
}
