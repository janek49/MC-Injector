package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundBlockEntityTagQuery implements Packet {
   private int transactionId;
   private BlockPos pos;

   public ServerboundBlockEntityTagQuery() {
   }

   public ServerboundBlockEntityTagQuery(int transactionId, BlockPos pos) {
      this.transactionId = transactionId;
      this.pos = pos;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.transactionId = friendlyByteBuf.readVarInt();
      this.pos = friendlyByteBuf.readBlockPos();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.transactionId);
      friendlyByteBuf.writeBlockPos(this.pos);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleBlockEntityTagQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
