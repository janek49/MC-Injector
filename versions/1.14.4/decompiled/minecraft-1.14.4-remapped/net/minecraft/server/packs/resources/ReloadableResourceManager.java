package net.minecraft.server.packs.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;

public interface ReloadableResourceManager extends ResourceManager {
   CompletableFuture reload(Executor var1, Executor var2, List var3, CompletableFuture var4);

   ReloadInstance createQueuedReload(Executor var1, Executor var2, CompletableFuture var3);

   ReloadInstance createFullReload(Executor var1, Executor var2, CompletableFuture var3, List var4);

   void registerReloadListener(PreparableReloadListener var1);
}
