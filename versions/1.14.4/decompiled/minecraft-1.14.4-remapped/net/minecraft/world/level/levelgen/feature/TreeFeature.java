package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class TreeFeature extends AbstractTreeFeature {
   private static final BlockState DEFAULT_TRUNK = Blocks.OAK_LOG.defaultBlockState();
   private static final BlockState DEFAULT_LEAF = Blocks.OAK_LEAVES.defaultBlockState();
   protected final int baseHeight;
   private final boolean addJungleFeatures;
   private final BlockState trunk;
   private final BlockState leaf;

   public TreeFeature(Function function, boolean var2) {
      this(function, var2, 4, DEFAULT_TRUNK, DEFAULT_LEAF, false);
   }

   public TreeFeature(Function function, boolean var2, int baseHeight, BlockState trunk, BlockState leaf, boolean addJungleFeatures) {
      super(function, var2);
      this.baseHeight = baseHeight;
      this.trunk = trunk;
      this.leaf = leaf;
      this.addJungleFeatures = addJungleFeatures;
   }

   public boolean doPlace(Set set, LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BoundingBox boundingBox) {
      int var6 = this.getTreeHeight(random);
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
            int var8 = 3;
            int var9 = 0;

            for(int var10 = blockPos.getY() - 3 + var6; var10 <= blockPos.getY() + var6; ++var10) {
               int var11 = var10 - (blockPos.getY() + var6);
               int var12 = 1 - var11 / 2;

               for(int var13 = blockPos.getX() - var12; var13 <= blockPos.getX() + var12; ++var13) {
                  int var14 = var13 - blockPos.getX();

                  for(int var15 = blockPos.getZ() - var12; var15 <= blockPos.getZ() + var12; ++var15) {
                     int var16 = var15 - blockPos.getZ();
                     if(Math.abs(var14) != var12 || Math.abs(var16) != var12 || random.nextInt(2) != 0 && var11 != 0) {
                        BlockPos var17 = new BlockPos(var13, var10, var15);
                        if(isAirOrLeaves(levelSimulatedRW, var17) || isReplaceablePlant(levelSimulatedRW, var17)) {
                           this.setBlock(set, levelSimulatedRW, var17, this.leaf, boundingBox);
                        }
                     }
                  }
               }
            }

            for(int var10 = 0; var10 < var6; ++var10) {
               if(isAirOrLeaves(levelSimulatedRW, blockPos.above(var10)) || isReplaceablePlant(levelSimulatedRW, blockPos.above(var10))) {
                  this.setBlock(set, levelSimulatedRW, blockPos.above(var10), this.trunk, boundingBox);
                  if(this.addJungleFeatures && var10 > 0) {
                     if(random.nextInt(3) > 0 && isAir(levelSimulatedRW, blockPos.offset(-1, var10, 0))) {
                        this.addVine(levelSimulatedRW, blockPos.offset(-1, var10, 0), VineBlock.EAST);
                     }

                     if(random.nextInt(3) > 0 && isAir(levelSimulatedRW, blockPos.offset(1, var10, 0))) {
                        this.addVine(levelSimulatedRW, blockPos.offset(1, var10, 0), VineBlock.WEST);
                     }

                     if(random.nextInt(3) > 0 && isAir(levelSimulatedRW, blockPos.offset(0, var10, -1))) {
                        this.addVine(levelSimulatedRW, blockPos.offset(0, var10, -1), VineBlock.SOUTH);
                     }

                     if(random.nextInt(3) > 0 && isAir(levelSimulatedRW, blockPos.offset(0, var10, 1))) {
                        this.addVine(levelSimulatedRW, blockPos.offset(0, var10, 1), VineBlock.NORTH);
                     }
                  }
               }
            }

            if(this.addJungleFeatures) {
               for(int var10 = blockPos.getY() - 3 + var6; var10 <= blockPos.getY() + var6; ++var10) {
                  int var11 = var10 - (blockPos.getY() + var6);
                  int var12 = 2 - var11 / 2;
                  BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos();

                  for(int var14 = blockPos.getX() - var12; var14 <= blockPos.getX() + var12; ++var14) {
                     for(int var15 = blockPos.getZ() - var12; var15 <= blockPos.getZ() + var12; ++var15) {
                        var13.set(var14, var10, var15);
                        if(isLeaves(levelSimulatedRW, var13)) {
                           BlockPos var16 = var13.west();
                           BlockPos var17 = var13.east();
                           BlockPos var18 = var13.north();
                           BlockPos var19 = var13.south();
                           if(random.nextInt(4) == 0 && isAir(levelSimulatedRW, var16)) {
                              this.addHangingVine(levelSimulatedRW, var16, VineBlock.EAST);
                           }

                           if(random.nextInt(4) == 0 && isAir(levelSimulatedRW, var17)) {
                              this.addHangingVine(levelSimulatedRW, var17, VineBlock.WEST);
                           }

                           if(random.nextInt(4) == 0 && isAir(levelSimulatedRW, var18)) {
                              this.addHangingVine(levelSimulatedRW, var18, VineBlock.SOUTH);
                           }

                           if(random.nextInt(4) == 0 && isAir(levelSimulatedRW, var19)) {
                              this.addHangingVine(levelSimulatedRW, var19, VineBlock.NORTH);
                           }
                        }
                     }
                  }
               }

               if(random.nextInt(5) == 0 && var6 > 5) {
                  for(int var10 = 0; var10 < 2; ++var10) {
                     for(Direction var12 : Direction.Plane.HORIZONTAL) {
                        if(random.nextInt(4 - var10) == 0) {
                           Direction var13 = var12.getOpposite();
                           this.placeCocoa(levelSimulatedRW, random.nextInt(3), blockPos.offset(var13.getStepX(), var6 - 5 + var10, var13.getStepZ()), var12);
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

   protected int getTreeHeight(Random random) {
      return this.baseHeight + random.nextInt(3);
   }

   private void placeCocoa(LevelWriter levelWriter, int var2, BlockPos blockPos, Direction direction) {
      this.setBlock(levelWriter, blockPos, (BlockState)((BlockState)Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, Integer.valueOf(var2))).setValue(CocoaBlock.FACING, direction));
   }

   private void addVine(LevelWriter levelWriter, BlockPos blockPos, BooleanProperty booleanProperty) {
      this.setBlock(levelWriter, blockPos, (BlockState)Blocks.VINE.defaultBlockState().setValue(booleanProperty, Boolean.valueOf(true)));
   }

   private void addHangingVine(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, BooleanProperty booleanProperty) {
      this.addVine(levelSimulatedRW, blockPos, booleanProperty);
      int var4 = 4;

      for(blockPos = blockPos.below(); isAir(levelSimulatedRW, blockPos) && var4 > 0; --var4) {
         this.addVine(levelSimulatedRW, blockPos, booleanProperty);
         blockPos = blockPos.below();
      }

   }
}
