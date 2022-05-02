package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class DefendVillageTargetGoal extends TargetGoal {
   private final IronGolem golem;
   private LivingEntity potentialTarget;
   private final TargetingConditions attackTargeting = (new TargetingConditions()).range(64.0D);

   public DefendVillageTargetGoal(IronGolem golem) {
      super(golem, false, true);
      this.golem = golem;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean canUse() {
      AABB var1 = this.golem.getBoundingBox().inflate(10.0D, 8.0D, 10.0D);
      List<LivingEntity> var2 = this.golem.level.getNearbyEntities(Villager.class, this.attackTargeting, this.golem, var1);
      List<Player> var3 = this.golem.level.getNearbyPlayers(this.attackTargeting, this.golem, var1);

      for(LivingEntity var5 : var2) {
         Villager var6 = (Villager)var5;

         for(Player var8 : var3) {
            int var9 = var6.getPlayerReputation(var8);
            if(var9 <= -100) {
               this.potentialTarget = var8;
            }
         }
      }

      return this.potentialTarget != null;
   }

   public void start() {
      this.golem.setTarget(this.potentialTarget);
      super.start();
   }
}
