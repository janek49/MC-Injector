package net.minecraft.util.thread;

import net.minecraft.util.thread.BlockableEventLoop;

public abstract class ReentrantBlockableEventLoop extends BlockableEventLoop {
   private int reentrantCount;

   public ReentrantBlockableEventLoop(String string) {
      super(string);
   }

   protected boolean scheduleExecutables() {
      return this.runningTask() || super.scheduleExecutables();
   }

   protected boolean runningTask() {
      return this.reentrantCount != 0;
   }

   protected void doRunTask(Runnable runnable) {
      ++this.reentrantCount;

      try {
         super.doRunTask(runnable);
      } finally {
         --this.reentrantCount;
      }

   }
}
