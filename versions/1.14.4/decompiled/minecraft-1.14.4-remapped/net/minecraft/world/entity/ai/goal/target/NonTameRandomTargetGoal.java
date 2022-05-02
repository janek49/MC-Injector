package net.minecraft.world.entity.ai.goal.target;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class NonTameRandomTargetGoal extends NearestAttackableTargetGoal {
   private final TamableAnimal tamableMob;

   public NonTameRandomTargetGoal(TamableAnimal tamableMob, Class class, boolean var3, @Nullable Predicate predicate) {
      super(tamableMob, class, 10, var3, false, predicate);
      this.tamableMob = tamableMob;
   }

   public boolean canUse() {
      return !this.tamableMob.isTame() && super.canUse();
   }

   public boolean canContinueToUse() {
      return this.targetConditions != null?this.targetConditions.test(this.mob, this.target):super.canContinueToUse();
   }
}
