package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class ClientboundChunkBlocksUpdatePacket implements Packet {
   private ChunkPos chunkPos;
   private ClientboundChunkBlocksUpdatePacket.BlockUpdate[] updates;

   public ClientboundChunkBlocksUpdatePacket() {
   }

   public ClientboundChunkBlocksUpdatePacket(int updates, short[] shorts, LevelChunk levelChunk) {
      this.chunkPos = levelChunk.getPos();
      this.updates = new ClientboundChunkBlocksUpdatePacket.BlockUpdate[updates];

      for(int var4 = 0; var4 < this.updates.length; ++var4) {
         this.updates[var4] = new ClientboundChunkBlocksUpdatePacket.BlockUpdate(shorts[var4], levelChunk);
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.chunkPos = new ChunkPos(friendlyByteBuf.readInt(), friendlyByteBuf.readInt());
      this.updates = new ClientboundChunkBlocksUpdatePacket.BlockUpdate[friendlyByteBuf.readVarInt()];

      for(int var2 = 0; var2 < this.updates.length; ++var2) {
         this.updates[var2] = new ClientboundChunkBlocksUpdatePacket.BlockUpdate(friendlyByteBuf.readShort(), (BlockState)Block.BLOCK_STATE_REGISTRY.byId(friendlyByteBuf.readVarInt()));
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeInt(this.chunkPos.x);
      friendlyByteBuf.writeInt(this.chunkPos.z);
      friendlyByteBuf.writeVarInt(this.updates.length);

      for(ClientboundChunkBlocksUpdatePacket.BlockUpdate var5 : this.updates) {
         friendlyByteBuf.writeShort(var5.getOffset());
         friendlyByteBuf.writeVarInt(Block.getId(var5.getBlock()));
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleChunkBlocksUpdate(this);
   }

   public ClientboundChunkBlocksUpdatePacket.BlockUpdate[] getUpdates() {
      return this.updates;
   }

   public class BlockUpdate {
      private final short offset;
      private final BlockState block;

      public BlockUpdate(short offset, BlockState block) {
         this.offset = offset;
         this.block = block;
      }

      public BlockUpdate(short offset, LevelChunk levelChunk) {
         this.offset = offset;
         this.block = levelChunk.getBlockState(this.getPos());
      }

      public BlockPos getPos() {
         return new BlockPos(ClientboundChunkBlocksUpdatePacket.this.chunkPos.getBlockAt(this.offset >> 12 & 15, this.offset & 255, this.offset >> 8 & 15));
      }

      public short getOffset() {
         return this.offset;
      }

      public BlockState getBlock() {
         return this.block;
      }
   }
}
