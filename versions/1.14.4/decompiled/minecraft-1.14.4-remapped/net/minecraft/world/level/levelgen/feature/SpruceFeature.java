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

public class SpruceFeature extends AbstractTreeFeature {
   private static final BlockState TRUNK = Blocks.SPRUCE_LOG.defaultBlockState();
   private static final BlockState LEAF = Blocks.SPRUCE_LEAVES.defaultBlockState();

   public SpruceFeature(Function function, boolean var2) {
      super(function, var2);
   }

   public boolean doPlace(Set set, LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BoundingBox boundingBox) {
      int var6 = random.nextInt(4) + 6;
      int var7 = 1 + random.nextInt(2);
      int var8 = var6 - var7;
      int var9 = 2 + random.nextInt(2);
      boolean var10 = true;
      if(blockPos.getY() >= 1 && blockPos.getY() + var6 + 1 <= 256) {
         for(int var11 = blockPos.getY(); var11 <= blockPos.getY() + 1 + var6 && var10; ++var11) {
            int var12;
            if(var11 - blockPos.getY() < var7) {
               var12 = 0;
            } else {
               var12 = var9;
            }

            BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos();

            for(int var14 = blockPos.getX() - var12; var14 <= blockPos.getX() + var12 && var10; ++var14) {
               for(int var15 = blockPos.getZ() - var12; var15 <= blockPos.getZ() + var12 && var10; ++var15) {
                  if(var11 >= 0 && var11 < 256) {
                     var13.set(var14, var11, var15);
                     if(!isAirOrLeaves(levelSimulatedRW, var13)) {
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
         } else if(isGrassOrDirtOrFarmland(levelSimulatedRW, blockPos.below()) && blockPos.getY() < 256 - var6 - 1) {
            this.setDirtAt(levelSimulatedRW, blockPos.below());
            int var11 = random.nextInt(2);
            int var12 = 1;
            int var13 = 0;

            for(int var14 = 0; var14 <= var8; ++var14) {
               int var15 = blockPos.getY() + var6 - var14;

               for(int var16 = blockPos.getX() - var11; var16 <= blockPos.getX() + var11; ++var16) {
                  int var17 = var16 - blockPos.getX();

                  for(int var18 = blockPos.getZ() - var11; var18 <= blockPos.getZ() + var11; ++var18) {
                     int var19 = var18 - blockPos.getZ();
                     if(Math.abs(var17) != var11 || Math.abs(var19) != var11 || var11 <= 0) {
                        BlockPos var20 = new BlockPos(var16, var15, var18);
                        if(isAirOrLeaves(levelSimulatedRW, var20) || isReplaceablePlant(levelSimulatedRW, var20)) {
                           this.setBlock(set, levelSimulatedRW, var20, LEAF, boundingBox);
                        }
                     }
                  }
               }

               if(var11 >= var12) {
                  var11 = var13;
                  var13 = 1;
                  ++var12;
                  if(var12 > var9) {
                     var12 = var9;
                  }
               } else {
                  ++var11;
               }
            }

            int var14 = random.nextInt(3);

            for(int var15 = 0; var15 < var6 - var14; ++var15) {
               if(isAirOrLeaves(levelSimulatedRW, blockPos.above(var15))) {
                  this.setBlock(set, levelSimulatedRW, blockPos.above(var15), TRUNK, boundingBox);
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
