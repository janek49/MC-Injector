package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class FlyingPathNavigation extends PathNavigation {
   public FlyingPathNavigation(Mob mob, Level level) {
      super(mob, level);
   }

   protected PathFinder createPathFinder(int i) {
      this.nodeEvaluator = new FlyNodeEvaluator();
      this.nodeEvaluator.setCanPassDoors(true);
      return new PathFinder(this.nodeEvaluator, i);
   }

   protected boolean canUpdatePath() {
      return this.canFloat() && this.isInLiquid() || !this.mob.isPassenger();
   }

   protected Vec3 getTempMobPos() {
      return new Vec3(this.mob.x, this.mob.y, this.mob.z);
   }

   public Path createPath(Entity entity, int var2) {
      return this.createPath(new BlockPos(entity), var2);
   }

   public void tick() {
      ++this.tick;
      if(this.hasDelayedRecomputation) {
         this.recomputePath();
      }

      if(!this.isDone()) {
         if(this.canUpdatePath()) {
            this.updatePath();
         } else if(this.path != null && this.path.getIndex() < this.path.getSize()) {
            Vec3 var1 = this.path.getPos(this.mob, this.path.getIndex());
            if(Mth.floor(this.mob.x) == Mth.floor(var1.x) && Mth.floor(this.mob.y) == Mth.floor(var1.y) && Mth.floor(this.mob.z) == Mth.floor(var1.z)) {
               this.path.setIndex(this.path.getIndex() + 1);
            }
         }

         DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
         if(!this.isDone()) {
            Vec3 var1 = this.path.currentPos(this.mob);
            this.mob.getMoveControl().setWantedPosition(var1.x, var1.y, var1.z, this.speedModifier);
         }
      }
   }

   protected boolean canMoveDirectly(Vec3 var1, Vec3 var2, int var3, int var4, int var5) {
      int var6 = Mth.floor(var1.x);
      int var7 = Mth.floor(var1.y);
      int var8 = Mth.floor(var1.z);
      double var9 = var2.x - var1.x;
      double var11 = var2.y - var1.y;
      double var13 = var2.z - var1.z;
      double var15 = var9 * var9 + var11 * var11 + var13 * var13;
      if(var15 < 1.0E-8D) {
         return false;
      } else {
         double var17 = 1.0D / Math.sqrt(var15);
         var9 = var9 * var17;
         var11 = var11 * var17;
         var13 = var13 * var17;
         double var19 = 1.0D / Math.abs(var9);
         double var21 = 1.0D / Math.abs(var11);
         double var23 = 1.0D / Math.abs(var13);
         double var25 = (double)var6 - var1.x;
         double var27 = (double)var7 - var1.y;
         double var29 = (double)var8 - var1.z;
         if(var9 >= 0.0D) {
            ++var25;
         }

         if(var11 >= 0.0D) {
            ++var27;
         }

         if(var13 >= 0.0D) {
            ++var29;
         }

         var25 = var25 / var9;
         var27 = var27 / var11;
         var29 = var29 / var13;
         int var31 = var9 < 0.0D?-1:1;
         int var32 = var11 < 0.0D?-1:1;
         int var33 = var13 < 0.0D?-1:1;
         int var34 = Mth.floor(var2.x);
         int var35 = Mth.floor(var2.y);
         int var36 = Mth.floor(var2.z);
         int var37 = var34 - var6;
         int var38 = var35 - var7;
         int var39 = var36 - var8;

         while(var37 * var31 > 0 || var38 * var32 > 0 || var39 * var33 > 0) {
            if(var25 < var29 && var25 <= var27) {
               var25 += var19;
               var6 += var31;
               var37 = var34 - var6;
            } else if(var27 < var25 && var27 <= var29) {
               var27 += var21;
               var7 += var32;
               var38 = var35 - var7;
            } else {
               var29 += var23;
               var8 += var33;
               var39 = var36 - var8;
            }
         }

         return true;
      }
   }

   public void setCanOpenDoors(boolean canOpenDoors) {
      this.nodeEvaluator.setCanOpenDoors(canOpenDoors);
   }

   public void setCanPassDoors(boolean canPassDoors) {
      this.nodeEvaluator.setCanPassDoors(canPassDoors);
   }

   public boolean isStableDestination(BlockPos blockPos) {
      return this.level.getBlockState(blockPos).entityCanStandOn(this.level, blockPos, this.mob);
   }
}
