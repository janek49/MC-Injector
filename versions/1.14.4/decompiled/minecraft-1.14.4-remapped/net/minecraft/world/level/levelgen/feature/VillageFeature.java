package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.VillageConfiguration;
import net.minecraft.world.level.levelgen.feature.VillagePieces;
import net.minecraft.world.level.levelgen.structure.BeardedStructureStart;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class VillageFeature extends StructureFeature {
   public VillageFeature(Function function) {
      super(function);
   }

   protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(ChunkGenerator chunkGenerator, Random random, int var3, int var4, int var5, int var6) {
      int var7 = chunkGenerator.getSettings().getVillagesSpacing();
      int var8 = chunkGenerator.getSettings().getVillagesSeparation();
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0?var9 - var7 + 1:var9;
      int var12 = var10 < 0?var10 - var7 + 1:var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((WorldgenRandom)random).setLargeFeatureWithSalt(chunkGenerator.getSeed(), var13, var14, 10387312);
      var13 = var13 * var7;
      var14 = var14 * var7;
      var13 = var13 + random.nextInt(var7 - var8);
      var14 = var14 + random.nextInt(var7 - var8);
      return new ChunkPos(var13, var14);
   }

   public boolean isFeatureChunk(ChunkGenerator chunkGenerator, Random random, int var3, int var4) {
      ChunkPos var5 = this.getPotentialFeatureChunkFromLocationWithOffset(chunkGenerator, random, var3, var4, 0, 0);
      if(var3 == var5.x && var4 == var5.z) {
         Biome var6 = chunkGenerator.getBiomeSource().getBiome(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9));
         return chunkGenerator.isBiomeValidStartForStructure(var6, Feature.VILLAGE);
      } else {
         return false;
      }
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return VillageFeature.FeatureStart::<init>;
   }

   public String getFeatureName() {
      return "Village";
   }

   public int getLookupRange() {
      return 8;
   }

   public static class FeatureStart extends BeardedStructureStart {
      public FeatureStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         VillageConfiguration var6 = (VillageConfiguration)chunkGenerator.getStructureConfiguration(biome, Feature.VILLAGE);
         BlockPos var7 = new BlockPos(var3 * 16, 0, var4 * 16);
         VillagePieces.addPieces(chunkGenerator, structureManager, var7, this.pieces, this.random, var6);
         this.calculateBoundingBox();
      }
   }
}
