package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class SwampTreeFeature extends AbstractTreeFeature {
   private static final BlockState TRUNK = Blocks.OAK_LOG.defaultBlockState();
   private static final BlockState LEAF = Blocks.OAK_LEAVES.defaultBlockState();

   public SwampTreeFeature(Function function) {
      super(function, false);
   }

   public boolean doPlace(Set set, LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BoundingBox boundingBox) {
      int var6 = random.nextInt(4) + 5;
      blockPos = levelSimulatedRW.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR, blockPos);
      boolean var7 = true;
      if(blockPos.getY() >= 1 && blockPos.getY() + var6 + 1 <= 256) {
         for(int var8 = blockPos.getY(); var8 <= blockPos.getY() + 1 + var6; ++var8) {
            int var9 = 1;
            if(var8 == blockPos.getY()) {
               var9 = 0;
            }

            if(var8 >= blockPos.getY() + 1 + var6 - 2) {
               var9 = 3;
            }

            BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();

            for(int var11 = blockPos.getX() - var9; var11 <= blockPos.getX() + var9 && var7; ++var11) {
               for(int var12 = blockPos.getZ() - var9; var12 <= blockPos.getZ() + var9 && var7; ++var12) {
                  if(var8 >= 0 && var8 < 256) {
                     var10.set(var11, var8, var12);
                     if(!isAirOrLeaves(levelSimulatedRW, var10)) {
                        if(isBlockWater(levelSimulatedRW, var10)) {
                           if(var8 > blockPos.getY()) {
                              var7 = false;
                           }
                        } else {
                           var7 = false;
                        }
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

            for(int var8 = blockPos.getY() - 3 + var6; var8 <= blockPos.getY() + var6; ++var8) {
               int var9 = var8 - (blockPos.getY() + var6);
               int var10 = 2 - var9 / 2;

               for(int var11 = blockPos.getX() - var10; var11 <= blockPos.getX() + var10; ++var11) {
                  int var12 = var11 - blockPos.getX();

                  for(int var13 = blockPos.getZ() - var10; var13 <= blockPos.getZ() + var10; ++var13) {
                     int var14 = var13 - blockPos.getZ();
                     if(Math.abs(var12) != var10 || Math.abs(var14) != var10 || random.nextInt(2) != 0 && var9 != 0) {
                        BlockPos var15 = new BlockPos(var11, var8, var13);
                        if(isAirOrLeaves(levelSimulatedRW, var15) || isReplaceablePlant(levelSimulatedRW, var15)) {
                           this.setBlock(set, levelSimulatedRW, var15, LEAF, boundingBox);
                        }
                     }
                  }
               }
            }

            for(int var8 = 0; var8 < var6; ++var8) {
               BlockPos var9 = blockPos.above(var8);
               if(isAirOrLeaves(levelSimulatedRW, var9) || isBlockWater(levelSimulatedRW, var9)) {
                  this.setBlock(set, levelSimulatedRW, var9, TRUNK, boundingBox);
               }
            }

            for(int var8 = blockPos.getY() - 3 + var6; var8 <= blockPos.getY() + var6; ++var8) {
               int var9 = var8 - (blockPos.getY() + var6);
               int var10 = 2 - var9 / 2;
               BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

               for(int var12 = blockPos.getX() - var10; var12 <= blockPos.getX() + var10; ++var12) {
                  for(int var13 = blockPos.getZ() - var10; var13 <= blockPos.getZ() + var10; ++var13) {
                     var11.set(var12, var8, var13);
                     if(isLeaves(levelSimulatedRW, var11)) {
                        BlockPos var14 = var11.west();
                        BlockPos var15 = var11.east();
                        BlockPos var16 = var11.north();
                        BlockPos var17 = var11.south();
                        if(random.nextInt(4) == 0 && isAir(levelSimulatedRW, var14)) {
                           this.addVine(levelSimulatedRW, var14, VineBlock.EAST);
                        }

                        if(random.nextInt(4) == 0 && isAir(levelSimulatedRW, var15)) {
                           this.addVine(levelSimulatedRW, var15, VineBlock.WEST);
                        }

                        if(random.nextInt(4) == 0 && isAir(levelSimulatedRW, var16)) {
                           this.addVine(levelSimulatedRW, var16, VineBlock.SOUTH);
                        }

                        if(random.nextInt(4) == 0 && isAir(levelSimulatedRW, var17)) {
                           this.addVine(levelSimulatedRW, var17, VineBlock.NORTH);
                        }
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

   private void addVine(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, BooleanProperty booleanProperty) {
      BlockState var4 = (BlockState)Blocks.VINE.defaultBlockState().setValue(booleanProperty, Boolean.valueOf(true));
      this.setBlock(levelSimulatedRW, blockPos, var4);
      int var5 = 4;

      for(blockPos = blockPos.below(); isAir(levelSimulatedRW, blockPos) && var5 > 0; --var5) {
         this.setBlock(levelSimulatedRW, blockPos, var4);
         blockPos = blockPos.below();
      }

   }
}
