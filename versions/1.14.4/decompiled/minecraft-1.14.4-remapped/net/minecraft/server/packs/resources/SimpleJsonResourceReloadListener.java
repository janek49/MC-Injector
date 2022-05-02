package net.minecraft.server.packs.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SimpleJsonResourceReloadListener extends SimplePreparableReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int PATH_SUFFIX_LENGTH = ".json".length();
   private final Gson gson;
   private final String directory;

   public SimpleJsonResourceReloadListener(Gson gson, String directory) {
      this.gson = gson;
      this.directory = directory;
   }

   protected Map prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      Map<ResourceLocation, JsonObject> map = Maps.newHashMap();
      int var4 = this.directory.length() + 1;

      for(ResourceLocation var6 : resourceManager.listResources(this.directory, (string) -> {
         return string.endsWith(".json");
      })) {
         String var7 = var6.getPath();
         ResourceLocation var8 = new ResourceLocation(var6.getNamespace(), var7.substring(var4, var7.length() - PATH_SUFFIX_LENGTH));

         try {
            Resource var9 = resourceManager.getResource(var6);
            Throwable var10 = null;

            try {
               InputStream var11 = var9.getInputStream();
               Throwable var12 = null;

               try {
                  Reader var13 = new BufferedReader(new InputStreamReader(var11, StandardCharsets.UTF_8));
                  Throwable var14 = null;

                  try {
                     JsonObject var15 = (JsonObject)GsonHelper.fromJson(this.gson, var13, JsonObject.class);
                     if(var15 != null) {
                        JsonObject var16 = (JsonObject)map.put(var8, var15);
                        if(var16 != null) {
                           throw new IllegalStateException("Duplicate data file ignored with ID " + var8);
                        }
                     } else {
                        LOGGER.error("Couldn\'t load data file {} from {} as it\'s null or empty", var8, var6);
                     }
                  } catch (Throwable var62) {
                     var14 = var62;
                     throw var62;
                  } finally {
                     if(var13 != null) {
                        if(var14 != null) {
                           try {
                              var13.close();
                           } catch (Throwable var61) {
                              var14.addSuppressed(var61);
                           }
                        } else {
                           var13.close();
                        }
                     }

                  }
               } catch (Throwable var64) {
                  var12 = var64;
                  throw var64;
               } finally {
                  if(var11 != null) {
                     if(var12 != null) {
                        try {
                           var11.close();
                        } catch (Throwable var60) {
                           var12.addSuppressed(var60);
                        }
                     } else {
                        var11.close();
                     }
                  }

               }
            } catch (Throwable var66) {
               var10 = var66;
               throw var66;
            } finally {
               if(var9 != null) {
                  if(var10 != null) {
                     try {
                        var9.close();
                     } catch (Throwable var59) {
                        var10.addSuppressed(var59);
                     }
                  } else {
                     var9.close();
                  }
               }

            }
         } catch (IllegalArgumentException | IOException | JsonParseException var68) {
            LOGGER.error("Couldn\'t parse data file {} from {}", var8, var6, var68);
         }
      }

      return map;
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
