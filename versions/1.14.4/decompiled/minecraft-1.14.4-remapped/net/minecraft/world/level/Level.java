package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public abstract class Level implements BlockAndBiomeGetter, LevelAccessor, AutoCloseable {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Direction[] DIRECTIONS = Direction.values();
   public final List blockEntityList = Lists.newArrayList();
   public final List tickableBlockEntities = Lists.newArrayList();
   protected final List pendingBlockEntities = Lists.newArrayList();
   protected final List blockEntitiesToUnload = Lists.newArrayList();
   private final long cloudColor = 16777215L;
   private final Thread thread;
   private int skyDarken;
   protected int randValue = (new Random()).nextInt();
   protected final int addend = 1013904223;
   protected float oRainLevel;
   protected float rainLevel;
   protected float oThunderLevel;
   protected float thunderLevel;
   private int skyFlashTime;
   public final Random random = new Random();
   public final Dimension dimension;
   protected final ChunkSource chunkSource;
   protected final LevelData levelData;
   private final ProfilerFiller profiler;
   public final boolean isClientSide;
   protected boolean updatingBlockEntities;
   private final WorldBorder worldBorder;

   protected Level(LevelData levelData, DimensionType dimensionType, BiFunction biFunction, ProfilerFiller profiler, boolean isClientSide) {
      this.profiler = profiler;
      this.levelData = levelData;
      this.dimension = dimensionType.create(this);
      this.chunkSource = (ChunkSource)biFunction.apply(this, this.dimension);
      this.isClientSide = isClientSide;
      this.worldBorder = this.dimension.createWorldBorder();
      this.thread = Thread.currentThread();
   }

   public Biome getBiome(BlockPos blockPos) {
      ChunkSource var2 = this.getChunkSource();
      LevelChunk var3 = var2.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, false);
      if(var3 != null) {
         return var3.getBiome(blockPos);
      } else {
         ChunkGenerator<?> var4 = this.getChunkSource().getGenerator();
         return var4 == null?Biomes.PLAINS:var4.getBiomeSource().getBiome(blockPos);
      }
   }

   public boolean isClientSide() {
      return this.isClientSide;
   }

   @Nullable
   public MinecraftServer getServer() {
      return null;
   }

   public void validateSpawn() {
      this.setSpawnPos(new BlockPos(8, 64, 8));
   }

   public BlockState getTopBlockState(BlockPos blockPos) {
      BlockPos blockPos;
      for(blockPos = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ()); !this.isEmptyBlock(blockPos.above()); blockPos = blockPos.above()) {
         ;
      }

      return this.getBlockState(blockPos);
   }

   public static boolean isInWorldBounds(BlockPos blockPos) {
      return !isOutsideBuildHeight(blockPos) && blockPos.getX() >= -30000000 && blockPos.getZ() >= -30000000 && blockPos.getX() < 30000000 && blockPos.getZ() < 30000000;
   }

   public static boolean isOutsideBuildHeight(BlockPos blockPos) {
      return isOutsideBuildHeight(blockPos.getY());
   }

   public static boolean isOutsideBuildHeight(int i) {
      return i < 0 || i >= 256;
   }

   public LevelChunk getChunkAt(BlockPos blockPos) {
      return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
   }

   public LevelChunk getChunk(int var1, int var2) {
      return (LevelChunk)this.getChunk(var1, var2, ChunkStatus.FULL);
   }

   public ChunkAccess getChunk(int var1, int var2, ChunkStatus chunkStatus, boolean var4) {
      ChunkAccess chunkAccess = this.chunkSource.getChunk(var1, var2, chunkStatus, var4);
      if(chunkAccess == null && var4) {
         throw new IllegalStateException("Should always be able to create a chunk!");
      } else {
         return chunkAccess;
      }
   }

   public boolean setBlock(BlockPos blockPos, BlockState blockState, int var3) {
      if(isOutsideBuildHeight(blockPos)) {
         return false;
      } else if(!this.isClientSide && this.levelData.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES) {
         return false;
      } else {
         LevelChunk var4 = this.getChunkAt(blockPos);
         Block var5 = blockState.getBlock();
         BlockState var6 = var4.setBlockState(blockPos, blockState, (var3 & 64) != 0);
         if(var6 == null) {
            return false;
         } else {
            BlockState var7 = this.getBlockState(blockPos);
            if(var7 != var6 && (var7.getLightBlock(this, blockPos) != var6.getLightBlock(this, blockPos) || var7.getLightEmission() != var6.getLightEmission() || var7.useShapeForLightOcclusion() || var6.useShapeForLightOcclusion())) {
               this.profiler.push("queueCheckLight");
               this.getChunkSource().getLightEngine().checkBlock(blockPos);
               this.profiler.pop();
            }

            if(var7 == blockState) {
               if(var6 != var7) {
                  this.setBlocksDirty(blockPos, var6, var7);
               }

               if((var3 & 2) != 0 && (!this.isClientSide || (var3 & 4) == 0) && (this.isClientSide || var4.getFullStatus() != null && var4.getFullStatus().isOrAfter(ChunkHolder.FullChunkStatus.TICKING))) {
                  this.sendBlockUpdated(blockPos, var6, blockState, var3);
               }

               if(!this.isClientSide && (var3 & 1) != 0) {
                  this.blockUpdated(blockPos, var6.getBlock());
                  if(blockState.hasAnalogOutputSignal()) {
                     this.updateNeighbourForOutputSignal(blockPos, var5);
                  }
               }

               if((var3 & 16) == 0) {
                  int var8 = var3 & -2;
                  var6.updateIndirectNeighbourShapes(this, blockPos, var8);
                  blockState.updateNeighbourShapes(this, blockPos, var8);
                  blockState.updateIndirectNeighbourShapes(this, blockPos, var8);
               }

               this.onBlockStateChange(blockPos, var6, var7);
            }

            return true;
         }
      }
   }

   public void onBlockStateChange(BlockPos blockPos, BlockState var2, BlockState var3) {
   }

   public boolean removeBlock(BlockPos blockPos, boolean var2) {
      FluidState var3 = this.getFluidState(blockPos);
      return this.setBlock(blockPos, var3.createLegacyBlock(), 3 | (var2?64:0));
   }

   public boolean destroyBlock(BlockPos blockPos, boolean var2) {
      BlockState var3 = this.getBlockState(blockPos);
      if(var3.isAir()) {
         return false;
      } else {
         FluidState var4 = this.getFluidState(blockPos);
         this.levelEvent(2001, blockPos, Block.getId(var3));
         if(var2) {
            BlockEntity var5 = var3.getBlock().isEntityBlock()?this.getBlockEntity(blockPos):null;
            Block.dropResources(var3, this, blockPos, var5);
         }

         return this.setBlock(blockPos, var4.createLegacyBlock(), 3);
      }
   }

   public boolean setBlockAndUpdate(BlockPos blockPos, BlockState blockState) {
      return this.setBlock(blockPos, blockState, 3);
   }

   public abstract void sendBlockUpdated(BlockPos var1, BlockState var2, BlockState var3, int var4);

   public void blockUpdated(BlockPos blockPos, Block block) {
      if(this.levelData.getGeneratorType() != LevelType.DEBUG_ALL_BLOCK_STATES) {
         this.updateNeighborsAt(blockPos, block);
      }

   }

   public void setBlocksDirty(BlockPos blockPos, BlockState var2, BlockState var3) {
   }

   public void updateNeighborsAt(BlockPos blockPos, Block block) {
      this.neighborChanged(blockPos.west(), block, blockPos);
      this.neighborChanged(blockPos.east(), block, blockPos);
      this.neighborChanged(blockPos.below(), block, blockPos);
      this.neighborChanged(blockPos.above(), block, blockPos);
      this.neighborChanged(blockPos.north(), block, blockPos);
      this.neighborChanged(blockPos.south(), block, blockPos);
   }

   public void updateNeighborsAtExceptFromFacing(BlockPos blockPos, Block block, Direction direction) {
      if(direction != Direction.WEST) {
         this.neighborChanged(blockPos.west(), block, blockPos);
      }

      if(direction != Direction.EAST) {
         this.neighborChanged(blockPos.east(), block, blockPos);
      }

      if(direction != Direction.DOWN) {
         this.neighborChanged(blockPos.below(), block, blockPos);
      }

      if(direction != Direction.UP) {
         this.neighborChanged(blockPos.above(), block, blockPos);
      }

      if(direction != Direction.NORTH) {
         this.neighborChanged(blockPos.north(), block, blockPos);
      }

      if(direction != Direction.SOUTH) {
         this.neighborChanged(blockPos.south(), block, blockPos);
      }

   }

   public void neighborChanged(BlockPos var1, Block block, BlockPos var3) {
      if(!this.isClientSide) {
         BlockState var4 = this.getBlockState(var1);

         try {
            var4.neighborChanged(this, var1, block, var3, false);
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Exception while updating neighbours");
            CrashReportCategory var7 = var6.addCategory("Block being updated");
            var7.setDetail("Source block type", () -> {
               try {
                  return String.format("ID #%s (%s // %s)", new Object[]{Registry.BLOCK.getKey(block), block.getDescriptionId(), block.getClass().getCanonicalName()});
               } catch (Throwable var2) {
                  return "ID #" + Registry.BLOCK.getKey(block);
               }
            });
            CrashReportCategory.populateBlockDetails(var7, var1, var4);
            throw new ReportedException(var6);
         }
      }
   }

   public int getRawBrightness(BlockPos blockPos, int var2) {
      if(blockPos.getX() >= -30000000 && blockPos.getZ() >= -30000000 && blockPos.getX() < 30000000 && blockPos.getZ() < 30000000) {
         if(blockPos.getY() < 0) {
            return 0;
         } else {
            if(blockPos.getY() >= 256) {
               blockPos = new BlockPos(blockPos.getX(), 255, blockPos.getZ());
            }

            return this.getChunkAt(blockPos).getRawBrightness(blockPos, var2);
         }
      } else {
         return 15;
      }
   }

   public int getHeight(Heightmap.Types heightmap$Types, int var2, int var3) {
      int var4;
      if(var2 >= -30000000 && var3 >= -30000000 && var2 < 30000000 && var3 < 30000000) {
         if(this.hasChunk(var2 >> 4, var3 >> 4)) {
            var4 = this.getChunk(var2 >> 4, var3 >> 4).getHeight(heightmap$Types, var2 & 15, var3 & 15) + 1;
         } else {
            var4 = 0;
         }
      } else {
         var4 = this.getSeaLevel() + 1;
      }

      return var4;
   }

   public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
      return this.getChunkSource().getLightEngine().getLayerListener(lightLayer).getLightValue(blockPos);
   }

   public BlockState getBlockState(BlockPos blockPos) {
      if(isOutsideBuildHeight(blockPos)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         LevelChunk var2 = this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
         return var2.getBlockState(blockPos);
      }
   }

   public FluidState getFluidState(BlockPos blockPos) {
      if(isOutsideBuildHeight(blockPos)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         LevelChunk var2 = this.getChunkAt(blockPos);
         return var2.getFluidState(blockPos);
      }
   }

   public boolean isDay() {
      return this.skyDarken < 4;
   }

   public void playSound(@Nullable Player player, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float var5, float var6) {
      this.playSound(player, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, soundEvent, soundSource, var5, var6);
   }

   public abstract void playSound(@Nullable Player var1, double var2, double var4, double var6, SoundEvent var8, SoundSource var9, float var10, float var11);

   public abstract void playSound(@Nullable Player var1, Entity var2, SoundEvent var3, SoundSource var4, float var5, float var6);

   public void playLocalSound(double var1, double var3, double var5, SoundEvent soundEvent, SoundSource soundSource, float var9, float var10, boolean var11) {
   }

   public void addParticle(ParticleOptions particleOptions, double var2, double var4, double var6, double var8, double var10, double var12) {
   }

   public void addParticle(ParticleOptions particleOptions, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
   }

   public void addAlwaysVisibleParticle(ParticleOptions particleOptions, double var2, double var4, double var6, double var8, double var10, double var12) {
   }

   public void addAlwaysVisibleParticle(ParticleOptions particleOptions, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
   }

   public float getSkyDarken(float f) {
      float var2 = this.getTimeOfDay(f);
      float var3 = 1.0F - (Mth.cos(var2 * 6.2831855F) * 2.0F + 0.2F);
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0D - (double)(this.getRainLevel(f) * 5.0F) / 16.0D));
      var3 = (float)((double)var3 * (1.0D - (double)(this.getThunderLevel(f) * 5.0F) / 16.0D));
      return var3 * 0.8F + 0.2F;
   }

   public Vec3 getSkyColor(BlockPos blockPos, float var2) {
      float var3 = this.getTimeOfDay(var2);
      float var4 = Mth.cos(var3 * 6.2831855F) * 2.0F + 0.5F;
      var4 = Mth.clamp(var4, 0.0F, 1.0F);
      Biome var5 = this.getBiome(blockPos);
      float var6 = var5.getTemperature(blockPos);
      int var7 = var5.getSkyColor(var6);
      float var8 = (float)(var7 >> 16 & 255) / 255.0F;
      float var9 = (float)(var7 >> 8 & 255) / 255.0F;
      float var10 = (float)(var7 & 255) / 255.0F;
      var8 = var8 * var4;
      var9 = var9 * var4;
      var10 = var10 * var4;
      float var11 = this.getRainLevel(var2);
      if(var11 > 0.0F) {
         float var12 = (var8 * 0.3F + var9 * 0.59F + var10 * 0.11F) * 0.6F;
         float var13 = 1.0F - var11 * 0.75F;
         var8 = var8 * var13 + var12 * (1.0F - var13);
         var9 = var9 * var13 + var12 * (1.0F - var13);
         var10 = var10 * var13 + var12 * (1.0F - var13);
      }

      float var12 = this.getThunderLevel(var2);
      if(var12 > 0.0F) {
         float var13 = (var8 * 0.3F + var9 * 0.59F + var10 * 0.11F) * 0.2F;
         float var14 = 1.0F - var12 * 0.75F;
         var8 = var8 * var14 + var13 * (1.0F - var14);
         var9 = var9 * var14 + var13 * (1.0F - var14);
         var10 = var10 * var14 + var13 * (1.0F - var14);
      }

      if(this.skyFlashTime > 0) {
         float var13 = (float)this.skyFlashTime - var2;
         if(var13 > 1.0F) {
            var13 = 1.0F;
         }

         var13 = var13 * 0.45F;
         var8 = var8 * (1.0F - var13) + 0.8F * var13;
         var9 = var9 * (1.0F - var13) + 0.8F * var13;
         var10 = var10 * (1.0F - var13) + 1.0F * var13;
      }

      return new Vec3((double)var8, (double)var9, (double)var10);
   }

   public float getSunAngle(float f) {
      float var2 = this.getTimeOfDay(f);
      return var2 * 6.2831855F;
   }

   public Vec3 getCloudColor(float f) {
      float var2 = this.getTimeOfDay(f);
      float var3 = Mth.cos(var2 * 6.2831855F) * 2.0F + 0.5F;
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      float var4 = 1.0F;
      float var5 = 1.0F;
      float var6 = 1.0F;
      float var7 = this.getRainLevel(f);
      if(var7 > 0.0F) {
         float var8 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.6F;
         float var9 = 1.0F - var7 * 0.95F;
         var4 = var4 * var9 + var8 * (1.0F - var9);
         var5 = var5 * var9 + var8 * (1.0F - var9);
         var6 = var6 * var9 + var8 * (1.0F - var9);
      }

      var4 = var4 * (var3 * 0.9F + 0.1F);
      var5 = var5 * (var3 * 0.9F + 0.1F);
      var6 = var6 * (var3 * 0.85F + 0.15F);
      float var8 = this.getThunderLevel(f);
      if(var8 > 0.0F) {
         float var9 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.2F;
         float var10 = 1.0F - var8 * 0.95F;
         var4 = var4 * var10 + var9 * (1.0F - var10);
         var5 = var5 * var10 + var9 * (1.0F - var10);
         var6 = var6 * var10 + var9 * (1.0F - var10);
      }

      return new Vec3((double)var4, (double)var5, (double)var6);
   }

   public Vec3 getFogColor(float f) {
      float var2 = this.getTimeOfDay(f);
      return this.dimension.getFogColor(var2, f);
   }

   public float getStarBrightness(float f) {
      float var2 = this.getTimeOfDay(f);
      float var3 = 1.0F - (Mth.cos(var2 * 6.2831855F) * 2.0F + 0.25F);
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      return var3 * var3 * 0.5F;
   }

   public boolean addBlockEntity(BlockEntity blockEntity) {
      if(this.updatingBlockEntities) {
         LOGGER.error("Adding block entity while ticking: {} @ {}", new Supplier[]{() -> {
            return Registry.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
         }, blockEntity::getBlockPos});
      }

      boolean var2 = this.blockEntityList.add(blockEntity);
      if(var2 && blockEntity instanceof TickableBlockEntity) {
         this.tickableBlockEntities.add(blockEntity);
      }

      if(this.isClientSide) {
         BlockPos var3 = blockEntity.getBlockPos();
         BlockState var4 = this.getBlockState(var3);
         this.sendBlockUpdated(var3, var4, var4, 2);
      }

      return var2;
   }

   public void addAllPendingBlockEntities(Collection collection) {
      if(this.updatingBlockEntities) {
         this.pendingBlockEntities.addAll(collection);
      } else {
         for(BlockEntity var3 : collection) {
            this.addBlockEntity(var3);
         }
      }

   }

   public void tickBlockEntities() {
      ProfilerFiller var1 = this.getProfiler();
      var1.push("blockEntities");
      if(!this.blockEntitiesToUnload.isEmpty()) {
         this.tickableBlockEntities.removeAll(this.blockEntitiesToUnload);
         this.blockEntityList.removeAll(this.blockEntitiesToUnload);
         this.blockEntitiesToUnload.clear();
      }

      this.updatingBlockEntities = true;
      Iterator<BlockEntity> var2 = this.tickableBlockEntities.iterator();

      while(var2.hasNext()) {
         BlockEntity var3 = (BlockEntity)var2.next();
         if(!var3.isRemoved() && var3.hasLevel()) {
            BlockPos var4 = var3.getBlockPos();
            if(this.chunkSource.isTickingChunk(var4) && this.getWorldBorder().isWithinBounds(var4)) {
               try {
                  var1.push(() -> {
                     return String.valueOf(BlockEntityType.getKey(var3.getType()));
                  });
                  if(var3.getType().isValid(this.getBlockState(var4).getBlock())) {
                     ((TickableBlockEntity)var3).tick();
                  } else {
                     var3.logInvalidState();
                  }

                  var1.pop();
               } catch (Throwable var8) {
                  CrashReport var6 = CrashReport.forThrowable(var8, "Ticking block entity");
                  CrashReportCategory var7 = var6.addCategory("Block entity being ticked");
                  var3.fillCrashReportCategory(var7);
                  throw new ReportedException(var6);
               }
            }
         }

         if(var3.isRemoved()) {
            var2.remove();
            this.blockEntityList.remove(var3);
            if(this.hasChunkAt(var3.getBlockPos())) {
               this.getChunkAt(var3.getBlockPos()).removeBlockEntity(var3.getBlockPos());
            }
         }
      }

      this.updatingBlockEntities = false;
      var1.popPush("pendingBlockEntities");
      if(!this.pendingBlockEntities.isEmpty()) {
         for(int var3 = 0; var3 < this.pendingBlockEntities.size(); ++var3) {
            BlockEntity var4 = (BlockEntity)this.pendingBlockEntities.get(var3);
            if(!var4.isRemoved()) {
               if(!this.blockEntityList.contains(var4)) {
                  this.addBlockEntity(var4);
               }

               if(this.hasChunkAt(var4.getBlockPos())) {
                  LevelChunk var5 = this.getChunkAt(var4.getBlockPos());
                  BlockState var6 = var5.getBlockState(var4.getBlockPos());
                  var5.setBlockEntity(var4.getBlockPos(), var4);
                  this.sendBlockUpdated(var4.getBlockPos(), var6, var6, 3);
               }
            }
         }

         this.pendingBlockEntities.clear();
      }

      var1.pop();
   }

   public void guardEntityTick(Consumer consumer, Entity entity) {
      try {
         consumer.accept(entity);
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, "Ticking entity");
         CrashReportCategory var5 = var4.addCategory("Entity being ticked");
         entity.fillCrashReportCategory(var5);
         throw new ReportedException(var4);
      }
   }

   public boolean containsAnyBlocks(AABB aABB) {
      int var2 = Mth.floor(aABB.minX);
      int var3 = Mth.ceil(aABB.maxX);
      int var4 = Mth.floor(aABB.minY);
      int var5 = Mth.ceil(aABB.maxY);
      int var6 = Mth.floor(aABB.minZ);
      int var7 = Mth.ceil(aABB.maxZ);
      BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var9 = null;

      try {
         for(int var10 = var2; var10 < var3; ++var10) {
            for(int var11 = var4; var11 < var5; ++var11) {
               for(int var12 = var6; var12 < var7; ++var12) {
                  BlockState var13 = this.getBlockState(var8.set(var10, var11, var12));
                  if(!var13.isAir()) {
                     boolean var14 = true;
                     return var14;
                  }
               }
            }
         }

         return false;
      } catch (Throwable var24) {
         var9 = var24;
         throw var24;
      } finally {
         if(var8 != null) {
            if(var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var23) {
                  var9.addSuppressed(var23);
               }
            } else {
               var8.close();
            }
         }

      }
   }

   public boolean containsFireBlock(AABB aABB) {
      int var2 = Mth.floor(aABB.minX);
      int var3 = Mth.ceil(aABB.maxX);
      int var4 = Mth.floor(aABB.minY);
      int var5 = Mth.ceil(aABB.maxY);
      int var6 = Mth.floor(aABB.minZ);
      int var7 = Mth.ceil(aABB.maxZ);
      if(this.hasChunksAt(var2, var4, var6, var3, var5, var7)) {
         BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var9 = null;

         try {
            for(int var10 = var2; var10 < var3; ++var10) {
               for(int var11 = var4; var11 < var5; ++var11) {
                  for(int var12 = var6; var12 < var7; ++var12) {
                     Block var13 = this.getBlockState(var8.set(var10, var11, var12)).getBlock();
                     if(var13 == Blocks.FIRE || var13 == Blocks.LAVA) {
                        boolean var14 = true;
                        return var14;
                     }
                  }
               }
            }

            return false;
         } catch (Throwable var24) {
            var9 = var24;
            throw var24;
         } finally {
            if(var8 != null) {
               if(var9 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var23) {
                     var9.addSuppressed(var23);
                  }
               } else {
                  var8.close();
               }
            }

         }
      } else {
         return false;
      }
   }

   @Nullable
   public BlockState containsBlock(AABB aABB, Block block) {
      int var3 = Mth.floor(aABB.minX);
      int var4 = Mth.ceil(aABB.maxX);
      int var5 = Mth.floor(aABB.minY);
      int var6 = Mth.ceil(aABB.maxY);
      int var7 = Mth.floor(aABB.minZ);
      int var8 = Mth.ceil(aABB.maxZ);
      if(this.hasChunksAt(var3, var5, var7, var4, var6, var8)) {
         BlockPos.PooledMutableBlockPos var9 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var10 = null;

         try {
            for(int var11 = var3; var11 < var4; ++var11) {
               for(int var12 = var5; var12 < var6; ++var12) {
                  for(int var13 = var7; var13 < var8; ++var13) {
                     BlockState var14 = this.getBlockState(var9.set(var11, var12, var13));
                     if(var14.getBlock() == block) {
                        BlockState var15 = var14;
                        return var15;
                     }
                  }
               }
            }

            return null;
         } catch (Throwable var25) {
            var10 = var25;
            throw var25;
         } finally {
            if(var9 != null) {
               if(var10 != null) {
                  try {
                     var9.close();
                  } catch (Throwable var24) {
                     var10.addSuppressed(var24);
                  }
               } else {
                  var9.close();
               }
            }

         }
      } else {
         return null;
      }
   }

   public boolean containsMaterial(AABB aABB, Material material) {
      int var3 = Mth.floor(aABB.minX);
      int var4 = Mth.ceil(aABB.maxX);
      int var5 = Mth.floor(aABB.minY);
      int var6 = Mth.ceil(aABB.maxY);
      int var7 = Mth.floor(aABB.minZ);
      int var8 = Mth.ceil(aABB.maxZ);
      BlockMaterialPredicate var9 = BlockMaterialPredicate.forMaterial(material);
      return BlockPos.betweenClosedStream(var3, var5, var7, var4 - 1, var6 - 1, var8 - 1).anyMatch((blockPos) -> {
         return var9.test(this.getBlockState(blockPos));
      });
   }

   public Explosion explode(@Nullable Entity entity, double var2, double var4, double var6, float var8, Explosion.BlockInteraction explosion$BlockInteraction) {
      return this.explode(entity, (DamageSource)null, var2, var4, var6, var8, false, explosion$BlockInteraction);
   }

   public Explosion explode(@Nullable Entity entity, double var2, double var4, double var6, float var8, boolean var9, Explosion.BlockInteraction explosion$BlockInteraction) {
      return this.explode(entity, (DamageSource)null, var2, var4, var6, var8, var9, explosion$BlockInteraction);
   }

   public Explosion explode(@Nullable Entity entity, @Nullable DamageSource damageSource, double var3, double var5, double var7, float var9, boolean var10, Explosion.BlockInteraction explosion$BlockInteraction) {
      Explosion explosion = new Explosion(this, entity, var3, var5, var7, var9, var10, explosion$BlockInteraction);
      if(damageSource != null) {
         explosion.setDamageSource(damageSource);
      }

      explosion.explode();
      explosion.finalizeExplosion(true);
      return explosion;
   }

   public boolean extinguishFire(@Nullable Player player, BlockPos blockPos, Direction direction) {
      blockPos = blockPos.relative(direction);
      if(this.getBlockState(blockPos).getBlock() == Blocks.FIRE) {
         this.levelEvent(player, 1009, blockPos, 0);
         this.removeBlock(blockPos, false);
         return true;
      } else {
         return false;
      }
   }

   public String gatherChunkSourceStats() {
      return this.chunkSource.gatherStats();
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos) {
      if(isOutsideBuildHeight(blockPos)) {
         return null;
      } else if(!this.isClientSide && Thread.currentThread() != this.thread) {
         return null;
      } else {
         BlockEntity blockEntity = null;
         if(this.updatingBlockEntities) {
            blockEntity = this.getPendingBlockEntityAt(blockPos);
         }

         if(blockEntity == null) {
            blockEntity = this.getChunkAt(blockPos).getBlockEntity(blockPos, LevelChunk.EntityCreationType.IMMEDIATE);
         }

         if(blockEntity == null) {
            blockEntity = this.getPendingBlockEntityAt(blockPos);
         }

         return blockEntity;
      }
   }

   @Nullable
   private BlockEntity getPendingBlockEntityAt(BlockPos blockPos) {
      for(int var2 = 0; var2 < this.pendingBlockEntities.size(); ++var2) {
         BlockEntity var3 = (BlockEntity)this.pendingBlockEntities.get(var2);
         if(!var3.isRemoved() && var3.getBlockPos().equals(blockPos)) {
            return var3;
         }
      }

      return null;
   }

   public void setBlockEntity(BlockPos blockPos, @Nullable BlockEntity blockEntity) {
      if(!isOutsideBuildHeight(blockPos)) {
         if(blockEntity != null && !blockEntity.isRemoved()) {
            if(this.updatingBlockEntities) {
               blockEntity.setPosition(blockPos);
               Iterator<BlockEntity> var3 = this.pendingBlockEntities.iterator();

               while(var3.hasNext()) {
                  BlockEntity var4 = (BlockEntity)var3.next();
                  if(var4.getBlockPos().equals(blockPos)) {
                     var4.setRemoved();
                     var3.remove();
                  }
               }

               this.pendingBlockEntities.add(blockEntity);
            } else {
               this.getChunkAt(blockPos).setBlockEntity(blockPos, blockEntity);
               this.addBlockEntity(blockEntity);
            }
         }

      }
   }

   public void removeBlockEntity(BlockPos blockPos) {
      BlockEntity var2 = this.getBlockEntity(blockPos);
      if(var2 != null && this.updatingBlockEntities) {
         var2.setRemoved();
         this.pendingBlockEntities.remove(var2);
      } else {
         if(var2 != null) {
            this.pendingBlockEntities.remove(var2);
            this.blockEntityList.remove(var2);
            this.tickableBlockEntities.remove(var2);
         }

         this.getChunkAt(blockPos).removeBlockEntity(blockPos);
      }

   }

   public boolean isLoaded(BlockPos blockPos) {
      return isOutsideBuildHeight(blockPos)?false:this.chunkSource.hasChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
   }

   public boolean loadedAndEntityCanStandOn(BlockPos blockPos, Entity entity) {
      if(isOutsideBuildHeight(blockPos)) {
         return false;
      } else {
         ChunkAccess var3 = this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FULL, false);
         return var3 == null?false:var3.getBlockState(blockPos).entityCanStandOn(this, blockPos, entity);
      }
   }

   public void updateSkyBrightness() {
      double var1 = 1.0D - (double)(this.getRainLevel(1.0F) * 5.0F) / 16.0D;
      double var3 = 1.0D - (double)(this.getThunderLevel(1.0F) * 5.0F) / 16.0D;
      double var5 = 0.5D + 2.0D * Mth.clamp((double)Mth.cos(this.getTimeOfDay(1.0F) * 6.2831855F), -0.25D, 0.25D);
      this.skyDarken = (int)((1.0D - var5 * var1 * var3) * 11.0D);
   }

   public void setSpawnSettings(boolean var1, boolean var2) {
      this.getChunkSource().setSpawnSettings(var1, var2);
   }

   protected void prepareWeather() {
      if(this.levelData.isRaining()) {
         this.rainLevel = 1.0F;
         if(this.levelData.isThundering()) {
            this.thunderLevel = 1.0F;
         }
      }

   }

   public void close() throws IOException {
      this.chunkSource.close();
   }

   public ChunkStatus statusForCollisions() {
      return ChunkStatus.FULL;
   }

   public List getEntities(@Nullable Entity entity, AABB aABB, @Nullable Predicate predicate) {
      List<Entity> list = Lists.newArrayList();
      int var5 = Mth.floor((aABB.minX - 2.0D) / 16.0D);
      int var6 = Mth.floor((aABB.maxX + 2.0D) / 16.0D);
      int var7 = Mth.floor((aABB.minZ - 2.0D) / 16.0D);
      int var8 = Mth.floor((aABB.maxZ + 2.0D) / 16.0D);

      for(int var9 = var5; var9 <= var6; ++var9) {
         for(int var10 = var7; var10 <= var8; ++var10) {
            LevelChunk var11 = this.getChunkSource().getChunk(var9, var10, false);
            if(var11 != null) {
               var11.getEntities(entity, aABB, list, predicate);
            }
         }
      }

      return list;
   }

   public List getEntities(@Nullable EntityType entityType, AABB aABB, Predicate predicate) {
      int var4 = Mth.floor((aABB.minX - 2.0D) / 16.0D);
      int var5 = Mth.ceil((aABB.maxX + 2.0D) / 16.0D);
      int var6 = Mth.floor((aABB.minZ - 2.0D) / 16.0D);
      int var7 = Mth.ceil((aABB.maxZ + 2.0D) / 16.0D);
      List<Entity> var8 = Lists.newArrayList();

      for(int var9 = var4; var9 < var5; ++var9) {
         for(int var10 = var6; var10 < var7; ++var10) {
            LevelChunk var11 = this.getChunkSource().getChunk(var9, var10, false);
            if(var11 != null) {
               var11.getEntities(entityType, aABB, var8, predicate);
            }
         }
      }

      return var8;
   }

   public List getEntitiesOfClass(Class class, AABB aABB, @Nullable Predicate predicate) {
      int var4 = Mth.floor((aABB.minX - 2.0D) / 16.0D);
      int var5 = Mth.ceil((aABB.maxX + 2.0D) / 16.0D);
      int var6 = Mth.floor((aABB.minZ - 2.0D) / 16.0D);
      int var7 = Mth.ceil((aABB.maxZ + 2.0D) / 16.0D);
      List<T> var8 = Lists.newArrayList();
      ChunkSource var9 = this.getChunkSource();

      for(int var10 = var4; var10 < var5; ++var10) {
         for(int var11 = var6; var11 < var7; ++var11) {
            LevelChunk var12 = var9.getChunk(var10, var11, false);
            if(var12 != null) {
               var12.getEntitiesOfClass(class, aABB, var8, predicate);
            }
         }
      }

      return var8;
   }

   public List getLoadedEntitiesOfClass(Class class, AABB aABB, @Nullable Predicate predicate) {
      int var4 = Mth.floor((aABB.minX - 2.0D) / 16.0D);
      int var5 = Mth.ceil((aABB.maxX + 2.0D) / 16.0D);
      int var6 = Mth.floor((aABB.minZ - 2.0D) / 16.0D);
      int var7 = Mth.ceil((aABB.maxZ + 2.0D) / 16.0D);
      List<T> var8 = Lists.newArrayList();
      ChunkSource var9 = this.getChunkSource();

      for(int var10 = var4; var10 < var5; ++var10) {
         for(int var11 = var6; var11 < var7; ++var11) {
            LevelChunk var12 = var9.getChunkNow(var10, var11);
            if(var12 != null) {
               var12.getEntitiesOfClass(class, aABB, var8, predicate);
            }
         }
      }

      return var8;
   }

   @Nullable
   public abstract Entity getEntity(int var1);

   public void blockEntityChanged(BlockPos blockPos, BlockEntity blockEntity) {
      if(this.hasChunkAt(blockPos)) {
         this.getChunkAt(blockPos).markUnsaved();
      }

   }

   public int getSeaLevel() {
      return 63;
   }

   public Level getLevel() {
      return this;
   }

   public LevelType getGeneratorType() {
      return this.levelData.getGeneratorType();
   }

   public int getDirectSignalTo(BlockPos blockPos) {
      int var2 = 0;
      var2 = Math.max(var2, this.getDirectSignal(blockPos.below(), Direction.DOWN));
      if(var2 >= 15) {
         return var2;
      } else {
         var2 = Math.max(var2, this.getDirectSignal(blockPos.above(), Direction.UP));
         if(var2 >= 15) {
            return var2;
         } else {
            var2 = Math.max(var2, this.getDirectSignal(blockPos.north(), Direction.NORTH));
            if(var2 >= 15) {
               return var2;
            } else {
               var2 = Math.max(var2, this.getDirectSignal(blockPos.south(), Direction.SOUTH));
               if(var2 >= 15) {
                  return var2;
               } else {
                  var2 = Math.max(var2, this.getDirectSignal(blockPos.west(), Direction.WEST));
                  if(var2 >= 15) {
                     return var2;
                  } else {
                     var2 = Math.max(var2, this.getDirectSignal(blockPos.east(), Direction.EAST));
                     return var2 >= 15?var2:var2;
                  }
               }
            }
         }
      }
   }

   public boolean hasSignal(BlockPos blockPos, Direction direction) {
      return this.getSignal(blockPos, direction) > 0;
   }

   public int getSignal(BlockPos blockPos, Direction direction) {
      BlockState var3 = this.getBlockState(blockPos);
      return var3.isRedstoneConductor(this, blockPos)?this.getDirectSignalTo(blockPos):var3.getSignal(this, blockPos, direction);
   }

   public boolean hasNeighborSignal(BlockPos blockPos) {
      return this.getSignal(blockPos.below(), Direction.DOWN) > 0?true:(this.getSignal(blockPos.above(), Direction.UP) > 0?true:(this.getSignal(blockPos.north(), Direction.NORTH) > 0?true:(this.getSignal(blockPos.south(), Direction.SOUTH) > 0?true:(this.getSignal(blockPos.west(), Direction.WEST) > 0?true:this.getSignal(blockPos.east(), Direction.EAST) > 0))));
   }

   public int getBestNeighborSignal(BlockPos blockPos) {
      int var2 = 0;

      for(Direction var6 : DIRECTIONS) {
         int var7 = this.getSignal(blockPos.relative(var6), var6);
         if(var7 >= 15) {
            return 15;
         }

         if(var7 > var2) {
            var2 = var7;
         }
      }

      return var2;
   }

   public void disconnect() {
   }

   public void setGameTime(long gameTime) {
      this.levelData.setGameTime(gameTime);
   }

   public long getSeed() {
      return this.levelData.getSeed();
   }

   public long getGameTime() {
      return this.levelData.getGameTime();
   }

   public long getDayTime() {
      return this.levelData.getDayTime();
   }

   public void setDayTime(long dayTime) {
      this.levelData.setDayTime(dayTime);
   }

   protected void tickTime() {
      this.setGameTime(this.levelData.getGameTime() + 1L);
      if(this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
         this.setDayTime(this.levelData.getDayTime() + 1L);
      }

   }

   public BlockPos getSharedSpawnPos() {
      BlockPos blockPos = new BlockPos(this.levelData.getXSpawn(), this.levelData.getYSpawn(), this.levelData.getZSpawn());
      if(!this.getWorldBorder().isWithinBounds(blockPos)) {
         blockPos = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
      }

      return blockPos;
   }

   public void setSpawnPos(BlockPos spawnPos) {
      this.levelData.setSpawn(spawnPos);
   }

   public boolean mayInteract(Player player, BlockPos blockPos) {
      return true;
   }

   public void broadcastEntityEvent(Entity entity, byte var2) {
   }

   public ChunkSource getChunkSource() {
      return this.chunkSource;
   }

   public void blockEvent(BlockPos blockPos, Block block, int var3, int var4) {
      this.getBlockState(blockPos).triggerEvent(this, blockPos, var3, var4);
   }

   public LevelData getLevelData() {
      return this.levelData;
   }

   public GameRules getGameRules() {
      return this.levelData.getGameRules();
   }

   public float getThunderLevel(float f) {
      return Mth.lerp(f, this.oThunderLevel, this.thunderLevel) * this.getRainLevel(f);
   }

   public void setThunderLevel(float thunderLevel) {
      this.oThunderLevel = thunderLevel;
      this.thunderLevel = thunderLevel;
   }

   public float getRainLevel(float f) {
      return Mth.lerp(f, this.oRainLevel, this.rainLevel);
   }

   public void setRainLevel(float rainLevel) {
      this.oRainLevel = rainLevel;
      this.rainLevel = rainLevel;
   }

   public boolean isThundering() {
      return this.dimension.isHasSkyLight() && !this.dimension.isHasCeiling()?(double)this.getThunderLevel(1.0F) > 0.9D:false;
   }

   public boolean isRaining() {
      return (double)this.getRainLevel(1.0F) > 0.2D;
   }

   public boolean isRainingAt(BlockPos blockPos) {
      return !this.isRaining()?false:(!this.canSeeSky(blockPos)?false:(this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()?false:this.getBiome(blockPos).getPrecipitation() == Biome.Precipitation.RAIN));
   }

   public boolean isHumidAt(BlockPos blockPos) {
      Biome var2 = this.getBiome(blockPos);
      return var2.isHumid();
   }

   @Nullable
   public abstract MapItemSavedData getMapData(String var1);

   public abstract void setMapData(MapItemSavedData var1);

   public abstract int getFreeMapId();

   public void globalLevelEvent(int var1, BlockPos blockPos, int var3) {
   }

   public int getHeight() {
      return this.dimension.isHasCeiling()?128:256;
   }

   public double getHorizonHeight() {
      return this.levelData.getGeneratorType() == LevelType.FLAT?0.0D:63.0D;
   }

   public CrashReportCategory fillReportDetails(CrashReport crashReport) {
      CrashReportCategory crashReportCategory = crashReport.addCategory("Affected level", 1);
      crashReportCategory.setDetail("All players", () -> {
         return this.players().size() + " total; " + this.players();
      });
      ChunkSource var10002 = this.chunkSource;
      this.chunkSource.getClass();
      crashReportCategory.setDetail("Chunk stats", var10002::gatherStats);
      crashReportCategory.setDetail("Level dimension", () -> {
         return this.dimension.getType().toString();
      });

      try {
         this.levelData.fillCrashReportCategory(crashReportCategory);
      } catch (Throwable var4) {
         crashReportCategory.setDetailError("Level Data Unobtainable", var4);
      }

      return crashReportCategory;
   }

   public abstract void destroyBlockProgress(int var1, BlockPos var2, int var3);

   public void createFireworks(double var1, double var3, double var5, double var7, double var9, double var11, @Nullable CompoundTag compoundTag) {
   }

   public abstract Scoreboard getScoreboard();

   public void updateNeighbourForOutputSignal(BlockPos blockPos, Block block) {
      for(Direction var4 : Direction.Plane.HORIZONTAL) {
         BlockPos var5 = blockPos.relative(var4);
         if(this.hasChunkAt(var5)) {
            BlockState var6 = this.getBlockState(var5);
            if(var6.getBlock() == Blocks.COMPARATOR) {
               var6.neighborChanged(this, var5, block, blockPos, false);
            } else if(var6.isRedstoneConductor(this, var5)) {
               var5 = var5.relative(var4);
               var6 = this.getBlockState(var5);
               if(var6.getBlock() == Blocks.COMPARATOR) {
                  var6.neighborChanged(this, var5, block, blockPos, false);
               }
            }
         }
      }

   }

   public DifficultyInstance getCurrentDifficultyAt(BlockPos blockPos) {
      long var2 = 0L;
      float var4 = 0.0F;
      if(this.hasChunkAt(blockPos)) {
         var4 = this.getMoonBrightness();
         var2 = this.getChunkAt(blockPos).getInhabitedTime();
      }

      return new DifficultyInstance(this.getDifficulty(), this.getDayTime(), var2, var4);
   }

   public int getSkyDarken() {
      return this.skyDarken;
   }

   public int getSkyFlashTime() {
      return this.skyFlashTime;
   }

   public void setSkyFlashTime(int skyFlashTime) {
      this.skyFlashTime = skyFlashTime;
   }

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public void sendPacketToServer(Packet packet) {
      throw new UnsupportedOperationException("Can\'t send packets to server unless you\'re on the client.");
   }

   @Nullable
   public BlockPos findNearestMapFeature(String string, BlockPos var2, int var3, boolean var4) {
      return null;
   }

   public Dimension getDimension() {
      return this.dimension;
   }

   public Random getRandom() {
      return this.random;
   }

   public boolean isStateAtPosition(BlockPos blockPos, Predicate predicate) {
      return predicate.test(this.getBlockState(blockPos));
   }

   public abstract RecipeManager getRecipeManager();

   public abstract TagManager getTagManager();

   public BlockPos getBlockRandomPos(int var1, int var2, int var3, int var4) {
      this.randValue = this.randValue * 3 + 1013904223;
      int var5 = this.randValue >> 2;
      return new BlockPos(var1 + (var5 & 15), var2 + (var5 >> 16 & var4), var3 + (var5 >> 8 & 15));
   }

   public boolean noSave() {
      return false;
   }

   public ProfilerFiller getProfiler() {
      return this.profiler;
   }

   public BlockPos getHeightmapPos(Heightmap.Types heightmap$Types, BlockPos var2) {
      return new BlockPos(var2.getX(), this.getHeight(heightmap$Types, var2.getX(), var2.getZ()), var2.getZ());
   }

   // $FF: synthetic method
   public ChunkAccess getChunk(int var1, int var2) {
      return this.getChunk(var1, var2);
   }
}
