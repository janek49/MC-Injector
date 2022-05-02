package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieAttackGoal extends MeleeAttackGoal {
   private final Zombie zombie;
   private int raiseArmTicks;

   public ZombieAttackGoal(Zombie zombie, double var2, boolean var4) {
      super(zombie, var2, var4);
      this.zombie = zombie;
   }

   public void start() {
      super.start();
      this.raiseArmTicks = 0;
   }

   public void stop() {
      super.stop();
      this.zombie.setAggressive(false);
   }

   public void tick() {
      super.tick();
      ++this.raiseArmTicks;
      if(this.raiseArmTicks >= 5 && this.attackTime < 10) {
         this.zombie.setAggressive(true);
      } else {
         this.zombie.setAggressive(false);
      }

   }
}
