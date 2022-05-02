package net.minecraft.client.renderer.chunk;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.chunk.ChunkCompileTask;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class ChunkRenderWorker implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkRenderDispatcher dispatcher;
   private final ChunkBufferBuilderPack fixedBuffers;
   private boolean running;

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcher) {
      this(chunkRenderDispatcher, (ChunkBufferBuilderPack)null);
   }

   public ChunkRenderWorker(ChunkRenderDispatcher dispatcher, @Nullable ChunkBufferBuilderPack fixedBuffers) {
      this.running = true;
      this.dispatcher = dispatcher;
      this.fixedBuffers = fixedBuffers;
   }

   public void run() {
      while(this.running) {
         try {
            this.doTask(this.dispatcher.takeChunk());
         } catch (InterruptedException var3) {
            LOGGER.debug("Stopping chunk worker due to interrupt");
            return;
         } catch (Throwable var4) {
            CrashReport var2 = CrashReport.forThrowable(var4, "Batching chunks");
            Minecraft.getInstance().delayCrash(Minecraft.getInstance().fillReport(var2));
            return;
         }
      }

   }

   void doTask(final ChunkCompileTask chunkCompileTask) throws InterruptedException {
      chunkCompileTask.getStatusLock().lock();

      try {
         if(!checkState(chunkCompileTask, ChunkCompileTask.Status.PENDING)) {
            return;
         }

         if(!chunkCompileTask.getChunk().hasAllNeighbors()) {
            chunkCompileTask.cancel();
            return;
         }

         chunkCompileTask.setStatus(ChunkCompileTask.Status.COMPILING);
      } finally {
         chunkCompileTask.getStatusLock().unlock();
      }

      final ChunkBufferBuilderPack var2 = this.takeBuffers();
      chunkCompileTask.getStatusLock().lock();

      try {
         if(!checkState(chunkCompileTask, ChunkCompileTask.Status.COMPILING)) {
            this.releaseBuffers(var2);
            return;
         }
      } finally {
         chunkCompileTask.getStatusLock().unlock();
      }

      chunkCompileTask.setBuilders(var2);
      Vec3 var3 = this.dispatcher.getCameraPosition();
      float var4 = (float)var3.x;
      float var5 = (float)var3.y;
      float var6 = (float)var3.z;
      ChunkCompileTask.Type var7 = chunkCompileTask.getType();
      if(var7 == ChunkCompileTask.Type.REBUILD_CHUNK) {
         chunkCompileTask.getChunk().compile(var4, var5, var6, chunkCompileTask);
      } else if(var7 == ChunkCompileTask.Type.RESORT_TRANSPARENCY) {
         chunkCompileTask.getChunk().rebuildTransparent(var4, var5, var6, chunkCompileTask);
      }

      chunkCompileTask.getStatusLock().lock();

      try {
         if(!checkState(chunkCompileTask, ChunkCompileTask.Status.COMPILING)) {
            this.releaseBuffers(var2);
            return;
         }

         chunkCompileTask.setStatus(ChunkCompileTask.Status.UPLOADING);
      } finally {
         chunkCompileTask.getStatusLock().unlock();
      }

      final CompiledChunk var8 = chunkCompileTask.getCompiledChunk();
      ArrayList var9 = Lists.newArrayList();
      if(var7 == ChunkCompileTask.Type.REBUILD_CHUNK) {
         for(BlockLayer var13 : BlockLayer.values()) {
            if(var8.hasLayer(var13)) {
               var9.add(this.dispatcher.uploadChunkLayer(var13, chunkCompileTask.getBuilders().builder(var13), chunkCompileTask.getChunk(), var8, chunkCompileTask.getDistAtCreation()));
            }
         }
      } else if(var7 == ChunkCompileTask.Type.RESORT_TRANSPARENCY) {
         var9.add(this.dispatcher.uploadChunkLayer(BlockLayer.TRANSLUCENT, chunkCompileTask.getBuilders().builder(BlockLayer.TRANSLUCENT), chunkCompileTask.getChunk(), var8, chunkCompileTask.getDistAtCreation()));
      }

      ListenableFuture<List<Void>> var10 = Futures.allAsList(var9);
      chunkCompileTask.addCancelListener(() -> {
         var26.cancel(false);
      });
      Futures.addCallback(var10, new FutureCallback() {
         public void onSuccess(@Nullable List list) {
            ChunkRenderWorker.this.releaseBuffers(var2);
            chunkCompileTask.getStatusLock().lock();

            label21: {
               try {
                  if(ChunkRenderWorker.checkState(chunkCompileTask, ChunkCompileTask.Status.UPLOADING)) {
                     chunkCompileTask.setStatus(ChunkCompileTask.Status.DONE);
                     break label21;
                  }
               } finally {
                  chunkCompileTask.getStatusLock().unlock();
               }

               return;
            }

            chunkCompileTask.getChunk().setCompiledChunk(var8);
         }

         public void onFailure(Throwable throwable) {
            ChunkRenderWorker.this.releaseBuffers(var2);
            if(!(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
               Minecraft.getInstance().delayCrash(CrashReport.forThrowable(throwable, "Rendering chunk"));
            }

         }

         // $FF: synthetic method
         public void onSuccess(@Nullable Object var1) {
            this.onSuccess((List)var1);
         }
      });
   }

   private static boolean checkState(ChunkCompileTask chunkCompileTask, ChunkCompileTask.Status chunkCompileTask$Status) {
      if(chunkCompileTask.getStatus() != chunkCompileTask$Status) {
         if(!chunkCompileTask.wasCancelled()) {
            LOGGER.warn("Chunk render task was {} when I expected it to be {}; ignoring task", chunkCompileTask.getStatus(), chunkCompileTask$Status);
         }

         return false;
      } else {
         return true;
      }
   }

   private ChunkBufferBuilderPack takeBuffers() throws InterruptedException {
      return this.fixedBuffers != null?this.fixedBuffers:this.dispatcher.takeChunkBufferBuilder();
   }

   private void releaseBuffers(ChunkBufferBuilderPack chunkBufferBuilderPack) {
      if(chunkBufferBuilderPack != this.fixedBuffers) {
         this.dispatcher.releaseChunkBufferBuilder(chunkBufferBuilderPack);
      }

   }

   public void stop() {
      this.running = false;
   }
}
