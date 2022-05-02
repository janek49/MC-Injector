package net.minecraft.world.entity.ai.goal.target;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.scores.Team;

public abstract class TargetGoal extends Goal {
   protected final Mob mob;
   protected final boolean mustSee;
   private final boolean mustReach;
   private int reachCache;
   private int reachCacheTime;
   private int unseenTicks;
   protected LivingEntity targetMob;
   protected int unseenMemoryTicks;

   public TargetGoal(Mob mob, boolean var2) {
      this(mob, var2, false);
   }

   public TargetGoal(Mob mob, boolean mustSee, boolean mustReach) {
      this.unseenMemoryTicks = 60;
      this.mob = mob;
      this.mustSee = mustSee;
      this.mustReach = mustReach;
   }

   public boolean canContinueToUse() {
      LivingEntity var1 = this.mob.getTarget();
      if(var1 == null) {
         var1 = this.targetMob;
      }

      if(var1 == null) {
         return false;
      } else if(!var1.isAlive()) {
         return false;
      } else {
         Team var2 = this.mob.getTeam();
         Team var3 = var1.getTeam();
         if(var2 != null && var3 == var2) {
            return false;
         } else {
            double var4 = this.getFollowDistance();
            if(this.mob.distanceToSqr(var1) > var4 * var4) {
               return false;
            } else {
               if(this.mustSee) {
                  if(this.mob.getSensing().canSee(var1)) {
                     this.unseenTicks = 0;
                  } else if(++this.unseenTicks > this.unseenMemoryTicks) {
                     return false;
                  }
               }

               if(var1 instanceof Player && ((Player)var1).abilities.invulnerable) {
                  return false;
               } else {
                  this.mob.setTarget(var1);
                  return true;
               }
            }
         }
      }
   }

   protected double getFollowDistance() {
      AttributeInstance var1 = this.mob.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
      return var1 == null?16.0D:var1.getValue();
   }

   public void start() {
      this.reachCache = 0;
      this.reachCacheTime = 0;
      this.unseenTicks = 0;
   }

   public void stop() {
      this.mob.setTarget((LivingEntity)null);
      this.targetMob = null;
   }

   protected boolean canAttack(@Nullable LivingEntity livingEntity, TargetingConditions targetingConditions) {
      if(livingEntity == null) {
         return false;
      } else if(!targetingConditions.test(this.mob, livingEntity)) {
         return false;
      } else if(!this.mob.isWithinRestriction(new BlockPos(livingEntity))) {
         return false;
      } else {
         if(this.mustReach) {
            if(--this.reachCacheTime <= 0) {
               this.reachCache = 0;
            }

            if(this.reachCache == 0) {
               this.reachCache = this.canReach(livingEntity)?1:2;
            }

            if(this.reachCache == 2) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean canReach(LivingEntity livingEntity) {
      this.reachCacheTime = 10 + this.mob.getRandom().nextInt(5);
      Path var2 = this.mob.getNavigation().createPath((Entity)livingEntity, 0);
      if(var2 == null) {
         return false;
      } else {
         Node var3 = var2.last();
         if(var3 == null) {
            return false;
         } else {
            int var4 = var3.x - Mth.floor(livingEntity.x);
            int var5 = var3.z - Mth.floor(livingEntity.z);
            return (double)(var4 * var4 + var5 * var5) <= 2.25D;
         }
      }
   }

   public TargetGoal setUnseenMemoryTicks(int unseenMemoryTicks) {
      this.unseenMemoryTicks = unseenMemoryTicks;
      return this;
   }
}
