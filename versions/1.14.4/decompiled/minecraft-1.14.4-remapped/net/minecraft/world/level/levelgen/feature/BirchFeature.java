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

public class BirchFeature extends AbstractTreeFeature {
   private static final BlockState LOG = Blocks.BIRCH_LOG.defaultBlockState();
   private static final BlockState LEAF = Blocks.BIRCH_LEAVES.defaultBlockState();
   private final boolean superBirch;

   public BirchFeature(Function function, boolean var2, boolean superBirch) {
      super(function, var2);
      this.superBirch = superBirch;
   }

   public boolean doPlace(Set set, LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BoundingBox boundingBox) {
      int var6 = random.nextInt(3) + 5;
      if(this.superBirch) {
         var6 += random.nextInt(7);
      }

      boolean var7 = true;
      if(blockPos.getY() >= 1 && blockPos.getY() + var6 + 1 <= 256) {
         for(int var8 = blockPos.getY(); var8 <= blockPos.getY() + 1 + var6; ++var8) {
            int var9 = 1;
            if(var8 == blockPos.getY()) {
               var9 = 0;
            }

            if(var8 >= blockPos.getY() + 1 + var6 - 2) {
               var9 = 2;
            }

            BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();

            for(int var11 = blockPos.getX() - var9; var11 <= blockPos.getX() + var9 && var7; ++var11) {
               for(int var12 = blockPos.getZ() - var9; var12 <= blockPos.getZ() + var9 && var7; ++var12) {
                  if(var8 >= 0 && var8 < 256) {
                     if(!isFree(levelSimulatedRW, var10.set(var11, var8, var12))) {
                        var7 = false;
                     }
                  } else {
                     var7 = false;
                  }
               }
            }
         }

         if(!var7) {
            return false;
         } else if(isGrassOrDirtOrFarmland(levelSimulatedRW, blockPos.below()) && blockPos.getY() < 256 - var6 - 1) {
            this.setDirtAt(levelSimulatedRW, blockPos.below());

            for(int var8 = blockPos.getY() - 3 + var6; var8 <= blockPos.getY() + var6; ++var8) {
               int var9 = var8 - (blockPos.getY() + var6);
               int var10 = 1 - var9 / 2;

               for(int var11 = blockPos.getX() - var10; var11 <= blockPos.getX() + var10; ++var11) {
                  int var12 = var11 - blockPos.getX();

                  for(int var13 = blockPos.getZ() - var10; var13 <= blockPos.getZ() + var10; ++var13) {
                     int var14 = var13 - blockPos.getZ();
                     if(Math.abs(var12) != var10 || Math.abs(var14) != var10 || random.nextInt(2) != 0 && var9 != 0) {
                        BlockPos var15 = new BlockPos(var11, var8, var13);
                        if(isAirOrLeaves(levelSimulatedRW, var15)) {
                           this.setBlock(set, levelSimulatedRW, var15, LEAF, boundingBox);
                        }
                     }
                  }
               }
            }

            for(int var8 = 0; var8 < var6; ++var8) {
               if(isAirOrLeaves(levelSimulatedRW, blockPos.above(var8))) {
                  this.setBlock(set, levelSimulatedRW, blockPos.above(var8), LOG, boundingBox);
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
