package net.minecraft.world.level.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

public class EmptyLevelChunk extends LevelChunk {
   private static final Biome[] BIOMES = (Biome[])Util.make(new Biome[256], (biomes) -> {
      Arrays.fill(biomes, Biomes.PLAINS);
   });

   public EmptyLevelChunk(Level level, ChunkPos chunkPos) {
      super(level, chunkPos, BIOMES);
   }

   public BlockState getBlockState(BlockPos blockPos) {
      return Blocks.VOID_AIR.defaultBlockState();
   }

   @Nullable
   public BlockState setBlockState(BlockPos blockPos, BlockState var2, boolean var3) {
      return null;
   }

   public FluidState getFluidState(BlockPos blockPos) {
      return Fluids.EMPTY.defaultFluidState();
   }

   @Nullable
   public LevelLightEngine getLightEngine() {
      return null;
   }

   public int getLightEmission(BlockPos blockPos) {
      return 0;
   }

   public void addEntity(Entity entity) {
   }

   public void removeEntity(Entity entity) {
   }

   public void removeEntity(Entity entity, int var2) {
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos, LevelChunk.EntityCreationType levelChunk$EntityCreationType) {
      return null;
   }

   public void addBlockEntity(BlockEntity blockEntity) {
   }

   public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
   }

   public void removeBlockEntity(BlockPos blockPos) {
   }

   public void markUnsaved() {
   }

   public void getEntities(@Nullable Entity entity, AABB aABB, List list, Predicate predicate) {
   }

   public void getEntitiesOfClass(Class class, AABB aABB, List list, Predicate predicate) {
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean isYSpaceEmpty(int var1, int var2) {
      return true;
   }

   public ChunkHolder.FullChunkStatus getFullStatus() {
      return ChunkHolder.FullChunkStatus.BORDER;
   }
}
