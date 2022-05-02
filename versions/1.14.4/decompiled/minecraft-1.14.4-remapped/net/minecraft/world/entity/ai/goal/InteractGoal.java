package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class InteractGoal extends LookAtPlayerGoal {
   public InteractGoal(Mob mob, Class class, float var3, float var4) {
      super(mob, class, var3, var4);
      this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
   }
}
