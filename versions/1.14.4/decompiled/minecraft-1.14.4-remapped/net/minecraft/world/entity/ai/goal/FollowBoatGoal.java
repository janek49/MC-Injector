package net.minecraft.world.entity.ai.goal;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.BoatGoals;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public class FollowBoatGoal extends Goal {
   private int timeToRecalcPath;
   private final PathfinderMob mob;
   private LivingEntity following;
   private BoatGoals currentGoal;

   public FollowBoatGoal(PathfinderMob mob) {
      this.mob = mob;
   }

   public boolean canUse() {
      List<Boat> var1 = this.mob.level.getEntitiesOfClass(Boat.class, this.mob.getBoundingBox().inflate(5.0D));
      boolean var2 = false;

      for(Boat var4 : var1) {
         if(var4.getControllingPassenger() != null && (Mth.abs(((LivingEntity)var4.getControllingPassenger()).xxa) > 0.0F || Mth.abs(((LivingEntity)var4.getControllingPassenger()).zza) > 0.0F)) {
            var2 = true;
            break;
         }
      }

      return this.following != null && (Mth.abs(this.following.xxa) > 0.0F || Mth.abs(this.following.zza) > 0.0F) || var2;
   }

   public boolean isInterruptable() {
      return true;
   }

   public boolean canContinueToUse() {
      return this.following != null && this.following.isPassenger() && (Mth.abs(this.following.xxa) > 0.0F || Mth.abs(this.following.zza) > 0.0F);
   }

   public void start() {
      for(Boat var3 : this.mob.level.getEntitiesOfClass(Boat.class, this.mob.getBoundingBox().inflate(5.0D))) {
         if(var3.getControllingPassenger() != null && var3.getControllingPassenger() instanceof LivingEntity) {
            this.following = (LivingEntity)var3.getControllingPassenger();
            break;
         }
      }

      this.timeToRecalcPath = 0;
      this.currentGoal = BoatGoals.GO_TO_BOAT;
   }

   public void stop() {
      this.following = null;
   }

   public void tick() {
      boolean var1 = Mth.abs(this.following.xxa) > 0.0F || Mth.abs(this.following.zza) > 0.0F;
      float var2 = this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION?(var1?0.17999999F:0.0F):0.135F;
      this.mob.moveRelative(var2, new Vec3((double)this.mob.xxa, (double)this.mob.yya, (double)this.mob.zza));
      this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
      if(--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = 10;
         if(this.currentGoal == BoatGoals.GO_TO_BOAT) {
            BlockPos var3 = (new BlockPos(this.following)).relative(this.following.getDirection().getOpposite());
            var3 = var3.offset(0, -1, 0);
            this.mob.getNavigation().moveTo((double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), 1.0D);
            if(this.mob.distanceTo(this.following) < 4.0F) {
               this.timeToRecalcPath = 0;
               this.currentGoal = BoatGoals.GO_IN_BOAT_DIRECTION;
            }
         } else if(this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION) {
            Direction var3 = this.following.getMotionDirection();
            BlockPos var4 = (new BlockPos(this.following)).relative(var3, 10);
            this.mob.getNavigation().moveTo((double)var4.getX(), (double)(var4.getY() - 1), (double)var4.getZ(), 1.0D);
            if(this.mob.distanceTo(this.following) > 12.0F) {
               this.timeToRecalcPath = 0;
               this.currentGoal = BoatGoals.GO_TO_BOAT;
            }
         }

      }
   }
}
