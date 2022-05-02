package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class PineFeature extends AbstractTreeFeature {
   private static final BlockState TRUNK = Blocks.SPRUCE_LOG.defaultBlockState();
   private static final BlockState LEAF = Blocks.SPRUCE_LEAVES.defaultBlockState();

   public PineFeature(Function function) {
      super(function, false);
   }

   public boolean doPlace(Set set, LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BoundingBox boundingBox) {
      int var6 = random.nextInt(5) + 7;
      int var7 = var6 - random.nextInt(2) - 3;
      int var8 = var6 - var7;
      int var9 = 1 + random.nextInt(var8 + 1);
      if(blockPos.getY() >= 1 && blockPos.getY() + var6 + 1 <= 256) {
         boolean var10 = true;

         for(int var11 = blockPos.getY(); var11 <= blockPos.getY() + 1 + var6 && var10; ++var11) {
            int var12 = 1;
            if(var11 - blockPos.getY() < var7) {
               var12 = 0;
            } else {
               var12 = var9;
            }

            BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos();

            for(int var14 = blockPos.getX() - var12; var14 <= blockPos.getX() + var12 && var10; ++var14) {
               for(int var15 = blockPos.getZ() - var12; var15 <= blockPos.getZ() + var12 && var10; ++var15) {
                  if(var11 >= 0 && var11 < 256) {
                     if(!isFree(levelSimulatedRW, var13.set(var14, var11, var15))) {
                        var10 = false;
                     }
                  } else {
                     var10 = false;
                  }
               }
            }
         }

         if(!var10) {
            return false;
         } else if(isGrassOrDirt(levelSimulatedRW, blockPos.below()) && blockPos.getY() < 256 - var6 - 1) {
            this.setDirtAt(levelSimulatedRW, blockPos.below());
            int var11 = 0;

            for(int var12 = blockPos.getY() + var6; var12 >= blockPos.getY() + var7; --var12) {
               for(int var13 = blockPos.getX() - var11; var13 <= blockPos.getX() + var11; ++var13) {
                  int var14 = var13 - blockPos.getX();

                  for(int var15 = blockPos.getZ() - var11; var15 <= blockPos.getZ() + var11; ++var15) {
                     int var16 = var15 - blockPos.getZ();
                     if(Math.abs(var14) != var11 || Math.abs(var16) != var11 || var11 <= 0) {
                        BlockPos var17 = new BlockPos(var13, var12, var15);
                        if(isAirOrLeaves(levelSimulatedRW, var17)) {
                           this.setBlock(set, levelSimulatedRW, var17, LEAF, boundingBox);
                        }
                     }
                  }
               }

               if(var11 >= 1 && var12 == blockPos.getY() + var7 + 1) {
                  --var11;
               } else if(var11 < var9) {
                  ++var11;
               }
            }

            for(int var12 = 0; var12 < var6 - 1; ++var12) {
               if(isAirOrLeaves(levelSimulatedRW, blockPos.above(var12))) {
                  this.setBlock(set, levelSimulatedRW, blockPos.above(var12), TRUNK, boundingBox);
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
