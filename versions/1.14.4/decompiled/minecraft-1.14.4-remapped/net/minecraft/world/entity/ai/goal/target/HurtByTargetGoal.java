package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

public class HurtByTargetGoal extends TargetGoal {
   private static final TargetingConditions HURT_BY_TARGETING = (new TargetingConditions()).allowUnseeable().ignoreInvisibilityTesting();
   private boolean alertSameType;
   private int timestamp;
   private final Class[] toIgnoreDamage;
   private Class[] toIgnoreAlert;

   public HurtByTargetGoal(PathfinderMob pathfinderMob, Class... toIgnoreDamage) {
      super(pathfinderMob, true);
      this.toIgnoreDamage = toIgnoreDamage;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean canUse() {
      int var1 = this.mob.getLastHurtByMobTimestamp();
      LivingEntity var2 = this.mob.getLastHurtByMob();
      if(var1 != this.timestamp && var2 != null) {
         for(Class<?> var6 : this.toIgnoreDamage) {
            if(var6.isAssignableFrom(var2.getClass())) {
               return false;
            }
         }

         return this.canAttack(var2, HURT_BY_TARGETING);
      } else {
         return false;
      }
   }

   public HurtByTargetGoal setAlertOthers(Class... alertOthers) {
      this.alertSameType = true;
      this.toIgnoreAlert = alertOthers;
      return this;
   }

   public void start() {
      this.mob.setTarget(this.mob.getLastHurtByMob());
      this.targetMob = this.mob.getTarget();
      this.timestamp = this.mob.getLastHurtByMobTimestamp();
      this.unseenMemoryTicks = 300;
      if(this.alertSameType) {
         this.alertOthers();
      }

      super.start();
   }

   protected void alertOthers() {
      double var1 = this.getFollowDistance();
      List<Mob> var3 = this.mob.level.getLoadedEntitiesOfClass(this.mob.getClass(), (new AABB(this.mob.x, this.mob.y, this.mob.z, this.mob.x + 1.0D, this.mob.y + 1.0D, this.mob.z + 1.0D)).inflate(var1, 10.0D, var1));
      Iterator var4 = var3.iterator();

      while(true) {
         Mob var5;
         while(true) {
            if(!var4.hasNext()) {
               return;
            }

            var5 = (Mob)var4.next();
            if(this.mob != var5 && var5.getTarget() == null && (!(this.mob instanceof TamableAnimal) || ((TamableAnimal)this.mob).getOwner() == ((TamableAnimal)var5).getOwner()) && !var5.isAlliedTo(this.mob.getLastHurtByMob())) {
               if(this.toIgnoreAlert == null) {
                  break;
               }

               boolean var6 = false;

               for(Class<?> var10 : this.toIgnoreAlert) {
                  if(var5.getClass() == var10) {
                     var6 = true;
                     break;
                  }
               }

               if(!var6) {
                  break;
               }
            }
         }

         this.alertOther(var5, this.mob.getLastHurtByMob());
      }
   }

   protected void alertOther(Mob mob, LivingEntity livingEntity) {
      mob.setTarget(livingEntity);
   }
}
