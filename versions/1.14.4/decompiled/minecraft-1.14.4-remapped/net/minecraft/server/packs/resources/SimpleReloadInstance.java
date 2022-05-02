package net.minecraft.server.packs.resources;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.InactiveProfiler;

public class SimpleReloadInstance implements ReloadInstance {
   protected final ResourceManager resourceManager;
   protected final CompletableFuture allPreparations = new CompletableFuture();
   protected final CompletableFuture allDone;
   private final Set preparingListeners;
   private final int listenerCount;
   private int startedReloads;
   private int finishedReloads;
   private final AtomicInteger startedTaskCounter = new AtomicInteger();
   private final AtomicInteger doneTaskCounter = new AtomicInteger();

   public static SimpleReloadInstance of(ResourceManager resourceManager, List list, Executor var2, Executor var3, CompletableFuture completableFuture) {
      return new SimpleReloadInstance(var2, var3, resourceManager, list, (preparableReloadListener$PreparationBarrier, resourceManager, preparableReloadListener, var4, var5) -> {
         return preparableReloadListener.reload(preparableReloadListener$PreparationBarrier, resourceManager, InactiveProfiler.INACTIVE, InactiveProfiler.INACTIVE, var2, var5);
      }, completableFuture);
   }

   protected SimpleReloadInstance(Executor var1, final Executor var2, ResourceManager resourceManager, List list, SimpleReloadInstance.StateFactory simpleReloadInstance$StateFactory, CompletableFuture completableFuture) {
      this.resourceManager = resourceManager;
      this.listenerCount = list.size();
      this.startedTaskCounter.incrementAndGet();
      AtomicInteger var10001 = this.doneTaskCounter;
      this.doneTaskCounter.getClass();
      completableFuture.thenRun(var10001::incrementAndGet);
      List<CompletableFuture<S>> list = new ArrayList();
      final CompletableFuture<?> var8 = completableFuture;
      this.preparingListeners = Sets.newHashSet(list);

      for(final PreparableReloadListener var10 : list) {
         CompletableFuture<S> var12 = simpleReloadInstance$StateFactory.create(new PreparableReloadListener.PreparationBarrier() {
            public CompletableFuture wait(Object object) {
               var2.execute(() -> {
                  SimpleReloadInstance.this.preparingListeners.remove(var10);
                  if(SimpleReloadInstance.this.preparingListeners.isEmpty()) {
                     SimpleReloadInstance.this.allPreparations.complete(Unit.INSTANCE);
                  }

               });
               return SimpleReloadInstance.this.allPreparations.thenCombine(var8, (unit, var2x) -> {
                  return object;
               });
            }
         }, resourceManager, var10, (runnable) -> {
            this.startedTaskCounter.incrementAndGet();
            var1.execute(() -> {
               runnable.run();
               this.doneTaskCounter.incrementAndGet();
            });
         }, (runnable) -> {
            ++this.startedReloads;
            var2.execute(() -> {
               runnable.run();
               ++this.finishedReloads;
            });
         });
         list.add(var12);
         var8 = var12;
      }

      this.allDone = Util.sequence(list);
   }

   public CompletableFuture done() {
      return this.allDone.thenApply((list) -> {
         return Unit.INSTANCE;
      });
   }

   public float getActualProgress() {
      int var1 = this.listenerCount - this.preparingListeners.size();
      float var2 = (float)(this.doneTaskCounter.get() * 2 + this.finishedReloads * 2 + var1 * 1);
      float var3 = (float)(this.startedTaskCounter.get() * 2 + this.startedReloads * 2 + this.listenerCount * 1);
      return var2 / var3;
   }

   public boolean isApplying() {
      return this.allPreparations.isDone();
   }

   public boolean isDone() {
      return this.allDone.isDone();
   }

   public void checkExceptions() {
      if(this.allDone.isCompletedExceptionally()) {
         this.allDone.join();
      }

   }

   public interface StateFactory {
      CompletableFuture create(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, PreparableReloadListener var3, Executor var4, Executor var5);
   }
}
