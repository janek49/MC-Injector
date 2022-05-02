package net.minecraft.world.level.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ChunkTickList;
import net.minecraft.world.level.EmptyTickList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ProtoTickList;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelChunk implements ChunkAccess {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LevelChunkSection EMPTY_SECTION = null;
   private final LevelChunkSection[] sections;
   private final Biome[] biomes;
   private final Map pendingBlockEntities;
   private boolean loaded;
   private final Level level;
   private final Map heightmaps;
   private final UpgradeData upgradeData;
   private final Map blockEntities;
   private final ClassInstanceMultiMap[] entitySections;
   private final Map structureStarts;
   private final Map structuresRefences;
   private final ShortList[] postProcessing;
   private TickList blockTicks;
   private TickList liquidTicks;
   private boolean lastSaveHadEntities;
   private long lastSaveTime;
   private volatile boolean unsaved;
   private long inhabitedTime;
   @Nullable
   private Supplier fullStatus;
   @Nullable
   private Consumer postLoad;
   private final ChunkPos chunkPos;
   private volatile boolean isLightCorrect;

   public LevelChunk(Level level, ChunkPos chunkPos, Biome[] biomes) {
      this(level, chunkPos, biomes, UpgradeData.EMPTY, EmptyTickList.empty(), EmptyTickList.empty(), 0L, (LevelChunkSection[])null, (Consumer)null);
   }

   public LevelChunk(Level level, ChunkPos chunkPos, Biome[] biomes, UpgradeData upgradeData, TickList blockTicks, TickList liquidTicks, long inhabitedTime, @Nullable LevelChunkSection[] levelChunkSections, @Nullable Consumer postLoad) {
      this.sections = new LevelChunkSection[16];
      this.pendingBlockEntities = Maps.newHashMap();
      this.heightmaps = Maps.newEnumMap(Heightmap.Types.class);
      this.blockEntities = Maps.newHashMap();
      this.structureStarts = Maps.newHashMap();
      this.structuresRefences = Maps.newHashMap();
      this.postProcessing = new ShortList[16];
      this.entitySections = (ClassInstanceMultiMap[])(new ClassInstanceMultiMap[16]);
      this.level = level;
      this.chunkPos = chunkPos;
      this.upgradeData = upgradeData;

      for(Heightmap.Types var14 : Heightmap.Types.values()) {
         if(ChunkStatus.FULL.heightmapsAfter().contains(var14)) {
            this.heightmaps.put(var14, new Heightmap(this, var14));
         }
      }

      for(int var11 = 0; var11 < this.entitySections.length; ++var11) {
         this.entitySections[var11] = new ClassInstanceMultiMap(Entity.class);
      }

      this.biomes = biomes;
      this.blockTicks = blockTicks;
      this.liquidTicks = liquidTicks;
      this.inhabitedTime = inhabitedTime;
      this.postLoad = postLoad;
      if(levelChunkSections != null) {
         if(this.sections.length == levelChunkSections.length) {
            System.arraycopy(levelChunkSections, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", Integer.valueOf(levelChunkSections.length), Integer.valueOf(this.sections.length));
         }
      }

   }

   public LevelChunk(Level level, ProtoChunk protoChunk) {
      this(level, protoChunk.getPos(), protoChunk.getBiomes(), protoChunk.getUpgradeData(), protoChunk.getBlockTicks(), protoChunk.getLiquidTicks(), protoChunk.getInhabitedTime(), protoChunk.getSections(), (Consumer)null);

      for(CompoundTag var4 : protoChunk.getEntities()) {
         EntityType.loadEntityRecursive(var4, level, (entity) -> {
            this.addEntity(entity);
            return entity;
         });
      }

      for(BlockEntity var4 : protoChunk.getBlockEntities().values()) {
         this.addBlockEntity(var4);
      }

      this.pendingBlockEntities.putAll(protoChunk.getBlockEntityNbts());

      for(int var3 = 0; var3 < protoChunk.getPostProcessing().length; ++var3) {
         this.postProcessing[var3] = protoChunk.getPostProcessing()[var3];
      }

      this.setAllStarts(protoChunk.getAllStarts());
      this.setAllReferences(protoChunk.getAllReferences());

      for(Entry<Heightmap.Types, Heightmap> var4 : protoChunk.getHeightmaps()) {
         if(ChunkStatus.FULL.heightmapsAfter().contains(var4.getKey())) {
            this.getOrCreateHeightmapUnprimed((Heightmap.Types)var4.getKey()).setRawData(((Heightmap)var4.getValue()).getRawData());
         }
      }

      this.setLightCorrect(protoChunk.isLightCorrect());
      this.unsaved = true;
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types heightmap$Types) {
      return (Heightmap)this.heightmaps.computeIfAbsent(heightmap$Types, (heightmap$Types) -> {
         return new Heightmap(this, heightmap$Types);
      });
   }

   public Set getBlockEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.pendingBlockEntities.keySet());
      set.addAll(this.blockEntities.keySet());
      return set;
   }

   public LevelChunkSection[] getSections() {
      return this.sections;
   }

   public BlockState getBlockState(BlockPos blockPos) {
      int var2 = blockPos.getX();
      int var3 = blockPos.getY();
      int var4 = blockPos.getZ();
      if(this.level.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES) {
         BlockState var5 = null;
         if(var3 == 60) {
            var5 = Blocks.BARRIER.defaultBlockState();
         }

         if(var3 == 70) {
            var5 = DebugLevelSource.getBlockStateFor(var2, var4);
         }

         return var5 == null?Blocks.AIR.defaultBlockState():var5;
      } else {
         try {
            if(var3 >= 0 && var3 >> 4 < this.sections.length) {
               LevelChunkSection var5 = this.sections[var3 >> 4];
               if(!LevelChunkSection.isEmpty(var5)) {
                  return var5.getBlockState(var2 & 15, var3 & 15, var4 & 15);
               }
            }

            return Blocks.AIR.defaultBlockState();
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Getting block state");
            CrashReportCategory var7 = var6.addCategory("Block being got");
            var7.setDetail("Location", () -> {
               return CrashReportCategory.formatLocation(var2, var3, var4);
            });
            throw new ReportedException(var6);
         }
      }
   }

   public FluidState getFluidState(BlockPos blockPos) {
      return this.getFluidState(blockPos.getX(), blockPos.getY(), blockPos.getZ());
   }

   public FluidState getFluidState(int var1, int var2, int var3) {
      try {
         if(var2 >= 0 && var2 >> 4 < this.sections.length) {
            LevelChunkSection var4 = this.sections[var2 >> 4];
            if(!LevelChunkSection.isEmpty(var4)) {
               return var4.getFluidState(var1 & 15, var2 & 15, var3 & 15);
            }
         }

         return Fluids.EMPTY.defaultFluidState();
      } catch (Throwable var7) {
         CrashReport var5 = CrashReport.forThrowable(var7, "Getting fluid state");
         CrashReportCategory var6 = var5.addCategory("Block being got");
         var6.setDetail("Location", () -> {
            return CrashReportCategory.formatLocation(var1, var2, var3);
         });
         throw new ReportedException(var5);
      }
   }

   @Nullable
   public BlockState setBlockState(BlockPos blockPos, BlockState var2, boolean var3) {
      int var4 = blockPos.getX() & 15;
      int var5 = blockPos.getY();
      int var6 = blockPos.getZ() & 15;
      LevelChunkSection var7 = this.sections[var5 >> 4];
      if(var7 == EMPTY_SECTION) {
         if(var2.isAir()) {
            return null;
         }

         var7 = new LevelChunkSection(var5 >> 4 << 4);
         this.sections[var5 >> 4] = var7;
      }

      boolean var8 = var7.isEmpty();
      BlockState var9 = var7.setBlockState(var4, var5 & 15, var6, var2);
      if(var9 == var2) {
         return null;
      } else {
         Block var10 = var2.getBlock();
         Block var11 = var9.getBlock();
         ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING)).update(var4, var5, var6, var2);
         ((Heightmap)this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).update(var4, var5, var6, var2);
         ((Heightmap)this.heightmaps.get(Heightmap.Types.OCEAN_FLOOR)).update(var4, var5, var6, var2);
         ((Heightmap)this.heightmaps.get(Heightmap.Types.WORLD_SURFACE)).update(var4, var5, var6, var2);
         boolean var12 = var7.isEmpty();
         if(var8 != var12) {
            this.level.getChunkSource().getLightEngine().updateSectionStatus(blockPos, var12);
         }

         if(!this.level.isClientSide) {
            var9.onRemove(this.level, blockPos, var2, var3);
         } else if(var11 != var10 && var11 instanceof EntityBlock) {
            this.level.removeBlockEntity(blockPos);
         }

         if(var7.getBlockState(var4, var5 & 15, var6).getBlock() != var10) {
            return null;
         } else {
            if(var11 instanceof EntityBlock) {
               BlockEntity var13 = this.getBlockEntity(blockPos, LevelChunk.EntityCreationType.CHECK);
               if(var13 != null) {
                  var13.clearCache();
               }
            }

            if(!this.level.isClientSide) {
               var2.onPlace(this.level, blockPos, var9, var3);
            }

            if(var10 instanceof EntityBlock) {
               BlockEntity var13 = this.getBlockEntity(blockPos, LevelChunk.EntityCreationType.CHECK);
               if(var13 == null) {
                  var13 = ((EntityBlock)var10).newBlockEntity(this.level);
                  this.level.setBlockEntity(blockPos, var13);
               } else {
                  var13.clearCache();
               }
            }

            this.unsaved = true;
            return var9;
         }
      }
   }

   @Nullable
   public LevelLightEngine getLightEngine() {
      return this.level.getChunkSource().getLightEngine();
   }

   public int getRawBrightness(BlockPos blockPos, int var2) {
      return this.getRawBrightness(blockPos, var2, this.level.getDimension().isHasSkyLight());
   }

   public void addEntity(Entity entity) {
      this.lastSaveHadEntities = true;
      int var2 = Mth.floor(entity.x / 16.0D);
      int var3 = Mth.floor(entity.z / 16.0D);
      if(var2 != this.chunkPos.x || var3 != this.chunkPos.z) {
         LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", Integer.valueOf(var2), Integer.valueOf(var3), Integer.valueOf(this.chunkPos.x), Integer.valueOf(this.chunkPos.z), entity);
         entity.removed = true;
      }

      int var4 = Mth.floor(entity.y / 16.0D);
      if(var4 < 0) {
         var4 = 0;
      }

      if(var4 >= this.entitySections.length) {
         var4 = this.entitySections.length - 1;
      }

      entity.inChunk = true;
      entity.xChunk = this.chunkPos.x;
      entity.yChunk = var4;
      entity.zChunk = this.chunkPos.z;
      this.entitySections[var4].add(entity);
   }

   public void setHeightmap(Heightmap.Types heightmap$Types, long[] longs) {
      ((Heightmap)this.heightmaps.get(heightmap$Types)).setRawData(longs);
   }

   public void removeEntity(Entity entity) {
      this.removeEntity(entity, entity.yChunk);
   }

   public void removeEntity(Entity entity, int var2) {
      if(var2 < 0) {
         var2 = 0;
      }

      if(var2 >= this.entitySections.length) {
         var2 = this.entitySections.length - 1;
      }

      this.entitySections[var2].remove(entity);
   }

   public int getHeight(Heightmap.Types heightmap$Types, int var2, int var3) {
      return ((Heightmap)this.heightmaps.get(heightmap$Types)).getFirstAvailable(var2 & 15, var3 & 15) - 1;
   }

   @Nullable
   private BlockEntity createBlockEntity(BlockPos blockPos) {
      BlockState var2 = this.getBlockState(blockPos);
      Block var3 = var2.getBlock();
      return !var3.isEntityBlock()?null:((EntityBlock)var3).newBlockEntity(this.level);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos) {
      return this.getBlockEntity(blockPos, LevelChunk.EntityCreationType.CHECK);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos, LevelChunk.EntityCreationType levelChunk$EntityCreationType) {
      BlockEntity blockEntity = (BlockEntity)this.blockEntities.get(blockPos);
      if(blockEntity == null) {
         CompoundTag var4 = (CompoundTag)this.pendingBlockEntities.remove(blockPos);
         if(var4 != null) {
            BlockEntity var5 = this.promotePendingBlockEntity(blockPos, var4);
            if(var5 != null) {
               return var5;
            }
         }
      }

      if(blockEntity == null) {
         if(levelChunk$EntityCreationType == LevelChunk.EntityCreationType.IMMEDIATE) {
            blockEntity = this.createBlockEntity(blockPos);
            this.level.setBlockEntity(blockPos, blockEntity);
         }
      } else if(blockEntity.isRemoved()) {
         this.blockEntities.remove(blockPos);
         return null;
      }

      return blockEntity;
   }

   public void addBlockEntity(BlockEntity blockEntity) {
      this.setBlockEntity(blockEntity.getBlockPos(), blockEntity);
      if(this.loaded || this.level.isClientSide()) {
         this.level.setBlockEntity(blockEntity.getBlockPos(), blockEntity);
      }

   }

   public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
      if(this.getBlockState(blockPos).getBlock() instanceof EntityBlock) {
         blockEntity.setLevel(this.level);
         blockEntity.setPosition(blockPos);
         blockEntity.clearRemoved();
         BlockEntity blockEntity = (BlockEntity)this.blockEntities.put(blockPos.immutable(), blockEntity);
         if(blockEntity != null && blockEntity != blockEntity) {
            blockEntity.setRemoved();
         }

      }
   }

   public void setBlockEntityNbt(CompoundTag blockEntityNbt) {
      this.pendingBlockEntities.put(new BlockPos(blockEntityNbt.getInt("x"), blockEntityNbt.getInt("y"), blockEntityNbt.getInt("z")), blockEntityNbt);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos blockPos) {
      BlockEntity var2 = this.getBlockEntity(blockPos);
      if(var2 != null && !var2.isRemoved()) {
         CompoundTag var3 = var2.save(new CompoundTag());
         var3.putBoolean("keepPacked", false);
         return var3;
      } else {
         CompoundTag var3 = (CompoundTag)this.pendingBlockEntities.get(blockPos);
         if(var3 != null) {
            var3 = var3.copy();
            var3.putBoolean("keepPacked", true);
         }

         return var3;
      }
   }

   public void removeBlockEntity(BlockPos blockPos) {
      if(this.loaded || this.level.isClientSide()) {
         BlockEntity var2 = (BlockEntity)this.blockEntities.remove(blockPos);
         if(var2 != null) {
            var2.setRemoved();
         }
      }

   }

   public void runPostLoad() {
      if(this.postLoad != null) {
         this.postLoad.accept(this);
         this.postLoad = null;
      }

   }

   public void markUnsaved() {
      this.unsaved = true;
   }

   public void getEntities(@Nullable Entity entity, AABB aABB, List list, @Nullable Predicate predicate) {
      int var5 = Mth.floor((aABB.minY - 2.0D) / 16.0D);
      int var6 = Mth.floor((aABB.maxY + 2.0D) / 16.0D);
      var5 = Mth.clamp(var5, 0, this.entitySections.length - 1);
      var6 = Mth.clamp(var6, 0, this.entitySections.length - 1);

      for(int var7 = var5; var7 <= var6; ++var7) {
         if(!this.entitySections[var7].isEmpty()) {
            for(Entity var9 : this.entitySections[var7]) {
               if(var9.getBoundingBox().intersects(aABB) && var9 != entity) {
                  if(predicate == null || predicate.test(var9)) {
                     list.add(var9);
                  }

                  if(var9 instanceof EnderDragon) {
                     for(EnderDragonPart var13 : ((EnderDragon)var9).getSubEntities()) {
                        if(var13 != entity && var13.getBoundingBox().intersects(aABB) && (predicate == null || predicate.test(var13))) {
                           list.add(var13);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void getEntities(@Nullable EntityType entityType, AABB aABB, List list, Predicate predicate) {
      int var5 = Mth.floor((aABB.minY - 2.0D) / 16.0D);
      int var6 = Mth.floor((aABB.maxY + 2.0D) / 16.0D);
      var5 = Mth.clamp(var5, 0, this.entitySections.length - 1);
      var6 = Mth.clamp(var6, 0, this.entitySections.length - 1);

      for(int var7 = var5; var7 <= var6; ++var7) {
         for(Entity var9 : this.entitySections[var7].find(Entity.class)) {
            if((entityType == null || var9.getType() == entityType) && var9.getBoundingBox().intersects(aABB) && predicate.test(var9)) {
               list.add(var9);
            }
         }
      }

   }

   public void getEntitiesOfClass(Class class, AABB aABB, List list, @Nullable Predicate predicate) {
      int var5 = Mth.floor((aABB.minY - 2.0D) / 16.0D);
      int var6 = Mth.floor((aABB.maxY + 2.0D) / 16.0D);
      var5 = Mth.clamp(var5, 0, this.entitySections.length - 1);
      var6 = Mth.clamp(var6, 0, this.entitySections.length - 1);

      for(int var7 = var5; var7 <= var6; ++var7) {
         for(T var9 : this.entitySections[var7].find(class)) {
            if(var9.getBoundingBox().intersects(aABB) && (predicate == null || predicate.test(var9))) {
               list.add(var9);
            }
         }
      }

   }

   public boolean isEmpty() {
      return false;
   }

   public ChunkPos getPos() {
      return this.chunkPos;
   }

   public void replaceWithPacketData(FriendlyByteBuf friendlyByteBuf, CompoundTag compoundTag, int var3, boolean var4) {
      Predicate<BlockPos> var5 = var4?(blockPos) -> {
         return true;
      }:(blockPos) -> {
         return (var3 & 1 << (blockPos.getY() >> 4)) != 0;
      };
      Stream var10000 = Sets.newHashSet(this.blockEntities.keySet()).stream().filter(var5);
      Level var10001 = this.level;
      this.level.getClass();
      var10000.forEach(var10001::removeBlockEntity);

      for(int var6 = 0; var6 < this.sections.length; ++var6) {
         LevelChunkSection var7 = this.sections[var6];
         if((var3 & 1 << var6) == 0) {
            if(var4 && var7 != EMPTY_SECTION) {
               this.sections[var6] = EMPTY_SECTION;
            }
         } else {
            if(var7 == EMPTY_SECTION) {
               var7 = new LevelChunkSection(var6 << 4);
               this.sections[var6] = var7;
            }

            var7.read(friendlyByteBuf);
         }
      }

      if(var4) {
         for(int var6 = 0; var6 < this.biomes.length; ++var6) {
            this.biomes[var6] = (Biome)Registry.BIOME.byId(friendlyByteBuf.readInt());
         }
      }

      for(Heightmap.Types var9 : Heightmap.Types.values()) {
         String var10 = var9.getSerializationKey();
         if(compoundTag.contains(var10, 12)) {
            this.setHeightmap(var9, compoundTag.getLongArray(var10));
         }
      }

      for(BlockEntity var7 : this.blockEntities.values()) {
         var7.clearCache();
      }

   }

   public Biome[] getBiomes() {
      return this.biomes;
   }

   public void setLoaded(boolean loaded) {
      this.loaded = loaded;
   }

   public Level getLevel() {
      return this.level;
   }

   public Collection getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public Map getBlockEntities() {
      return this.blockEntities;
   }

   public ClassInstanceMultiMap[] getEntitySections() {
      return this.entitySections;
   }

   public CompoundTag getBlockEntityNbt(BlockPos blockPos) {
      return (CompoundTag)this.pendingBlockEntities.get(blockPos);
   }

   public Stream getLights() {
      return StreamSupport.stream(BlockPos.betweenClosed(this.chunkPos.getMinBlockX(), 0, this.chunkPos.getMinBlockZ(), this.chunkPos.getMaxBlockX(), 255, this.chunkPos.getMaxBlockZ()).spliterator(), false).filter((blockPos) -> {
         return this.getBlockState(blockPos).getLightEmission() != 0;
      });
   }

   public TickList getBlockTicks() {
      return this.blockTicks;
   }

   public TickList getLiquidTicks() {
      return this.liquidTicks;
   }

   public void setUnsaved(boolean unsaved) {
      this.unsaved = unsaved;
   }

   public boolean isUnsaved() {
      return this.unsaved || this.lastSaveHadEntities && this.level.getGameTime() != this.lastSaveTime;
   }

   public void setLastSaveHadEntities(boolean lastSaveHadEntities) {
      this.lastSaveHadEntities = lastSaveHadEntities;
   }

   public void setLastSaveTime(long lastSaveTime) {
      this.lastSaveTime = lastSaveTime;
   }

   @Nullable
   public StructureStart getStartForFeature(String string) {
      return (StructureStart)this.structureStarts.get(string);
   }

   public void setStartForFeature(String string, StructureStart structureStart) {
      this.structureStarts.put(string, structureStart);
   }

   public Map getAllStarts() {
      return this.structureStarts;
   }

   public void setAllStarts(Map allStarts) {
      this.structureStarts.clear();
      this.structureStarts.putAll(allStarts);
   }

   public LongSet getReferencesForFeature(String string) {
      return (LongSet)this.structuresRefences.computeIfAbsent(string, (string) -> {
         return new LongOpenHashSet();
      });
   }

   public void addReferenceForFeature(String string, long var2) {
      ((LongSet)this.structuresRefences.computeIfAbsent(string, (string) -> {
         return new LongOpenHashSet();
      })).add(var2);
   }

   public Map getAllReferences() {
      return this.structuresRefences;
   }

   public void setAllReferences(Map allReferences) {
      this.structuresRefences.clear();
      this.structuresRefences.putAll(allReferences);
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long inhabitedTime) {
      this.inhabitedTime = inhabitedTime;
   }

   public void postProcessGeneration() {
      ChunkPos var1 = this.getPos();

      for(int var2 = 0; var2 < this.postProcessing.length; ++var2) {
         if(this.postProcessing[var2] != null) {
            ShortListIterator var3 = this.postProcessing[var2].iterator();

            while(var3.hasNext()) {
               Short var4 = (Short)var3.next();
               BlockPos var5 = ProtoChunk.unpackOffsetCoordinates(var4.shortValue(), var2, var1);
               BlockState var6 = this.getBlockState(var5);
               BlockState var7 = Block.updateFromNeighbourShapes(var6, this.level, var5);
               this.level.setBlock(var5, var7, 20);
            }

            this.postProcessing[var2].clear();
         }
      }

      this.unpackTicks();

      for(BlockPos var3 : Sets.newHashSet(this.pendingBlockEntities.keySet())) {
         this.getBlockEntity(var3);
      }

      this.pendingBlockEntities.clear();
      this.upgradeData.upgrade(this);
   }

   @Nullable
   private BlockEntity promotePendingBlockEntity(BlockPos blockPos, CompoundTag compoundTag) {
      BlockEntity blockEntity;
      if("DUMMY".equals(compoundTag.getString("id"))) {
         Block var4 = this.getBlockState(blockPos).getBlock();
         if(var4 instanceof EntityBlock) {
            blockEntity = ((EntityBlock)var4).newBlockEntity(this.level);
         } else {
            blockEntity = null;
            LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", blockPos, this.getBlockState(blockPos));
         }
      } else {
         blockEntity = BlockEntity.loadStatic(compoundTag);
      }

      if(blockEntity != null) {
         blockEntity.setPosition(blockPos);
         this.addBlockEntity(blockEntity);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", this.getBlockState(blockPos), blockPos);
      }

      return blockEntity;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public ShortList[] getPostProcessing() {
      return this.postProcessing;
   }

   public void unpackTicks() {
      if(this.blockTicks instanceof ProtoTickList) {
         ((ProtoTickList)this.blockTicks).copyOut(this.level.getBlockTicks(), (blockPos) -> {
            return this.getBlockState(blockPos).getBlock();
         });
         this.blockTicks = EmptyTickList.empty();
      } else if(this.blockTicks instanceof ChunkTickList) {
         this.level.getBlockTicks().addAll(((ChunkTickList)this.blockTicks).ticks());
         this.blockTicks = EmptyTickList.empty();
      }

      if(this.liquidTicks instanceof ProtoTickList) {
         ((ProtoTickList)this.liquidTicks).copyOut(this.level.getLiquidTicks(), (blockPos) -> {
            return this.getFluidState(blockPos).getType();
         });
         this.liquidTicks = EmptyTickList.empty();
      } else if(this.liquidTicks instanceof ChunkTickList) {
         this.level.getLiquidTicks().addAll(((ChunkTickList)this.liquidTicks).ticks());
         this.liquidTicks = EmptyTickList.empty();
      }

   }

   public void packTicks(ServerLevel serverLevel) {
      if(this.blockTicks == EmptyTickList.empty()) {
         DefaultedRegistry var10003 = Registry.BLOCK;
         Registry.BLOCK.getClass();
         this.blockTicks = new ChunkTickList(var10003::getKey, serverLevel.getBlockTicks().fetchTicksInChunk(this.chunkPos, true, false));
         this.setUnsaved(true);
      }

      if(this.liquidTicks == EmptyTickList.empty()) {
         DefaultedRegistry var2 = Registry.FLUID;
         Registry.FLUID.getClass();
         this.liquidTicks = new ChunkTickList(var2::getKey, serverLevel.getLiquidTicks().fetchTicksInChunk(this.chunkPos, true, false));
         this.setUnsaved(true);
      }

   }

   public ChunkStatus getStatus() {
      return ChunkStatus.FULL;
   }

   public ChunkHolder.FullChunkStatus getFullStatus() {
      return this.fullStatus == null?ChunkHolder.FullChunkStatus.BORDER:(ChunkHolder.FullChunkStatus)this.fullStatus.get();
   }

   public void setFullStatus(Supplier fullStatus) {
      this.fullStatus = fullStatus;
   }

   public void setLightEngine(LevelLightEngine lightEngine) {
   }

   public boolean isLightCorrect() {
      return this.isLightCorrect;
   }

   public void setLightCorrect(boolean lightCorrect) {
      this.isLightCorrect = lightCorrect;
      this.setUnsaved(true);
   }

   public static enum EntityCreationType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }
}
