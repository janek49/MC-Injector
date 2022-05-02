package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class DarkOakFeature extends AbstractTreeFeature {
   private static final BlockState LOG = Blocks.DARK_OAK_LOG.defaultBlockState();
   private static final BlockState LEAVES = Blocks.DARK_OAK_LEAVES.defaultBlockState();

   public DarkOakFeature(Function function, boolean var2) {
      super(function, var2);
   }

   public boolean doPlace(Set set, LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BoundingBox boundingBox) {
      int var6 = random.nextInt(3) + random.nextInt(2) + 6;
      int var7 = blockPos.getX();
      int var8 = blockPos.getY();
      int var9 = blockPos.getZ();
      if(var8 >= 1 && var8 + var6 + 1 < 256) {
         BlockPos var10 = blockPos.below();
         if(!isGrassOrDirt(levelSimulatedRW, var10)) {
            return false;
         } else if(!this.canPlaceTreeOfHeight(levelSimulatedRW, blockPos, var6)) {
            return false;
         } else {
            this.setDirtAt(levelSimulatedRW, var10);
            this.setDirtAt(levelSimulatedRW, var10.east());
            this.setDirtAt(levelSimulatedRW, var10.south());
            this.setDirtAt(levelSimulatedRW, var10.south().east());
            Direction var11 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int var12 = var6 - random.nextInt(4);
            int var13 = 2 - random.nextInt(3);
            int var14 = var7;
            int var15 = var9;
            int var16 = var8 + var6 - 1;

            for(int var17 = 0; var17 < var6; ++var17) {
               if(var17 >= var12 && var13 > 0) {
                  var14 += var11.getStepX();
                  var15 += var11.getStepZ();
                  --var13;
               }

               int var18 = var8 + var17;
               BlockPos var19 = new BlockPos(var14, var18, var15);
               if(isAirOrLeaves(levelSimulatedRW, var19)) {
                  this.placeLogAt(set, levelSimulatedRW, var19, boundingBox);
                  this.placeLogAt(set, levelSimulatedRW, var19.east(), boundingBox);
                  this.placeLogAt(set, levelSimulatedRW, var19.south(), boundingBox);
                  this.placeLogAt(set, levelSimulatedRW, var19.east().south(), boundingBox);
               }
            }

            for(int var17 = -2; var17 <= 0; ++var17) {
               for(int var18 = -2; var18 <= 0; ++var18) {
                  int var19 = -1;
                  this.placeLeafAt(levelSimulatedRW, var14 + var17, var16 + var19, var15 + var18, boundingBox, set);
                  this.placeLeafAt(levelSimulatedRW, 1 + var14 - var17, var16 + var19, var15 + var18, boundingBox, set);
                  this.placeLeafAt(levelSimulatedRW, var14 + var17, var16 + var19, 1 + var15 - var18, boundingBox, set);
                  this.placeLeafAt(levelSimulatedRW, 1 + var14 - var17, var16 + var19, 1 + var15 - var18, boundingBox, set);
                  if((var17 > -2 || var18 > -1) && (var17 != -1 || var18 != -2)) {
                     var19 = 1;
                     this.placeLeafAt(levelSimulatedRW, var14 + var17, var16 + var19, var15 + var18, boundingBox, set);
                     this.placeLeafAt(levelSimulatedRW, 1 + var14 - var17, var16 + var19, var15 + var18, boundingBox, set);
                     this.placeLeafAt(levelSimulatedRW, var14 + var17, var16 + var19, 1 + var15 - var18, boundingBox, set);
                     this.placeLeafAt(levelSimulatedRW, 1 + var14 - var17, var16 + var19, 1 + var15 - var18, boundingBox, set);
                  }
               }
            }

            if(random.nextBoolean()) {
               this.placeLeafAt(levelSimulatedRW, var14, var16 + 2, var15, boundingBox, set);
               this.placeLeafAt(levelSimulatedRW, var14 + 1, var16 + 2, var15, boundingBox, set);
               this.placeLeafAt(levelSimulatedRW, var14 + 1, var16 + 2, var15 + 1, boundingBox, set);
               this.placeLeafAt(levelSimulatedRW, var14, var16 + 2, var15 + 1, boundingBox, set);
            }

            for(int var17 = -3; var17 <= 4; ++var17) {
               for(int var18 = -3; var18 <= 4; ++var18) {
                  if((var17 != -3 || var18 != -3) && (var17 != -3 || var18 != 4) && (var17 != 4 || var18 != -3) && (var17 != 4 || var18 != 4) && (Math.abs(var17) < 3 || Math.abs(var18) < 3)) {
                     this.placeLeafAt(levelSimulatedRW, var14 + var17, var16, var15 + var18, boundingBox, set);
                  }
               }
            }

            for(int var17 = -1; var17 <= 2; ++var17) {
               for(int var18 = -1; var18 <= 2; ++var18) {
                  if((var17 < 0 || var17 > 1 || var18 < 0 || var18 > 1) && random.nextInt(3) <= 0) {
                     int var19 = random.nextInt(3) + 2;

                     for(int var20 = 0; var20 < var19; ++var20) {
                        this.placeLogAt(set, levelSimulatedRW, new BlockPos(var7 + var17, var16 - var20 - 1, var9 + var18), boundingBox);
                     }

                     for(int var20 = -1; var20 <= 1; ++var20) {
                        for(int var21 = -1; var21 <= 1; ++var21) {
                           this.placeLeafAt(levelSimulatedRW, var14 + var17 + var20, var16, var15 + var18 + var21, boundingBox, set);
                        }
                     }

                     for(int var20 = -2; var20 <= 2; ++var20) {
                        for(int var21 = -2; var21 <= 2; ++var21) {
                           if(Math.abs(var20) != 2 || Math.abs(var21) != 2) {
                              this.placeLeafAt(levelSimulatedRW, var14 + var17 + var20, var16 - 1, var15 + var18 + var21, boundingBox, set);
                           }
                        }
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean canPlaceTreeOfHeight(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos, int var3) {
      int var4 = blockPos.getX();
      int var5 = blockPos.getY();
      int var6 = blockPos.getZ();
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      for(int var8 = 0; var8 <= var3 + 1; ++var8) {
         int var9 = 1;
         if(var8 == 0) {
            var9 = 0;
         }

         if(var8 >= var3 - 1) {
            var9 = 2;
         }

         for(int var10 = -var9; var10 <= var9; ++var10) {
            for(int var11 = -var9; var11 <= var9; ++var11) {
               if(!isFree(levelSimulatedReader, var7.set(var4 + var10, var5 + var8, var6 + var11))) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private void placeLogAt(Set set, LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, BoundingBox boundingBox) {
      if(isFree(levelSimulatedRW, blockPos)) {
         this.setBlock(set, levelSimulatedRW, blockPos, LOG, boundingBox);
      }

   }

   private void placeLeafAt(LevelSimulatedRW levelSimulatedRW, int var2, int var3, int var4, BoundingBox boundingBox, Set set) {
      BlockPos var7 = new BlockPos(var2, var3, var4);
      if(isAir(levelSimulatedRW, var7)) {
         this.setBlock(set, levelSimulatedRW, var7, LEAVES, boundingBox);
      }

   }
}
