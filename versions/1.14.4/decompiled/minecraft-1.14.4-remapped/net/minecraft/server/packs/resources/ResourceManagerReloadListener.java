package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ResourceManagerReloadListener extends PreparableReloadListener {
   default CompletableFuture reload(PreparableReloadListener.PreparationBarrier preparableReloadListener$PreparationBarrier, ResourceManager resourceManager, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      return preparableReloadListener$PreparationBarrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
         this.onResourceManagerReload(resourceManager);
      }, var6);
   }

   void onResourceManagerReload(ResourceManager var1);
}
