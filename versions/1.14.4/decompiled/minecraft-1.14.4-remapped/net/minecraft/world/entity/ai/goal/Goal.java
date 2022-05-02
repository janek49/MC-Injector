package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;

public abstract class Goal {
   private final EnumSet flags = EnumSet.noneOf(Goal.Flag.class);

   public abstract boolean canUse();

   public boolean canContinueToUse() {
      return this.canUse();
   }

   public boolean isInterruptable() {
      return true;
   }

   public void start() {
   }

   public void stop() {
   }

   public void tick() {
   }

   public void setFlags(EnumSet flags) {
      this.flags.clear();
      this.flags.addAll(flags);
   }

   public EnumSet getFlags() {
      return this.flags;
   }

   public static enum Flag {
      MOVE,
      LOOK,
      JUMP,
      TARGET;
   }
}
