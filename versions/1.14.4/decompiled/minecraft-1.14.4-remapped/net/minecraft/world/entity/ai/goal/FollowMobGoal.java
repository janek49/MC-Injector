package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class FollowMobGoal extends Goal {
   private final Mob mob;
   private final Predicate followPredicate;
   private Mob followingMob;
   private final double speedModifier;
   private final PathNavigation navigation;
   private int timeToRecalcPath;
   private final float stopDistance;
   private float oldWaterCost;
   private final float areaSize;

   public FollowMobGoal(Mob mob, double speedModifier, float stopDistance, float areaSize) {
      this.mob = mob;
      this.followPredicate = (var1) -> {
         return var1 != null && mob.getClass() != var1.getClass();
      };
      this.speedModifier = speedModifier;
      this.navigation = mob.getNavigation();
      this.stopDistance = stopDistance;
      this.areaSize = areaSize;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if(!(mob.getNavigation() instanceof GroundPathNavigation) && !(mob.getNavigation() instanceof FlyingPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
      }
   }

   public boolean canUse() {
      List<Mob> var1 = this.mob.level.getEntitiesOfClass(Mob.class, this.mob.getBoundingBox().inflate((double)this.areaSize), this.followPredicate);
      if(!var1.isEmpty()) {
         for(Mob var3 : var1) {
            if(!var3.isInvisible()) {
               this.followingMob = var3;
               return true;
            }
         }
      }

      return false;
   }

   public boolean canContinueToUse() {
      return this.followingMob != null && !this.navigation.isDone() && this.mob.distanceToSqr(this.followingMob) > (double)(this.stopDistance * this.stopDistance);
   }

   public void start() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.mob.getPathfindingMalus(BlockPathTypes.WATER);
      this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
   }

   public void stop() {
      this.followingMob = null;
      this.navigation.stop();
      this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
   }

   public void tick() {
      if(this.followingMob != null && !this.mob.isLeashed()) {
         this.mob.getLookControl().setLookAt(this.followingMob, 10.0F, (float)this.mob.getMaxHeadXRot());
         if(--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            double var1 = this.mob.x - this.followingMob.x;
            double var3 = this.mob.y - this.followingMob.y;
            double var5 = this.mob.z - this.followingMob.z;
            double var7 = var1 * var1 + var3 * var3 + var5 * var5;
            if(var7 > (double)(this.stopDistance * this.stopDistance)) {
               this.navigation.moveTo((Entity)this.followingMob, this.speedModifier);
            } else {
               this.navigation.stop();
               LookControl var9 = this.followingMob.getLookControl();
               if(var7 <= (double)this.stopDistance || var9.getWantedX() == this.mob.x && var9.getWantedY() == this.mob.y && var9.getWantedZ() == this.mob.z) {
                  double var10 = this.followingMob.x - this.mob.x;
                  double var12 = this.followingMob.z - this.mob.z;
                  this.navigation.moveTo(this.mob.x - var10, this.mob.y, this.mob.z - var12, this.speedModifier);
               }

            }
         }
      }
   }
}
