package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClientboundBlockUpdatePacket implements Packet {
   private BlockPos pos;
   private BlockState blockState;

   public ClientboundBlockUpdatePacket() {
   }

   public ClientboundBlockUpdatePacket(BlockGetter blockGetter, BlockPos pos) {
      this.pos = pos;
      this.blockState = blockGetter.getBlockState(pos);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.pos = friendlyByteBuf.readBlockPos();
      this.blockState = (BlockState)Block.BLOCK_STATE_REGISTRY.byId(friendlyByteBuf.readVarInt());
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBlockPos(this.pos);
      friendlyByteBuf.writeVarInt(Block.getId(this.blockState));
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleBlockUpdate(this);
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
