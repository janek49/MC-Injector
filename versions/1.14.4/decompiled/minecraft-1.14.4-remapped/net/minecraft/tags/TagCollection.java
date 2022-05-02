package net.minecraft.tags;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagCollection {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final int PATH_SUFFIX_LENGTH = ".json".length();
   private Map tags = ImmutableMap.of();
   private final Function idToValue;
   private final String directory;
   private final boolean ordered;
   private final String name;

   public TagCollection(Function idToValue, String directory, boolean ordered, String name) {
      this.idToValue = idToValue;
      this.directory = directory;
      this.ordered = ordered;
      this.name = name;
   }

   @Nullable
   public Tag getTag(ResourceLocation resourceLocation) {
      return (Tag)this.tags.get(resourceLocation);
   }

   public Tag getTagOrEmpty(ResourceLocation resourceLocation) {
      Tag<T> tag = (Tag)this.tags.get(resourceLocation);
      return tag == null?new Tag(resourceLocation):tag;
   }

   public Collection getAvailableTags() {
      return this.tags.keySet();
   }

   public Collection getMatchingTags(Object object) {
      List<ResourceLocation> var2 = Lists.newArrayList();

      for(Entry<ResourceLocation, Tag<T>> var4 : this.tags.entrySet()) {
         if(((Tag)var4.getValue()).contains(object)) {
            var2.add(var4.getKey());
         }
      }

      return var2;
   }

   public CompletableFuture prepare(ResourceManager resourceManager, Executor executor) {
      return CompletableFuture.supplyAsync(() -> {
         Map<ResourceLocation, Tag.Builder<T>> map = Maps.newHashMap();

         for(ResourceLocation var4 : resourceManager.listResources(this.directory, (string) -> {
            return string.endsWith(".json");
         })) {
            String var5 = var4.getPath();
            ResourceLocation var6 = new ResourceLocation(var4.getNamespace(), var5.substring(this.directory.length() + 1, var5.length() - PATH_SUFFIX_LENGTH));

            try {
               for(Resource var8 : resourceManager.getResources(var4)) {
                  try {
                     InputStream var9 = var8.getInputStream();
                     Throwable var10 = null;

                     try {
                        Reader var11 = new BufferedReader(new InputStreamReader(var9, StandardCharsets.UTF_8));
                        Throwable var12 = null;

                        try {
                           JsonObject var13 = (JsonObject)GsonHelper.fromJson(GSON, var11, JsonObject.class);
                           if(var13 == null) {
                              LOGGER.error("Couldn\'t load {} tag list {} from {} in data pack {} as it\'s empty or null", this.name, var6, var4, var8.getSourceName());
                           } else {
                              ((Tag.Builder)map.computeIfAbsent(var6, (resourceLocation) -> {
                                 return (Tag.Builder)Util.make(Tag.Builder.tag(), (tag$Builder) -> {
                                    tag$Builder.keepOrder(this.ordered);
                                 });
                              })).addFromJson(this.idToValue, var13);
                           }
                        } catch (Throwable var53) {
                           var12 = var53;
                           throw var53;
                        } finally {
                           if(var11 != null) {
                              if(var12 != null) {
                                 try {
                                    var11.close();
                                 } catch (Throwable var52) {
                                    var12.addSuppressed(var52);
                                 }
                              } else {
                                 var11.close();
                              }
                           }

                        }
                     } catch (Throwable var55) {
                        var10 = var55;
                        throw var55;
                     } finally {
                        if(var9 != null) {
                           if(var10 != null) {
                              try {
                                 var9.close();
                              } catch (Throwable var51) {
                                 var10.addSuppressed(var51);
                              }
                           } else {
                              var9.close();
                           }
                        }

                     }
                  } catch (RuntimeException | IOException var57) {
                     LOGGER.error("Couldn\'t read {} tag list {} from {} in data pack {}", this.name, var6, var4, var8.getSourceName(), var57);
                  } finally {
                     IOUtils.closeQuietly(var8);
                  }
               }
            } catch (IOException var59) {
               LOGGER.error("Couldn\'t read {} tag list {} from {}", this.name, var6, var4, var59);
            }
         }

         return map;
      }, executor);
   }

   public void load(Map map) {
      Map<ResourceLocation, Tag<T>> map = Maps.newHashMap();

      while(!map.isEmpty()) {
         boolean var3 = false;
         Iterator<Entry<ResourceLocation, Tag.Builder<T>>> var4 = map.entrySet().iterator();

         while(var4.hasNext()) {
            Entry<ResourceLocation, Tag.Builder<T>> var5 = (Entry)var4.next();
            Tag.Builder<T> var6 = (Tag.Builder)var5.getValue();
            map.getClass();
            if(var6.canBuild(map::get)) {
               var3 = true;
               ResourceLocation var7 = (ResourceLocation)var5.getKey();
               map.put(var7, var6.build(var7));
               var4.remove();
            }
         }

         if(!var3) {
            map.forEach((resourceLocation, tag$Builder) -> {
               LOGGER.error("Couldn\'t load {} tag {} as it either references another tag that doesn\'t exist, or ultimately references itself", this.name, resourceLocation);
            });
            break;
         }
      }

      map.forEach((resourceLocation, tag$Builder) -> {
         Tag var10000 = (Tag)map.put(resourceLocation, tag$Builder.build(resourceLocation));
      });
      this.replace(map);
   }

   protected void replace(Map map) {
      this.tags = ImmutableMap.copyOf(map);
   }

   public Map getAllTags() {
      return this.tags;
   }
}
