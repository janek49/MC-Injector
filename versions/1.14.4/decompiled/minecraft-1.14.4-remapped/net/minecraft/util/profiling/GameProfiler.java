package net.minecraft.util.profiling;

import java.time.Duration;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameProfiler implements ProfilerFiller {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final long MAXIMUM_TICK_TIME_NANOS = Duration.ofMillis(300L).toNanos();
   private final IntSupplier getTickTime;
   private final GameProfiler.ProfilerImpl continuous = new GameProfiler.ProfilerImpl();
   private final GameProfiler.ProfilerImpl perTick = new GameProfiler.ProfilerImpl();

   public GameProfiler(IntSupplier getTickTime) {
      this.getTickTime = getTickTime;
   }

   public GameProfiler.Profiler continuous() {
      return this.continuous;
   }

   public void startTick() {
      this.continuous.collector.startTick();
      this.perTick.collector.startTick();
   }

   public void endTick() {
      this.continuous.collector.endTick();
      this.perTick.collector.endTick();
   }

   public void push(String string) {
      this.continuous.collector.push(string);
      this.perTick.collector.push(string);
   }

   public void push(Supplier supplier) {
      this.continuous.collector.push(supplier);
      this.perTick.collector.push(supplier);
   }

   public void pop() {
      this.continuous.collector.pop();
      this.perTick.collector.pop();
   }

   public void popPush(String string) {
      this.continuous.collector.popPush(string);
      this.perTick.collector.popPush(string);
   }

   public void popPush(Supplier supplier) {
      this.continuous.collector.popPush(supplier);
      this.perTick.collector.popPush(supplier);
   }

   public interface Profiler {
      boolean isEnabled();

      ProfileResults disable();

      ProfileResults getResults();

      void enable();
   }

   class ProfilerImpl implements GameProfiler.Profiler {
      protected ProfileCollector collector;

      private ProfilerImpl() {
         this.collector = InactiveProfiler.INACTIVE;
      }

      public boolean isEnabled() {
         return this.collector != InactiveProfiler.INACTIVE;
      }

      public ProfileResults disable() {
         ProfileResults profileResults = this.collector.getResults();
         this.collector = InactiveProfiler.INACTIVE;
         return profileResults;
      }

      public ProfileResults getResults() {
         return this.collector.getResults();
      }

      public void enable() {
         if(this.collector == InactiveProfiler.INACTIVE) {
            this.collector = new ActiveProfiler(Util.getNanos(), GameProfiler.this.getTickTime);
         }

      }
   }
}
