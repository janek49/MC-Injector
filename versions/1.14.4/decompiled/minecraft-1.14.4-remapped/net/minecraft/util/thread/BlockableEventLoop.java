package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.util.thread.ProcessorHandle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockableEventLoop implements ProcessorHandle, Executor {
   private final String name;
   private static final Logger LOGGER = LogManager.getLogger();
   private final Queue pendingRunnables = Queues.newConcurrentLinkedQueue();
   private int blockingCount;

   protected BlockableEventLoop(String name) {
      this.name = name;
   }

   protected abstract Runnable wrapRunnable(Runnable var1);

   protected abstract boolean shouldRun(Runnable var1);

   public boolean isSameThread() {
      return Thread.currentThread() == this.getRunningThread();
   }

   protected abstract Thread getRunningThread();

   protected boolean scheduleExecutables() {
      return !this.isSameThread();
   }

   public int getPendingTasksCount() {
      return this.pendingRunnables.size();
   }

   public String name() {
      return this.name;
   }

   public CompletableFuture submit(Supplier supplier) {
      return this.scheduleExecutables()?CompletableFuture.supplyAsync(supplier, this):CompletableFuture.completedFuture(supplier.get());
   }

   private CompletableFuture submitAsync(Runnable runnable) {
      return CompletableFuture.supplyAsync(() -> {
         runnable.run();
         return null;
      }, this);
   }

   public CompletableFuture submit(Runnable runnable) {
      if(this.scheduleExecutables()) {
         return this.submitAsync(runnable);
      } else {
         runnable.run();
         return CompletableFuture.completedFuture((Object)null);
      }
   }

   public void executeBlocking(Runnable runnable) {
      if(!this.isSameThread()) {
         this.submitAsync(runnable).join();
      } else {
         runnable.run();
      }

   }

   public void tell(Runnable runnable) {
      this.pendingRunnables.add(runnable);
      LockSupport.unpark(this.getRunningThread());
   }

   public void execute(Runnable runnable) {
      if(this.scheduleExecutables()) {
         this.tell(this.wrapRunnable(runnable));
      } else {
         runnable.run();
      }

   }

   protected void dropAllTasks() {
      this.pendingRunnables.clear();
   }

   protected void runAllTasks() {
      while(this.pollTask()) {
         ;
      }

   }

   protected boolean pollTask() {
      R var1 = (Runnable)this.pendingRunnables.peek();
      if(var1 == null) {
         return false;
      } else if(this.blockingCount == 0 && !this.shouldRun(var1)) {
         return false;
      } else {
         this.doRunTask((Runnable)this.pendingRunnables.remove());
         return true;
      }
   }

   public void managedBlock(BooleanSupplier booleanSupplier) {
      ++this.blockingCount;

      try {
         while(!booleanSupplier.getAsBoolean()) {
            if(!this.pollTask()) {
               this.waitForTasks();
            }
         }
      } finally {
         --this.blockingCount;
      }

   }

   protected void waitForTasks() {
      Thread.yield();
      LockSupport.parkNanos("waiting for tasks", 100000L);
   }

   protected void doRunTask(Runnable runnable) {
      try {
         runnable.run();
      } catch (Exception var3) {
         LOGGER.fatal("Error executing task on {}", this.name(), var3);
      }

   }

   // $FF: synthetic method
   public void tell(Object var1) {
      this.tell((Runnable)var1);
   }
}
