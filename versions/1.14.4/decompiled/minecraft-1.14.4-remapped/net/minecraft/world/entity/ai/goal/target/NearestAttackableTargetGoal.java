package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class NearestAttackableTargetGoal extends TargetGoal {
   protected final Class targetType;
   protected final int randomInterval;
   protected LivingEntity target;
   protected TargetingConditions targetConditions;

   public NearestAttackableTargetGoal(Mob mob, Class class, boolean var3) {
      this(mob, class, var3, false);
   }

   public NearestAttackableTargetGoal(Mob mob, Class class, boolean var3, boolean var4) {
      this(mob, class, 10, var3, var4, (Predicate)null);
   }

   public NearestAttackableTargetGoal(Mob mob, Class targetType, int randomInterval, boolean var4, boolean var5, @Nullable Predicate predicate) {
      super(mob, var4, var5);
      this.targetType = targetType;
      this.randomInterval = randomInterval;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
      this.targetConditions = (new TargetingConditions()).range(this.getFollowDistance()).selector(predicate);
   }

   public boolean canUse() {
      if(this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
         return false;
      } else {
         this.findTarget();
         return this.target != null;
      }
   }

   protected AABB getTargetSearchArea(double d) {
      return this.mob.getBoundingBox().inflate(d, 4.0D, d);
   }

   protected void findTarget() {
      if(this.targetType != Player.class && this.targetType != ServerPlayer.class) {
         this.target = this.mob.level.getNearestLoadedEntity(this.targetType, this.targetConditions, this.mob, this.mob.x, this.mob.y + (double)this.mob.getEyeHeight(), this.mob.z, this.getTargetSearchArea(this.getFollowDistance()));
      } else {
         this.target = this.mob.level.getNearestPlayer(this.targetConditions, this.mob, this.mob.x, this.mob.y + (double)this.mob.getEyeHeight(), this.mob.z);
      }

   }

   public void start() {
      this.mob.setTarget(this.target);
      super.start();
   }
}
