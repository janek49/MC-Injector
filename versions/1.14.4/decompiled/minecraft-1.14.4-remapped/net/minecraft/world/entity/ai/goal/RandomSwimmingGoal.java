package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class RandomSwimmingGoal extends RandomStrollGoal {
   public RandomSwimmingGoal(PathfinderMob pathfinderMob, double var2, int var4) {
      super(pathfinderMob, var2, var4);
   }

   @Nullable
   protected Vec3 getPosition() {
      Vec3 vec3 = RandomPos.getPos(this.mob, 10, 7);

      for(int var2 = 0; vec3 != null && !this.mob.level.getBlockState(new BlockPos(vec3)).isPathfindable(this.mob.level, new BlockPos(vec3), PathComputationType.WATER) && var2++ < 10; vec3 = RandomPos.getPos(this.mob, 10, 7)) {
         ;
      }

      return vec3;
   }
}
