package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LogBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BigTreeFeature extends AbstractTreeFeature {
   private static final BlockState LOG = Blocks.OAK_LOG.defaultBlockState();
   private static final BlockState LEAVES = Blocks.OAK_LEAVES.defaultBlockState();

   public BigTreeFeature(Function function, boolean var2) {
      super(function, var2);
   }

   private void crossSection(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, float var3, BoundingBox boundingBox, Set set) {
      int var6 = (int)((double)var3 + 0.618D);

      for(int var7 = -var6; var7 <= var6; ++var7) {
         for(int var8 = -var6; var8 <= var6; ++var8) {
            if(Math.pow((double)Math.abs(var7) + 0.5D, 2.0D) + Math.pow((double)Math.abs(var8) + 0.5D, 2.0D) <= (double)(var3 * var3)) {
               BlockPos var9 = blockPos.offset(var7, 0, var8);
               if(isAirOrLeaves(levelSimulatedRW, var9)) {
                  this.setBlock(set, levelSimulatedRW, var9, LEAVES, boundingBox);
               }
            }
         }
      }

   }

   private float treeShape(int var1, int var2) {
      if((float)var2 < (float)var1 * 0.3F) {
         return -1.0F;
      } else {
         float var3 = (float)var1 / 2.0F;
         float var4 = var3 - (float)var2;
         float var5 = Mth.sqrt(var3 * var3 - var4 * var4);
         if(var4 == 0.0F) {
            var5 = var3;
         } else if(Math.abs(var4) >= var3) {
            return 0.0F;
         }

         return var5 * 0.5F;
      }
   }

   private float foliageShape(int i) {
      return i >= 0 && i < 5?(i != 0 && i != 4?3.0F:2.0F):-1.0F;
   }

   private void foliageCluster(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, BoundingBox boundingBox, Set set) {
      for(int var5 = 0; var5 < 5; ++var5) {
         this.crossSection(levelSimulatedRW, blockPos.above(var5), this.foliageShape(var5), boundingBox, set);
      }

   }

   private int makeLimb(Set set, LevelSimulatedRW levelSimulatedRW, BlockPos var3, BlockPos var4, boolean var5, BoundingBox boundingBox) {
      if(!var5 && Objects.equals(var3, var4)) {
         return -1;
      } else {
         BlockPos var7 = var4.offset(-var3.getX(), -var3.getY(), -var3.getZ());
         int var8 = this.getSteps(var7);
         float var9 = (float)var7.getX() / (float)var8;
         float var10 = (float)var7.getY() / (float)var8;
         float var11 = (float)var7.getZ() / (float)var8;

         for(int var12 = 0; var12 <= var8; ++var12) {
            BlockPos var13 = var3.offset((double)(0.5F + (float)var12 * var9), (double)(0.5F + (float)var12 * var10), (double)(0.5F + (float)var12 * var11));
            if(var5) {
               this.setBlock(set, levelSimulatedRW, var13, (BlockState)LOG.setValue(LogBlock.AXIS, this.getLogAxis(var3, var13)), boundingBox);
            } else if(!isFree(levelSimulatedRW, var13)) {
               return var12;
            }
         }

         return -1;
      }
   }

   private int getSteps(BlockPos blockPos) {
      int var2 = Mth.abs(blockPos.getX());
      int var3 = Mth.abs(blockPos.getY());
      int var4 = Mth.abs(blockPos.getZ());
      return var4 > var2 && var4 > var3?var4:(var3 > var2?var3:var2);
   }

   private Direction.Axis getLogAxis(BlockPos var1, BlockPos var2) {
      Direction.Axis direction$Axis = Direction.Axis.Y;
      int var4 = Math.abs(var2.getX() - var1.getX());
      int var5 = Math.abs(var2.getZ() - var1.getZ());
      int var6 = Math.max(var4, var5);
      if(var6 > 0) {
         if(var4 == var6) {
            direction$Axis = Direction.Axis.X;
         } else if(var5 == var6) {
            direction$Axis = Direction.Axis.Z;
         }
      }

      return direction$Axis;
   }

   private void makeFoliage(LevelSimulatedRW levelSimulatedRW, int var2, BlockPos blockPos, List list, BoundingBox boundingBox, Set set) {
      for(BigTreeFeature.FoliageCoords var8 : list) {
         if(this.trimBranches(var2, var8.getBranchBase() - blockPos.getY())) {
            this.foliageCluster(levelSimulatedRW, var8, boundingBox, set);
         }
      }

   }

   private boolean trimBranches(int var1, int var2) {
      return (double)var2 >= (double)var1 * 0.2D;
   }

   private void makeTrunk(Set set, LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, int var4, BoundingBox boundingBox) {
      this.makeLimb(set, levelSimulatedRW, blockPos, blockPos.above(var4), true, boundingBox);
   }

   private void makeBranches(Set set, LevelSimulatedRW levelSimulatedRW, int var3, BlockPos blockPos, List list, BoundingBox boundingBox) {
      for(BigTreeFeature.FoliageCoords var8 : list) {
         int var9 = var8.getBranchBase();
         BlockPos var10 = new BlockPos(blockPos.getX(), var9, blockPos.getZ());
         if(!var10.equals(var8) && this.trimBranches(var3, var9 - blockPos.getY())) {
            this.makeLimb(set, levelSimulatedRW, var10, var8, true, boundingBox);
         }
      }

   }

   public boolean doPlace(Set set, LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BoundingBox boundingBox) {
      Random random = new Random(random.nextLong());
      int var7 = this.checkLocation(set, levelSimulatedRW, blockPos, 5 + random.nextInt(12), boundingBox);
      if(var7 == -1) {
         return false;
      } else {
         this.setDirtAt(levelSimulatedRW, blockPos.below());
         int var8 = (int)((double)var7 * 0.618D);
         if(var8 >= var7) {
            var8 = var7 - 1;
         }

         double var9 = 1.0D;
         int var11 = (int)(1.382D + Math.pow(1.0D * (double)var7 / 13.0D, 2.0D));
         if(var11 < 1) {
            var11 = 1;
         }

         int var12 = blockPos.getY() + var8;
         int var13 = var7 - 5;
         List<BigTreeFeature.FoliageCoords> var14 = Lists.newArrayList();
         var14.add(new BigTreeFeature.FoliageCoords(blockPos.above(var13), var12));

         for(; var13 >= 0; --var13) {
            float var15 = this.treeShape(var7, var13);
            if(var15 >= 0.0F) {
               for(int var16 = 0; var16 < var11; ++var16) {
                  double var17 = 1.0D;
                  double var19 = 1.0D * (double)var15 * ((double)random.nextFloat() + 0.328D);
                  double var21 = (double)(random.nextFloat() * 2.0F) * 3.141592653589793D;
                  double var23 = var19 * Math.sin(var21) + 0.5D;
                  double var25 = var19 * Math.cos(var21) + 0.5D;
                  BlockPos var27 = blockPos.offset(var23, (double)(var13 - 1), var25);
                  BlockPos var28 = var27.above(5);
                  if(this.makeLimb(set, levelSimulatedRW, var27, var28, false, boundingBox) == -1) {
                     int var29 = blockPos.getX() - var27.getX();
                     int var30 = blockPos.getZ() - var27.getZ();
                     double var31 = (double)var27.getY() - Math.sqrt((double)(var29 * var29 + var30 * var30)) * 0.381D;
                     int var33 = var31 > (double)var12?var12:(int)var31;
                     BlockPos var34 = new BlockPos(blockPos.getX(), var33, blockPos.getZ());
                     if(this.makeLimb(set, levelSimulatedRW, var34, var27, false, boundingBox) == -1) {
                        var14.add(new BigTreeFeature.FoliageCoords(var27, var34.getY()));
                     }
                  }
               }
            }
         }

         this.makeFoliage(levelSimulatedRW, var7, blockPos, var14, boundingBox, set);
         this.makeTrunk(set, levelSimulatedRW, blockPos, var8, boundingBox);
         this.makeBranches(set, levelSimulatedRW, var7, blockPos, var14, boundingBox);
         return true;
      }
   }

   private int checkLocation(Set set, LevelSimulatedRW levelSimulatedRW, BlockPos blockPos, int var4, BoundingBox boundingBox) {
      if(!isGrassOrDirtOrFarmland(levelSimulatedRW, blockPos.below())) {
         return -1;
      } else {
         int var6 = this.makeLimb(set, levelSimulatedRW, blockPos, blockPos.above(var4 - 1), false, boundingBox);
         return var6 == -1?var4:(var6 < 6?-1:var6);
      }
   }

   static class FoliageCoords extends BlockPos {
      private final int branchBase;

      public FoliageCoords(BlockPos blockPos, int branchBase) {
         super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
         this.branchBase = branchBase;
      }

      public int getBranchBase() {
         return this.branchBase;
      }
   }
}
