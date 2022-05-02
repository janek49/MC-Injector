package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;

public class ServerChunkCache extends ChunkSource {
   private static final int MAGIC_NUMBER = (int)Math.pow(17.0D, 2.0D);
   private static final List CHUNK_STATUSES = ChunkStatus.getStatusList();
   private final DistanceManager distanceManager;
   private final ChunkGenerator generator;
   private final ServerLevel level;
   private final Thread mainThread;
   private final ThreadedLevelLightEngine lightEngine;
   private final ServerChunkCache.MainThreadExecutor mainThreadProcessor;
   public final ChunkMap chunkMap;
   private final DimensionDataStorage dataStorage;
   private long lastInhabitedUpdate;
   private boolean spawnEnemies = true;
   private boolean spawnFriendlies = true;
   private final long[] lastChunkPos = new long[4];
   private final ChunkStatus[] lastChunkStatus = new ChunkStatus[4];
   private final ChunkAccess[] lastChunk = new ChunkAccess[4];

   public ServerChunkCache(ServerLevel level, File file, DataFixer dataFixer, StructureManager structureManager, Executor executor, ChunkGenerator generator, int var7, ChunkProgressListener chunkProgressListener, Supplier supplier) {
      this.level = level;
      this.mainThreadProcessor = new ServerChunkCache.MainThreadExecutor(level);
      this.generator = generator;
      this.mainThread = Thread.currentThread();
      File file = level.getDimension().getType().getStorageFolder(file);
      File var11 = new File(file, "data");
      var11.mkdirs();
      this.dataStorage = new DimensionDataStorage(var11, dataFixer);
      this.chunkMap = new ChunkMap(level, file, dataFixer, structureManager, executor, this.mainThreadProcessor, this, this.getGenerator(), chunkProgressListener, supplier, var7);
      this.lightEngine = this.chunkMap.getLightEngine();
      this.distanceManager = this.chunkMap.getDistanceManager();
      this.clearCache();
   }

   public ThreadedLevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   @Nullable
   private ChunkHolder getVisibleChunkIfPresent(long l) {
      return this.chunkMap.getVisibleChunkIfPresent(l);
   }

   public int getTickingGenerated() {
      return this.chunkMap.getTickingGenerated();
   }

   private void storeInCache(long var1, ChunkAccess chunkAccess, ChunkStatus chunkStatus) {
      for(int var5 = 3; var5 > 0; --var5) {
         this.lastChunkPos[var5] = this.lastChunkPos[var5 - 1];
         this.lastChunkStatus[var5] = this.lastChunkStatus[var5 - 1];
         this.lastChunk[var5] = this.lastChunk[var5 - 1];
      }

      this.lastChunkPos[0] = var1;
      this.lastChunkStatus[0] = chunkStatus;
      this.lastChunk[0] = chunkAccess;
   }

   @Nullable
   public ChunkAccess getChunk(int var1, int var2, ChunkStatus chunkStatus, boolean var4) {
      if(Thread.currentThread() != this.mainThread) {
         return (ChunkAccess)CompletableFuture.supplyAsync(() -> {
            return this.getChunk(var1, var2, chunkStatus, var4);
         }, this.mainThreadProcessor).join();
      } else {
         long var5 = ChunkPos.asLong(var1, var2);

         for(int var7 = 0; var7 < 4; ++var7) {
            if(var5 == this.lastChunkPos[var7] && chunkStatus == this.lastChunkStatus[var7]) {
               ChunkAccess var8 = this.lastChunk[var7];
               if(var8 != null || !var4) {
                  return var8;
               }
            }
         }

         CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var7 = this.getChunkFutureMainThread(var1, var2, chunkStatus, var4);
         this.mainThreadProcessor.managedBlock(var7::isDone);
         ChunkAccess var8 = (ChunkAccess)((Either)var7.join()).map((chunkAccess) -> {
            return chunkAccess;
         }, (chunkHolder$ChunkLoadingFailure) -> {
            if(var4) {
               throw new IllegalStateException("Chunk not there when requested: " + chunkHolder$ChunkLoadingFailure);
            } else {
               return null;
            }
         });
         this.storeInCache(var5, var8, chunkStatus);
         return var8;
      }
   }

