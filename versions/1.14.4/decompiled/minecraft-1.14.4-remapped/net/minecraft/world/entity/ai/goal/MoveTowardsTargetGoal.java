package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class MoveTowardsTargetGoal extends Goal {
   private final PathfinderMob mob;
   private LivingEntity target;
   private double wantedX;
   private double wantedY;
   private double wantedZ;
   private final double speedModifier;
   private final float within;

   public MoveTowardsTargetGoal(PathfinderMob mob, double speedModifier, float within) {
      this.mob = mob;
      this.speedModifier = speedModifier;
      this.within = within;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      this.target = this.mob.getTarget();
      if(this.target == null) {
         return false;
      } else if(this.target.distanceToSqr(this.mob) > (double)(this.within * this.within)) {
         return false;
      } else {
         Vec3 var1 = RandomPos.getPosTowards(this.mob, 16, 7, new Vec3(this.target.x, this.target.y, this.target.z));
         if(var1 == null) {
            return false;
         } else {
            this.wantedX = var1.x;
            this.wantedY = var1.y;
            this.wantedZ = var1.z;
            return true;
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone() && this.target.isAlive() && this.target.distanceToSqr(this.mob) < (double)(this.within * this.within);
   }

   public void stop() {
      this.target = null;
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
   }
}
