package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.goal.Goal;

public class WrappedGoal extends Goal {
   private final Goal goal;
   private final int priority;
   private boolean isRunning;

   public WrappedGoal(int priority, Goal goal) {
      this.priority = priority;
      this.goal = goal;
   }

   public boolean canBeReplacedBy(WrappedGoal wrappedGoal) {
      return this.isInterruptable() && wrappedGoal.getPriority() < this.getPriority();
   }

   public boolean canUse() {
      return this.goal.canUse();
   }

   public boolean canContinueToUse() {
      return this.goal.canContinueToUse();
   }

   public boolean isInterruptable() {
      return this.goal.isInterruptable();
   }

   public void start() {
      if(!this.isRunning) {
         this.isRunning = true;
         this.goal.start();
      }
   }

   public void stop() {
      if(this.isRunning) {
         this.isRunning = false;
         this.goal.stop();
      }
   }

   public void tick() {
      this.goal.tick();
   }

   public void setFlags(EnumSet flags) {
      this.goal.setFlags(flags);
   }

   public EnumSet getFlags() {
      return this.goal.getFlags();
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public int getPriority() {
      return this.priority;
   }

   public Goal getGoal() {
      return this.goal;
   }

   public boolean equals(@Nullable Object object) {
      return this == object?true:(object != null && this.getClass() == object.getClass()?this.goal.equals(((WrappedGoal)object).goal):false);
   }

   public int hashCode() {
      return this.goal.hashCode();
   }
}
