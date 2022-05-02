package net.minecraft.world.entity.ai.goal.target;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.raid.Raider;

public class NearestAttackableWitchTargetGoal extends NearestAttackableTargetGoal {
   private boolean canAttack = true;

   public NearestAttackableWitchTargetGoal(Raider raider, Class class, int var3, boolean var4, boolean var5, @Nullable Predicate predicate) {
      super(raider, class, var3, var4, var5, predicate);
   }

   public void setCanAttack(boolean canAttack) {
      this.canAttack = canAttack;
   }

   public boolean canUse() {
      return this.canAttack && super.canUse();
   }
}
