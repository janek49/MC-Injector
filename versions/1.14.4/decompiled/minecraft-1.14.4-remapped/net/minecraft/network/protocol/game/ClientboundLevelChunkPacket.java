package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

public class ClientboundLevelChunkPacket implements Packet {
   private int x;
   private int z;
   private int availableSections;
   private CompoundTag heightmaps;
   private byte[] buffer;
   private List blockEntitiesTags;
   private boolean fullChunk;

   public ClientboundLevelChunkPacket() {
   }

   public ClientboundLevelChunkPacket(LevelChunk levelChunk, int var2) {
      ChunkPos var3 = levelChunk.getPos();
      this.x = var3.x;
      this.z = var3.z;
      this.fullChunk = var2 == '\uffff';
      this.heightmaps = new CompoundTag();

      for(Entry<Heightmap.Types, Heightmap> var5 : levelChunk.getHeightmaps()) {
         if(((Heightmap.Types)var5.getKey()).sendToClient()) {
            this.heightmaps.put(((Heightmap.Types)var5.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)var5.getValue()).getRawData()));
         }
      }

      this.buffer = new byte[this.calculateChunkSize(levelChunk, var2)];
      this.availableSections = this.extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), levelChunk, var2);
      this.blockEntitiesTags = Lists.newArrayList();

      for(Entry<BlockPos, BlockEntity> var5 : levelChunk.getBlockEntities().entrySet()) {
         BlockPos var6 = (BlockPos)var5.getKey();
         BlockEntity var7 = (BlockEntity)var5.getValue();
         int var8 = var6.getY() >> 4;
         if(this.isFullChunk() || (var2 & 1 << var8) != 0) {
            CompoundTag var9 = var7.getUpdateTag();
            this.blockEntitiesTags.add(var9);
         }
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.x = friendlyByteBuf.readInt();
      this.z = friendlyByteBuf.readInt();
      this.fullChunk = friendlyByteBuf.readBoolean();
      this.availableSections = friendlyByteBuf.readVarInt();
      this.heightmaps = friendlyByteBuf.readNbt();
      int var2 = friendlyByteBuf.readVarInt();
      if(var2 > 2097152) {
         throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
      } else {
         this.buffer = new byte[var2];
         friendlyByteBuf.readBytes(this.buffer);
         int var3 = friendlyByteBuf.readVarInt();
         this.blockEntitiesTags = Lists.newArrayList();

         for(int var4 = 0; var4 < var3; ++var4) {
            this.blockEntitiesTags.add(friendlyByteBuf.readNbt());
         }

      }
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeInt(this.x);
      friendlyByteBuf.writeInt(this.z);
      friendlyByteBuf.writeBoolean(this.fullChunk);
      friendlyByteBuf.writeVarInt(this.availableSections);
      friendlyByteBuf.writeNbt(this.heightmaps);
      friendlyByteBuf.writeVarInt(this.buffer.length);
      friendlyByteBuf.writeBytes(this.buffer);
      friendlyByteBuf.writeVarInt(this.blockEntitiesTags.size());

      for(CompoundTag var3 : this.blockEntitiesTags) {
         friendlyByteBuf.writeNbt(var3);
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleLevelChunk(this);
   }

   public FriendlyByteBuf getReadBuffer() {
      return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
   }

   private ByteBuf getWriteBuffer() {
      ByteBuf byteBuf = Unpooled.wrappedBuffer(this.buffer);
      byteBuf.writerIndex(0);
      return byteBuf;
   }

   public int extractChunkData(FriendlyByteBuf friendlyByteBuf, LevelChunk levelChunk, int var3) {
      int var4 = 0;
      LevelChunkSection[] vars5 = levelChunk.getSections();
      int var6 = 0;

      for(int var7 = vars5.length; var6 < var7; ++var6) {
         LevelChunkSection var8 = vars5[var6];
         if(var8 != LevelChunk.EMPTY_SECTION && (!this.isFullChunk() || !var8.isEmpty()) && (var3 & 1 << var6) != 0) {
            var4 |= 1 << var6;
            var8.write(friendlyByteBuf);
         }
      }

      if(this.isFullChunk()) {
         Biome[] vars6 = levelChunk.getBiomes();

         for(int var7 = 0; var7 < vars6.length; ++var7) {
            friendlyByteBuf.writeInt(Registry.BIOME.getId(vars6[var7]));
         }
      }

      return var4;
   }

   protected int calculateChunkSize(LevelChunk levelChunk, int var2) {
      int var3 = 0;
      LevelChunkSection[] vars4 = levelChunk.getSections();
      int var5 = 0;

      for(int var6 = vars4.length; var5 < var6; ++var5) {
         LevelChunkSection var7 = vars4[var5];
         if(var7 != LevelChunk.EMPTY_SECTION && (!this.isFullChunk() || !var7.isEmpty()) && (var2 & 1 << var5) != 0) {
            var3 += var7.getSerializedSize();
         }
      }

      if(this.isFullChunk()) {
         var3 += levelChunk.getBiomes().length * 4;
      }

      return var3;
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public int getAvailableSections() {
      return this.availableSections;
   }

   public boolean isFullChunk() {
      return this.fullChunk;
   }

   public CompoundTag getHeightmaps() {
      return this.heightmaps;
   }

   public List getBlockEntitiesTags() {
      return this.blockEntitiesTags;
   }
}
