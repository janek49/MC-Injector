package net.minecraft.client.sounds;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.audio.OggAudioStream;
import com.mojang.blaze3d.audio.SoundBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

@ClientJarOnly
public class SoundBufferLibrary {
   private final ResourceManager resourceManager;
   private final Map cache = Maps.newHashMap();

   public SoundBufferLibrary(ResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public CompletableFuture getCompleteBuffer(ResourceLocation resourceLocation) {
      return (CompletableFuture)this.cache.computeIfAbsent(resourceLocation, (resourceLocation) -> {
         return CompletableFuture.supplyAsync(() -> {
            try {
               Resource var2 = this.resourceManager.getResource(resourceLocation);
               Throwable var3 = null;

               SoundBuffer var9;
               try {
                  InputStream var4 = var2.getInputStream();
                  Throwable var5 = null;

                  try {
                     AudioStream var6 = new OggAudioStream(var4);
                     Throwable var7 = null;

                     try {
                        ByteBuffer var8 = var6.readAll();
                        var9 = new SoundBuffer(var8, var6.getFormat());
                     } catch (Throwable var56) {
                        var7 = var56;
                        throw var56;
                     } finally {
                        if(var6 != null) {
                           if(var7 != null) {
                              try {
                                 var6.close();
                              } catch (Throwable var55) {
                                 var7.addSuppressed(var55);
                              }
                           } else {
                              var6.close();
                           }
                        }

                     }
                  } catch (Throwable var58) {
                     var5 = var58;
                     throw var58;
                  } finally {
                     if(var4 != null) {
                        if(var5 != null) {
                           try {
                              var4.close();
                           } catch (Throwable var54) {
                              var5.addSuppressed(var54);
                           }
                        } else {
                           var4.close();
                        }
                     }

                  }
               } catch (Throwable var60) {
                  var3 = var60;
                  throw var60;
               } finally {
                  if(var2 != null) {
                     if(var3 != null) {
                        try {
                           var2.close();
                        } catch (Throwable var53) {
                           var3.addSuppressed(var53);
                        }
                     } else {
                        var2.close();
                     }
                  }

               }

               return var9;
            } catch (IOException var62) {
               throw new CompletionException(var62);
            }
         }, Util.backgroundExecutor());
      });
   }

   public CompletableFuture getStream(ResourceLocation resourceLocation) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            Resource var2 = this.resourceManager.getResource(resourceLocation);
            InputStream var3 = var2.getInputStream();
            return new OggAudioStream(var3);
         } catch (IOException var4) {
            throw new CompletionException(var4);
         }
      }, Util.backgroundExecutor());
   }

   public void clear() {
      this.cache.values().forEach((completableFuture) -> {
         completableFuture.thenAccept(SoundBuffer::discardAlBuffer);
      });
      this.cache.clear();
   }

   public CompletableFuture preload(Collection collection) {
      return CompletableFuture.allOf((CompletableFuture[])collection.stream().map((sound) -> {
         return this.getCompleteBuffer(sound.getPath());
      }).toArray((i) -> {
         return new CompletableFuture[i];
      }));
   }
}
