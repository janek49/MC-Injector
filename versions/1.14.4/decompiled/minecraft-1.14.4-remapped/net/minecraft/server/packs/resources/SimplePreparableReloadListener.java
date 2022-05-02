package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class SimplePreparableReloadListener implements PreparableReloadListener {
   public final CompletableFuture reload(PreparableReloadListener.PreparationBarrier preparableReloadListener$PreparationBarrier, ResourceManager resourceManager, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      CompletableFuture var10000 = CompletableFuture.supplyAsync(() -> {
         return this.prepare(resourceManager, var3);
      }, var5);
      preparableReloadListener$PreparationBarrier.getClass();
      return var10000.thenCompose(preparableReloadListener$PreparationBarrier::wait).thenAcceptAsync((object) -> {
         this.apply(object, resourceManager, var4);
      }, var6);
   }

   protected abstract Object prepare(ResourceManager var1, ProfilerFiller var2);

   protected abstract void apply(Object var1, ResourceManager var2, ProfilerFiller var3);
}
