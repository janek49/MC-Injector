package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class WaterAvoidingRandomStrollGoal extends RandomStrollGoal {
   protected final float probability;

   public WaterAvoidingRandomStrollGoal(PathfinderMob pathfinderMob, double var2) {
      this(pathfinderMob, var2, 0.001F);
   }

   public WaterAvoidingRandomStrollGoal(PathfinderMob pathfinderMob, double var2, float probability) {
      super(pathfinderMob, var2);
      this.probability = probability;
   }

   @Nullable
   protected Vec3 getPosition() {
      if(this.mob.isInWaterOrBubble()) {
         Vec3 vec3 = RandomPos.getLandPos(this.mob, 15, 7);
         return vec3 == null?super.getPosition():vec3;
      } else {
         return this.mob.getRandom().nextFloat() >= this.probability?RandomPos.getLandPos(this.mob, 10, 7):super.getPosition();
      }
   }
}
