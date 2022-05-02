package net.minecraft.server.level;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenTickList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenRegion implements LevelAccessor {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List cache;
   private final int x;
   private final int z;
   private final int size;
   private final ServerLevel level;
   private final long seed;
   private final int seaLevel;
   private final LevelData levelData;
   private final Random random;
   private final Dimension dimension;
   private final ChunkGeneratorSettings settings;
   private final TickList blockTicks = new WorldGenTickList((blockPos) -> {
      return this.getChunk(blockPos).getBlockTicks();
   });
   private final TickList liquidTicks = new WorldGenTickList((blockPos) -> {
      return this.getChunk(blockPos).getLiquidTicks();
   });

   public WorldGenRegion(ServerLevel level, List cache) {
      int var3 = Mth.floor(Math.sqrt((double)cache.size()));
      if(var3 * var3 != cache.size()) {
         throw new IllegalStateException("Cache size is not a square.");
      } else {
         ChunkPos var4 = ((ChunkAccess)cache.get(cache.size() / 2)).getPos();
         this.cache = cache;
         this.x = var4.x;
         this.z = var4.z;
         this.size = var3;
         this.level = level;
         this.seed = level.getSeed();
         this.settings = level.getChunkSource().getGenerator().getSettings();
         this.seaLevel = level.getSeaLevel();
         this.levelData = level.getLevelData();
         this.random = level.getRandom();
         this.dimension = level.getDimension();
      }
   }

   public int getCenterX() {
      return this.x;
   }

   public int getCenterZ() {
      return this.z;
   }

   public ChunkAccess getChunk(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.EMPTY);
   }

   @Nullable
   public ChunkAccess getChunk(int var1, int var2, ChunkStatus chunkStatus, boolean var4) {
      ChunkAccess chunkAccess;
      if(this.hasChunk(var1, var2)) {
         ChunkPos var6 = ((ChunkAccess)this.cache.get(0)).getPos();
         int var7 = var1 - var6.x;
         int var8 = var2 - var6.z;
         chunkAccess = (ChunkAccess)this.cache.get(var7 + var8 * this.size);
         if(chunkAccess.getStatus().isOrAfter(chunkStatus)) {
            return chunkAccess;
         }
      } else {
         chunkAccess = null;
      }

      if(!var4) {
         return null;
      } else {
         ChunkAccess var6 = (ChunkAccess)this.cache.get(0);
         ChunkAccess var7 = (ChunkAccess)this.cache.get(this.cache.size() - 1);
         LOGGER.error("Requested chunk : {} {}", Integer.valueOf(var1), Integer.valueOf(var2));
         LOGGER.error("Region bounds : {} {} | {} {}", Integer.valueOf(var6.getPos().x), Integer.valueOf(var6.getPos().z), Integer.valueOf(var7.getPos().x), Integer.valueOf(var7.getPos().z));
         if(chunkAccess != null) {
            throw new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", new Object[]{chunkStatus, chunkAccess.getStatus(), Integer.valueOf(var1), Integer.valueOf(var2)}));
         } else {
            throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", new Object[]{Integer.valueOf(var1), Integer.valueOf(var2)}));
         }
      }
   }

   public boolean hasChunk(int var1, int var2) {
      ChunkAccess var3 = (ChunkAccess)this.cache.get(0);
      ChunkAccess var4 = (ChunkAccess)this.cache.get(this.cache.size() - 1);
      return var1 >= var3.getPos().x && var1 <= var4.getPos().x && var2 >= var3.getPos().z && var2 <= var4.getPos().z;
   }

   public BlockState getBlockState(BlockPos blockPos) {
      return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4).getBlockState(blockPos);
   }

   public FluidState getFluidState(BlockPos blockPos) {
      return this.getChunk(blockPos).getFluidState(blockPos);
   }

   @Nullable
   public Player getNearestPlayer(double var1, double var3, double var5, double var7, Predicate predicate) {
      return null;
   }

   public int getSkyDarken() {
      return 0;
   }

   public Biome getBiome(BlockPos blockPos) {
      Biome biome = this.getChunk(blockPos).getBiomes()[blockPos.getX() & 15 | (blockPos.getZ() & 15) << 4];
      if(biome == null) {
         throw new RuntimeException(String.format("Biome is null @ %s", new Object[]{blockPos}));
      } else {
         return biome;
      }
   }

   public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
      return this.getChunkSource().getLightEngine().getLayerListener(lightLayer).getLightValue(blockPos);
   }

   public int getRawBrightness(BlockPos blockPos, int var2) {
      return this.getChunk(blockPos).getRawBrightness(blockPos, var2, this.getDimension().isHasSkyLight());
   }

   public boolean destroyBlock(BlockPos blockPos, boolean var2) {
      BlockState var3 = this.getBlockState(blockPos);
      if(var3.isAir()) {
         return false;
      } else {
         if(var2) {
            BlockEntity var4 = var3.getBlock().isEntityBlock()?this.getBlockEntity(blockPos):null;
            Block.dropResources(var3, this.level, blockPos, var4);
         }

         return this.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
      }
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos) {
      ChunkAccess var2 = this.getChunk(blockPos);
      BlockEntity var3 = var2.getBlockEntity(blockPos);
      if(var3 != null) {
         return var3;
      } else {
         CompoundTag var4 = var2.getBlockEntityNbt(blockPos);
         if(var4 != null) {
            if("DUMMY".equals(var4.getString("id"))) {
               Block var5 = this.getBlockState(blockPos).getBlock();
               if(!(var5 instanceof EntityBlock)) {
                  return null;
               }

               var3 = ((EntityBlock)var5).newBlockEntity(this.level);
            } else {
               var3 = BlockEntity.loadStatic(var4);
            }

            if(var3 != null) {
               var2.setBlockEntity(blockPos, var3);
               return var3;
            }
         }

         if(var2.getBlockState(blockPos).getBlock() instanceof EntityBlock) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", blockPos);
         }

         return null;
      }
   }

   public boolean setBlock(BlockPos blockPos, BlockState blockState, int var3) {
      ChunkAccess var4 = this.getChunk(blockPos);
      BlockState var5 = var4.setBlockState(blockPos, blockState, false);
      if(var5 != null) {
         this.level.onBlockStateChange(blockPos, var5, blockState);
      }

      Block var6 = blockState.getBlock();
      if(var6.isEntityBlock()) {
         if(var4.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
            var4.setBlockEntity(blockPos, ((EntityBlock)var6).newBlockEntity(this));
         } else {
            CompoundTag var7 = new CompoundTag();
            var7.putInt("x", blockPos.getX());
            var7.putInt("y", blockPos.getY());
            var7.putInt("z", blockPos.getZ());
            var7.putString("id", "DUMMY");
            var4.setBlockEntityNbt(var7);
         }
      } else if(var5 != null && var5.getBlock().isEntityBlock()) {
         var4.removeBlockEntity(blockPos);
      }

      if(blockState.hasPostProcess(this, blockPos)) {
         this.markPosForPostprocessing(blockPos);
      }

      return true;
   }

   private void markPosForPostprocessing(BlockPos blockPos) {
      this.getChunk(blockPos).markPosForPostprocessing(blockPos);
   }

   public boolean addFreshEntity(Entity entity) {
      int var2 = Mth.floor(entity.x / 16.0D);
      int var3 = Mth.floor(entity.z / 16.0D);
      this.getChunk(var2, var3).addEntity(entity);
      return true;
   }

   public boolean removeBlock(BlockPos blockPos, boolean var2) {
      return this.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
   }

   public WorldBorder getWorldBorder() {
      return this.level.getWorldBorder();
   }

   public boolean isUnobstructed(@Nullable Entity entity, VoxelShape voxelShape) {
      return true;
   }

   public boolean isClientSide() {
      return false;
   }

   @Deprecated
   public ServerLevel getLevel() {
      return this.level;
   }

   public LevelData getLevelData() {
      return this.levelData;
   }

   public DifficultyInstance getCurrentDifficultyAt(BlockPos blockPos) {
      if(!this.hasChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
         throw new RuntimeException("We are asking a region for a chunk out of bound");
      } else {
         return new DifficultyInstance(this.level.getDifficulty(), this.level.getDayTime(), 0L, this.level.getMoonBrightness());
      }
   }

   public ChunkSource getChunkSource() {
      return this.level.getChunkSource();
   }

   public long getSeed() {
      return this.seed;
   }

   public TickList getBlockTicks() {
      return this.blockTicks;
   }

   public TickList getLiquidTicks() {
      return this.liquidTicks;
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public Random getRandom() {
      return this.random;
   }

   public void blockUpdated(BlockPos blockPos, Block block) {
   }

   public int getHeight(Heightmap.Types heightmap$Types, int var2, int var3) {
      return this.getChunk(var2 >> 4, var3 >> 4).getHeight(heightmap$Types, var2 & 15, var3 & 15) + 1;
   }

   public void playSound(@Nullable Player player, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float var5, float var6) {
   }

   public void addParticle(ParticleOptions particleOptions, double var2, double var4, double var6, double var8, double var10, double var12) {
   }

   public void levelEvent(@Nullable Player player, int var2, BlockPos blockPos, int var4) {
   }

   public BlockPos getSharedSpawnPos() {
      return this.level.getSharedSpawnPos();
   }

   public Dimension getDimension() {
      return this.dimension;
   }

   public boolean isStateAtPosition(BlockPos blockPos, Predicate predicate) {
      return predicate.test(this.getBlockState(blockPos));
   }

   public List getEntitiesOfClass(Class class, AABB aABB, @Nullable Predicate predicate) {
      return Collections.emptyList();
   }

   public List getEntities(@Nullable Entity entity, AABB aABB, @Nullable Predicate predicate) {
      return Collections.emptyList();
   }

   public List players() {
      return Collections.emptyList();
   }

   public BlockPos getHeightmapPos(Heightmap.Types heightmap$Types, BlockPos var2) {
      return new BlockPos(var2.getX(), this.getHeight(heightmap$Types, var2.getX(), var2.getZ()), var2.getZ());
   }

   // $FF: synthetic method
   @Deprecated
   public Level getLevel() {
      return this.getLevel();
   }
}
