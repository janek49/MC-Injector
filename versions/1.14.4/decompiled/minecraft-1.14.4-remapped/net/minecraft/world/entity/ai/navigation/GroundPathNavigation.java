package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class GroundPathNavigation extends PathNavigation {
   private boolean avoidSun;

   public GroundPathNavigation(Mob mob, Level level) {
      super(mob, level);
   }

   protected PathFinder createPathFinder(int i) {
      this.nodeEvaluator = new WalkNodeEvaluator();
      this.nodeEvaluator.setCanPassDoors(true);
      return new PathFinder(this.nodeEvaluator, i);
   }

   protected boolean canUpdatePath() {
      return this.mob.onGround || this.isInLiquid() || this.mob.isPassenger();
   }

   protected Vec3 getTempMobPos() {
      return new Vec3(this.mob.x, (double)this.getSurfaceY(), this.mob.z);
   }

   public Path createPath(BlockPos blockPos, int var2) {
      if(this.level.getBlockState(blockPos).isAir()) {
         BlockPos blockPos;
         for(blockPos = blockPos.below(); blockPos.getY() > 0 && this.level.getBlockState(blockPos).isAir(); blockPos = blockPos.below()) {
            ;
         }

         if(blockPos.getY() > 0) {
            return super.createPath(blockPos.above(), var2);
         }

         while(blockPos.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(blockPos).isAir()) {
            blockPos = blockPos.above();
         }

         blockPos = blockPos;
      }

      if(!this.level.getBlockState(blockPos).getMaterial().isSolid()) {
         return super.createPath(blockPos, var2);
      } else {
         BlockPos blockPos;
         for(blockPos = blockPos.above(); blockPos.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(blockPos).getMaterial().isSolid(); blockPos = blockPos.above()) {
            ;
         }

         return super.createPath(blockPos, var2);
      }
   }

   public Path createPath(Entity entity, int var2) {
      return this.createPath(new BlockPos(entity), var2);
   }

   private int getSurfaceY() {
      if(this.mob.isInWater() && this.canFloat()) {
         int var1 = Mth.floor(this.mob.getBoundingBox().minY);
         Block var2 = this.level.getBlockState(new BlockPos(this.mob.x, (double)var1, this.mob.z)).getBlock();
         int var3 = 0;

         while(var2 == Blocks.WATER) {
            ++var1;
            var2 = this.level.getBlockState(new BlockPos(this.mob.x, (double)var1, this.mob.z)).getBlock();
            ++var3;
            if(var3 > 16) {
               return Mth.floor(this.mob.getBoundingBox().minY);
            }
         }

         return var1;
      } else {
         return Mth.floor(this.mob.getBoundingBox().minY + 0.5D);
      }
   }

   protected void trimPath() {
      super.trimPath();
      if(this.avoidSun) {
         if(this.level.canSeeSky(new BlockPos(this.mob.x, this.mob.getBoundingBox().minY + 0.5D, this.mob.z))) {
            return;
         }

         for(int var1 = 0; var1 < this.path.getSize(); ++var1) {
            Node var2 = this.path.get(var1);
            if(this.level.canSeeSky(new BlockPos(var2.x, var2.y, var2.z))) {
               this.path.truncate(var1);
               return;
            }
         }
      }

   }

   protected boolean canMoveDirectly(Vec3 var1, Vec3 var2, int var3, int var4, int var5) {
      int var6 = Mth.floor(var1.x);
      int var7 = Mth.floor(var1.z);
      double var8 = var2.x - var1.x;
      double var10 = var2.z - var1.z;
      double var12 = var8 * var8 + var10 * var10;
      if(var12 < 1.0E-8D) {
         return false;
      } else {
         double var14 = 1.0D / Math.sqrt(var12);
         var8 = var8 * var14;
         var10 = var10 * var14;
         var3 = var3 + 2;
         var5 = var5 + 2;
         if(!this.canWalkOn(var6, Mth.floor(var1.y), var7, var3, var4, var5, var1, var8, var10)) {
            return false;
         } else {
            var3 = var3 - 2;
            var5 = var5 - 2;
            double var16 = 1.0D / Math.abs(var8);
            double var18 = 1.0D / Math.abs(var10);
            double var20 = (double)var6 - var1.x;
            double var22 = (double)var7 - var1.z;
            if(var8 >= 0.0D) {
               ++var20;
            }

            if(var10 >= 0.0D) {
               ++var22;
            }

            var20 = var20 / var8;
            var22 = var22 / var10;
            int var24 = var8 < 0.0D?-1:1;
            int var25 = var10 < 0.0D?-1:1;
            int var26 = Mth.floor(var2.x);
            int var27 = Mth.floor(var2.z);
            int var28 = var26 - var6;
            int var29 = var27 - var7;

            while(var28 * var24 > 0 || var29 * var25 > 0) {
               if(var20 < var22) {
                  var20 += var16;
                  var6 += var24;
                  var28 = var26 - var6;
               } else {
                  var22 += var18;
                  var7 += var25;
                  var29 = var27 - var7;
               }

               if(!this.canWalkOn(var6, Mth.floor(var1.y), var7, var3, var4, var5, var1, var8, var10)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private boolean canWalkOn(int var1, int var2, int var3, int var4, int var5, int var6, Vec3 vec3, double var8, double var10) {
      int var12 = var1 - var4 / 2;
      int var13 = var3 - var6 / 2;
      if(!this.canWalkAbove(var12, var2, var13, var4, var5, var6, vec3, var8, var10)) {
         return false;
      } else {
         for(int var14 = var12; var14 < var12 + var4; ++var14) {
            for(int var15 = var13; var15 < var13 + var6; ++var15) {
               double var16 = (double)var14 + 0.5D - vec3.x;
               double var18 = (double)var15 + 0.5D - vec3.z;
               if(var16 * var8 + var18 * var10 >= 0.0D) {
                  BlockPathTypes var20 = this.nodeEvaluator.getBlockPathType(this.level, var14, var2 - 1, var15, this.mob, var4, var5, var6, true, true);
                  if(var20 == BlockPathTypes.WATER) {
                     return false;
                  }

                  if(var20 == BlockPathTypes.LAVA) {
                     return false;
                  }

                  if(var20 == BlockPathTypes.OPEN) {
                     return false;
                  }

                  var20 = this.nodeEvaluator.getBlockPathType(this.level, var14, var2, var15, this.mob, var4, var5, var6, true, true);
                  float var21 = this.mob.getPathfindingMalus(var20);
                  if(var21 < 0.0F || var21 >= 8.0F) {
                     return false;
                  }

                  if(var20 == BlockPathTypes.DAMAGE_FIRE || var20 == BlockPathTypes.DANGER_FIRE || var20 == BlockPathTypes.DAMAGE_OTHER) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean canWalkAbove(int var1, int var2, int var3, int var4, int var5, int var6, Vec3 vec3, double var8, double var10) {
      for(BlockPos var13 : BlockPos.betweenClosed(new BlockPos(var1, var2, var3), new BlockPos(var1 + var4 - 1, var2 + var5 - 1, var3 + var6 - 1))) {
         double var14 = (double)var13.getX() + 0.5D - vec3.x;
         double var16 = (double)var13.getZ() + 0.5D - vec3.z;
         if(var14 * var8 + var16 * var10 >= 0.0D && !this.level.getBlockState(var13).isPathfindable(this.level, var13, PathComputationType.LAND)) {
            return false;
         }
      }

      return true;
   }

   public void setCanOpenDoors(boolean canOpenDoors) {
      this.nodeEvaluator.setCanOpenDoors(canOpenDoors);
   }

   public boolean canOpenDoors() {
      return this.nodeEvaluator.canPassDoors();
   }

   public void setAvoidSun(boolean avoidSun) {
      this.avoidSun = avoidSun;
   }
}
