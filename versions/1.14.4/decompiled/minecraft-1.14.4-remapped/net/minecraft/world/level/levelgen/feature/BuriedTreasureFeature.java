package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.BuriedTreasureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class BuriedTreasureFeature extends StructureFeature {
   public BuriedTreasureFeature(Function function) {
      super(function);
   }

   public boolean isFeatureChunk(ChunkGenerator chunkGenerator, Random random, int var3, int var4) {
      Biome var5 = chunkGenerator.getBiomeSource().getBiome(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9));
      if(chunkGenerator.isBiomeValidStartForStructure(var5, Feature.BURIED_TREASURE)) {
         ((WorldgenRandom)random).setLargeFeatureWithSalt(chunkGenerator.getSeed(), var3, var4, 10387320);
         BuriedTreasureConfiguration var6 = (BuriedTreasureConfiguration)chunkGenerator.getStructureConfiguration(var5, Feature.BURIED_TREASURE);
         return random.nextFloat() < var6.probability;
      } else {
         return false;
      }
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return BuriedTreasureFeature.BuriedTreasureStart::<init>;
   }

   public String getFeatureName() {
      return "Buried_Treasure";
   }

   public int getLookupRange() {
      return 1;
   }

   public static class BuriedTreasureStart extends StructureStart {
      public BuriedTreasureStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         int var6 = var3 * 16;
         int var7 = var4 * 16;
         BlockPos var8 = new BlockPos(var6 + 9, 90, var7 + 9);
         this.pieces.add(new BuriedTreasurePieces.BuriedTreasurePiece(var8));
         this.calculateBoundingBox();
      }

      public BlockPos getLocatePos() {
         return new BlockPos((this.getChunkX() << 4) + 9, 0, (this.getChunkZ() << 4) + 9);
      }
   }
}
