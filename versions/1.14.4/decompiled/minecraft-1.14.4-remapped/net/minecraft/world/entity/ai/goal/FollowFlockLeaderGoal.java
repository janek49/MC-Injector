package net.minecraft.world.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;

public class FollowFlockLeaderGoal extends Goal {
   private final AbstractSchoolingFish mob;
   private int timeToRecalcPath;
   private int nextStartTick;

   public FollowFlockLeaderGoal(AbstractSchoolingFish mob) {
      this.mob = mob;
      this.nextStartTick = this.nextStartTick(mob);
   }

   protected int nextStartTick(AbstractSchoolingFish abstractSchoolingFish) {
      return 200 + abstractSchoolingFish.getRandom().nextInt(200) % 20;
   }

   public boolean canUse() {
      if(this.mob.hasFollowers()) {
         return false;
      } else if(this.mob.isFollower()) {
         return true;
      } else if(this.nextStartTick > 0) {
         --this.nextStartTick;
         return false;
      } else {
         this.nextStartTick = this.nextStartTick(this.mob);
         Predicate<AbstractSchoolingFish> var1 = (abstractSchoolingFish) -> {
            return abstractSchoolingFish.canBeFollowed() || !abstractSchoolingFish.isFollower();
         };
         List<AbstractSchoolingFish> var2 = this.mob.level.getEntitiesOfClass(this.mob.getClass(), this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), var1);
         AbstractSchoolingFish var3 = (AbstractSchoolingFish)var2.stream().filter(AbstractSchoolingFish::canBeFollowed).findAny().orElse(this.mob);
         var3.addFollowers(var2.stream().filter((abstractSchoolingFish) -> {
            return !abstractSchoolingFish.isFollower();
         }));
         return this.mob.isFollower();
      }
   }

   public boolean canContinueToUse() {
      return this.mob.isFollower() && this.mob.inRangeOfLeader();
   }

   public void start() {
      this.timeToRecalcPath = 0;
   }

   public void stop() {
      this.mob.stopFollowing();
   }

   public void tick() {
      if(--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = 10;
         this.mob.pathToLeader();
      }
   }
}
