package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ChunkTickList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ProtoTickList;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIO;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
   private static final Logger LOGGER = LogManager.getLogger();

   public static ProtoChunk read(ServerLevel serverLevel, StructureManager structureManager, PoiManager poiManager, ChunkPos chunkPos, CompoundTag compoundTag) {
      ChunkGenerator<?> var5 = serverLevel.getChunkSource().getGenerator();
      BiomeSource var6 = var5.getBiomeSource();
      CompoundTag var7 = compoundTag.getCompound("Level");
      ChunkPos var8 = new ChunkPos(var7.getInt("xPos"), var7.getInt("zPos"));
      if(!Objects.equals(chunkPos, var8)) {
         LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", chunkPos, chunkPos, var8);
      }

      Biome[] vars9 = new Biome[256];
      BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();
      if(var7.contains("Biomes", 11)) {
         int[] vars11 = var7.getIntArray("Biomes");

         for(int var12 = 0; var12 < vars11.length; ++var12) {
            vars9[var12] = (Biome)Registry.BIOME.byId(vars11[var12]);
            if(vars9[var12] == null) {
               vars9[var12] = var6.getBiome(var10.set((var12 & 15) + chunkPos.getMinBlockX(), 0, (var12 >> 4 & 15) + chunkPos.getMinBlockZ()));
            }
         }
      } else {
         for(int var11 = 0; var11 < vars9.length; ++var11) {
            vars9[var11] = var6.getBiome(var10.set((var11 & 15) + chunkPos.getMinBlockX(), 0, (var11 >> 4 & 15) + chunkPos.getMinBlockZ()));
         }
      }

      UpgradeData var11 = var7.contains("UpgradeData", 10)?new UpgradeData(var7.getCompound("UpgradeData")):UpgradeData.EMPTY;
      ProtoTickList<Block> var12 = new ProtoTickList((block) -> {
         return block == null || block.defaultBlockState().isAir();
      }, chunkPos, var7.getList("ToBeTicked", 9));
      ProtoTickList<Fluid> var13 = new ProtoTickList((fluid) -> {
         return fluid == null || fluid == Fluids.EMPTY;
      }, chunkPos, var7.getList("LiquidsToBeTicked", 9));
      boolean var14 = var7.getBoolean("isLightOn");
      ListTag var15 = var7.getList("Sections", 10);
      int var16 = 16;
      LevelChunkSection[] vars17 = new LevelChunkSection[16];
      boolean var18 = serverLevel.getDimension().isHasSkyLight();
      ChunkSource var19 = serverLevel.getChunkSource();
      LevelLightEngine var20 = var19.getLightEngine();
      if(var14) {
         var20.retainData(chunkPos, true);
      }

      for(int var21 = 0; var21 < var15.size(); ++var21) {
         CompoundTag var22 = var15.getCompound(var21);
         int var23 = var22.getByte("Y");
         if(var22.contains("Palette", 9) && var22.contains("BlockStates", 12)) {
            LevelChunkSection var24 = new LevelChunkSection(var23 << 4);
            var24.getStates().read(var22.getList("Palette", 10), var22.getLongArray("BlockStates"));
            var24.recalcBlockCounts();
            if(!var24.isEmpty()) {
               vars17[var23] = var24;
            }

            poiManager.checkConsistencyWithBlocks(chunkPos, var24);
         }

         if(var14) {
            if(var22.contains("BlockLight", 7)) {
               var20.queueSectionData(LightLayer.BLOCK, SectionPos.of(chunkPos, var23), new DataLayer(var22.getByteArray("BlockLight")));
            }

            if(var18 && var22.contains("SkyLight", 7)) {
               var20.queueSectionData(LightLayer.SKY, SectionPos.of(chunkPos, var23), new DataLayer(var22.getByteArray("SkyLight")));
            }
         }
      }

      long var21 = var7.getLong("InhabitedTime");
      ChunkStatus.ChunkType var23 = getChunkTypeFromTag(compoundTag);
      ChunkAccess var24;
      if(var23 == ChunkStatus.ChunkType.LEVELCHUNK) {
         TickList<Block> var25;
         if(var7.contains("TileTicks", 9)) {
            ListTag var10000 = var7.getList("TileTicks", 10);
            DefaultedRegistry var10001 = Registry.BLOCK;
            Registry.BLOCK.getClass();
            Function var61 = var10001::getKey;
            DefaultedRegistry var10002 = Registry.BLOCK;
            Registry.BLOCK.getClass();
            var25 = ChunkTickList.create(var10000, var61, var10002::get);
         } else {
            var25 = var12;
         }

         TickList<Fluid> var26;
         if(var7.contains("LiquidTicks", 9)) {
            ListTag var60 = var7.getList("LiquidTicks", 10);
            DefaultedRegistry var62 = Registry.FLUID;
            Registry.FLUID.getClass();
            Function var63 = var62::getKey;
            DefaultedRegistry var64 = Registry.FLUID;
            Registry.FLUID.getClass();
            var26 = ChunkTickList.create(var60, var63, var64::get);
         } else {
            var26 = var13;
         }

         var24 = new LevelChunk(serverLevel.getLevel(), chunkPos, vars9, var11, var25, var26, var21, vars17, (levelChunk) -> {
            postLoadChunk(var7, levelChunk);
         });
      } else {
         ProtoChunk var25 = new ProtoChunk(chunkPos, var11, vars17, var12, var13);
         var24 = var25;
         var25.setBiomes(vars9);
         var25.setInhabitedTime(var21);
         var25.setStatus(ChunkStatus.byName(var7.getString("Status")));
         if(var25.getStatus().isOrAfter(ChunkStatus.FEATURES)) {
            var25.setLightEngine(var20);
         }

         if(!var14 && var25.getStatus().isOrAfter(ChunkStatus.LIGHT)) {
            for(BlockPos var27 : BlockPos.betweenClosed(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), 255, chunkPos.getMaxBlockZ())) {
               if(var24.getBlockState(var27).getLightEmission() != 0) {
                  var25.addLight(var27);
               }
            }
         }
      }

      var24.setLightCorrect(var14);
      CompoundTag var25 = var7.getCompound("Heightmaps");
      EnumSet<Heightmap.Types> var26 = EnumSet.noneOf(Heightmap.Types.class);

      for(Heightmap.Types var28 : var24.getStatus().heightmapsAfter()) {
         String var29 = var28.getSerializationKey();
         if(var25.contains(var29, 12)) {
            var24.setHeightmap(var28, var25.getLongArray(var29));
         } else {
            var26.add(var28);
         }
      }

      Heightmap.primeHeightmaps(var24, var26);
      CompoundTag var27 = var7.getCompound("Structures");
      var24.setAllStarts(unpackStructureStart(var5, structureManager, var6, var27));
      var24.setAllReferences(unpackStructureReferences(var27));
      if(var7.getBoolean("shouldSave")) {
         var24.setUnsaved(true);
      }

      ListTag var28 = var7.getList("PostProcessing", 9);

      for(int var29 = 0; var29 < var28.size(); ++var29) {
         ListTag var30 = var28.getList(var29);

         for(int var31 = 0; var31 < var30.size(); ++var31) {
            var24.addPackedPostProcess(var30.getShort(var31), var29);
         }
      }

      if(var23 == ChunkStatus.ChunkType.LEVELCHUNK) {
         return new ImposterProtoChunk((LevelChunk)var24);
      } else {
         ProtoChunk var29 = (ProtoChunk)var24;
         ListTag var30 = var7.getList("Entities", 10);

         for(int var31 = 0; var31 < var30.size(); ++var31) {
            var29.addEntity(var30.getCompound(var31));
         }

         ListTag var31 = var7.getList("TileEntities", 10);

         for(int var32 = 0; var32 < var31.size(); ++var32) {
            CompoundTag var33 = var31.getCompound(var32);
            var24.setBlockEntityNbt(var33);
         }

         ListTag var32 = var7.getList("Lights", 9);

         for(int var33 = 0; var33 < var32.size(); ++var33) {
            ListTag var34 = var32.getList(var33);

            for(int var35 = 0; var35 < var34.size(); ++var35) {
               var29.addLight(var34.getShort(var35), var33);
            }
         }

         CompoundTag var33 = var7.getCompound("CarvingMasks");

         for(String var35 : var33.getAllKeys()) {
            GenerationStep.Carving var36 = GenerationStep.Carving.valueOf(var35);
            var29.setCarvingMask(var36, BitSet.valueOf(var33.getByteArray(var35)));
         }

         return var29;
      }
   }

   public static CompoundTag write(ServerLevel serverLevel, ChunkAccess chunkAccess) {
      ChunkPos var2 = chunkAccess.getPos();
      CompoundTag var3 = new CompoundTag();
      CompoundTag var4 = new CompoundTag();
      var3.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      var3.put("Level", var4);
      var4.putInt("xPos", var2.x);
      var4.putInt("zPos", var2.z);
      var4.putLong("LastUpdate", serverLevel.getGameTime());
      var4.putLong("InhabitedTime", chunkAccess.getInhabitedTime());
      var4.putString("Status", chunkAccess.getStatus().getName());
      UpgradeData var5 = chunkAccess.getUpgradeData();
      if(!var5.isEmpty()) {
         var4.put("UpgradeData", var5.write());
      }

      LevelChunkSection[] vars6 = chunkAccess.getSections();
      ListTag var7 = new ListTag();
      LevelLightEngine var8 = serverLevel.getChunkSource().getLightEngine();
      boolean var9 = chunkAccess.isLightCorrect();

      for(int var10 = -1; var10 < 17; ++var10) {
         LevelChunkSection var12 = (LevelChunkSection)Arrays.stream(vars6).filter((levelChunkSection) -> {
            return levelChunkSection != null && levelChunkSection.bottomBlockY() >> 4 == var10;
         }).findFirst().orElse(LevelChunk.EMPTY_SECTION);
         DataLayer var13 = var8.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(var2, var10));
         DataLayer var14 = var8.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(var2, var10));
         if(var12 != LevelChunk.EMPTY_SECTION || var13 != null || var14 != null) {
            CompoundTag var15 = new CompoundTag();
            var15.putByte("Y", (byte)(var10 & 255));
            if(var12 != LevelChunk.EMPTY_SECTION) {
               var12.getStates().write(var15, "Palette", "BlockStates");
            }

            if(var13 != null && !var13.isEmpty()) {
               var15.putByteArray("BlockLight", var13.getData());
            }

            if(var14 != null && !var14.isEmpty()) {
               var15.putByteArray("SkyLight", var14.getData());
            }

            var7.add(var15);
         }
      }

      var4.put("Sections", var7);
      if(var9) {
         var4.putBoolean("isLightOn", true);
      }

      Biome[] vars10 = chunkAccess.getBiomes();
      int[] vars11 = vars10 != null?new int[vars10.length]:new int[0];
      if(vars10 != null) {
         for(int var12 = 0; var12 < vars10.length; ++var12) {
            vars11[var12] = Registry.BIOME.getId(vars10[var12]);
         }
      }

      var4.putIntArray("Biomes", vars11);
      ListTag var12 = new ListTag();

      for(BlockPos var14 : chunkAccess.getBlockEntitiesPos()) {
         CompoundTag var15 = chunkAccess.getBlockEntityNbtForSaving(var14);
         if(var15 != null) {
            var12.add(var15);
         }
      }

      var4.put("TileEntities", var12);
      ListTag var13 = new ListTag();
      if(chunkAccess.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
         LevelChunk var14 = (LevelChunk)chunkAccess;
         var14.setLastSaveHadEntities(false);

         for(int var15 = 0; var15 < var14.getEntitySections().length; ++var15) {
            for(Entity var17 : var14.getEntitySections()[var15]) {
               CompoundTag var18 = new CompoundTag();
               if(var17.save(var18)) {
                  var14.setLastSaveHadEntities(true);
                  var13.add(var18);
               }
            }
         }
      } else {
         ProtoChunk var14 = (ProtoChunk)chunkAccess;
         var13.addAll(var14.getEntities());
         var4.put("Lights", packOffsets(var14.getPackedLights()));
         CompoundTag var15 = new CompoundTag();

         for(GenerationStep.Carving var19 : GenerationStep.Carving.values()) {
            var15.putByteArray(var19.toString(), chunkAccess.getCarvingMask(var19).toByteArray());
         }

         var4.put("CarvingMasks", var15);
      }

      var4.put("Entities", var13);
      TickList<Block> var14 = chunkAccess.getBlockTicks();
      if(var14 instanceof ProtoTickList) {
         var4.put("ToBeTicked", ((ProtoTickList)var14).save());
      } else if(var14 instanceof ChunkTickList) {
         var4.put("TileTicks", ((ChunkTickList)var14).save(serverLevel.getGameTime()));
      } else {
         var4.put("TileTicks", serverLevel.getBlockTicks().save(var2));
      }

      TickList<Fluid> var15 = chunkAccess.getLiquidTicks();
      if(var15 instanceof ProtoTickList) {
         var4.put("LiquidsToBeTicked", ((ProtoTickList)var15).save());
      } else if(var15 instanceof ChunkTickList) {
         var4.put("LiquidTicks", ((ChunkTickList)var15).save(serverLevel.getGameTime()));
      } else {
         var4.put("LiquidTicks", serverLevel.getLiquidTicks().save(var2));
      }

      var4.put("PostProcessing", packOffsets(chunkAccess.getPostProcessing()));
      CompoundTag var16 = new CompoundTag();

      for(Entry<Heightmap.Types, Heightmap> var18 : chunkAccess.getHeightmaps()) {
         if(chunkAccess.getStatus().heightmapsAfter().contains(var18.getKey())) {
            var16.put(((Heightmap.Types)var18.getKey()).getSerializationKey(), new LongArrayTag(((Heightmap)var18.getValue()).getRawData()));
         }
      }

      var4.put("Heightmaps", var16);
      var4.put("Structures", packStructureData(var2, chunkAccess.getAllStarts(), chunkAccess.getAllReferences()));
      return var3;
   }

   public static ChunkStatus.ChunkType getChunkTypeFromTag(@Nullable CompoundTag compoundTag) {
      if(compoundTag != null) {
         ChunkStatus var1 = ChunkStatus.byName(compoundTag.getCompound("Level").getString("Status"));
         if(var1 != null) {
            return var1.getChunkType();
         }
      }

      return ChunkStatus.ChunkType.PROTOCHUNK;
   }

   private static void postLoadChunk(CompoundTag compoundTag, LevelChunk levelChunk) {
      ListTag var2 = compoundTag.getList("Entities", 10);
      Level var3 = levelChunk.getLevel();

      for(int var4 = 0; var4 < var2.size(); ++var4) {
         CompoundTag var5 = var2.getCompound(var4);
         EntityType.loadEntityRecursive(var5, var3, (var1) -> {
            levelChunk.addEntity(var1);
            return var1;
         });
         levelChunk.setLastSaveHadEntities(true);
      }

      ListTag var4 = compoundTag.getList("TileEntities", 10);

      for(int var5 = 0; var5 < var4.size(); ++var5) {
         CompoundTag var6 = var4.getCompound(var5);
         boolean var7 = var6.getBoolean("keepPacked");
         if(var7) {
            levelChunk.setBlockEntityNbt(var6);
         } else {
            BlockEntity var8 = BlockEntity.loadStatic(var6);
            if(var8 != null) {
               levelChunk.addBlockEntity(var8);
            }
         }
      }

   }

   private static CompoundTag packStructureData(ChunkPos chunkPos, Map var1, Map var2) {
      CompoundTag compoundTag = new CompoundTag();
      CompoundTag var4 = new CompoundTag();

      for(Entry<String, StructureStart> var6 : var1.entrySet()) {
         var4.put((String)var6.getKey(), ((StructureStart)var6.getValue()).createTag(chunkPos.x, chunkPos.z));
      }

      compoundTag.put("Starts", var4);
      CompoundTag var5 = new CompoundTag();

      for(Entry<String, LongSet> var7 : var2.entrySet()) {
         var5.put((String)var7.getKey(), new LongArrayTag((LongSet)var7.getValue()));
      }

      compoundTag.put("References", var5);
      return compoundTag;
   }

   private static Map unpackStructureStart(ChunkGenerator chunkGenerator, StructureManager structureManager, BiomeSource biomeSource, CompoundTag compoundTag) {
      Map<String, StructureStart> map = Maps.newHashMap();
      CompoundTag var5 = compoundTag.getCompound("Starts");

      for(String var7 : var5.getAllKeys()) {
         map.put(var7, StructureFeatureIO.loadStaticStart(chunkGenerator, structureManager, biomeSource, var5.getCompound(var7)));
      }

      return map;
   }

   private static Map unpackStructureReferences(CompoundTag compoundTag) {
      Map<String, LongSet> map = Maps.newHashMap();
      CompoundTag var2 = compoundTag.getCompound("References");

      for(String var4 : var2.getAllKeys()) {
         map.put(var4, new LongOpenHashSet(var2.getLongArray(var4)));
      }

      return map;
   }

   public static ListTag packOffsets(ShortList[] shortLists) {
      ListTag listTag = new ListTag();

      for(ShortList var5 : shortLists) {
         ListTag var6 = new ListTag();
         if(var5 != null) {
            ShortListIterator var7 = var5.iterator();

            while(var7.hasNext()) {
               Short var8 = (Short)var7.next();
               var6.add(new ShortTag(var8.shortValue()));
            }
         }

         listTag.add(var6);
      }

      return listTag;
   }
}
