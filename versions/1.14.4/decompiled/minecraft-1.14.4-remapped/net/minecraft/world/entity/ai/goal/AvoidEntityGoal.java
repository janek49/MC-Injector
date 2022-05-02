package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class AvoidEntityGoal extends Goal {
   protected final PathfinderMob mob;
   private final double walkSpeedModifier;
   private final double sprintSpeedModifier;
   protected LivingEntity toAvoid;
   protected final float maxDist;
   protected Path path;
   protected final PathNavigation pathNav;
   protected final Class avoidClass;
   protected final Predicate avoidPredicate;
   protected final Predicate predicateOnAvoidEntity;
   private final TargetingConditions avoidEntityTargeting;

   public AvoidEntityGoal(PathfinderMob pathfinderMob, Class class, float var3, double var4, double var6) {
      Predicate var10003 = (livingEntity) -> {
         return true;
      };
      Predicate var10007 = EntitySelector.NO_CREATIVE_OR_SPECTATOR;
      EntitySelector.NO_CREATIVE_OR_SPECTATOR.getClass();
      this(pathfinderMob, class, var10003, var3, var4, var6, var10007::test);
   }

   public AvoidEntityGoal(PathfinderMob mob, Class avoidClass, Predicate avoidPredicate, float maxDist, double walkSpeedModifier, double sprintSpeedModifier, Predicate predicateOnAvoidEntity) {
      this.mob = mob;
      this.avoidClass = avoidClass;
      this.avoidPredicate = avoidPredicate;
      this.maxDist = maxDist;
      this.walkSpeedModifier = walkSpeedModifier;
      this.sprintSpeedModifier = sprintSpeedModifier;
      this.predicateOnAvoidEntity = predicateOnAvoidEntity;
      this.pathNav = mob.getNavigation();
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      this.avoidEntityTargeting = (new TargetingConditions()).range((double)maxDist).selector(predicateOnAvoidEntity.and(avoidPredicate));
   }

   public AvoidEntityGoal(PathfinderMob pathfinderMob, Class class, float var3, double var4, double var6, Predicate predicate) {
      this(pathfinderMob, class, (livingEntity) -> {
         return true;
      }, var3, var4, var6, predicate);
   }

   public boolean canUse() {
      this.toAvoid = this.mob.level.getNearestLoadedEntity(this.avoidClass, this.avoidEntityTargeting, this.mob, this.mob.x, this.mob.y, this.mob.z, this.mob.getBoundingBox().inflate((double)this.maxDist, 3.0D, (double)this.maxDist));
      if(this.toAvoid == null) {
         return false;
      } else {
         Vec3 var1 = RandomPos.getPosAvoid(this.mob, 16, 7, new Vec3(this.toAvoid.x, this.toAvoid.y, this.toAvoid.z));
         if(var1 == null) {
            return false;
         } else if(this.toAvoid.distanceToSqr(var1.x, var1.y, var1.z) < this.toAvoid.distanceToSqr(this.mob)) {
            return false;
         } else {
            this.path = this.pathNav.createPath(var1.x, var1.y, var1.z, 0);
            return this.path != null;
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.pathNav.isDone();
   }

   public void start() {
      this.pathNav.moveTo(this.path, this.walkSpeedModifier);
   }

   public void stop() {
      this.toAvoid = null;
   }

   public void tick() {
      if(this.mob.distanceToSqr(this.toAvoid) < 49.0D) {
         this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
      } else {
         this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
      }

   }
}
