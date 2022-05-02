package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;

public class TradeWithPlayerGoal extends Goal {
   private final AbstractVillager mob;

   public TradeWithPlayerGoal(AbstractVillager mob) {
      this.mob = mob;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if(!this.mob.isAlive()) {
         return false;
      } else if(this.mob.isInWater()) {
         return false;
      } else if(!this.mob.onGround) {
         return false;
      } else if(this.mob.hurtMarked) {
         return false;
      } else {
         Player var1 = this.mob.getTradingPlayer();
         return var1 == null?false:(this.mob.distanceToSqr(var1) > 16.0D?false:var1.containerMenu != null);
      }
   }

   public void start() {
      this.mob.getNavigation().stop();
   }

   public void stop() {
      this.mob.setTradingPlayer((Player)null);
   }
}
