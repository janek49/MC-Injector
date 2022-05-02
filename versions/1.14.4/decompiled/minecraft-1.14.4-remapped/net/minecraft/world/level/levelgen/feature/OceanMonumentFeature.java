package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.OceanMonumentPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class OceanMonumentFeature extends StructureFeature {
   private static final List MONUMENT_ENEMIES = Lists.newArrayList(new Biome.SpawnerData[]{new Biome.SpawnerData(EntityType.GUARDIAN, 1, 2, 4)});

   public OceanMonumentFeature(Function function) {
      super(function);
   }

   protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(ChunkGenerator chunkGenerator, Random random, int var3, int var4, int var5, int var6) {
      int var7 = chunkGenerator.getSettings().getMonumentsSpacing();
      int var8 = chunkGenerator.getSettings().getMonumentsSeparation();
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0?var9 - var7 + 1:var9;
      int var12 = var10 < 0?var10 - var7 + 1:var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((WorldgenRandom)random).setLargeFeatureWithSalt(chunkGenerator.getSeed(), var13, var14, 10387313);
      var13 = var13 * var7;
      var14 = var14 * var7;
      var13 = var13 + (random.nextInt(var7 - var8) + random.nextInt(var7 - var8)) / 2;
      var14 = var14 + (random.nextInt(var7 - var8) + random.nextInt(var7 - var8)) / 2;
      return new ChunkPos(var13, var14);
   }

   public boolean isFeatureChunk(ChunkGenerator chunkGenerator, Random random, int var3, int var4) {
      ChunkPos var5 = this.getPotentialFeatureChunkFromLocationWithOffset(chunkGenerator, random, var3, var4, 0, 0);
      if(var3 == var5.x && var4 == var5.z) {
         for(Biome var8 : chunkGenerator.getBiomeSource().getBiomesWithin(var3 * 16 + 9, var4 * 16 + 9, 16)) {
            if(!chunkGenerator.isBiomeValidStartForStructure(var8, Feature.OCEAN_MONUMENT)) {
               return false;
            }
         }

         for(Biome var9 : chunkGenerator.getBiomeSource().getBiomesWithin(var3 * 16 + 9, var4 * 16 + 9, 29)) {
            if(var9.getBiomeCategory() != Biome.BiomeCategory.OCEAN && var9.getBiomeCategory() != Biome.BiomeCategory.RIVER) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return OceanMonumentFeature.OceanMonumentStart::<init>;
   }

   public String getFeatureName() {
      return "Monument";
   }

   public int getLookupRange() {
      return 8;
   }

   public List getSpecialEnemies() {
      return MONUMENT_ENEMIES;
   }

   public static class OceanMonumentStart extends StructureStart {
      private boolean isCreated;

      public OceanMonumentStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         this.generatePieces(var3, var4);
      }

      private void generatePieces(int var1, int var2) {
         int var3 = var1 * 16 - 29;
         int var4 = var2 * 16 - 29;
         Direction var5 = Direction.Plane.HORIZONTAL.getRandomDirection(this.random);
         this.pieces.add(new OceanMonumentPieces.MonumentBuilding(this.random, var3, var4, var5));
         this.calculateBoundingBox();
         this.isCreated = true;
      }

      public void postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         if(!this.isCreated) {
            this.pieces.clear();
            this.generatePieces(this.getChunkX(), this.getChunkZ());
         }

         super.postProcess(levelAccessor, random, boundingBox, chunkPos);
      }
   }
}
