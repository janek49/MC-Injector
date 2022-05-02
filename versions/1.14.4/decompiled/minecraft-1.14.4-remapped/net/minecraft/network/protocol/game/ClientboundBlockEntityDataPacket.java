package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundBlockEntityDataPacket implements Packet {
   private BlockPos pos;
   private int type;
   private CompoundTag tag;

   public ClientboundBlockEntityDataPacket() {
   }

   public ClientboundBlockEntityDataPacket(BlockPos pos, int type, CompoundTag tag) {
      this.pos = pos;
      this.type = type;
      this.tag = tag;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.pos = friendlyByteBuf.readBlockPos();
      this.type = friendlyByteBuf.readUnsignedByte();
      this.tag = friendlyByteBuf.readNbt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBlockPos(this.pos);
      friendlyByteBuf.writeByte((byte)this.type);
      friendlyByteBuf.writeNbt(this.tag);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleBlockEntityData(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getType() {
      return this.type;
   }

   public CompoundTag getTag() {
      return this.tag;
   }
}
