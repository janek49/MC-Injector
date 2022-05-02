package net.minecraft.world.level.lighting;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class LayerLightEngine extends DynamicGraphMinFixedPoint implements LayerLightEventListener {
   private static final Direction[] DIRECTIONS = Direction.values();
   protected final LightChunkGetter chunkSource;
   protected final LightLayer layer;
   protected final LayerLightSectionStorage storage;
   private boolean runningLightUpdates;
   protected final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
   private final long[] lastChunkPos = new long[2];
   private final BlockGetter[] lastChunk = new BlockGetter[2];

   public LayerLightEngine(LightChunkGetter chunkSource, LightLayer layer, LayerLightSectionStorage storage) {
      super(16, 256, 8192);
      this.chunkSource = chunkSource;
      this.layer = layer;
      this.storage = storage;
      this.clearCache();
   }

   protected void checkNode(long l) {
      this.storage.runAllUpdates();
      if(this.storage.storingLightForSection(SectionPos.blockToSection(l))) {
         super.checkNode(l);
      }

   }

   @Nullable
   private BlockGetter getChunk(int var1, int var2) {
      long var3 = ChunkPos.asLong(var1, var2);

      for(int var5 = 0; var5 < 2; ++var5) {
         if(var3 == this.lastChunkPos[var5]) {
            return this.lastChunk[var5];
         }
      }

      BlockGetter var5 = this.chunkSource.getChunkForLighting(var1, var2);

      for(int var6 = 1; var6 > 0; --var6) {
         this.lastChunkPos[var6] = this.lastChunkPos[var6 - 1];
         this.lastChunk[var6] = this.lastChunk[var6 - 1];
      }

      this.lastChunkPos[0] = var3;
      this.lastChunk[0] = var5;
      return var5;
   }

   private void clearCache() {
      Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
      Arrays.fill(this.lastChunk, (Object)null);
   }

   protected BlockState getStateAndOpacity(long var1, @Nullable AtomicInteger atomicInteger) {
      if(var1 == Long.MAX_VALUE) {
         if(atomicInteger != null) {
            atomicInteger.set(0);
         }

         return Blocks.AIR.defaultBlockState();
      } else {
         int var4 = SectionPos.blockToSectionCoord(BlockPos.getX(var1));
         int var5 = SectionPos.blockToSectionCoord(BlockPos.getZ(var1));
         BlockGetter var6 = this.getChunk(var4, var5);
         if(var6 == null) {
            if(atomicInteger != null) {
               atomicInteger.set(16);
            }

            return Blocks.BEDROCK.defaultBlockState();
         } else {
            this.pos.set(var1);
            BlockState var7 = var6.getBlockState(this.pos);
            boolean var8 = var7.canOcclude() && var7.useShapeForLightOcclusion();
            if(atomicInteger != null) {
               atomicInteger.set(var7.getLightBlock(this.chunkSource.getLevel(), this.pos));
            }

            return var8?var7:Blocks.AIR.defaultBlockState();
         }
      }
   }

   protected VoxelShape getShape(BlockState blockState, long var2, Direction direction) {
      return blockState.canOcclude()?blockState.getFaceOcclusionShape(this.chunkSource.getLevel(), this.pos.set(var2), direction):Shapes.empty();
   }

   public static int getLightBlockInto(BlockGetter blockGetter, BlockState var1, BlockPos var2, BlockState var3, BlockPos var4, Direction direction, int var6) {
      boolean var7 = var1.canOcclude() && var1.useShapeForLightOcclusion();
      boolean var8 = var3.canOcclude() && var3.useShapeForLightOcclusion();
      if(!var7 && !var8) {
         return var6;
      } else {
         VoxelShape var9 = var7?var1.getOcclusionShape(blockGetter, var2):Shapes.empty();
         VoxelShape var10 = var8?var3.getOcclusionShape(blockGetter, var4):Shapes.empty();
         return Shapes.mergedFaceOccludes(var9, var10, direction)?16:var6;
      }
   }

   protected boolean isSource(long l) {
      return l == Long.MAX_VALUE;
   }

   protected int getComputedLevel(long var1, long var3, int var5) {
      return 0;
   }

   protected int getLevel(long l) {
      return l == Long.MAX_VALUE?0:15 - this.storage.getStoredLevel(l);
   }

   protected int getLevel(DataLayer dataLayer, long var2) {
      return 15 - dataLayer.get(SectionPos.sectionRelative(BlockPos.getX(var2)), SectionPos.sectionRelative(BlockPos.getY(var2)), SectionPos.sectionRelative(BlockPos.getZ(var2)));
   }

   protected void setLevel(long var1, int var3) {
      this.storage.setStoredLevel(var1, Math.min(15, 15 - var3));
   }

   protected int computeLevelFromNeighbor(long var1, long var3, int var5) {
      return 0;
   }

   public boolean hasLightWork() {
      return this.hasWork() || this.storage.hasWork() || this.storage.hasInconsistencies();
   }

   public int runUpdates(int var1, boolean var2, boolean var3) {
      if(!this.runningLightUpdates) {
         if(this.storage.hasWork()) {
            var1 = this.storage.runUpdates(var1);
            if(var1 == 0) {
               return var1;
            }
         }

         this.storage.markNewInconsistencies(this, var2, var3);
      }

      this.runningLightUpdates = true;
      if(this.hasWork()) {
         var1 = this.runUpdates(var1);
         this.clearCache();
         if(var1 == 0) {
            return var1;
         }
      }

      this.runningLightUpdates = false;
      this.storage.swapSectionMap();
      return var1;
   }

   protected void queueSectionData(long var1, @Nullable DataLayer dataLayer) {
      this.storage.queueSectionData(var1, dataLayer);
   }

   @Nullable
   public DataLayer getDataLayerData(SectionPos sectionPos) {
      return this.storage.getDataLayerData(sectionPos.asLong());
   }

   public int getLightValue(BlockPos blockPos) {
      return this.storage.getLightValue(blockPos.asLong());
   }

   public String getDebugData(long l) {
      return "" + this.storage.getLevel(l);
   }

   public void checkBlock(BlockPos blockPos) {
      long var2 = blockPos.asLong();
      this.checkNode(var2);

      for(Direction var7 : DIRECTIONS) {
         this.checkNode(BlockPos.offset(var2, var7));
      }

   }

   public void onBlockEmissionIncrease(BlockPos blockPos, int var2) {
   }

   public void updateSectionStatus(SectionPos sectionPos, boolean var2) {
      this.storage.updateSectionStatus(sectionPos.asLong(), var2);
   }

   public void enableLightSources(ChunkPos chunkPos, boolean var2) {
      long var3 = SectionPos.getZeroNode(SectionPos.asLong(chunkPos.x, 0, chunkPos.z));
      this.storage.runAllUpdates();
      this.storage.enableLightSources(var3, var2);
   }

   public void retainData(ChunkPos chunkPos, boolean var2) {
      long var3 = SectionPos.getZeroNode(SectionPos.asLong(chunkPos.x, 0, chunkPos.z));
      this.storage.retainData(var3, var2);
   }
}
