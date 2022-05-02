package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.block.Block;

public class ClientboundBlockEventPacket implements Packet {
   private BlockPos pos;
   private int b0;
   private int b1;
   private Block block;

   public ClientboundBlockEventPacket() {
   }

   public ClientboundBlockEventPacket(BlockPos pos, Block block, int b0, int b1) {
      this.pos = pos;
      this.block = block;
      this.b0 = b0;
      this.b1 = b1;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.pos = friendlyByteBuf.readBlockPos();
      this.b0 = friendlyByteBuf.readUnsignedByte();
      this.b1 = friendlyByteBuf.readUnsignedByte();
      this.block = (Block)Registry.BLOCK.byId(friendlyByteBuf.readVarInt());
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBlockPos(this.pos);
      friendlyByteBuf.writeByte(this.b0);
      friendlyByteBuf.writeByte(this.b1);
      friendlyByteBuf.writeVarInt(Registry.BLOCK.getId(this.block));
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleBlockEvent(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getB0() {
      return this.b0;
   }

   public int getB1() {
      return this.b1;
   }

   public Block getBlock() {
      return this.block;
   }
}