   @Nullable
   public LevelChunk getChunkNow(int var1, int var2) {
      if(Thread.currentThread() != this.mainThread) {
         return null;
      } else {
         long var3 = ChunkPos.asLong(var1, var2);

         for(int var5 = 0; var5 < 4; ++var5) {
            if(var3 == this.lastChunkPos[var5] && this.lastChunkStatus[var5] == ChunkStatus.FULL) {
               ChunkAccess var6 = this.lastChunk[var5];
               return var6 instanceof LevelChunk?(LevelChunk)var6:null;
            }
         }

         ChunkHolder var5 = this.getVisibleChunkIfPresent(var3);
         if(var5 == null) {
            return null;
         } else {
            Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> var6 = (Either)var5.getFutureIfPresent(ChunkStatus.FULL).getNow((Object)null);
            if(var6 == null) {
               return null;
            } else {
               ChunkAccess var7 = (ChunkAccess)var6.left().orElse((Object)null);
               if(var7 != null) {
                  this.storeInCache(var3, var7, ChunkStatus.FULL);
                  if(var7 instanceof LevelChunk) {
                     return (LevelChunk)var7;
                  }
               }

               return null;
            }
         }
      }
   }

   private void clearCache() {
      Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
      Arrays.fill(this.lastChunkStatus, (Object)null);
      Arrays.fill(this.lastChunk, (Object)null);
   }

   public CompletableFuture getChunkFuture(int var1, int var2, ChunkStatus chunkStatus, boolean var4) {
      boolean var5 = Thread.currentThread() == this.mainThread;
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var6;
      if(var5) {
         var6 = this.getChunkFutureMainThread(var1, var2, chunkStatus, var4);
         this.mainThreadProcessor.managedBlock(var6::isDone);
      } else {
         var6 = CompletableFuture.supplyAsync(() -> {
            return this.getChunkFutureMainThread(var1, var2, chunkStatus, var4);
         }, this.mainThreadProcessor).thenCompose((completableFuture) -> {
            return completableFuture;
         });
      }

      return var6;
   }

   private CompletableFuture getChunkFutureMainThread(int var1, int var2, ChunkStatus chunkStatus, boolean var4) {
      ChunkPos var5 = new ChunkPos(var1, var2);
      long var6 = var5.toLong();
      int var8 = 33 + ChunkStatus.getDistance(chunkStatus);
      ChunkHolder var9 = this.getVisibleChunkIfPresent(var6);
      if(var4) {
         this.distanceManager.addTicket(TicketType.UNKNOWN, var5, var8, var5);
         if(this.chunkAbsent(var9, var8)) {
            ProfilerFiller var10 = this.level.getProfiler();
            var10.push("chunkLoad");
            this.runDistanceManagerUpdates();
            var9 = this.getVisibleChunkIfPresent(var6);
            var10.pop();
            if(this.chunkAbsent(var9, var8)) {
               throw new IllegalStateException("No chunk holder after ticket has been added");
            }
         }
      }

      return this.chunkAbsent(var9, var8)?ChunkHolder.UNLOADED_CHUNK_FUTURE:var9.getOrScheduleFuture(chunkStatus, this.chunkMap);
   }

   private boolean chunkAbsent(@Nullable ChunkHolder chunkHolder, int var2) {
      return chunkHolder == null || chunkHolder.getTicketLevel() > var2;
   }

   public boolean hasChunk(int var1, int var2) {
      ChunkHolder var3 = this.getVisibleChunkIfPresent((new ChunkPos(var1, var2)).toLong());
      int var4 = 33 + ChunkStatus.getDistance(ChunkStatus.FULL);
      return !this.chunkAbsent(var3, var4);
   }

   public BlockGetter getChunkForLighting(int var1, int var2) {
      long var3 = ChunkPos.asLong(var1, var2);
      ChunkHolder var5 = this.getVisibleChunkIfPresent(var3);
      if(var5 == null) {
         return null;
      } else {
         int var6 = CHUNK_STATUSES.size() - 1;

         while(true) {
            ChunkStatus var7 = (ChunkStatus)CHUNK_STATUSES.get(var6);
            Optional<ChunkAccess> var8 = ((Either)var5.getFutureIfPresentUnchecked(var7).getNow(ChunkHolder.UNLOADED_CHUNK)).left();
            if(var8.isPresent()) {
               return (BlockGetter)var8.get();
            }

            if(var7 == ChunkStatus.LIGHT.getParent()) {
               return null;
            }

            --var6;
         }
      }
   }

   public Level getLevel() {
      return this.level;
   }

   public boolean pollTask() {
      return this.mainThreadProcessor.pollTask();
   }

