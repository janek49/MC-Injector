package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedLevelLightEngine extends LevelLightEngine implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ProcessorMailbox taskMailbox;
   private final ObjectList lightTasks = new ObjectArrayList();
   private final ChunkMap chunkMap;
   private final ProcessorHandle sorterMailbox;
   private volatile int taskPerBatch = 5;
   private final AtomicBoolean scheduled = new AtomicBoolean();

   public ThreadedLevelLightEngine(LightChunkGetter lightChunkGetter, ChunkMap chunkMap, boolean var3, ProcessorMailbox taskMailbox, ProcessorHandle sorterMailbox) {
      super(lightChunkGetter, true, var3);
      this.chunkMap = chunkMap;
      this.sorterMailbox = sorterMailbox;
      this.taskMailbox = taskMailbox;
   }

   public void close() {
   }

   public int runUpdates(int var1, boolean var2, boolean var3) {
      throw new UnsupportedOperationException("Ran authomatically on a different thread!");
   }

   public void onBlockEmissionIncrease(BlockPos blockPos, int var2) {
      throw new UnsupportedOperationException("Ran authomatically on a different thread!");
   }

   public void checkBlock(BlockPos blockPos) {
      BlockPos blockPos = blockPos.immutable();
      this.addTask(blockPos.getX() >> 4, blockPos.getZ() >> 4, ThreadedLevelLightEngine.TaskType.POST_UPDATE, Util.name(() -> {
         super.checkBlock(blockPos);
      }, () -> {
         return "checkBlock " + blockPos;
      }));
   }

   protected void updateChunkStatus(ChunkPos chunkPos) {
      this.addTask(chunkPos.x, chunkPos.z, () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.retainData(chunkPos, false);
         super.enableLightSources(chunkPos, false);

         for(int var2 = -1; var2 < 17; ++var2) {
            super.queueSectionData(LightLayer.BLOCK, SectionPos.of(chunkPos, var2), (DataLayer)null);
            super.queueSectionData(LightLayer.SKY, SectionPos.of(chunkPos, var2), (DataLayer)null);
         }

         for(int var2 = 0; var2 < 16; ++var2) {
            super.updateSectionStatus(SectionPos.of(chunkPos, var2), true);
         }

      }, () -> {
         return "updateChunkStatus " + chunkPos + " " + true;
      }));
   }

   public void updateSectionStatus(SectionPos sectionPos, boolean var2) {
      this.addTask(sectionPos.x(), sectionPos.z(), () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.updateSectionStatus(sectionPos, var2);
      }, () -> {
         return "updateSectionStatus " + sectionPos + " " + var2;
      }));
   }

   public void enableLightSources(ChunkPos chunkPos, boolean var2) {
      this.addTask(chunkPos.x, chunkPos.z, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.enableLightSources(chunkPos, var2);
      }, () -> {
         return "enableLight " + chunkPos + " " + var2;
      }));
   }

   public void queueSectionData(LightLayer lightLayer, SectionPos sectionPos, @Nullable DataLayer dataLayer) {
      this.addTask(sectionPos.x(), sectionPos.z(), () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.queueSectionData(lightLayer, sectionPos, dataLayer);
      }, () -> {
         return "queueData " + sectionPos;
      }));
   }

   private void addTask(int var1, int var2, ThreadedLevelLightEngine.TaskType threadedLevelLightEngine$TaskType, Runnable runnable) {
      this.addTask(var1, var2, this.chunkMap.getChunkQueueLevel(ChunkPos.asLong(var1, var2)), threadedLevelLightEngine$TaskType, runnable);
   }

   private void addTask(int var1, int var2, IntSupplier intSupplier, ThreadedLevelLightEngine.TaskType threadedLevelLightEngine$TaskType, Runnable runnable) {
      this.sorterMailbox.tell(ChunkTaskPriorityQueueSorter.message(() -> {
         this.lightTasks.add(Pair.of(threadedLevelLightEngine$TaskType, runnable));
         if(this.lightTasks.size() >= this.taskPerBatch) {
            this.runUpdate();
         }

      }, ChunkPos.asLong(var1, var2), intSupplier));
   }

   public void retainData(ChunkPos chunkPos, boolean var2) {
      this.addTask(chunkPos.x, chunkPos.z, () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.retainData(chunkPos, var2);
      }, () -> {
         return "retainData " + chunkPos;
      }));
   }

   public CompletableFuture lightChunk(ChunkAccess chunkAccess, boolean var2) {
      ChunkPos var3 = chunkAccess.getPos();
      chunkAccess.setLightCorrect(false);
      this.addTask(var3.x, var3.z, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         LevelChunkSection[] vars4 = chunkAccess.getSections();

         for(int var5 = 0; var5 < 16; ++var5) {
            LevelChunkSection var6 = vars4[var5];
            if(!LevelChunkSection.isEmpty(var6)) {
               super.updateSectionStatus(SectionPos.of(var3, var5), false);
            }
         }

         super.enableLightSources(var3, true);
         if(!var2) {
            chunkAccess.getLights().forEach((blockPos) -> {
               super.onBlockEmissionIncrease(blockPos, chunkAccess.getLightEmission(blockPos));
            });
         }

         this.chunkMap.releaseLightTicket(var3);
      }, () -> {
         return "lightChunk " + var3 + " " + var2;
      }));
      return CompletableFuture.supplyAsync(() -> {
         chunkAccess.setLightCorrect(true);
         super.retainData(var3, false);
         return chunkAccess;
      }, (runnable) -> {
         this.addTask(var3.x, var3.z, ThreadedLevelLightEngine.TaskType.POST_UPDATE, runnable);
      });
   }

   public void tryScheduleUpdate() {
      if((!this.lightTasks.isEmpty() || super.hasLightWork()) && this.scheduled.compareAndSet(false, true)) {
         this.taskMailbox.tell(() -> {
            this.runUpdate();
            this.scheduled.set(false);
         });
      }

   }

   private void runUpdate() {
      int var1 = Math.min(this.lightTasks.size(), this.taskPerBatch);
      ObjectListIterator<Pair<ThreadedLevelLightEngine.TaskType, Runnable>> var2 = this.lightTasks.iterator();

      int var3;
      for(var3 = 0; var2.hasNext() && var3 < var1; ++var3) {
         Pair<ThreadedLevelLightEngine.TaskType, Runnable> var4 = (Pair)var2.next();
         if(var4.getFirst() == ThreadedLevelLightEngine.TaskType.PRE_UPDATE) {
            ((Runnable)var4.getSecond()).run();
         }
      }

      var2.back(var3);
      super.runUpdates(Integer.MAX_VALUE, true, true);

      for(var3 = 0; var2.hasNext() && var3 < var1; ++var3) {
         Pair<ThreadedLevelLightEngine.TaskType, Runnable> var4 = (Pair)var2.next();
         if(var4.getFirst() == ThreadedLevelLightEngine.TaskType.POST_UPDATE) {
            ((Runnable)var4.getSecond()).run();
         }

         var2.remove();
      }

   }

   public void setTaskPerBatch(int taskPerBatch) {
      this.taskPerBatch = taskPerBatch;
   }

   static enum TaskType {
      PRE_UPDATE,
      POST_UPDATE;
   }
}
