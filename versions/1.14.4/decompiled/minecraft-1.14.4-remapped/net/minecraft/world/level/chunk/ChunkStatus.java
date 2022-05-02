package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class ChunkStatus {
   private static final EnumSet PRE_FEATURES = EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG, Heightmap.Types.WORLD_SURFACE_WG);
   private static final EnumSet POST_FEATURES = EnumSet.of(Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE, Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
   private static final ChunkStatus.LoadingTask PASSTHROUGH_LOAD_TASK = (chunkStatus, serverLevel, structureManager, threadedLevelLightEngine, function, chunkAccess) -> {
      if(chunkAccess instanceof ProtoChunk && !chunkAccess.getStatus().isOrAfter(chunkStatus)) {
         ((ProtoChunk)chunkAccess).setStatus(chunkStatus);
      }

      return CompletableFuture.completedFuture(Either.left(chunkAccess));
   };
   public static final ChunkStatus EMPTY = registerSimple("empty", (ChunkStatus)null, -1, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {
   });
   public static final ChunkStatus STRUCTURE_STARTS = register("structure_starts", EMPTY, 0, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (chunkStatus, serverLevel, chunkGenerator, structureManager, threadedLevelLightEngine, function, list, chunkAccess) -> {
      if(!chunkAccess.getStatus().isOrAfter(chunkStatus)) {
         if(serverLevel.getLevelData().isGenerateMapFeatures()) {
            chunkGenerator.createStructures(chunkAccess, chunkGenerator, structureManager);
         }

         if(chunkAccess instanceof ProtoChunk) {
            ((ProtoChunk)chunkAccess).setStatus(chunkStatus);
         }
      }

      return CompletableFuture.completedFuture(Either.left(chunkAccess));
   });
   public static final ChunkStatus STRUCTURE_REFERENCES = registerSimple("structure_references", STRUCTURE_STARTS, 8, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {
      chunkGenerator.createReferences(new WorldGenRegion(serverLevel, list), chunkAccess);
   });
   public static final ChunkStatus BIOMES = registerSimple("biomes", STRUCTURE_REFERENCES, 0, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {
      chunkGenerator.createBiomes(chunkAccess);
   });
   public static final ChunkStatus NOISE = registerSimple("noise", BIOMES, 8, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {
      chunkGenerator.fillFromNoise(new WorldGenRegion(serverLevel, list), chunkAccess);
   });
   public static final ChunkStatus SURFACE = registerSimple("surface", NOISE, 0, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {
      chunkGenerator.buildSurfaceAndBedrock(chunkAccess);
   });
   public static final ChunkStatus CARVERS = registerSimple("carvers", SURFACE, 0, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {
      chunkGenerator.applyCarvers(chunkAccess, GenerationStep.Carving.AIR);
   });
   public static final ChunkStatus LIQUID_CARVERS = registerSimple("liquid_carvers", CARVERS, 0, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {
      chunkGenerator.applyCarvers(chunkAccess, GenerationStep.Carving.LIQUID);
   });
   public static final ChunkStatus FEATURES = register("features", LIQUID_CARVERS, 8, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (chunkStatus, serverLevel, chunkGenerator, structureManager, threadedLevelLightEngine, function, list, chunkAccess) -> {
      chunkAccess.setLightEngine(threadedLevelLightEngine);
      if(!chunkAccess.getStatus().isOrAfter(chunkStatus)) {
         Heightmap.primeHeightmaps(chunkAccess, EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE));
         chunkGenerator.applyBiomeDecoration(new WorldGenRegion(serverLevel, list));
         if(chunkAccess instanceof ProtoChunk) {
            ((ProtoChunk)chunkAccess).setStatus(chunkStatus);
         }
      }

      return CompletableFuture.completedFuture(Either.left(chunkAccess));
   });
   public static final ChunkStatus LIGHT = register("light", FEATURES, 1, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (chunkStatus, serverLevel, chunkGenerator, structureManager, threadedLevelLightEngine, function, list, chunkAccess) -> {
      return lightChunk(chunkStatus, threadedLevelLightEngine, chunkAccess);
   }, (chunkStatus, serverLevel, structureManager, threadedLevelLightEngine, function, chunkAccess) -> {
      return lightChunk(chunkStatus, threadedLevelLightEngine, chunkAccess);
   });
   public static final ChunkStatus SPAWN = registerSimple("spawn", LIGHT, 0, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {
      chunkGenerator.spawnOriginalMobs(new WorldGenRegion(serverLevel, list));
   });
   public static final ChunkStatus HEIGHTMAPS = registerSimple("heightmaps", SPAWN, 0, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (serverLevel, chunkGenerator, list, chunkAccess) -> {
   });
   public static final ChunkStatus FULL = register("full", HEIGHTMAPS, 0, POST_FEATURES, ChunkStatus.ChunkType.LEVELCHUNK, (chunkStatus, serverLevel, chunkGenerator, structureManager, threadedLevelLightEngine, function, list, chunkAccess) -> {
      return (CompletableFuture)function.apply(chunkAccess);
   }, (chunkStatus, serverLevel, structureManager, threadedLevelLightEngine, function, chunkAccess) -> {
      return (CompletableFuture)function.apply(chunkAccess);
   });
   private static final List STATUS_BY_RANGE = ImmutableList.of(FULL, FEATURES, LIQUID_CARVERS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS);
   private static final IntList RANGE_BY_STATUS = (IntList)Util.make(new IntArrayList(getStatusList().size()), (intArrayList) -> {
      int var1 = 0;

      for(int var2 = getStatusList().size() - 1; var2 >= 0; --var2) {
         while(var1 + 1 < STATUS_BY_RANGE.size() && var2 <= ((ChunkStatus)STATUS_BY_RANGE.get(var1 + 1)).getIndex()) {
            ++var1;
         }

         intArrayList.add(0, var1);
      }

   });
   private final String name;
   private final int index;
   private final ChunkStatus parent;
   private final ChunkStatus.GenerationTask generationTask;
   private final ChunkStatus.LoadingTask loadingTask;
   private final int range;
   private final ChunkStatus.ChunkType chunkType;
   private final EnumSet heightmapsAfter;

   private static CompletableFuture lightChunk(ChunkStatus chunkStatus, ThreadedLevelLightEngine threadedLevelLightEngine, ChunkAccess chunkAccess) {
      boolean var3 = isLighted(chunkStatus, chunkAccess);
      if(!chunkAccess.getStatus().isOrAfter(chunkStatus)) {
         ((ProtoChunk)chunkAccess).setStatus(chunkStatus);
      }

      return threadedLevelLightEngine.lightChunk(chunkAccess, var3).thenApply(Either::left);
   }

   private static ChunkStatus registerSimple(String string, @Nullable ChunkStatus var1, int var2, EnumSet enumSet, ChunkStatus.ChunkType chunkStatus$ChunkType, ChunkStatus.SimpleGenerationTask chunkStatus$SimpleGenerationTask) {
      return register(string, var1, var2, enumSet, chunkStatus$ChunkType, chunkStatus$SimpleGenerationTask);
   }

   private static ChunkStatus register(String string, @Nullable ChunkStatus var1, int var2, EnumSet enumSet, ChunkStatus.ChunkType chunkStatus$ChunkType, ChunkStatus.GenerationTask chunkStatus$GenerationTask) {
      return register(string, var1, var2, enumSet, chunkStatus$ChunkType, chunkStatus$GenerationTask, PASSTHROUGH_LOAD_TASK);
   }

   private static ChunkStatus register(String string, @Nullable ChunkStatus var1, int var2, EnumSet enumSet, ChunkStatus.ChunkType chunkStatus$ChunkType, ChunkStatus.GenerationTask chunkStatus$GenerationTask, ChunkStatus.LoadingTask chunkStatus$LoadingTask) {
      return (ChunkStatus)Registry.register(Registry.CHUNK_STATUS, (String)string, new ChunkStatus(string, var1, var2, enumSet, chunkStatus$ChunkType, chunkStatus$GenerationTask, chunkStatus$LoadingTask));
   }

   public static List getStatusList() {
      List<ChunkStatus> list = Lists.newArrayList();

      ChunkStatus var1;
      for(var1 = FULL; var1.getParent() != var1; var1 = var1.getParent()) {
         list.add(var1);
      }

      list.add(var1);
      Collections.reverse(list);
      return list;
   }

   private static boolean isLighted(ChunkStatus chunkStatus, ChunkAccess chunkAccess) {
      return chunkAccess.getStatus().isOrAfter(chunkStatus) && chunkAccess.isLightCorrect();
   }

   public static ChunkStatus getStatus(int i) {
      return i >= STATUS_BY_RANGE.size()?EMPTY:(i < 0?FULL:(ChunkStatus)STATUS_BY_RANGE.get(i));
   }

   public static int maxDistance() {
      return STATUS_BY_RANGE.size();
   }

   public static int getDistance(ChunkStatus chunkStatus) {
      return RANGE_BY_STATUS.getInt(chunkStatus.getIndex());
   }

   ChunkStatus(String name, @Nullable ChunkStatus parent, int range, EnumSet heightmapsAfter, ChunkStatus.ChunkType chunkType, ChunkStatus.GenerationTask generationTask, ChunkStatus.LoadingTask loadingTask) {
      this.name = name;
      this.parent = parent == null?this:parent;
      this.generationTask = generationTask;
      this.loadingTask = loadingTask;
      this.range = range;
      this.chunkType = chunkType;
      this.heightmapsAfter = heightmapsAfter;
      this.index = parent == null?0:parent.getIndex() + 1;
   }

   public int getIndex() {
      return this.index;
   }

   public String getName() {
      return this.name;
   }

   public ChunkStatus getParent() {
      return this.parent;
   }

   public CompletableFuture generate(ServerLevel serverLevel, ChunkGenerator chunkGenerator, StructureManager structureManager, ThreadedLevelLightEngine threadedLevelLightEngine, Function function, List list) {
      return this.generationTask.doWork(this, serverLevel, chunkGenerator, structureManager, threadedLevelLightEngine, function, list, (ChunkAccess)list.get(list.size() / 2));
   }

   public CompletableFuture load(ServerLevel serverLevel, StructureManager structureManager, ThreadedLevelLightEngine threadedLevelLightEngine, Function function, ChunkAccess chunkAccess) {
      return this.loadingTask.doWork(this, serverLevel, structureManager, threadedLevelLightEngine, function, chunkAccess);
   }

   public int getRange() {
      return this.range;
   }

   public ChunkStatus.ChunkType getChunkType() {
      return this.chunkType;
   }

   public static ChunkStatus byName(String name) {
      return (ChunkStatus)Registry.CHUNK_STATUS.get(ResourceLocation.tryParse(name));
   }

   public EnumSet heightmapsAfter() {
      return this.heightmapsAfter;
   }

   public boolean isOrAfter(ChunkStatus chunkStatus) {
      return this.getIndex() >= chunkStatus.getIndex();
   }

   public String toString() {
      return Registry.CHUNK_STATUS.getKey(this).toString();
   }

   public static enum ChunkType {
      PROTOCHUNK,
      LEVELCHUNK;
   }

   interface GenerationTask {
      CompletableFuture doWork(ChunkStatus var1, ServerLevel var2, ChunkGenerator var3, StructureManager var4, ThreadedLevelLightEngine var5, Function var6, List var7, ChunkAccess var8);
   }

   interface LoadingTask {
      CompletableFuture doWork(ChunkStatus var1, ServerLevel var2, StructureManager var3, ThreadedLevelLightEngine var4, Function var5, ChunkAccess var6);
   }

   interface SimpleGenerationTask extends ChunkStatus.GenerationTask {
      default CompletableFuture doWork(ChunkStatus chunkStatus, ServerLevel serverLevel, ChunkGenerator chunkGenerator, StructureManager structureManager, ThreadedLevelLightEngine threadedLevelLightEngine, Function function, List list, ChunkAccess chunkAccess) {
         if(!chunkAccess.getStatus().isOrAfter(chunkStatus)) {
            this.doWork(serverLevel, chunkGenerator, list, chunkAccess);
            if(chunkAccess instanceof ProtoChunk) {
               ((ProtoChunk)chunkAccess).setStatus(chunkStatus);
            }
         }

         return CompletableFuture.completedFuture(Either.left(chunkAccess));
      }

      void doWork(ServerLevel var1, ChunkGenerator var2, List var3, ChunkAccess var4);
   }
}
