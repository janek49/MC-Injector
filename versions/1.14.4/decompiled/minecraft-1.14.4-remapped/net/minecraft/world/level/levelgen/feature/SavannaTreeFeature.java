package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class SavannaTreeFeature extends AbstractTreeFeature {
   private static final BlockState TRUNK = Blocks.ACACIA_LOG.defaultBlockState();
   private static final BlockState LEAF = Blocks.ACACIA_LEAVES.defaultBlockState();

   public SavannaTreeFeature(Function function, boolean var2) {
      super(function, var2);
   }

   public boolean doPlace(Set set, LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BoundingBox boundingBox) {
      int var6 = random.nextInt(3) + random.nextInt(3) + 5;
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
         } else if(isGrassOrDirt(levelSimulatedRW, blockPos.below()) && blockPos.getY() < 256 - var6 - 1) {
            this.setDirtAt(levelSimulatedRW, blockPos.below());
            Direction var8 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int var9 = var6 - random.nextInt(4) - 1;
            int var10 = 3 - random.nextInt(3);
            int var11 = blockPos.getX();
            int var12 = blockPos.getZ();
            int var13 = 0;

            for(int var14 = 0; var14 < var6; ++var14) {
               int var15 = blockPos.getY() + var14;
               if(var14 >= var9 && var10 > 0) {
                  var11 += var8.getStepX();
                  var12 += var8.getStepZ();
                  --var10;
               }

               BlockPos var16 = new BlockPos(var11, var15, var12);
               if(isAirOrLeaves(levelSimulatedRW, var16)) {
                  this.placeLogAt(set, levelSimulatedRW, var16, boundingBox);
                  var13 = var15;
               }
            }

            BlockPos var14 = new BlockPos(var11, var13, var12);

            for(int var15 = -3; var15 <= 3; ++var15) {
               for(int var16 = -3; var16 <= 3; ++var16) {
                  if(Math.abs(var15) != 3 || Math.abs(var16) != 3) {
                     this.placeLeafAt(set, levelSimulatedRW, var14.offset(var15, 0, var16), boundingBox);
                  }
               }
            }

            var14 = var14.above();

            for(int var15 = -1; var15 <= 1; ++var15) {
               for(int var16 = -1; var16 <= 1; ++var16) {
                  this.placeLeafAt(set, levelSimulatedRW, var14.offset(var15, 0, var16), boundingBox);
               }
            }

            this.placeLeafAt(set, levelSimulatedRW, var14.east(2), boundingBox);
            this.placeLeafAt(set, levelSimulatedRW, var14.west(2), boundingBox);
            this.placeLeafAt(set, levelSimulatedRW, var14.south(2), boundingBox);
            this.placeLeafAt(set, levelSimulatedRW, var14.north(2), boundingBox);
            var11 = blockPos.getX();
            var12 = blockPos.getZ();
            Direction var14 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            if(var14 != var8) {
               int var15 = var9 - random.nextInt(2) - 1;
               int var16 = 1 + random.nextInt(3);
               var13 = 0;

               for(int var17 = var15; var17 < var6 && var16 > 0; --var16) {
                  if(var17 >= 1) {
                     int var18 = blockPos.getY() + var17;
                     var11 += var14.getStepX();
                     var12 += var14.getStepZ();
                     BlockPos var19 = new BlockPos(var11, var18, var12);
                     if(isAirOrLeaves(levelSimulatedRW, var19)) {
                        this.placeLogAt(set, levelSimulatedRW, var19, boundingBox);
                        var13 = var18;
                     }
                  }

                  ++var17;
               }

               if(var13 > 0) {
                  BlockPos var17 = new BlockPos(var11, var13, var12);

                  for(int var18 = -2; var18 <= 2; ++var18) {
                     for(int var19 = -2; var19 <= 2; ++var19) {
                        if(Math.abs(var18) != 2 || Math.abs(var19) != 2) {
                           this.placeLeafAt(set, levelSimulatedRW, var17.offset(var18, 0, var19), boundingBox);
                        }
                     }
                  }

                  var17 = var17.above();

                  for(int var18 = -1; var18 <= 1; ++var18) {
                     for(int var19 = -1; var19 <= 1; ++var19) {
                        this.placeLeafAt(set, levelSimulatedRW, var17.offset(var18, 0, var19), boundingBox);
                     }
                  }
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

   private void placeLogAt(Set set, LevelWriter levelWriter, BlockPos blockPos, BoundingBox boundingBox) {
      this.setBlock(set, levelWriter, blockPos, TRUNK, boundingBox);
   }

   private void placeLeafAt(Set set, LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, BoundingBox boundingBox) {
      if(isAirOrLeaves(levelSimulatedRW, blockPos)) {
         this.setBlock(set, levelSimulatedRW, blockPos, LEAF, boundingBox);
      }

   }
}
