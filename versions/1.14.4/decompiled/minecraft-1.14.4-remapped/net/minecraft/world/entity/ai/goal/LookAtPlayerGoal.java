package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class LookAtPlayerGoal extends Goal {
   protected final Mob mob;
   protected Entity lookAt;
   protected final float lookDistance;
   private int lookTime;
   private final float probability;
   protected final Class lookAtType;
   protected final TargetingConditions lookAtContext;

   public LookAtPlayerGoal(Mob mob, Class class, float var3) {
      this(mob, class, var3, 0.02F);
   }

   public LookAtPlayerGoal(Mob mob, Class lookAtType, float lookDistance, float probability) {
      this.mob = mob;
      this.lookAtType = lookAtType;
      this.lookDistance = lookDistance;
      this.probability = probability;
      this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      if(lookAtType == Player.class) {
         this.lookAtContext = (new TargetingConditions()).range((double)lookDistance).allowSameTeam().allowInvulnerable().allowNonAttackable().selector((livingEntity) -> {
            return EntitySelector.notRiding(mob).test(livingEntity);
         });
      } else {
         this.lookAtContext = (new TargetingConditions()).range((double)lookDistance).allowSameTeam().allowInvulnerable().allowNonAttackable();
      }

   }

   public boolean canUse() {
      if(this.mob.getRandom().nextFloat() >= this.probability) {
         return false;
      } else {
         if(this.mob.getTarget() != null) {
            this.lookAt = this.mob.getTarget();
         }

         if(this.lookAtType == Player.class) {
            this.lookAt = this.mob.level.getNearestPlayer(this.lookAtContext, this.mob, this.mob.x, this.mob.y + (double)this.mob.getEyeHeight(), this.mob.z);
         } else {
            this.lookAt = this.mob.level.getNearestLoadedEntity(this.lookAtType, this.lookAtContext, this.mob, this.mob.x, this.mob.y + (double)this.mob.getEyeHeight(), this.mob.z, this.mob.getBoundingBox().inflate((double)this.lookDistance, 3.0D, (double)this.lookDistance));
         }

         return this.lookAt != null;
      }
   }

   public boolean canContinueToUse() {
      return !this.lookAt.isAlive()?false:(this.mob.distanceToSqr(this.lookAt) > (double)(this.lookDistance * this.lookDistance)?false:this.lookTime > 0);
   }

   public void start() {
      this.lookTime = 40 + this.mob.getRandom().nextInt(40);
   }

   public void stop() {
      this.lookAt = null;
   }

   public void tick() {
      this.mob.getLookControl().setLookAt(this.lookAt.x, this.lookAt.y + (double)this.lookAt.getEyeHeight(), this.lookAt.z);
      --this.lookTime;
   }
}
