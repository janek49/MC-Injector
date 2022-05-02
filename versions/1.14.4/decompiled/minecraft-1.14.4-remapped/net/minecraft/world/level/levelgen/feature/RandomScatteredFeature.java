package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public abstract class RandomScatteredFeature extends StructureFeature {
   public RandomScatteredFeature(Function function) {
      super(function);
   }

   protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(ChunkGenerator chunkGenerator, Random random, int var3, int var4, int var5, int var6) {
      int var7 = this.getSpacing(chunkGenerator);
      int var8 = this.getSeparation(chunkGenerator);
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0?var9 - var7 + 1:var9;
      int var12 = var10 < 0?var10 - var7 + 1:var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((WorldgenRandom)random).setLargeFeatureWithSalt(chunkGenerator.getSeed(), var13, var14, this.getRandomSalt());
      var13 = var13 * var7;
      var14 = var14 * var7;
      var13 = var13 + random.nextInt(var7 - var8);
      var14 = var14 + random.nextInt(var7 - var8);
      return new ChunkPos(var13, var14);
   }

   public boolean isFeatureChunk(ChunkGenerator chunkGenerator, Random random, int var3, int var4) {
      ChunkPos var5 = this.getPotentialFeatureChunkFromLocationWithOffset(chunkGenerator, random, var3, var4, 0, 0);
      if(var3 == var5.x && var4 == var5.z) {
         Biome var6 = chunkGenerator.getBiomeSource().getBiome(new BlockPos(var3 * 16 + 9, 0, var4 * 16 + 9));
         if(chunkGenerator.isBiomeValidStartForStructure(var6, this)) {
            return true;
         }
      }

      return false;
   }

   protected int getSpacing(ChunkGenerator chunkGenerator) {
      return chunkGenerator.getSettings().getTemplesSpacing();
   }

   protected int getSeparation(ChunkGenerator chunkGenerator) {
      return chunkGenerator.getSettings().getTemplesSeparation();
   }

   protected abstract int getRandomSalt();
}
