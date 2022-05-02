package net.minecraft.client.renderer.chunk;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.chunk.ChunkCompileTask;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

@ClientJarOnly
public class RenderChunk {
   private volatile Level level;
   private final LevelRenderer renderer;
   public static int updateCounter;
   public CompiledChunk compiled = CompiledChunk.UNCOMPILED;
   private final ReentrantLock taskLock = new ReentrantLock();
   private final ReentrantLock compileLock = new ReentrantLock();
   private ChunkCompileTask pendingTask;
   private final Set globalBlockEntities = Sets.newHashSet();
   private final VertexBuffer[] buffers = new VertexBuffer[BlockLayer.values().length];
   public AABB bb;
   private int lastFrame = -1;
   private boolean dirty = true;
   private final BlockPos.MutableBlockPos origin = new BlockPos.MutableBlockPos(-1, -1, -1);
   private final BlockPos.MutableBlockPos[] relativeOrigins = (BlockPos.MutableBlockPos[])Util.make(new BlockPos.MutableBlockPos[6], (blockPos$MutableBlockPoses) -> {
      for(int var1 = 0; var1 < blockPos$MutableBlockPoses.length; ++var1) {
         blockPos$MutableBlockPoses[var1] = new BlockPos.MutableBlockPos();
      }

   });
   private boolean playerChanged;

   public RenderChunk(Level level, LevelRenderer renderer) {
      this.level = level;
      this.renderer = renderer;
      if(GLX.useVbo()) {
         for(int var3 = 0; var3 < BlockLayer.values().length; ++var3) {
            this.buffers[var3] = new VertexBuffer(DefaultVertexFormat.BLOCK);
         }
      }

   }