   private boolean runDistanceManagerUpdates() {
      boolean var1 = this.distanceManager.runAllUpdates(this.chunkMap);
      boolean var2 = this.chunkMap.promoteChunkMap();
      if(!var1 && !var2) {
         return false;
      } else {
         this.clearCache();
         return true;
      }
   }

   public boolean isEntityTickingChunk(Entity entity) {
      long var2 = ChunkPos.asLong(Mth.floor(entity.x) >> 4, Mth.floor(entity.z) >> 4);
      return this.checkChunkFuture(var2, ChunkHolder::getEntityTickingChunkFuture);
   }

   public boolean isEntityTickingChunk(ChunkPos chunkPos) {
      return this.checkChunkFuture(chunkPos.toLong(), ChunkHolder::getEntityTickingChunkFuture);
   }

   public boolean isTickingChunk(BlockPos blockPos) {
      long var2 = ChunkPos.asLong(blockPos.getX() >> 4, blockPos.getZ() >> 4);
      return this.checkChunkFuture(var2, ChunkHolder::getTickingChunkFuture);
   }

   public boolean isInAccessibleChunk(Entity entity) {
      long var2 = ChunkPos.asLong(Mth.floor(entity.x) >> 4, Mth.floor(entity.z) >> 4);
      return this.checkChunkFuture(var2, ChunkHolder::getFullChunkFuture);
   }

