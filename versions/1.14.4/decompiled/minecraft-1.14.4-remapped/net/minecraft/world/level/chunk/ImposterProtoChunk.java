package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ProtoTickList;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class ImposterProtoChunk extends ProtoChunk {
   private final LevelChunk wrapped;

   public ImposterProtoChunk(LevelChunk wrapped) {
      super(wrapped.getPos(), UpgradeData.EMPTY);
      this.wrapped = wrapped;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos) {
      return this.wrapped.getBlockEntity(blockPos);
   }

   @Nullable
   public BlockState getBlockState(BlockPos blockPos) {
      return this.wrapped.getBlockState(blockPos);
   }

   public FluidState getFluidState(BlockPos blockPos) {
      return this.wrapped.getFluidState(blockPos);
   }

   public int getMaxLightLevel() {
      return this.wrapped.getMaxLightLevel();
   }

   @Nullable
   public BlockState setBlockState(BlockPos blockPos, BlockState var2, boolean var3) {
      return null;
   }

   public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
   }

   public void addEntity(Entity entity) {
   }

   public void setStatus(ChunkStatus status) {
   }

   public LevelChunkSection[] getSections() {
      return this.wrapped.getSections();
   }

   @Nullable
   public LevelLightEngine getLightEngine() {
      return this.wrapped.getLightEngine();
   }

   public void setHeightmap(Heightmap.Types heightmap$Types, long[] longs) {
   }

   private Heightmap.Types fixType(Heightmap.Types heightmap$Types) {
      return heightmap$Types == Heightmap.Types.WORLD_SURFACE_WG?Heightmap.Types.WORLD_SURFACE:(heightmap$Types == Heightmap.Types.OCEAN_FLOOR_WG?Heightmap.Types.OCEAN_FLOOR:heightmap$Types);
   }

   public int getHeight(Heightmap.Types heightmap$Types, int var2, int var3) {
      return this.wrapped.getHeight(this.fixType(heightmap$Types), var2, var3);
   }

   public ChunkPos getPos() {
      return this.wrapped.getPos();
   }

   public void setLastSaveTime(long lastSaveTime) {
   }

   @Nullable
   public StructureStart getStartForFeature(String string) {
      return this.wrapped.getStartForFeature(string);
   }

   public void setStartForFeature(String string, StructureStart structureStart) {
   }

   public Map getAllStarts() {
      return this.wrapped.getAllStarts();
   }

   public void setAllStarts(Map allStarts) {
   }

   public LongSet getReferencesForFeature(String string) {
      return this.wrapped.getReferencesForFeature(string);
   }

   public void addReferenceForFeature(String string, long var2) {
   }

   public Map getAllReferences() {
      return this.wrapped.getAllReferences();
   }

   public void setAllReferences(Map allReferences) {
   }

   public Biome[] getBiomes() {
      return this.wrapped.getBiomes();
   }

   public void setUnsaved(boolean unsaved) {
   }

   public boolean isUnsaved() {
      return false;
   }

   public ChunkStatus getStatus() {
      return this.wrapped.getStatus();
   }

   public void removeBlockEntity(BlockPos blockPos) {
   }

   public void markPosForPostprocessing(BlockPos blockPos) {
   }

   public void setBlockEntityNbt(CompoundTag blockEntityNbt) {
   }

   @Nullable
   public CompoundTag getBlockEntityNbt(BlockPos blockPos) {
      return this.wrapped.getBlockEntityNbt(blockPos);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos blockPos) {
      return this.wrapped.getBlockEntityNbtForSaving(blockPos);
   }

   public void setBiomes(Biome[] biomes) {
   }

   public Stream getLights() {
      return this.wrapped.getLights();
   }

   public ProtoTickList getBlockTicks() {
      return new ProtoTickList((block) -> {
         return block.defaultBlockState().isAir();
      }, this.getPos());
   }

   public ProtoTickList getLiquidTicks() {
      return new ProtoTickList((fluid) -> {
         return fluid == Fluids.EMPTY;
      }, this.getPos());
   }

   public BitSet getCarvingMask(GenerationStep.Carving generationStep$Carving) {
      return this.wrapped.getCarvingMask(generationStep$Carving);
   }

   public LevelChunk getWrapped() {
      return this.wrapped;
   }

   public boolean isLightCorrect() {
      return this.wrapped.isLightCorrect();
   }

   public void setLightCorrect(boolean lightCorrect) {
      this.wrapped.setLightCorrect(lightCorrect);
   }

   // $FF: synthetic method
   public TickList getLiquidTicks() {
      return this.getLiquidTicks();
   }

   // $FF: synthetic method
   public TickList getBlockTicks() {
      return this.getBlockTicks();
   }
}