   private static boolean doesChunkExistAt(BlockPos blockPos, Level level) {
      return !level.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4).isEmpty();
   }

   public boolean hasAllNeighbors() {
      int var1 = 24;
      if(this.getDistToPlayerSqr() <= 576.0D) {
         return true;
      } else {
         Level var2 = this.getLevel();
         return doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()], var2) && doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()], var2) && doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()], var2) && doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()], var2);
      }
   }

   public boolean setFrame(int frame) {
      if(this.lastFrame == frame) {
         return false;
      } else {
         this.lastFrame = frame;
         return true;
      }
   }

   public VertexBuffer getBuffer(int i) {
      return this.buffers[i];
   }

   public void setOrigin(int var1, int var2, int var3) {
      if(var1 != this.origin.getX() || var2 != this.origin.getY() || var3 != this.origin.getZ()) {
         this.reset();
         this.origin.set(var1, var2, var3);
         this.bb = new AABB((double)var1, (double)var2, (double)var3, (double)(var1 + 16), (double)(var2 + 16), (double)(var3 + 16));

         for(Direction var7 : Direction.values()) {
            this.relativeOrigins[var7.ordinal()].set((Vec3i)this.origin).move(var7, 16);
         }

      }
   }

   public void rebuildTransparent(float var1, float var2, float var3, ChunkCompileTask chunkCompileTask) {
      CompiledChunk var5 = chunkCompileTask.getCompiledChunk();
      if(var5.getTransparencyState() != null && !var5.isEmpty(BlockLayer.TRANSLUCENT)) {
         this.beginLayer(chunkCompileTask.getBuilders().builder(BlockLayer.TRANSLUCENT), this.origin);
         chunkCompileTask.getBuilders().builder(BlockLayer.TRANSLUCENT).restoreState(var5.getTransparencyState());
         this.preEndLayer(BlockLayer.TRANSLUCENT, var1, var2, var3, chunkCompileTask.getBuilders().builder(BlockLayer.TRANSLUCENT), var5);
      }
   }

   public void compile(float var1, float var2, float var3, ChunkCompileTask chunkCompileTask) {
      CompiledChunk var5 = new CompiledChunk();
      int var6 = 1;
      BlockPos var7 = this.origin.immutable();
      BlockPos var8 = var7.offset(15, 15, 15);
      Level var9 = this.level;
      if(var9 != null) {
         chunkCompileTask.getStatusLock().lock();

         try {
            if(chunkCompileTask.getStatus() != ChunkCompileTask.Status.COMPILING) {
               return;
            }

            chunkCompileTask.setCompiledChunk(var5);
         } finally {
            chunkCompileTask.getStatusLock().unlock();
         }

         VisGraph var10 = new VisGraph();
         HashSet var11 = Sets.newHashSet();
         RenderChunkRegion var12 = chunkCompileTask.takeRegion();
         if(var12 != null) {
            ++updateCounter;
            boolean[] vars13 = new boolean[BlockLayer.values().length];
            ModelBlockRenderer.enableCaching();
            Random var14 = new Random();
            BlockRenderDispatcher var15 = Minecraft.getInstance().getBlockRenderer();

            for(BlockPos var17 : BlockPos.betweenClosed(var7, var8)) {
               BlockState var18 = var12.getBlockState(var17);
               Block var19 = var18.getBlock();
               if(var18.isSolidRender(var12, var17)) {
                  var10.setOpaque(var17);
               }

               if(var19.isEntityBlock()) {
                  BlockEntity var20 = var12.getBlockEntity(var17, LevelChunk.EntityCreationType.CHECK);
                  if(var20 != null) {
                     BlockEntityRenderer<BlockEntity> var21 = BlockEntityRenderDispatcher.instance.getRenderer(var20);
                     if(var21 != null) {
                        var5.addRenderableBlockEntity(var20);
                        if(var21.shouldRenderOffScreen(var20)) {
                           var11.add(var20);
                        }
                     }
                  }
               }

               FluidState var20 = var12.getFluidState(var17);
               if(!var20.isEmpty()) {
                  BlockLayer var21 = var20.getRenderLayer();
                  int var22 = var21.ordinal();
                  BufferBuilder var23 = chunkCompileTask.getBuilders().builder(var22);
                  if(!var5.hasLayer(var21)) {
                     var5.layerIsPresent(var21);
                     this.beginLayer(var23, var7);
                  }

                  vars13[var22] |= var15.renderLiquid(var17, var12, var23, var20);
               }

               if(var18.getRenderShape() != RenderShape.INVISIBLE) {
                  BlockLayer var21 = var19.getRenderLayer();
                  int var22 = var21.ordinal();
                  BufferBuilder var23 = chunkCompileTask.getBuilders().builder(var22);
                  if(!var5.hasLayer(var21)) {
                     var5.layerIsPresent(var21);
                     this.beginLayer(var23, var7);
                  }

                  vars13[var22] |= var15.renderBatched(var18, var17, var12, var23, var14);
               }
            }

            for(BlockLayer var19 : BlockLayer.values()) {
               if(vars13[var19.ordinal()]) {
                  var5.setChanged(var19);
               }

               if(var5.hasLayer(var19)) {
                  this.preEndLayer(var19, var1, var2, var3, chunkCompileTask.getBuilders().builder(var19), var5);
               }
            }

            ModelBlockRenderer.clearCache();
         }

         var5.setVisibilitySet(var10.resolve());
         this.taskLock.lock();

         try {
            Set<BlockEntity> var13 = Sets.newHashSet(var11);
            Set<BlockEntity> var14 = Sets.newHashSet(this.globalBlockEntities);
            var13.removeAll(this.globalBlockEntities);
            var14.removeAll(var11);
            this.globalBlockEntities.clear();
            this.globalBlockEntities.addAll(var11);
            this.renderer.updateGlobalBlockEntities(var14, var13);
         } finally {
            this.taskLock.unlock();
         }

      }
   }

   protected void cancelCompile() {
      this.taskLock.lock();

      try {
         if(this.pendingTask != null && this.pendingTask.getStatus() != ChunkCompileTask.Status.DONE) {
            this.pendingTask.cancel();
            this.pendingTask = null;
         }
      } finally {
         this.taskLock.unlock();
      }

   }

   public ReentrantLock getTaskLock() {
      return this.taskLock;
   }

   public ChunkCompileTask createCompileTask() {
      this.taskLock.lock();

      ChunkCompileTask var4;
      try {
         this.cancelCompile();
         BlockPos var1 = this.origin.immutable();
         int var2 = 1;
         RenderChunkRegion var3 = RenderChunkRegion.createIfNotEmpty(this.level, var1.offset(-1, -1, -1), var1.offset(16, 16, 16), 1);
         this.pendingTask = new ChunkCompileTask(this, ChunkCompileTask.Type.REBUILD_CHUNK, this.getDistToPlayerSqr(), var3);
         var4 = this.pendingTask;
      } finally {
         this.taskLock.unlock();
      }

      return var4;
   }

   @Nullable
   public ChunkCompileTask createTransparencySortTask() {
      this.taskLock.lock();

      ChunkCompileTask var1;
      try {
         if(this.pendingTask == null || this.pendingTask.getStatus() != ChunkCompileTask.Status.PENDING) {
            if(this.pendingTask != null && this.pendingTask.getStatus() != ChunkCompileTask.Status.DONE) {
               this.pendingTask.cancel();
               this.pendingTask = null;
            }

            this.pendingTask = new ChunkCompileTask(this, ChunkCompileTask.Type.RESORT_TRANSPARENCY, this.getDistToPlayerSqr(), (RenderChunkRegion)null);
            this.pendingTask.setCompiledChunk(this.compiled);
            var1 = this.pendingTask;
            return var1;
         }

         var1 = null;
      } finally {
         this.taskLock.unlock();
      }

      return var1;
   }

   protected double getDistToPlayerSqr() {
      Camera var1 = Minecraft.getInstance().gameRenderer.getMainCamera();
      double var2 = this.bb.minX + 8.0D - var1.getPosition().x;
      double var4 = this.bb.minY + 8.0D - var1.getPosition().y;
      double var6 = this.bb.minZ + 8.0D - var1.getPosition().z;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   private void beginLayer(BufferBuilder bufferBuilder, BlockPos blockPos) {
      bufferBuilder.begin(7, DefaultVertexFormat.BLOCK);
      bufferBuilder.offset((double)(-blockPos.getX()), (double)(-blockPos.getY()), (double)(-blockPos.getZ()));
   }

   private void preEndLayer(BlockLayer blockLayer, float var2, float var3, float var4, BufferBuilder bufferBuilder, CompiledChunk compiledChunk) {
      if(blockLayer == BlockLayer.TRANSLUCENT && !compiledChunk.isEmpty(blockLayer)) {
         bufferBuilder.sortQuads(var2, var3, var4);
         compiledChunk.setTransparencyState(bufferBuilder.getState());
      }

      bufferBuilder.end();
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiled;
   }

   public void setCompiledChunk(CompiledChunk compiledChunk) {
      this.compileLock.lock();

      try {
         this.compiled = compiledChunk;
      } finally {
         this.compileLock.unlock();
      }

   }

   public void reset() {
      this.cancelCompile();
      this.compiled = CompiledChunk.UNCOMPILED;
      this.dirty = true;
   }

   public void releaseBuffers() {
      this.reset();
      this.level = null;

      for(int var1 = 0; var1 < BlockLayer.values().length; ++var1) {
         if(this.buffers[var1] != null) {
            this.buffers[var1].delete();
         }
      }

   }

   public BlockPos getOrigin() {
      return this.origin;
   }

   public void setDirty(boolean dirty) {
      if(this.dirty) {
         dirty |= this.playerChanged;
      }

      this.dirty = true;
      this.playerChanged = dirty;
   }

   public void setNotDirty() {
      this.dirty = false;
      this.playerChanged = false;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public boolean isDirtyFromPlayer() {
      return this.dirty && this.playerChanged;
   }

   public BlockPos getRelativeOrigin(Direction direction) {
      return this.relativeOrigins[direction.ordinal()];
   }

   public Level getLevel() {
      return this.level;
   }
}