   private boolean checkChunkFuture(long var1, Function function) {
      ChunkHolder var4 = this.getVisibleChunkIfPresent(var1);
      if(var4 == null) {
         return false;
      } else {
         Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> var5 = (Either)((CompletableFuture)function.apply(var4)).getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK);
         return var5.left().isPresent();
      }
   }

   public void save(boolean b) {
      this.runDistanceManagerUpdates();
      this.chunkMap.saveAllChunks(b);
   }

   public void close() throws IOException {
      this.save(true);
      this.lightEngine.close();
      this.chunkMap.close();
   }

   public void tick(BooleanSupplier booleanSupplier) {
      this.level.getProfiler().push("purge");
      this.distanceManager.purgeStaleTickets();
      this.runDistanceManagerUpdates();
      this.level.getProfiler().popPush("chunks");
      this.tickChunks();
      this.level.getProfiler().popPush("unload");
      this.chunkMap.tick(booleanSupplier);
      this.level.getProfiler().pop();
      this.clearCache();
   }

   private void tickChunks() {
      long var1 = this.level.getGameTime();
      long var3 = var1 - this.lastInhabitedUpdate;
      this.lastInhabitedUpdate = var1;
      LevelData var5 = this.level.getLevelData();
      boolean var6 = var5.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES;
      boolean var7 = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
      if(!var6) {
         this.level.getProfiler().push("pollingChunks");
         int var8 = this.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
         BlockPos var9 = this.level.getSharedSpawnPos();
         boolean var10 = var5.getGameTime() % 400L == 0L;
         this.level.getProfiler().push("naturalSpawnCount");
         int var11 = this.distanceManager.getNaturalSpawnChunkCount();
         MobCategory[] vars12 = MobCategory.values();
         Object2IntMap<MobCategory> var13 = this.level.getMobCategoryCounts();
         this.level.getProfiler().pop();
         this.chunkMap.getChunks().forEach((chunkHolder) -> {
            Optional<LevelChunk> var11 = ((Either)chunkHolder.getEntityTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK)).left();
            if(var11.isPresent()) {
               LevelChunk var12 = (LevelChunk)var11.get();
               this.level.getProfiler().push("broadcast");
               chunkHolder.broadcastChanges(var12);
               this.level.getProfiler().pop();
               ChunkPos var13 = chunkHolder.getPos();
               if(!this.chunkMap.noPlayersCloseForSpawning(var13)) {
                  var12.setInhabitedTime(var12.getInhabitedTime() + var3);
                  if(var7 && (this.spawnEnemies || this.spawnFriendlies) && this.level.getWorldBorder().isWithinBounds(var12.getPos())) {
                     this.level.getProfiler().push("spawner");

                     for(MobCategory var17 : vars12) {
                        if(var17 != MobCategory.MISC && (!var17.isFriendly() || this.spawnFriendlies) && (var17.isFriendly() || this.spawnEnemies) && (!var17.isPersistent() || var10)) {
                           int var18 = var17.getMaxInstancesPerChunk() * var11 / MAGIC_NUMBER;
                           if(var13.getInt(var17) <= var18) {
                              NaturalSpawner.spawnCategoryForChunk(var17, this.level, var12, var9);
                           }
                        }
                     }

                     this.level.getProfiler().pop();
                  }

                  this.level.tickChunk(var12, var8);
               }
            }
         });
         this.level.getProfiler().push("customSpawners");
         if(var7) {
            this.generator.tickCustomSpawners(this.level, this.spawnEnemies, this.spawnFriendlies);
         }

         this.level.getProfiler().pop();
         this.level.getProfiler().pop();
      }

      this.chunkMap.tick();
   }

   public String gatherStats() {
      return "ServerChunkCache: " + this.getLoadedChunksCount();
   }

   @VisibleForTesting
   public int getPendingTasksCount() {
      return this.mainThreadProcessor.getPendingTasksCount();
   }

   public ChunkGenerator getGenerator() {
      return this.generator;
   }

   public int getLoadedChunksCount() {
      return this.chunkMap.size();
   }

   public void blockChanged(BlockPos blockPos) {
      int var2 = blockPos.getX() >> 4;
      int var3 = blockPos.getZ() >> 4;
      ChunkHolder var4 = this.getVisibleChunkIfPresent(ChunkPos.asLong(var2, var3));
      if(var4 != null) {
         var4.blockChanged(blockPos.getX() & 15, blockPos.getY(), blockPos.getZ() & 15);
      }

   }

   public void onLightUpdate(LightLayer lightLayer, SectionPos sectionPos) {
      this.mainThreadProcessor.execute(() -> {
         ChunkHolder var3 = this.getVisibleChunkIfPresent(sectionPos.chunk().toLong());
         if(var3 != null) {
            var3.sectionLightChanged(lightLayer, sectionPos.y());
         }

      });
   }

   public void addRegionTicket(TicketType ticketType, ChunkPos chunkPos, int var3, Object object) {
      this.distanceManager.addRegionTicket(ticketType, chunkPos, var3, object);
   }

   public void removeRegionTicket(TicketType ticketType, ChunkPos chunkPos, int var3, Object object) {
      this.distanceManager.removeRegionTicket(ticketType, chunkPos, var3, object);
   }

   public void updateChunkForced(ChunkPos chunkPos, boolean var2) {
      this.distanceManager.updateChunkForced(chunkPos, var2);
   }

   public void move(ServerPlayer serverPlayer) {
      this.chunkMap.move(serverPlayer);
   }

   public void removeEntity(Entity entity) {
      this.chunkMap.removeEntity(entity);
   }

   public void addEntity(Entity entity) {
      this.chunkMap.addEntity(entity);
   }

   public void broadcastAndSend(Entity entity, Packet packet) {
      this.chunkMap.broadcastAndSend(entity, packet);
   }

   public void broadcast(Entity entity, Packet packet) {
      this.chunkMap.broadcast(entity, packet);
   }

   public void setViewDistance(int viewDistance) {
      this.chunkMap.setViewDistance(viewDistance);
   }

   public void setSpawnSettings(boolean spawnEnemies, boolean spawnFriendlies) {
      this.spawnEnemies = spawnEnemies;
      this.spawnFriendlies = spawnFriendlies;
   }

   public String getChunkDebugData(ChunkPos chunkPos) {
      return this.chunkMap.getChunkDebugData(chunkPos);
   }

   public DimensionDataStorage getDataStorage() {
      return this.dataStorage;
   }

   public PoiManager getPoiManager() {
      return this.chunkMap.getPoiManager();
   }

   // $FF: synthetic method
   public LevelLightEngine getLightEngine() {
      return this.getLightEngine();
   }

   // $FF: synthetic method
   public BlockGetter getLevel() {
      return this.getLevel();
   }

   final class MainThreadExecutor extends BlockableEventLoop {
      private MainThreadExecutor(Level level) {
         super("Chunk source main thread executor for " + Registry.DIMENSION_TYPE.getKey(level.getDimension().getType()));
      }

      protected Runnable wrapRunnable(Runnable runnable) {
         return runnable;
      }

      protected boolean shouldRun(Runnable runnable) {
         return true;
      }

      protected boolean scheduleExecutables() {
         return true;
      }

      protected Thread getRunningThread() {
         return ServerChunkCache.this.mainThread;
      }

      protected boolean pollTask() {
         if(ServerChunkCache.this.runDistanceManagerUpdates()) {
            return true;
         } else {
            ServerChunkCache.this.lightEngine.tryScheduleUpdate();
            return super.pollTask();
         }
      }
   }
}
