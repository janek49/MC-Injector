package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundBlockDestructionPacket implements Packet {
   private int id;
   private BlockPos pos;
   private int progress;

   public ClientboundBlockDestructionPacket() {
   }

   public ClientboundBlockDestructionPacket(int id, BlockPos pos, int progress) {
      this.id = id;
      this.pos = pos;
      this.progress = progress;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
      this.pos = friendlyByteBuf.readBlockPos();
      this.progress = friendlyByteBuf.readUnsignedByte();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
      friendlyByteBuf.writeBlockPos(this.pos);
      friendlyByteBuf.writeByte(this.progress);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleBlockDestruction(this);
   }

   public int getId() {
      return this.id;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getProgress() {
      return this.progress;
   }
}
