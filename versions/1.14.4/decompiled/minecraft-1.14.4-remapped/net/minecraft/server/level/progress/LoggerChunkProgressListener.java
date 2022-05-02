package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerChunkProgressListener implements ChunkProgressListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final int maxCount;
   private int count;
   private long startTime;
   private long nextTickTime = Long.MAX_VALUE;

   public LoggerChunkProgressListener(int i) {
      int var2 = i * 2 + 1;
      this.maxCount = var2 * var2;
   }

   public void updateSpawnPos(ChunkPos chunkPos) {
      this.nextTickTime = Util.getMillis();
      this.startTime = this.nextTickTime;
   }

   public void onStatusChange(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {
      if(chunkStatus == ChunkStatus.FULL) {
         ++this.count;
      }

      int var3 = this.getProgress();
      if(Util.getMillis() > this.nextTickTime) {
         this.nextTickTime += 500L;
         LOGGER.info((new TranslatableComponent("menu.preparingSpawn", new Object[]{Integer.valueOf(Mth.clamp(var3, 0, 100))})).getString());
      }

   }

   public void stop() {
      LOGGER.info("Time elapsed: {} ms", Long.valueOf(Util.getMillis() - this.startTime));
      this.nextTickTime = Long.MAX_VALUE;
   }

   public int getProgress() {
      return Mth.floor((float)this.count * 100.0F / (float)this.maxCount);
   }
}
