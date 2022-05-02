package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.MegaTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class MegaJungleTreeFeature extends MegaTreeFeature {
   public MegaJungleTreeFeature(Function function, boolean var2, int var3, int var4, BlockState var5, BlockState var6) {
      super(function, var2, var3, var4, var5, var6);
   }

   public boolean doPlace(Set set, LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BoundingBox boundingBox) {
      int var6 = this.calcTreeHeigth(random);
      if(!this.prepareTree(levelSimulatedRW, blockPos, var6)) {
         return false;
      } else {
         this.createCrown(levelSimulatedRW, blockPos.above(var6), 2, boundingBox, set);

         for(int var7 = blockPos.getY() + var6 - 2 - random.nextInt(4); var7 > blockPos.getY() + var6 / 2; var7 -= 2 + random.nextInt(4)) {
            float var8 = random.nextFloat() * 6.2831855F;
            int var9 = blockPos.getX() + (int)(0.5F + Mth.cos(var8) * 4.0F);
            int var10 = blockPos.getZ() + (int)(0.5F + Mth.sin(var8) * 4.0F);

            for(int var11 = 0; var11 < 5; ++var11) {
               var9 = blockPos.getX() + (int)(1.5F + Mth.cos(var8) * (float)var11);
               var10 = blockPos.getZ() + (int)(1.5F + Mth.sin(var8) * (float)var11);
               this.setBlock(set, levelSimulatedRW, new BlockPos(var9, var7 - 3 + var11 / 2, var10), this.trunk, boundingBox);
            }

            int var11 = 1 + random.nextInt(2);
            int var12 = var7;

            for(int var13 = var7 - var11; var13 <= var12; ++var13) {
               int var14 = var13 - var12;
               this.placeSingleTrunkLeaves(levelSimulatedRW, new BlockPos(var9, var13, var10), 1 - var14, boundingBox, set);
            }
         }

         for(int var8 = 0; var8 < var6; ++var8) {
            BlockPos var9 = blockPos.above(var8);
            if(isFree(levelSimulatedRW, var9)) {
               this.setBlock(set, levelSimulatedRW, var9, this.trunk, boundingBox);
               if(var8 > 0) {
                  this.placeVine(levelSimulatedRW, random, var9.west(), VineBlock.EAST);
                  this.placeVine(levelSimulatedRW, random, var9.north(), VineBlock.SOUTH);
               }
            }

            if(var8 < var6 - 1) {
               BlockPos var10 = var9.east();
               if(isFree(levelSimulatedRW, var10)) {
                  this.setBlock(set, levelSimulatedRW, var10, this.trunk, boundingBox);
                  if(var8 > 0) {
                     this.placeVine(levelSimulatedRW, random, var10.east(), VineBlock.WEST);
                     this.placeVine(levelSimulatedRW, random, var10.north(), VineBlock.SOUTH);
                  }
               }

               BlockPos var11 = var9.south().east();
               if(isFree(levelSimulatedRW, var11)) {
                  this.setBlock(set, levelSimulatedRW, var11, this.trunk, boundingBox);
                  if(var8 > 0) {
                     this.placeVine(levelSimulatedRW, random, var11.east(), VineBlock.WEST);
                     this.placeVine(levelSimulatedRW, random, var11.south(), VineBlock.NORTH);
                  }
               }

               BlockPos var12 = var9.south();
               if(isFree(levelSimulatedRW, var12)) {
                  this.setBlock(set, levelSimulatedRW, var12, this.trunk, boundingBox);
                  if(var8 > 0) {
                     this.placeVine(levelSimulatedRW, random, var12.west(), VineBlock.EAST);
                     this.placeVine(levelSimulatedRW, random, var12.south(), VineBlock.NORTH);
                  }
               }
            }
         }

         return true;
      }
   }

   private void placeVine(LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BooleanProperty booleanProperty) {
      if(random.nextInt(3) > 0 && isAir(levelSimulatedRW, blockPos)) {
         this.setBlock(levelSimulatedRW, blockPos, (BlockState)Blocks.VINE.defaultBlockState().setValue(booleanProperty, Boolean.valueOf(true)));
      }

   }

   private void createCrown(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, int var3, BoundingBox boundingBox, Set set) {
      int var6 = 2;

      for(int var7 = -2; var7 <= 0; ++var7) {
         this.placeDoubleTrunkLeaves(levelSimulatedRW, blockPos.above(var7), var3 + 1 - var7, boundingBox, set);
      }

   }
}
