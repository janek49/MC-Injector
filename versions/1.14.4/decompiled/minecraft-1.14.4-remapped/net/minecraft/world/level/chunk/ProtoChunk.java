package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoTickList;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProtoChunk implements ChunkAccess {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkPos chunkPos;
   private volatile boolean isDirty;
   private Biome[] biomes;
   @Nullable
   private volatile LevelLightEngine lightEngine;
   private final Map heightmaps;
   private volatile ChunkStatus status;
   private final Map blockEntities;
   private final Map blockEntityNbts;
   private final LevelChunkSection[] sections;
   private final List entities;
   private final List lights;
   private final ShortList[] postProcessing;
   private final Map structureStarts;
   private final Map structuresRefences;
   private final UpgradeData upgradeData;
   private final ProtoTickList blockTicks;
   private final ProtoTickList liquidTicks;
   private long inhabitedTime;
   private final Map carvingMasks;
   private volatile boolean isLightCorrect;

   public ProtoChunk(ChunkPos chunkPos, UpgradeData upgradeData) {
      this(chunkPos, upgradeData, (LevelChunkSection[])null, new ProtoTickList((block) -> {
         return block == null || block.defaultBlockState().isAir();
      }, chunkPos), new ProtoTickList((fluid) -> {
         return fluid == null || fluid == Fluids.EMPTY;
      }, chunkPos));
   }

   public ProtoChunk(ChunkPos chunkPos, UpgradeData upgradeData, @Nullable LevelChunkSection[] levelChunkSections, ProtoTickList blockTicks, ProtoTickList liquidTicks) {
      this.heightmaps = Maps.newEnumMap(Heightmap.Types.class);
      this.status = ChunkStatus.EMPTY;
      this.blockEntities = Maps.newHashMap();
      this.blockEntityNbts = Maps.newHashMap();
      this.sections = new LevelChunkSection[16];
      this.entities = Lists.newArrayList();
      this.lights = Lists.newArrayList();
      this.postProcessing = new ShortList[16];
      this.structureStarts = Maps.newHashMap();
      this.structuresRefences = Maps.newHashMap();
      this.carvingMasks = Maps.newHashMap();
      this.chunkPos = chunkPos;
      this.upgradeData = upgradeData;
      this.blockTicks = blockTicks;
      this.liquidTicks = liquidTicks;
      if(levelChunkSections != null) {
         if(this.sections.length == levelChunkSections.length) {
            System.arraycopy(levelChunkSections, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", Integer.valueOf(levelChunkSections.length), Integer.valueOf(this.sections.length));
         }
      }

   }

   public BlockState getBlockState(BlockPos blockPos) {
      int var2 = blockPos.getY();
      if(Level.isOutsideBuildHeight(var2)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         LevelChunkSection var3 = this.getSections()[var2 >> 4];
         return LevelChunkSection.isEmpty(var3)?Blocks.AIR.defaultBlockState():var3.getBlockState(blockPos.getX() & 15, var2 & 15, blockPos.getZ() & 15);
      }
   }

   public FluidState getFluidState(BlockPos blockPos) {
      int var2 = blockPos.getY();
      if(Level.isOutsideBuildHeight(var2)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         LevelChunkSection var3 = this.getSections()[var2 >> 4];
         return LevelChunkSection.isEmpty(var3)?Fluids.EMPTY.defaultFluidState():var3.getFluidState(blockPos.getX() & 15, var2 & 15, blockPos.getZ() & 15);
      }
   }

   public Stream getLights() {
      return this.lights.stream();
   }

   public ShortList[] getPackedLights() {
      ShortList[] shortLists = new ShortList[16];

      for(BlockPos var3 : this.lights) {
         ChunkAccess.getOrCreateOffsetList(shortLists, var3.getY() >> 4).add(packOffsetCoordinates(var3));
      }

      return shortLists;
   }

   public void addLight(short var1, int var2) {
      this.addLight(unpackOffsetCoordinates(var1, var2, this.chunkPos));
   }

   public void addLight(BlockPos blockPos) {
      this.lights.add(blockPos.immutable());
   }

   @Nullable
   public BlockState setBlockState(BlockPos blockPos, BlockState var2, boolean var3) {
      int var4 = blockPos.getX();
      int var5 = blockPos.getY();
      int var6 = blockPos.getZ();
      if(var5 >= 0 && var5 < 256) {
         if(this.sections[var5 >> 4] == LevelChunk.EMPTY_SECTION && var2.getBlock() == Blocks.AIR) {
            return var2;
         } else {
            if(var2.getLightEmission() > 0) {
               this.lights.add(new BlockPos((var4 & 15) + this.getPos().getMinBlockX(), var5, (var6 & 15) + this.getPos().getMinBlockZ()));
            }

            LevelChunkSection var7 = this.getOrCreateSection(var5 >> 4);
            BlockState var8 = var7.setBlockState(var4 & 15, var5 & 15, var6 & 15, var2);
            if(this.status.isOrAfter(ChunkStatus.FEATURES) && var2 != var8 && (var2.getLightBlock(this, blockPos) != var8.getLightBlock(this, blockPos) || var2.getLightEmission() != var8.getLightEmission() || var2.useShapeForLightOcclusion() || var8.useShapeForLightOcclusion())) {
               LevelLightEngine var9 = this.getLightEngine();
               var9.checkBlock(blockPos);
            }

            EnumSet<Heightmap.Types> var9 = this.getStatus().heightmapsAfter();
            EnumSet<Heightmap.Types> var10 = null;

            for(Heightmap.Types var12 : var9) {
               Heightmap var13 = (Heightmap)this.heightmaps.get(var12);
               if(var13 == null) {
                  if(var10 == null) {
                     var10 = EnumSet.noneOf(Heightmap.Types.class);
                  }

                  var10.add(var12);
               }
            }

            if(var10 != null) {
               Heightmap.primeHeightmaps(this, var10);
            }

            for(Heightmap.Types var12 : var9) {
               ((Heightmap)this.heightmaps.get(var12)).update(var4 & 15, var5, var6 & 15, var2);
            }

            return var8;
         }
      } else {
         return Blocks.VOID_AIR.defaultBlockState();
      }
   }

   public LevelChunkSection getOrCreateSection(int i) {
      if(this.sections[i] == LevelChunk.EMPTY_SECTION) {
         this.sections[i] = new LevelChunkSection(i << 4);
      }

      return this.sections[i];
   }

   public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
      blockEntity.setPosition(blockPos);
      this.blockEntities.put(blockPos, blockEntity);
   }

   public Set getBlockEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.blockEntityNbts.keySet());
      set.addAll(this.blockEntities.keySet());
      return set;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos) {
      return (BlockEntity)this.blockEntities.get(blockPos);
   }

   public Map getBlockEntities() {
      return this.blockEntities;
   }

   public void addEntity(CompoundTag compoundTag) {
      this.entities.add(compoundTag);
   }

   public void addEntity(Entity entity) {
      CompoundTag var2 = new CompoundTag();
      entity.save(var2);
      this.addEntity(var2);
   }

   public List getEntities() {
      return this.entities;
   }

   public void setBiomes(Biome[] biomes) {
      this.biomes = biomes;
   }

   public Biome[] getBiomes() {
      return this.biomes;
   }

   public void setUnsaved(boolean unsaved) {
      this.isDirty = unsaved;
   }

   public boolean isUnsaved() {
      return this.isDirty;
   }

   public ChunkStatus getStatus() {
      return this.status;
   }

   public void setStatus(ChunkStatus status) {
      this.status = status;
      this.setUnsaved(true);
   }

   public LevelChunkSection[] getSections() {
      return this.sections;
   }

   @Nullable
   public LevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   public Collection getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public void setHeightmap(Heightmap.Types heightmap$Types, long[] longs) {
      this.getOrCreateHeightmapUnprimed(heightmap$Types).setRawData(longs);
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types heightmap$Types) {
      return (Heightmap)this.heightmaps.computeIfAbsent(heightmap$Types, (heightmap$Types) -> {
         return new Heightmap(this, heightmap$Types);
      });
   }

   public int getHeight(Heightmap.Types heightmap$Types, int var2, int var3) {
      Heightmap var4 = (Heightmap)this.heightmaps.get(heightmap$Types);
      if(var4 == null) {
         Heightmap.primeHeightmaps(this, EnumSet.of(heightmap$Types));
         var4 = (Heightmap)this.heightmaps.get(heightmap$Types);
      }

      return var4.getFirstAvailable(var2 & 15, var3 & 15) - 1;
   }

   public ChunkPos getPos() {
      return this.chunkPos;
   }

   public void setLastSaveTime(long lastSaveTime) {
   }

   @Nullable
   public StructureStart getStartForFeature(String string) {
      return (StructureStart)this.structureStarts.get(string);
   }

   public void setStartForFeature(String string, StructureStart structureStart) {
      this.structureStarts.put(string, structureStart);
      this.isDirty = true;
   }

   public Map getAllStarts() {
      return Collections.unmodifiableMap(this.structureStarts);
   }

   public void setAllStarts(Map allStarts) {
      this.structureStarts.clear();
      this.structureStarts.putAll(allStarts);
      this.isDirty = true;
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
      this.isDirty = true;
   }

   public Map getAllReferences() {
      return Collections.unmodifiableMap(this.structuresRefences);
   }

   public void setAllReferences(Map allReferences) {
      this.structuresRefences.clear();
      this.structuresRefences.putAll(allReferences);
      this.isDirty = true;
   }

   public static short packOffsetCoordinates(BlockPos blockPos) {
      int var1 = blockPos.getX();
      int var2 = blockPos.getY();
      int var3 = blockPos.getZ();
      int var4 = var1 & 15;
      int var5 = var2 & 15;
      int var6 = var3 & 15;
      return (short)(var4 | var5 << 4 | var6 << 8);
   }

   public static BlockPos unpackOffsetCoordinates(short var0, int var1, ChunkPos chunkPos) {
      int var3 = (var0 & 15) + (chunkPos.x << 4);
      int var4 = (var0 >>> 4 & 15) + (var1 << 4);
      int var5 = (var0 >>> 8 & 15) + (chunkPos.z << 4);
      return new BlockPos(var3, var4, var5);
   }

   public void markPosForPostprocessing(BlockPos blockPos) {
      if(!Level.isOutsideBuildHeight(blockPos)) {
         ChunkAccess.getOrCreateOffsetList(this.postProcessing, blockPos.getY() >> 4).add(packOffsetCoordinates(blockPos));
      }

   }

   public ShortList[] getPostProcessing() {
      return this.postProcessing;
   }

   public void addPackedPostProcess(short var1, int var2) {
      ChunkAccess.getOrCreateOffsetList(this.postProcessing, var2).add(var1);
   }

   public ProtoTickList getBlockTicks() {
      return this.blockTicks;
   }

   public ProtoTickList getLiquidTicks() {
      return this.liquidTicks;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public void setInhabitedTime(long inhabitedTime) {
      this.inhabitedTime = inhabitedTime;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setBlockEntityNbt(CompoundTag blockEntityNbt) {
      this.blockEntityNbts.put(new BlockPos(blockEntityNbt.getInt("x"), blockEntityNbt.getInt("y"), blockEntityNbt.getInt("z")), blockEntityNbt);
   }

   public Map getBlockEntityNbts() {
      return Collections.unmodifiableMap(this.blockEntityNbts);
   }

   public CompoundTag getBlockEntityNbt(BlockPos blockPos) {
      return (CompoundTag)this.blockEntityNbts.get(blockPos);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos blockPos) {
      BlockEntity var2 = this.getBlockEntity(blockPos);
      return var2 != null?var2.save(new CompoundTag()):(CompoundTag)this.blockEntityNbts.get(blockPos);
   }

   public void removeBlockEntity(BlockPos blockPos) {
      this.blockEntities.remove(blockPos);
      this.blockEntityNbts.remove(blockPos);
   }

   public BitSet getCarvingMask(GenerationStep.Carving generationStep$Carving) {
      return (BitSet)this.carvingMasks.computeIfAbsent(generationStep$Carving, (generationStep$Carving) -> {
         return new BitSet(65536);
      });
   }

   public void setCarvingMask(GenerationStep.Carving generationStep$Carving, BitSet bitSet) {
      this.carvingMasks.put(generationStep$Carving, bitSet);
   }

   public void setLightEngine(LevelLightEngine lightEngine) {
      this.lightEngine = lightEngine;
   }

   public boolean isLightCorrect() {
      return this.isLightCorrect;
   }

   public void setLightCorrect(boolean lightCorrect) {
      this.isLightCorrect = lightCorrect;
      this.setUnsaved(true);
   }

   // $FF: synthetic method
   public TickList getLiquidTicks() {
      return this.getLiquidTicks();
   }

   // $FF: synthetic method
   public TickList getBlockTicks() {
      return this.getBlockTicks();
   }
}
