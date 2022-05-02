package net.minecraft.client.renderer.chunk;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexBufferUploader;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.chunk.ChunkCompileTask;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setNameFormat("Chunk Batcher %d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build();
   private final int bufferCount;
   private final List threads = Lists.newArrayList();
   private final List workers = Lists.newArrayList();
   private final PriorityBlockingQueue chunksToBatch = Queues.newPriorityBlockingQueue();
   private final BlockingQueue availableChunkBuffers;
   private final BufferUploader uploader = new BufferUploader();
   private final VertexBufferUploader vboUploader = new VertexBufferUploader();
   private final Queue pendingUploads = Queues.newPriorityQueue();
   private final ChunkRenderWorker localWorker;
   private Vec3 camera = Vec3.ZERO;

   public ChunkRenderDispatcher(boolean b) {
      int var2 = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / 10485760 - 1);
      int var3 = Runtime.getRuntime().availableProcessors();
      int var4 = b?var3:Math.min(var3, 4);
      int var5 = Math.max(1, Math.min(var4 * 2, var2));
      this.localWorker = new ChunkRenderWorker(this, new ChunkBufferBuilderPack());
      List<ChunkBufferBuilderPack> var6 = Lists.newArrayListWithExpectedSize(var5);

      try {
         for(int var7 = 0; var7 < var5; ++var7) {
            var6.add(new ChunkBufferBuilderPack());
         }
      } catch (OutOfMemoryError var11) {
         LOGGER.warn("Allocated only {}/{} buffers", Integer.valueOf(var6.size()), Integer.valueOf(var5));
         int var8 = var6.size() * 2 / 3;

         for(int var9 = 0; var9 < var8; ++var9) {
            var6.remove(var6.size() - 1);
         }

         System.gc();
      }

      this.bufferCount = var6.size();
      this.availableChunkBuffers = Queues.newArrayBlockingQueue(this.bufferCount);
      this.availableChunkBuffers.addAll(var6);
      int var7 = Math.min(var4, this.bufferCount);
      if(var7 > 1) {
         for(int var8 = 0; var8 < var7; ++var8) {
            ChunkRenderWorker var9 = new ChunkRenderWorker(this);
            Thread var10 = THREAD_FACTORY.newThread(var9);
            var10.start();
            this.workers.add(var9);
            this.threads.add(var10);
         }
      }

   }

   public String getStats() {
      return this.threads.isEmpty()?String.format("pC: %03d, single-threaded", new Object[]{Integer.valueOf(this.chunksToBatch.size())}):String.format("pC: %03d, pU: %02d, aB: %02d", new Object[]{Integer.valueOf(this.chunksToBatch.size()), Integer.valueOf(this.pendingUploads.size()), Integer.valueOf(this.availableChunkBuffers.size())});
   }

   public void setCamera(Vec3 camera) {
      this.camera = camera;
   }

   public Vec3 getCameraPosition() {
      return this.camera;
   }

   public boolean uploadAllPendingUploadsUntil(long l) {
      boolean var3 = false;

      while(true) {
         boolean var4 = false;
         if(this.threads.isEmpty()) {
            ChunkCompileTask var5 = (ChunkCompileTask)this.chunksToBatch.poll();
            if(var5 != null) {
               try {
                  this.localWorker.doTask(var5);
                  var4 = true;
               } catch (InterruptedException var9) {
                  LOGGER.warn("Skipped task due to interrupt");
               }
            }
         }

         int var5 = 0;
         synchronized(this.pendingUploads) {
            while(var5 < 10) {
               ChunkRenderDispatcher.PendingUpload var7 = (ChunkRenderDispatcher.PendingUpload)this.pendingUploads.poll();
               if(var7 == null) {
                  break;
               }

               if(!var7.future.isDone()) {
                  var7.future.run();
                  var4 = true;
                  var3 = true;
                  ++var5;
               }
            }
         }

         if(l == 0L || !var4 || l < Util.getNanos()) {
            break;
         }
      }

      return var3;
   }

   public boolean rebuildChunkAsync(RenderChunk renderChunk) {
      renderChunk.getTaskLock().lock();

      boolean var4;
      try {
         ChunkCompileTask var2 = renderChunk.createCompileTask();
         var2.addCancelListener(() -> {
            this.chunksToBatch.remove(var2);
         });
         boolean var3 = this.chunksToBatch.offer(var2);
         if(!var3) {
            var2.cancel();
         }

         var4 = var3;
      } finally {
         renderChunk.getTaskLock().unlock();
      }

      return var4;
   }

   public boolean rebuildChunkSync(RenderChunk renderChunk) {
      renderChunk.getTaskLock().lock();

      boolean var3;
      try {
         ChunkCompileTask var2 = renderChunk.createCompileTask();

         try {
            this.localWorker.doTask(var2);
         } catch (InterruptedException var7) {
            ;
         }

         var3 = true;
      } finally {
         renderChunk.getTaskLock().unlock();
      }

      return var3;
   }

   public void blockUntilClear() {
      this.clearBatchQueue();
      List<ChunkBufferBuilderPack> var1 = Lists.newArrayList();

      while(((List)var1).size() != this.bufferCount) {
         this.uploadAllPendingUploadsUntil(Long.MAX_VALUE);

         try {
            var1.add(this.takeChunkBufferBuilder());
         } catch (InterruptedException var3) {
            ;
         }
      }

      this.availableChunkBuffers.addAll(var1);
   }

   public void releaseChunkBufferBuilder(ChunkBufferBuilderPack chunkBufferBuilderPack) {
      this.availableChunkBuffers.add(chunkBufferBuilderPack);
   }

   public ChunkBufferBuilderPack takeChunkBufferBuilder() throws InterruptedException {
      return (ChunkBufferBuilderPack)this.availableChunkBuffers.take();
   }

   public ChunkCompileTask takeChunk() throws InterruptedException {
      return (ChunkCompileTask)this.chunksToBatch.take();
   }

   public boolean resortChunkTransparencyAsync(RenderChunk renderChunk) {
      renderChunk.getTaskLock().lock();

      boolean var3;
      try {
         ChunkCompileTask var2 = renderChunk.createTransparencySortTask();
         if(var2 == null) {
            var3 = true;
            return var3;
         }

         var2.addCancelListener(() -> {
            this.chunksToBatch.remove(var2);
         });
         var3 = this.chunksToBatch.offer(var2);
      } finally {
         renderChunk.getTaskLock().unlock();
      }

      return var3;
   }

   public ListenableFuture uploadChunkLayer(BlockLayer blockLayer, BufferBuilder bufferBuilder, RenderChunk renderChunk, CompiledChunk compiledChunk, double var5) {
      if(Minecraft.getInstance().isSameThread()) {
         if(GLX.useVbo()) {
            this.uploadChunkLayer(bufferBuilder, renderChunk.getBuffer(blockLayer.ordinal()));
         } else {
            this.compileChunkLayerIntoGlList(bufferBuilder, ((ListedRenderChunk)renderChunk).getGlListId(blockLayer, compiledChunk));
         }

         bufferBuilder.offset(0.0D, 0.0D, 0.0D);
         return Futures.immediateFuture((Object)null);
      } else {
         ListenableFutureTask<Void> var7 = ListenableFutureTask.create(() -> {
            this.uploadChunkLayer(blockLayer, bufferBuilder, renderChunk, compiledChunk, var5);
         }, (Object)null);
         synchronized(this.pendingUploads) {
            this.pendingUploads.add(new ChunkRenderDispatcher.PendingUpload(var7, var5));
            return var7;
         }
      }
   }

   private void compileChunkLayerIntoGlList(BufferBuilder bufferBuilder, int var2) {
      GlStateManager.newList(var2, 4864);
      this.uploader.end(bufferBuilder);
      GlStateManager.endList();
   }

   private void uploadChunkLayer(BufferBuilder bufferBuilder, VertexBuffer vertexBuffer) {
      this.vboUploader.setBuffer(vertexBuffer);
      this.vboUploader.end(bufferBuilder);
   }

   public void clearBatchQueue() {
      while(!this.chunksToBatch.isEmpty()) {
         ChunkCompileTask var1 = (ChunkCompileTask)this.chunksToBatch.poll();
         if(var1 != null) {
            var1.cancel();
         }
      }

   }

   public boolean isQueueEmpty() {
      return this.chunksToBatch.isEmpty() && this.pendingUploads.isEmpty();
   }

   public void dispose() {
      this.clearBatchQueue();

      for(ChunkRenderWorker var2 : this.workers) {
         var2.stop();
      }

      for(Thread var2 : this.threads) {
         try {
            var2.interrupt();
            var2.join();
         } catch (InterruptedException var4) {
            LOGGER.warn("Interrupted whilst waiting for worker to die", var4);
         }
      }

      this.availableChunkBuffers.clear();
   }

   @ClientJarOnly
   class PendingUpload implements Comparable {
      private final ListenableFutureTask future;
      private final double dist;

      public PendingUpload(ListenableFutureTask future, double dist) {
         this.future = future;
         this.dist = dist;
      }

      public int compareTo(ChunkRenderDispatcher.PendingUpload chunkRenderDispatcher$PendingUpload) {
         return Doubles.compare(this.dist, chunkRenderDispatcher$PendingUpload.dist);
      }

      // $FF: synthetic method
      public int compareTo(Object var1) {
         return this.compareTo((ChunkRenderDispatcher.PendingUpload)var1);
      }
   }
}
