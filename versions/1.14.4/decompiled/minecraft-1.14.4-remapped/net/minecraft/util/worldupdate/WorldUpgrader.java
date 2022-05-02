package net.minecraft.util.worldupdate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpgrader {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setDaemon(true).build();
   private final String levelName;
   private final boolean eraseCache;
   private final LevelStorage levelStorage;
   private final Thread thread;
   private final File pathToWorld;
   private volatile boolean running = true;
   private volatile boolean finished;
   private volatile float progress;
   private volatile int totalChunks;
   private volatile int converted;
   private volatile int skipped;
   private final Object2FloatMap progressMap = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(Util.identityStrategy()));
   private volatile Component status = new TranslatableComponent("optimizeWorld.stage.counting", new Object[0]);
   private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
   private final DimensionDataStorage overworldDataStorage;

   public WorldUpgrader(String string, LevelStorageSource levelStorageSource, LevelData levelData, boolean eraseCache) {
      this.levelName = levelData.getLevelName();
      this.eraseCache = eraseCache;
      this.levelStorage = levelStorageSource.selectLevel(string, (MinecraftServer)null);
      this.levelStorage.saveLevelData(levelData);
      this.overworldDataStorage = new DimensionDataStorage(new File(DimensionType.OVERWORLD.getStorageFolder(this.levelStorage.getFolder()), "data"), this.levelStorage.getFixerUpper());
      this.pathToWorld = this.levelStorage.getFolder();
      this.thread = THREAD_FACTORY.newThread(this::work);
      this.thread.setUncaughtExceptionHandler((thread, throwable) -> {
         LOGGER.error("Error upgrading world", throwable);
         this.status = new TranslatableComponent("optimizeWorld.stage.failed", new Object[0]);
      });
      this.thread.start();
   }

   public void cancel() {
      this.running = false;

      try {
         this.thread.join();
      } catch (InterruptedException var2) {
         ;
      }

   }

   private void work() {
      File var1 = this.levelStorage.getFolder();
      this.totalChunks = 0;
      Builder<DimensionType, ListIterator<ChunkPos>> var2 = ImmutableMap.builder();

      for(DimensionType var4 : DimensionType.getAllTypes()) {
         List<ChunkPos> var5 = this.getAllChunkPos(var4);
         var2.put(var4, var5.listIterator());
         this.totalChunks += var5.size();
      }

      if(this.totalChunks == 0) {
         this.finished = true;
      } else {
         float var3 = (float)this.totalChunks;
         ImmutableMap<DimensionType, ListIterator<ChunkPos>> var4 = var2.build();
         Builder<DimensionType, ChunkStorage> var5 = ImmutableMap.builder();

         for(DimensionType var7 : DimensionType.getAllTypes()) {
            File var8 = var7.getStorageFolder(var1);
            var5.put(var7, new ChunkStorage(new File(var8, "region"), this.levelStorage.getFixerUpper()));
         }

         ImmutableMap<DimensionType, ChunkStorage> var6 = var5.build();
         long var7 = Util.getMillis();
         this.status = new TranslatableComponent("optimizeWorld.stage.upgrading", new Object[0]);

         while(this.running) {
            boolean var9 = false;
            float var10 = 0.0F;

            for(DimensionType var12 : DimensionType.getAllTypes()) {
               ListIterator<ChunkPos> var13 = (ListIterator)var4.get(var12);
               ChunkStorage var14 = (ChunkStorage)var6.get(var12);
               if(var13.hasNext()) {
                  ChunkPos var15 = (ChunkPos)var13.next();
                  boolean var16 = false;

                  try {
                     CompoundTag var17 = var14.read(var15);
                     if(var17 != null) {
                        int var18 = ChunkStorage.getVersion(var17);
                        CompoundTag var19 = var14.upgradeChunkTag(var12, () -> {
                           return this.overworldDataStorage;
                        }, var17);
                        boolean var20 = var18 < SharedConstants.getCurrentVersion().getWorldVersion();
                        if(this.eraseCache) {
                           CompoundTag var21 = var19.getCompound("Level");
                           var20 = var20 || var21.contains("Heightmaps");
                           var21.remove("Heightmaps");
                           var20 = var20 || var21.contains("isLightOn");
                           var21.remove("isLightOn");
                        }

                        if(var20) {
                           var14.write(var15, var19);
                           var16 = true;
                        }
                     }
                  } catch (ReportedException var23) {
                     Throwable var18 = var23.getCause();
                     if(!(var18 instanceof IOException)) {
                        throw var23;
                     }

                     LOGGER.error("Error upgrading chunk {}", var15, var18);
                  } catch (IOException var24) {
                     LOGGER.error("Error upgrading chunk {}", var15, var24);
                  }

                  if(var16) {
                     ++this.converted;
                  } else {
                     ++this.skipped;
                  }

                  var9 = true;
               }

               float var15 = (float)var13.nextIndex() / var3;
               this.progressMap.put(var12, var15);
               var10 += var15;
            }

            this.progress = var10;
            if(!var9) {
               this.running = false;
            }
         }

         this.status = new TranslatableComponent("optimizeWorld.stage.finished", new Object[0]);
         UnmodifiableIterator var31 = var6.values().iterator();

         while(var31.hasNext()) {
            ChunkStorage var10 = (ChunkStorage)var31.next();

            try {
               var10.close();
            } catch (IOException var22) {
               LOGGER.error("Error upgrading chunk", var22);
            }
         }

         this.overworldDataStorage.save();
         var7 = Util.getMillis() - var7;
         LOGGER.info("World optimizaton finished after {} ms", Long.valueOf(var7));
         this.finished = true;
      }
   }

   private List getAllChunkPos(DimensionType dimensionType) {
      File var2 = dimensionType.getStorageFolder(this.pathToWorld);
      File var3 = new File(var2, "region");
      File[] vars4 = var3.listFiles((file, string) -> {
         return string.endsWith(".mca");
      });
      if(vars4 == null) {
         return ImmutableList.of();
      } else {
         List<ChunkPos> var5 = Lists.newArrayList();

         for(File var9 : vars4) {
            Matcher var10 = REGEX.matcher(var9.getName());
            if(var10.matches()) {
               int var11 = Integer.parseInt(var10.group(1)) << 5;
               int var12 = Integer.parseInt(var10.group(2)) << 5;

               try {
                  RegionFile var13 = new RegionFile(var9);
                  Throwable var14 = null;

                  try {
                     for(int var15 = 0; var15 < 32; ++var15) {
                        for(int var16 = 0; var16 < 32; ++var16) {
                           ChunkPos var17 = new ChunkPos(var15 + var11, var16 + var12);
                           if(var13.doesChunkExist(var17)) {
                              var5.add(var17);
                           }
                        }
                     }
                  } catch (Throwable var26) {
                     var14 = var26;
                     throw var26;
                  } finally {
                     if(var13 != null) {
                        if(var14 != null) {
                           try {
                              var13.close();
                           } catch (Throwable var25) {
                              var14.addSuppressed(var25);
                           }
                        } else {
                           var13.close();
                        }
                     }

                  }
               } catch (Throwable var28) {
                  ;
               }
            }
         }

         return var5;
      }
   }

   public boolean isFinished() {
      return this.finished;
   }

   public float dimensionProgress(DimensionType dimensionType) {
      return this.progressMap.getFloat(dimensionType);
   }

   public float getProgress() {
      return this.progress;
   }

   public int getTotalChunks() {
      return this.totalChunks;
   }

   public int getConverted() {
      return this.converted;
   }

   public int getSkipped() {
      return this.skipped;
   }

   public Component getStatus() {
      return this.status;
   }
}
