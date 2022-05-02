package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomScatteredFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BeardedStructureStart;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PillagerOutpostPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class PillagerOutpostFeature extends RandomScatteredFeature {
   private static final List OUTPOST_ENEMIES = Lists.newArrayList(new Biome.SpawnerData[]{new Biome.SpawnerData(EntityType.PILLAGER, 1, 1, 1)});

   public PillagerOutpostFeature(Function function) {
      super(function);
   }

   public String getFeatureName() {
      return "Pillager_Outpost";
   }

   public int getLookupRange() {
      return 3;
   }

   public List getSpecialEnemies() {
      return OUTPOST_ENEMIES;
   }

   public boolean isFeatureChunk(ChunkGenerator chunkGenerator, Random random, int var3, int var4) {
      ChunkPos var5 = this.getPotentialFeatureChunkFromLocationWithOffset(chunkGenerator, random, var3, var4, 0, 0);
      if(var3 == var5.x && var4 == var5.z) {
         int var6 = var3 >> 4;
         int var7 = var4 >> 4;
         random.setSeed((long)(var6 ^ var7 << 4) ^ chunkGenerator.getSeed());
         random.nextInt();
         if(random.nextInt(5) != 0) {
            return false;
         }

         Biome var8 = chunkGenerator.getBiomeSource().getBiome(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9));
         if(chunkGenerator.isBiomeValidStartForStructure(var8, Feature.PILLAGER_OUTPOST)) {
            for(int var9 = var3 - 10; var9 <= var3 + 10; ++var9) {
               for(int var10 = var4 - 10; var10 <= var4 + 10; ++var10) {
                  if(Feature.VILLAGE.isFeatureChunk(chunkGenerator, random, var9, var10)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      return false;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return PillagerOutpostFeature.FeatureStart::<init>;
   }

   protected int getRandomSalt() {
      return 165745296;
   }

   public static class FeatureStart extends BeardedStructureStart {
      public FeatureStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         BlockPos var6 = new BlockPos(var3 * 16, 90, var4 * 16);
         PillagerOutpostPieces.addPieces(chunkGenerator, structureManager, var6, this.pieces, this.random);
         this.calculateBoundingBox();
      }
   }
}
