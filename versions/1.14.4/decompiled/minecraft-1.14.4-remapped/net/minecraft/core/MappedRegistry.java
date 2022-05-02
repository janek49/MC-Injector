package net.minecraft.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MappedRegistry extends WritableRegistry {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final CrudeIncrementalIntIdentityHashBiMap map = new CrudeIncrementalIntIdentityHashBiMap(256);
   protected final BiMap storage = HashBiMap.create();
   protected Object[] randomCache;
   private int nextId;

   public Object registerMapping(int var1, ResourceLocation resourceLocation, Object var3) {
      this.map.addMapping(var3, var1);
      Validate.notNull(resourceLocation);
      Validate.notNull(var3);
      this.randomCache = null;
      if(this.storage.containsKey(resourceLocation)) {
         LOGGER.debug("Adding duplicate key \'{}\' to registry", resourceLocation);
      }

      this.storage.put(resourceLocation, var3);
      if(this.nextId <= var1) {
         this.nextId = var1 + 1;
      }

      return var3;
   }

   public Object register(ResourceLocation resourceLocation, Object var2) {
      return this.registerMapping(this.nextId, resourceLocation, var2);
   }

   @Nullable
   public ResourceLocation getKey(Object object) {
      return (ResourceLocation)this.storage.inverse().get(object);
   }

   public int getId(@Nullable Object object) {
      return this.map.getId(object);
   }

   @Nullable
   public Object byId(int id) {
      return this.map.byId(id);
   }

   public Iterator iterator() {
      return this.map.iterator();
   }

   @Nullable
   public Object get(@Nullable ResourceLocation resourceLocation) {
      return this.storage.get(resourceLocation);
   }

   public Optional getOptional(@Nullable ResourceLocation resourceLocation) {
      return Optional.ofNullable(this.storage.get(resourceLocation));
   }

   public Set keySet() {
      return Collections.unmodifiableSet(this.storage.keySet());
   }

   public boolean isEmpty() {
      return this.storage.isEmpty();
   }

   @Nullable
   public Object getRandom(Random random) {
      if(this.randomCache == null) {
         Collection<?> var2 = this.storage.values();
         if(var2.isEmpty()) {
            return null;
         }

         this.randomCache = var2.toArray(new Object[var2.size()]);
      }

      return this.randomCache[random.nextInt(this.randomCache.length)];
   }

   public boolean containsKey(ResourceLocation resourceLocation) {
      return this.storage.containsKey(resourceLocation);
   }
}
