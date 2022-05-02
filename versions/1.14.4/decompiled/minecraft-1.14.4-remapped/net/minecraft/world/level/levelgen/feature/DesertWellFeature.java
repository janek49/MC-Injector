package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class DesertWellFeature extends Feature {
   private static final BlockStatePredicate IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
   private final BlockState sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
   private final BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
   private final BlockState water = Blocks.WATER.defaultBlockState();

   public DesertWellFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      for(blockPos = blockPos.above(); levelAccessor.isEmptyBlock(blockPos) && blockPos.getY() > 2; blockPos = blockPos.below()) {
         ;
      }

      if(!IS_SAND.test(levelAccessor.getBlockState(blockPos))) {
         return false;
      } else {
         for(int var6 = -2; var6 <= 2; ++var6) {
            for(int var7 = -2; var7 <= 2; ++var7) {
               if(levelAccessor.isEmptyBlock(blockPos.offset(var6, -1, var7)) && levelAccessor.isEmptyBlock(blockPos.offset(var6, -2, var7))) {
                  return false;
               }
            }
         }

         for(int var6 = -1; var6 <= 0; ++var6) {
            for(int var7 = -2; var7 <= 2; ++var7) {
               for(int var8 = -2; var8 <= 2; ++var8) {
                  levelAccessor.setBlock(blockPos.offset(var7, var6, var8), this.sandstone, 2);
               }
            }
         }

         levelAccessor.setBlock(blockPos, this.water, 2);

         for(Direction var7 : Direction.Plane.HORIZONTAL) {
            levelAccessor.setBlock(blockPos.relative(var7), this.water, 2);
         }

         for(int var6 = -2; var6 <= 2; ++var6) {
            for(int var7 = -2; var7 <= 2; ++var7) {
               if(var6 == -2 || var6 == 2 || var7 == -2 || var7 == 2) {
                  levelAccessor.setBlock(blockPos.offset(var6, 1, var7), this.sandstone, 2);
               }
            }
         }

         levelAccessor.setBlock(blockPos.offset(2, 1, 0), this.sandSlab, 2);
         levelAccessor.setBlock(blockPos.offset(-2, 1, 0), this.sandSlab, 2);
         levelAccessor.setBlock(blockPos.offset(0, 1, 2), this.sandSlab, 2);
         levelAccessor.setBlock(blockPos.offset(0, 1, -2), this.sandSlab, 2);

         for(int var6 = -1; var6 <= 1; ++var6) {
            for(int var7 = -1; var7 <= 1; ++var7) {
               if(var6 == 0 && var7 == 0) {
                  levelAccessor.setBlock(blockPos.offset(var6, 4, var7), this.sandstone, 2);
               } else {
                  levelAccessor.setBlock(blockPos.offset(var6, 4, var7), this.sandSlab, 2);
               }
            }
         }

         for(int var6 = 1; var6 <= 3; ++var6) {
            levelAccessor.setBlock(blockPos.offset(-1, var6, -1), this.sandstone, 2);
            levelAccessor.setBlock(blockPos.offset(-1, var6, 1), this.sandstone, 2);
            levelAccessor.setBlock(blockPos.offset(1, var6, -1), this.sandstone, 2);
            levelAccessor.setBlock(blockPos.offset(1, var6, 1), this.sandstone, 2);
         }

         return true;
      }
   }
}
