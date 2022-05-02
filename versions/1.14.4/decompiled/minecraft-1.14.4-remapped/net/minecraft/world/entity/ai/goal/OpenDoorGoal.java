package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;

public class OpenDoorGoal extends DoorInteractGoal {
   private final boolean closeDoor;
   private int forgetTime;

   public OpenDoorGoal(Mob mob, boolean closeDoor) {
      super(mob);
      this.mob = mob;
      this.closeDoor = closeDoor;
   }

   public boolean canContinueToUse() {
      return this.closeDoor && this.forgetTime > 0 && super.canContinueToUse();
   }

   public void start() {
      this.forgetTime = 20;
      this.setOpen(true);
   }

   public void stop() {
      this.setOpen(false);
   }

   public void tick() {
      --this.forgetTime;
      super.tick();
   }
}
