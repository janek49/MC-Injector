package net.minecraft.server.packs.resources;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import net.minecraft.Util;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfiledReloadInstance extends SimpleReloadInstance {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Stopwatch total = Stopwatch.createUnstarted();

   public ProfiledReloadInstance(ResourceManager resourceManager, List list, Executor var3, Executor var4, CompletableFuture completableFuture) {
      super(var3, var4, resourceManager, list, (preparableReloadListener$PreparationBarrier, resourceManager, preparableReloadListener, var4x, var5) -> {
         AtomicLong var6 = new AtomicLong();
         AtomicLong var7 = new AtomicLong();
         ActiveProfiler var8 = new ActiveProfiler(Util.getNanos(), () -> {
            return 0;
         });
         ActiveProfiler var9 = new ActiveProfiler(Util.getNanos(), () -> {
            return 0;
         });
         CompletableFuture<Void> var10 = preparableReloadListener.reload(preparableReloadListener$PreparationBarrier, resourceManager, var8, var9, (runnable) -> {
            var4x.execute(() -> {
               long var2 = Util.getNanos();
               runnable.run();
               var6.addAndGet(Util.getNanos() - var2);
            });
         }, (runnable) -> {
            var5.execute(() -> {
               long var2 = Util.getNanos();
               runnable.run();
               var7.addAndGet(Util.getNanos() - var2);
            });
         });
         return var10.thenApplyAsync((void) -> {
            return new ProfiledReloadInstance.State(preparableReloadListener.getClass().getSimpleName(), var8.getResults(), var9.getResults(), var6, var7);
         }, var4);
      }, completableFuture);
      this.total.start();
      this.allDone.thenAcceptAsync(this::finish, var4);
   }

   private void finish(List list) {
      this.total.stop();
      int var2 = 0;
      LOGGER.info("Resource reload finished after " + this.total.elapsed(TimeUnit.MILLISECONDS) + " ms");

      for(ProfiledReloadInstance.State var4 : list) {
         ProfileResults var5 = var4.preparationResult;
         ProfileResults var6 = var4.reloadResult;
         int var7 = (int)((double)var4.preparationNanos.get() / 1000000.0D);
         int var8 = (int)((double)var4.reloadNanos.get() / 1000000.0D);
         int var9 = var7 + var8;
         String var10 = var4.name;
         LOGGER.info(var10 + " took approximately " + var9 + " ms (" + var7 + " ms preparing, " + var8 + " ms applying)");
         String var11 = var5.getProfilerResults();
         if(var11.length() > 0) {
            LOGGER.debug(var10 + " preparations:\n" + var11);
         }

         String var12 = var6.getProfilerResults();
         if(var12.length() > 0) {
            LOGGER.debug(var10 + " reload:\n" + var12);
         }

         LOGGER.info("----------");
         var2 += var8;
      }

      LOGGER.info("Total blocking time: " + var2 + " ms");
   }

   public static class State {
      private final String name;
      private final ProfileResults preparationResult;
      private final ProfileResults reloadResult;
      private final AtomicLong preparationNanos;
      private final AtomicLong reloadNanos;

      private State(String name, ProfileResults preparationResult, ProfileResults reloadResult, AtomicLong preparationNanos, AtomicLong reloadNanos) {
         this.name = name;
         this.preparationResult = preparationResult;
         this.reloadResult = reloadResult;
         this.preparationNanos = preparationNanos;
         this.reloadNanos = reloadNanos;
      }
   }
}
