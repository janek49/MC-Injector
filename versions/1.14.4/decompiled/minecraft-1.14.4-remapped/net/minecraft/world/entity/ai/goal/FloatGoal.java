package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class FloatGoal extends Goal {
   private final Mob mob;

   public FloatGoal(Mob mob) {
      this.mob = mob;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP));
      mob.getNavigation().setCanFloat(true);
   }

   public boolean canUse() {
      double var1 = (double)this.mob.getEyeHeight() < 0.4D?0.2D:0.4D;
      return this.mob.isInWater() && this.mob.getWaterHeight() > var1 || this.mob.isInLava();
   }

   public void tick() {
      if(this.mob.getRandom().nextFloat() < 0.8F) {
         this.mob.getJumpControl().jump();
      }

   }
}
