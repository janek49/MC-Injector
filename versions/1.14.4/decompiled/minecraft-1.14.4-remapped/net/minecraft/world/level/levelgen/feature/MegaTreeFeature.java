package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class MegaTreeFeature extends AbstractTreeFeature {
   protected final int baseHeight;
   protected final BlockState trunk;
   protected final BlockState leaf;
   protected final int heightInterval;

   public MegaTreeFeature(Function function, boolean var2, int baseHeight, int heightInterval, BlockState trunk, BlockState leaf) {
      super(function, var2);
      this.baseHeight = baseHeight;
      this.heightInterval = heightInterval;
      this.trunk = trunk;
      this.leaf = leaf;
   }

   protected int calcTreeHeigth(Random random) {
      int var2 = random.nextInt(3) + this.baseHeight;
      if(this.heightInterval > 1) {
         var2 += random.nextInt(this.heightInterval);
      }

      return var2;
   }

   private boolean checkIsFree(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos, int var3) {
      boolean var4 = true;
      if(blockPos.getY() >= 1 && blockPos.getY() + var3 + 1 <= 256) {
         for(int var5 = 0; var5 <= 1 + var3; ++var5) {
            int var6 = 2;
            if(var5 == 0) {
               var6 = 1;
            } else if(var5 >= 1 + var3 - 2) {
               var6 = 2;
            }

            for(int var7 = -var6; var7 <= var6 && var4; ++var7) {
               for(int var8 = -var6; var8 <= var6 && var4; ++var8) {
                  if(blockPos.getY() + var5 < 0 || blockPos.getY() + var5 >= 256 || !isFree(levelSimulatedReader, blockPos.offset(var7, var5, var8))) {
                     var4 = false;
                  }
               }
            }
         }

         return var4;
      } else {
         return false;
      }
   }

   private boolean makeDirtFloor(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      if(isGrassOrDirt(levelSimulatedRW, blockPos) && blockPos.getY() >= 2) {
         this.setDirtAt(levelSimulatedRW, blockPos);
         this.setDirtAt(levelSimulatedRW, blockPos.east());
         this.setDirtAt(levelSimulatedRW, blockPos.south());
         this.setDirtAt(levelSimulatedRW, blockPos.south().east());
         return true;
      } else {
         return false;
      }
   }

   protected boolean prepareTree(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, int var3) {
      return this.checkIsFree(levelSimulatedRW, blockPos, var3) && this.makeDirtFloor(levelSimulatedRW, blockPos);
   }

   protected void placeDoubleTrunkLeaves(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, int var3, BoundingBox boundingBox, Set set) {
      int var6 = var3 * var3;

      for(int var7 = -var3; var7 <= var3 + 1; ++var7) {
         for(int var8 = -var3; var8 <= var3 + 1; ++var8) {
            int var9 = Math.min(Math.abs(var7), Math.abs(var7 - 1));
            int var10 = Math.min(Math.abs(var8), Math.abs(var8 - 1));
            if(var9 + var10 < 7 && var9 * var9 + var10 * var10 <= var6) {
               BlockPos var11 = blockPos.offset(var7, 0, var8);
               if(isAirOrLeaves(levelSimulatedRW, var11)) {
                  this.setBlock(set, levelSimulatedRW, var11, this.leaf, boundingBox);
               }
            }
         }
      }

   }

   protected void placeSingleTrunkLeaves(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, int var3, BoundingBox boundingBox, Set set) {
      int var6 = var3 * var3;

      for(int var7 = -var3; var7 <= var3; ++var7) {
         for(int var8 = -var3; var8 <= var3; ++var8) {
            if(var7 * var7 + var8 * var8 <= var6) {
               BlockPos var9 = blockPos.offset(var7, 0, var8);
               if(isAirOrLeaves(levelSimulatedRW, var9)) {
                  this.setBlock(set, levelSimulatedRW, var9, this.leaf, boundingBox);
               }
            }
         }
      }

   }
}
