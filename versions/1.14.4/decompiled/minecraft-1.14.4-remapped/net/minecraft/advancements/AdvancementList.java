package net.minecraft.advancements;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementList {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map advancements = Maps.newHashMap();
   private final Set roots = Sets.newLinkedHashSet();
   private final Set tasks = Sets.newLinkedHashSet();
   private AdvancementList.Listener listener;

   private void remove(Advancement advancement) {
      for(Advancement var3 : advancement.getChildren()) {
         this.remove(var3);
      }

      LOGGER.info("Forgot about advancement {}", advancement.getId());
      this.advancements.remove(advancement.getId());
      if(advancement.getParent() == null) {
         this.roots.remove(advancement);
         if(this.listener != null) {
            this.listener.onRemoveAdvancementRoot(advancement);
         }
      } else {
         this.tasks.remove(advancement);
         if(this.listener != null) {
            this.listener.onRemoveAdvancementTask(advancement);
         }
      }

   }

   public void remove(Set set) {
      for(ResourceLocation var3 : set) {
         Advancement var4 = (Advancement)this.advancements.get(var3);
         if(var4 == null) {
            LOGGER.warn("Told to remove advancement {} but I don\'t know what that is", var3);
         } else {
            this.remove(var4);
         }
      }

   }

   public void add(Map map) {
      Function<ResourceLocation, Advancement> var2 = Functions.forMap(this.advancements, (Object)null);

      label18:
      while(!map.isEmpty()) {
         boolean var3 = false;
         Iterator<Entry<ResourceLocation, Advancement.Builder>> var4 = map.entrySet().iterator();

         while(var4.hasNext()) {
            Entry<ResourceLocation, Advancement.Builder> var5 = (Entry)var4.next();
            ResourceLocation var6 = (ResourceLocation)var5.getKey();
            Advancement.Builder var7 = (Advancement.Builder)var5.getValue();
            if(var7.canBuild(var2)) {
               Advancement var8 = var7.build(var6);
               this.advancements.put(var6, var8);
               var3 = true;
               var4.remove();
               if(var8.getParent() == null) {
                  this.roots.add(var8);
                  if(this.listener != null) {
                     this.listener.onAddAdvancementRoot(var8);
                  }
               } else {
                  this.tasks.add(var8);
                  if(this.listener != null) {
                     this.listener.onAddAdvancementTask(var8);
                  }
               }
            }
         }

         if(!var3) {
            var4 = map.entrySet().iterator();

            while(true) {
               if(!var4.hasNext()) {
                  break label18;
               }

               Entry<ResourceLocation, Advancement.Builder> var5 = (Entry)var4.next();
               LOGGER.error("Couldn\'t load advancement {}: {}", var5.getKey(), var5.getValue());
            }
         }
      }

      LOGGER.info("Loaded {} advancements", Integer.valueOf(this.advancements.size()));
   }

   public void clear() {
      this.advancements.clear();
      this.roots.clear();
      this.tasks.clear();
      if(this.listener != null) {
         this.listener.onAdvancementsCleared();
      }

   }

   public Iterable getRoots() {
      return this.roots;
   }

   public Collection getAllAdvancements() {
      return this.advancements.values();
   }

   @Nullable
   public Advancement get(ResourceLocation resourceLocation) {
      return (Advancement)this.advancements.get(resourceLocation);
   }

   public void setListener(@Nullable AdvancementList.Listener listener) {
      this.listener = listener;
      if(listener != null) {
         for(Advancement var3 : this.roots) {
            listener.onAddAdvancementRoot(var3);
         }

         for(Advancement var3 : this.tasks) {
            listener.onAddAdvancementTask(var3);
         }
      }

   }

   public interface Listener {
      void onAddAdvancementRoot(Advancement var1);

      void onRemoveAdvancementRoot(Advancement var1);

      void onAddAdvancementTask(Advancement var1);

      void onRemoveAdvancementTask(Advancement var1);

      void onAdvancementsCleared();
   }
}
