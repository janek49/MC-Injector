package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class WoodlandMansionFeature extends StructureFeature {
   public WoodlandMansionFeature(Function function) {
      super(function);
   }

   protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(ChunkGenerator chunkGenerator, Random random, int var3, int var4, int var5, int var6) {
      int var7 = chunkGenerator.getSettings().getWoodlandMansionSpacing();
      int var8 = chunkGenerator.getSettings().getWoodlandMangionSeparation();
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0?var9 - var7 + 1:var9;
      int var12 = var10 < 0?var10 - var7 + 1:var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((WorldgenRandom)random).setLargeFeatureWithSalt(chunkGenerator.getSeed(), var13, var14, 10387319);
      var13 = var13 * var7;
      var14 = var14 * var7;
      var13 = var13 + (random.nextInt(var7 - var8) + random.nextInt(var7 - var8)) / 2;
      var14 = var14 + (random.nextInt(var7 - var8) + random.nextInt(var7 - var8)) / 2;
      return new ChunkPos(var13, var14);
   }

   public boolean isFeatureChunk(ChunkGenerator chunkGenerator, Random random, int var3, int var4) {
      ChunkPos var5 = this.getPotentialFeatureChunkFromLocationWithOffset(chunkGenerator, random, var3, var4, 0, 0);
      if(var3 == var5.x && var4 == var5.z) {
         for(Biome var8 : chunkGenerator.getBiomeSource().getBiomesWithin(var3 * 16 + 9, var4 * 16 + 9, 32)) {
            if(!chunkGenerator.isBiomeValidStartForStructure(var8, Feature.WOODLAND_MANSION)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return WoodlandMansionFeature.WoodlandMansionStart::<init>;
   }

   public String getFeatureName() {
      return "Mansion";
   }

   public int getLookupRange() {
      return 8;
   }

   public static class WoodlandMansionStart extends StructureStart {
      public WoodlandMansionStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         Rotation var6 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         int var7 = 5;
         int var8 = 5;
         if(var6 == Rotation.CLOCKWISE_90) {
            var7 = -5;
         } else if(var6 == Rotation.CLOCKWISE_180) {
            var7 = -5;
            var8 = -5;
         } else if(var6 == Rotation.COUNTERCLOCKWISE_90) {
            var8 = -5;
         }

         int var9 = (var3 << 4) + 7;
         int var10 = (var4 << 4) + 7;
         int var11 = chunkGenerator.getFirstOccupiedHeight(var9, var10, Heightmap.Types.WORLD_SURFACE_WG);
         int var12 = chunkGenerator.getFirstOccupiedHeight(var9, var10 + var8, Heightmap.Types.WORLD_SURFACE_WG);
         int var13 = chunkGenerator.getFirstOccupiedHeight(var9 + var7, var10, Heightmap.Types.WORLD_SURFACE_WG);
         int var14 = chunkGenerator.getFirstOccupiedHeight(var9 + var7, var10 + var8, Heightmap.Types.WORLD_SURFACE_WG);
         int var15 = Math.min(Math.min(var11, var12), Math.min(var13, var14));
         if(var15 >= 60) {
            BlockPos var16 = new BlockPos(var3 * 16 + 8, var15 + 1, var4 * 16 + 8);
            List<WoodlandMansionPieces.WoodlandMansionPiece> var17 = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(structureManager, var16, var6, var17, this.random);
            this.pieces.addAll(var17);
            this.calculateBoundingBox();
         }
      }

      public void postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         super.postProcess(levelAccessor, random, boundingBox, chunkPos);
         int var5 = this.boundingBox.y0;

         for(int var6 = boundingBox.x0; var6 <= boundingBox.x1; ++var6) {
            for(int var7 = boundingBox.z0; var7 <= boundingBox.z1; ++var7) {
               BlockPos var8 = new BlockPos(var6, var5, var7);
               if(!levelAccessor.isEmptyBlock(var8) && this.boundingBox.isInside(var8)) {
                  boolean var9 = false;

                  for(StructurePiece var11 : this.pieces) {
                     if(var11.getBoundingBox().isInside(var8)) {
                        var9 = true;
                        break;
                     }
                  }

                  if(var9) {
                     for(int var10 = var5 - 1; var10 > 1; --var10) {
                        BlockPos var11 = new BlockPos(var6, var10, var7);
                        if(!levelAccessor.isEmptyBlock(var11) && !levelAccessor.getBlockState(var11).getMaterial().isLiquid()) {
                           break;
                        }

                        levelAccessor.setBlock(var11, Blocks.COBBLESTONE.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }

      }
   }
}
