package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ProfiledReloadInstance;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements ReloadableResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map namespacedPacks = Maps.newHashMap();
   private final List listeners = Lists.newArrayList();
   private final List recentlyRegistered = Lists.newArrayList();
   private final Set namespaces = Sets.newLinkedHashSet();
   private final PackType type;
   private final Thread mainThread;

   public SimpleReloadableResourceManager(PackType type, Thread mainThread) {
      this.type = type;
      this.mainThread = mainThread;
   }

   public void add(Pack pack) {
      for(String var3 : pack.getNamespaces(this.type)) {
         this.namespaces.add(var3);
         FallbackResourceManager var4 = (FallbackResourceManager)this.namespacedPacks.get(var3);
         if(var4 == null) {
            var4 = new FallbackResourceManager(this.type);
            this.namespacedPacks.put(var3, var4);
         }

         var4.add(pack);
      }

   }

   public Set getNamespaces() {
      return this.namespaces;
   }

   public Resource getResource(ResourceLocation resourceLocation) throws IOException {
      ResourceManager var2 = (ResourceManager)this.namespacedPacks.get(resourceLocation.getNamespace());
      if(var2 != null) {
         return var2.getResource(resourceLocation);
      } else {
         throw new FileNotFoundException(resourceLocation.toString());
      }
   }

   public boolean hasResource(ResourceLocation resourceLocation) {
      ResourceManager var2 = (ResourceManager)this.namespacedPacks.get(resourceLocation.getNamespace());
      return var2 != null?var2.hasResource(resourceLocation):false;
   }

   public List getResources(ResourceLocation resourceLocation) throws IOException {
      ResourceManager var2 = (ResourceManager)this.namespacedPacks.get(resourceLocation.getNamespace());
      if(var2 != null) {
         return var2.getResources(resourceLocation);
      } else {
         throw new FileNotFoundException(resourceLocation.toString());
      }
   }

   public Collection listResources(String string, Predicate predicate) {
      Set<ResourceLocation> var3 = Sets.newHashSet();

      for(FallbackResourceManager var5 : this.namespacedPacks.values()) {
         var3.addAll(var5.listResources(string, predicate));
      }

      List<ResourceLocation> var4 = Lists.newArrayList(var3);
      Collections.sort(var4);
      return var4;
   }

   private void clear() {
      this.namespacedPacks.clear();
      this.namespaces.clear();
   }

   public CompletableFuture reload(Executor var1, Executor var2, List list, CompletableFuture var4) {
      ReloadInstance var5 = this.createFullReload(var1, var2, var4, list);
      return var5.done();
   }

   public void registerReloadListener(PreparableReloadListener preparableReloadListener) {
      this.listeners.add(preparableReloadListener);
      this.recentlyRegistered.add(preparableReloadListener);
   }

   protected ReloadInstance createReload(Executor var1, Executor var2, List list, CompletableFuture completableFuture) {
      ReloadInstance reloadInstance;
      if(LOGGER.isDebugEnabled()) {
         reloadInstance = new ProfiledReloadInstance(this, new ArrayList(list), var1, var2, completableFuture);
      } else {
         reloadInstance = SimpleReloadInstance.of(this, new ArrayList(list), var1, var2, completableFuture);
      }

      this.recentlyRegistered.clear();
      return reloadInstance;
   }

   public ReloadInstance createQueuedReload(Executor var1, Executor var2, CompletableFuture completableFuture) {
      return this.createReload(var1, var2, this.recentlyRegistered, completableFuture);
   }

   public ReloadInstance createFullReload(Executor var1, Executor var2, CompletableFuture completableFuture, List list) {
      this.clear();
      LOGGER.info("Reloading ResourceManager: {}", list.stream().map(Pack::getName).collect(Collectors.joining(", ")));

      for(Pack var6 : list) {
         this.add(var6);
      }

      return this.createReload(var1, var2, this.listeners, completableFuture);
   }
}
