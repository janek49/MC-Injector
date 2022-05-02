package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.MegaTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class MegaPineTreeFeature extends MegaTreeFeature {
   private static final BlockState TRUNK = Blocks.SPRUCE_LOG.defaultBlockState();
   private static final BlockState LEAF = Blocks.SPRUCE_LEAVES.defaultBlockState();
   private static final BlockState PODZOL = Blocks.PODZOL.defaultBlockState();
   private final boolean isSpruce;

   public MegaPineTreeFeature(Function function, boolean var2, boolean isSpruce) {
      super(function, var2, 13, 15, TRUNK, LEAF);
      this.isSpruce = isSpruce;
   }

   public boolean doPlace(Set set, LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BoundingBox boundingBox) {
      int var6 = this.calcTreeHeigth(random);
      if(!this.prepareTree(levelSimulatedRW, blockPos, var6)) {
         return false;
      } else {
         this.createCrown(levelSimulatedRW, blockPos.getX(), blockPos.getZ(), blockPos.getY() + var6, 0, random, boundingBox, set);

         for(int var7 = 0; var7 < var6; ++var7) {
            if(isAirOrLeaves(levelSimulatedRW, blockPos.above(var7))) {
               this.setBlock(set, levelSimulatedRW, blockPos.above(var7), this.trunk, boundingBox);
            }

            if(var7 < var6 - 1) {
               if(isAirOrLeaves(levelSimulatedRW, blockPos.offset(1, var7, 0))) {
                  this.setBlock(set, levelSimulatedRW, blockPos.offset(1, var7, 0), this.trunk, boundingBox);
               }

               if(isAirOrLeaves(levelSimulatedRW, blockPos.offset(1, var7, 1))) {
                  this.setBlock(set, levelSimulatedRW, blockPos.offset(1, var7, 1), this.trunk, boundingBox);
               }

               if(isAirOrLeaves(levelSimulatedRW, blockPos.offset(0, var7, 1))) {
                  this.setBlock(set, levelSimulatedRW, blockPos.offset(0, var7, 1), this.trunk, boundingBox);
               }
            }
         }

         this.postPlaceTree(levelSimulatedRW, random, blockPos);
         return true;
      }
   }

   private void createCrown(LevelSimulatedRW levelSimulatedRW, int var2, int var3, int var4, int var5, Random random, BoundingBox boundingBox, Set set) {
      int var9 = random.nextInt(5) + (this.isSpruce?this.baseHeight:3);
      int var10 = 0;

      for(int var11 = var4 - var9; var11 <= var4; ++var11) {
         int var12 = var4 - var11;
         int var13 = var5 + Mth.floor((float)var12 / (float)var9 * 3.5F);
         this.placeDoubleTrunkLeaves(levelSimulatedRW, new BlockPos(var2, var11, var3), var13 + (var12 > 0 && var13 == var10 && (var11 & 1) == 0?1:0), boundingBox, set);
         var10 = var13;
      }

   }

   public void postPlaceTree(LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos) {
      this.placePodzolCircle(levelSimulatedRW, blockPos.west().north());
      this.placePodzolCircle(levelSimulatedRW, blockPos.east(2).north());
      this.placePodzolCircle(levelSimulatedRW, blockPos.west().south(2));
      this.placePodzolCircle(levelSimulatedRW, blockPos.east(2).south(2));

      for(int var4 = 0; var4 < 5; ++var4) {
         int var5 = random.nextInt(64);
         int var6 = var5 % 8;
         int var7 = var5 / 8;
         if(var6 == 0 || var6 == 7 || var7 == 0 || var7 == 7) {
            this.placePodzolCircle(levelSimulatedRW, blockPos.offset(-3 + var6, 0, -3 + var7));
         }
      }

   }

   private void placePodzolCircle(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos) {
      for(int var3 = -2; var3 <= 2; ++var3) {
         for(int var4 = -2; var4 <= 2; ++var4) {
            if(Math.abs(var3) != 2 || Math.abs(var4) != 2) {
               this.placePodzolAt(levelSimulatedRW, blockPos.offset(var3, 0, var4));
            }
         }
      }

   }

   private void placePodzolAt(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos) {
      for(int var3 = 2; var3 >= -3; --var3) {
         BlockPos var4 = blockPos.above(var3);
         if(isGrassOrDirt(levelSimulatedRW, var4)) {
            this.setBlock(levelSimulatedRW, var4, PODZOL);
            break;
         }

         if(!isAir(levelSimulatedRW, var4) && var3 < 0) {
            break;
         }
      }

   }
}
