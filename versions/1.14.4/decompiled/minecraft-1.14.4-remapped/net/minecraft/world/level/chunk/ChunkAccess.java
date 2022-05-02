package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.FeatureAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.apache.logging.log4j.LogManager;

public interface ChunkAccess extends FeatureAccess {
   @Nullable
   BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3);

   void setBlockEntity(BlockPos var1, BlockEntity var2);

   void addEntity(Entity var1);

   @Nullable
   default LevelChunkSection getHighestSection() {
      LevelChunkSection[] vars1 = this.getSections();

      for(int var2 = vars1.length - 1; var2 >= 0; --var2) {
         LevelChunkSection var3 = vars1[var2];
         if(!LevelChunkSection.isEmpty(var3)) {
            return var3;
         }
      }

      return null;
   }

   default int getHighestSectionPosition() {
      LevelChunkSection var1 = this.getHighestSection();
      return var1 == null?0:var1.bottomBlockY();
   }

   Set getBlockEntitiesPos();

   LevelChunkSection[] getSections();

   @Nullable
   LevelLightEngine getLightEngine();

   default int getRawBrightness(BlockPos blockPos, int var2, boolean var3) {
      LevelLightEngine var4 = this.getLightEngine();
      if(var4 != null && this.getStatus().isOrAfter(ChunkStatus.LIGHT)) {
         int var5 = var3?var4.getLayerListener(LightLayer.SKY).getLightValue(blockPos) - var2:0;
         int var6 = var4.getLayerListener(LightLayer.BLOCK).getLightValue(blockPos);
         return Math.max(var6, var5);
      } else {
         return 0;
      }
   }

   Collection getHeightmaps();

   void setHeightmap(Heightmap.Types var1, long[] var2);

   Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types var1);

   int getHeight(Heightmap.Types var1, int var2, int var3);

   ChunkPos getPos();

   void setLastSaveTime(long var1);

   Map getAllStarts();

   void setAllStarts(Map var1);

   default Biome getBiome(BlockPos blockPos) {
      int var2 = blockPos.getX() & 15;
      int var3 = blockPos.getZ() & 15;
      return this.getBiomes()[var3 << 4 | var2];
   }

   default boolean isYSpaceEmpty(int var1, int var2) {
      if(var1 < 0) {
         var1 = 0;
      }

      if(var2 >= 256) {
         var2 = 255;
      }

      for(int var3 = var1; var3 <= var2; var3 += 16) {
         if(!LevelChunkSection.isEmpty(this.getSections()[var3 >> 4])) {
            return false;
         }
      }

      return true;
   }

   Biome[] getBiomes();

   void setUnsaved(boolean var1);

   boolean isUnsaved();

   ChunkStatus getStatus();

   void removeBlockEntity(BlockPos var1);

   void setLightEngine(LevelLightEngine var1);

   default void markPosForPostprocessing(BlockPos blockPos) {
      LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", blockPos);
   }

   ShortList[] getPostProcessing();

   default void addPackedPostProcess(short var1, int var2) {
      getOrCreateOffsetList(this.getPostProcessing(), var2).add(var1);
   }

   default void setBlockEntityNbt(CompoundTag blockEntityNbt) {
      LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
   }

   @Nullable
   CompoundTag getBlockEntityNbt(BlockPos var1);

   @Nullable
   CompoundTag getBlockEntityNbtForSaving(BlockPos var1);

   default void setBiomes(Biome[] biomes) {
      throw new UnsupportedOperationException();
   }

   Stream getLights();

   TickList getBlockTicks();

   TickList getLiquidTicks();

   default BitSet getCarvingMask(GenerationStep.Carving generationStep$Carving) {
      throw new RuntimeException("Meaningless in this context");
   }

   UpgradeData getUpgradeData();

   void setInhabitedTime(long var1);

   long getInhabitedTime();

   static default ShortList getOrCreateOffsetList(ShortList[] shortLists, int var1) {
      if(shortLists[var1] == null) {
         shortLists[var1] = new ShortArrayList();
      }

      return shortLists[var1];
   }

   boolean isLightCorrect();

   void setLightCorrect(boolean var1);
}
