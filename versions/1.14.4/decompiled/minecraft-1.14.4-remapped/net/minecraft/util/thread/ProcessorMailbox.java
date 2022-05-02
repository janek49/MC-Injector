package net.minecraft.util.thread;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.StrictQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessorMailbox implements ProcessorHandle, AutoCloseable, Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final AtomicInteger status = new AtomicInteger(0);
   public final StrictQueue queue;
   private final Executor dispatcher;
   private final String name;

   public static ProcessorMailbox create(Executor executor, String string) {
      return new ProcessorMailbox(new StrictQueue.QueueStrictQueue(new ConcurrentLinkedQueue()), executor, string);
   }

   public ProcessorMailbox(StrictQueue queue, Executor dispatcher, String name) {
      this.dispatcher = dispatcher;
      this.queue = queue;
      this.name = name;
   }

   private boolean setAsScheduled() {
      while(true) {
         int var1 = this.status.get();
         if((var1 & 3) != 0) {
            return false;
         }

         if(this.status.compareAndSet(var1, var1 | 2)) {
            break;
         }
      }

      return true;
   }

   private void setAsIdle() {
      while(true) {
         int var1 = this.status.get();
         if(this.status.compareAndSet(var1, var1 & -3)) {
            break;
         }
      }

   }

   private boolean canBeScheduled() {
      return (this.status.get() & 1) != 0?false:!this.queue.isEmpty();
   }

   public void close() {
      while(true) {
         int var1 = this.status.get();
         if(this.status.compareAndSet(var1, var1 | 1)) {
            break;
         }
      }

   }

   private boolean shouldProcess() {
      return (this.status.get() & 2) != 0;
   }

   private boolean pollTask() {
      if(!this.shouldProcess()) {
         return false;
      } else {
         Runnable var1 = (Runnable)this.queue.pop();
         if(var1 == null) {
            return false;
         } else {
            var1.run();
            return true;
         }
      }
   }

   public void run() {
      try {
         this.pollUntil((i) -> {
            return i == 0;
         });
      } finally {
         this.setAsIdle();
         this.registerForExecution();
      }

   }

   public void tell(Object object) {
      this.queue.push(object);
      this.registerForExecution();
   }

   private void registerForExecution() {
      if(this.canBeScheduled() && this.setAsScheduled()) {
         try {
            this.dispatcher.execute(this);
         } catch (RejectedExecutionException var4) {
            try {
               this.dispatcher.execute(this);
            } catch (RejectedExecutionException var3) {
               LOGGER.error("Cound not schedule mailbox", var3);
            }
         }
      }

   }

   private int pollUntil(Int2BooleanFunction int2BooleanFunction) {
      int var2;
      for(var2 = 0; int2BooleanFunction.get(var2) && this.pollTask(); ++var2) {
         ;
      }

      return var2;
   }

   public String toString() {
      return this.name + " " + this.status.get() + " " + this.queue.isEmpty();
   }

   public String name() {
      return this.name;
   }
}
