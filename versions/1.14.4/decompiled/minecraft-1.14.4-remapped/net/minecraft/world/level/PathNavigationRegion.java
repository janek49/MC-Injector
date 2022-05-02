package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PathNavigationRegion implements LevelReader {
   protected final int centerX;
   protected final int centerZ;
   protected final ChunkAccess[][] chunks;
   protected boolean allEmpty;
   protected final Level level;

   public PathNavigationRegion(Level level, BlockPos var2, BlockPos var3) {
      this.level = level;
      this.centerX = var2.getX() >> 4;
      this.centerZ = var2.getZ() >> 4;
      int var4 = var3.getX() >> 4;
      int var5 = var3.getZ() >> 4;
      this.chunks = new ChunkAccess[var4 - this.centerX + 1][var5 - this.centerZ + 1];
      this.allEmpty = true;

      for(int var6 = this.centerX; var6 <= var4; ++var6) {
         for(int var7 = this.centerZ; var7 <= var5; ++var7) {
            this.chunks[var6 - this.centerX][var7 - this.centerZ] = level.getChunk(var6, var7, ChunkStatus.FULL, false);
         }
      }

      for(int var6 = var2.getX() >> 4; var6 <= var3.getX() >> 4; ++var6) {
         for(int var7 = var2.getZ() >> 4; var7 <= var3.getZ() >> 4; ++var7) {
            ChunkAccess var8 = this.chunks[var6 - this.centerX][var7 - this.centerZ];
            if(var8 != null && !var8.isYSpaceEmpty(var2.getY(), var3.getY())) {
               this.allEmpty = false;
               return;
            }
         }
      }

   }

   public int getRawBrightness(BlockPos blockPos, int var2) {
      return this.level.getRawBrightness(blockPos, var2);
   }

   @Nullable
   public ChunkAccess getChunk(int var1, int var2, ChunkStatus chunkStatus, boolean var4) {
      int var5 = var1 - this.centerX;
      int var6 = var2 - this.centerZ;
      if(var5 >= 0 && var5 < this.chunks.length && var6 >= 0 && var6 < this.chunks[var5].length) {
         ChunkAccess var7 = this.chunks[var5][var6];
         return (ChunkAccess)(var7 != null?var7:new EmptyLevelChunk(this.level, new ChunkPos(var1, var2)));
      } else {
         return new EmptyLevelChunk(this.level, new ChunkPos(var1, var2));
      }
   }

   public boolean hasChunk(int var1, int var2) {
      int var3 = var1 - this.centerX;
      int var4 = var2 - this.centerZ;
      return var3 >= 0 && var3 < this.chunks.length && var4 >= 0 && var4 < this.chunks[var3].length;
   }

   public BlockPos getHeightmapPos(Heightmap.Types heightmap$Types, BlockPos var2) {
      return this.level.getHeightmapPos(heightmap$Types, var2);
   }

   public int getHeight(Heightmap.Types heightmap$Types, int var2, int var3) {
      return this.level.getHeight(heightmap$Types, var2, var3);
   }

   public int getSkyDarken() {
      return this.level.getSkyDarken();
   }

   public WorldBorder getWorldBorder() {
      return this.level.getWorldBorder();
   }

   public boolean isUnobstructed(@Nullable Entity entity, VoxelShape voxelShape) {
      return true;
   }

   public boolean isClientSide() {
      return false;
   }

   public int getSeaLevel() {
      return this.level.getSeaLevel();
   }

   public Dimension getDimension() {
      return this.level.getDimension();
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos) {
      ChunkAccess var2 = this.getChunk(blockPos);
      return var2.getBlockEntity(blockPos);
   }

   public BlockState getBlockState(BlockPos blockPos) {
      if(Level.isOutsideBuildHeight(blockPos)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         ChunkAccess var2 = this.getChunk(blockPos);
         return var2.getBlockState(blockPos);
      }
   }

   public FluidState getFluidState(BlockPos blockPos) {
      if(Level.isOutsideBuildHeight(blockPos)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         ChunkAccess var2 = this.getChunk(blockPos);
         return var2.getFluidState(blockPos);
      }
   }

   public Biome getBiome(BlockPos blockPos) {
      ChunkAccess var2 = this.getChunk(blockPos);
      return var2.getBiome(blockPos);
   }

   public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
      return this.level.getBrightness(lightLayer, blockPos);
   }
}
