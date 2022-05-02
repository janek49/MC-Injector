package net.minecraft.client.sounds;

import com.fox2code.repacker.ClientJarOnly;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import net.minecraft.util.thread.BlockableEventLoop;

@ClientJarOnly
public class SoundEngineExecutor extends BlockableEventLoop {
   private Thread thread = this.createThread();
   private volatile boolean shutdown;

   public SoundEngineExecutor() {
      super("Sound executor");
   }

   private Thread createThread() {
      Thread thread = new Thread(this::run);
      thread.setDaemon(true);
      thread.setName("Sound engine");
      thread.start();
      return thread;
   }

   protected Runnable wrapRunnable(Runnable runnable) {
      return runnable;
   }

   protected boolean shouldRun(Runnable runnable) {
      return !this.shutdown;
   }

   protected Thread getRunningThread() {
      return this.thread;
   }

   private void run() {
      while(!this.shutdown) {
         this.managedBlock(() -> {
            return this.shutdown;
         });
      }

   }

   protected void waitForTasks() {
      LockSupport.park("waiting for tasks");
   }

   public void flush() {
      this.shutdown = true;
      this.thread.interrupt();

      try {
         this.thread.join();
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
      }

      this.dropAllTasks();
      this.shutdown = false;
      this.thread = this.createThread();
   }
}
