package net.minecraft.server.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkTaskPriorityQueue;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.PlayerMap;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelConflictException;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkMap extends ChunkStorage implements ChunkHolder.PlayerProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final int MAX_CHUNK_DISTANCE = 33 + ChunkStatus.maxDistance();
   private final Long2ObjectLinkedOpenHashMap updatingChunkMap = new Long2ObjectLinkedOpenHashMap();
   private volatile Long2ObjectLinkedOpenHashMap visibleChunkMap;
   private final Long2ObjectLinkedOpenHashMap pendingUnloads;
   private final LongSet entitiesInLevel;
   private final ServerLevel level;
   private final ThreadedLevelLightEngine lightEngine;
   private final BlockableEventLoop mainThreadExecutor;
   private final ChunkGenerator generator;
   private final Supplier overworldDataStorage;
   private final PoiManager poiManager;
   private final LongSet toDrop;
   private boolean modified;
   private final ChunkTaskPriorityQueueSorter queueSorter;
   private final ProcessorHandle worldgenMailbox;
   private final ProcessorHandle mainThreadMailbox;
   private final ChunkProgressListener progressListener;
   private final ChunkMap.DistanceManager distanceManager;
   private final AtomicInteger tickingGenerated;
   private final StructureManager structureManager;
   private final File storageFolder;
   private final PlayerMap playerMap;
   private final Int2ObjectMap entityMap;
   private final Queue unloadQueue;
   private int viewDistance;

   public ChunkMap(ServerLevel level, File file, DataFixer dataFixer, StructureManager structureManager, Executor executor, BlockableEventLoop mainThreadExecutor, LightChunkGetter lightChunkGetter, ChunkGenerator generator, ChunkProgressListener progressListener, Supplier overworldDataStorage, int viewDistance) {
      super(new File(level.getDimension().getType().getStorageFolder(file), "region"), dataFixer);
      this.visibleChunkMap = this.updatingChunkMap.clone();
      this.pendingUnloads = new Long2ObjectLinkedOpenHashMap();
      this.entitiesInLevel = new LongOpenHashSet();
      this.toDrop = new LongOpenHashSet();
      this.tickingGenerated = new AtomicInteger();
      this.playerMap = new PlayerMap();
      this.entityMap = new Int2ObjectOpenHashMap();
      this.unloadQueue = Queues.newConcurrentLinkedQueue();
      this.structureManager = structureManager;
      this.storageFolder = level.getDimension().getType().getStorageFolder(file);
      this.level = level;
      this.generator = generator;
      this.mainThreadExecutor = mainThreadExecutor;
      ProcessorMailbox<Runnable> var12 = ProcessorMailbox.create(executor, "worldgen");
      mainThreadExecutor.getClass();
      ProcessorHandle<Runnable> var13 = ProcessorHandle.of("main", mainThreadExecutor::tell);
      this.progressListener = progressListener;
      ProcessorMailbox<Runnable> var14 = ProcessorMailbox.create(executor, "light");
      this.queueSorter = new ChunkTaskPriorityQueueSorter(ImmutableList.of(var12, var13, var14), executor, Integer.MAX_VALUE);
      this.worldgenMailbox = this.queueSorter.getProcessor(var12, false);
      this.mainThreadMailbox = this.queueSorter.getProcessor(var13, false);
      this.lightEngine = new ThreadedLevelLightEngine(lightChunkGetter, this, this.level.getDimension().isHasSkyLight(), var14, this.queueSorter.getProcessor(var14, false));
      this.distanceManager = new ChunkMap.DistanceManager(executor, mainThreadExecutor);
      this.overworldDataStorage = overworldDataStorage;
      this.poiManager = new PoiManager(new File(this.storageFolder, "poi"), dataFixer);
      this.setViewDistance(viewDistance);
   }

   private static double euclideanDistanceSquared(ChunkPos chunkPos, Entity entity) {
      double var2 = (double)(chunkPos.x * 16 + 8);
      double var4 = (double)(chunkPos.z * 16 + 8);
      double var6 = var2 - entity.x;
      double var8 = var4 - entity.z;
      return var6 * var6 + var8 * var8;
   }

   private static int checkerboardDistance(ChunkPos chunkPos, ServerPlayer serverPlayer, boolean var2) {
      int var3;
      int var4;
      if(var2) {
         SectionPos var5 = serverPlayer.getLastSectionPos();
         var3 = var5.x();
         var4 = var5.z();
      } else {
         var3 = Mth.floor(serverPlayer.x / 16.0D);
         var4 = Mth.floor(serverPlayer.z / 16.0D);
      }

      return checkerboardDistance(chunkPos, var3, var4);
   }

   private static int checkerboardDistance(ChunkPos chunkPos, int var1, int var2) {
      int var3 = chunkPos.x - var1;
      int var4 = chunkPos.z - var2;
      return Math.max(Math.abs(var3), Math.abs(var4));
   }

   protected ThreadedLevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   @Nullable
   protected ChunkHolder getUpdatingChunkIfPresent(long l) {
      return (ChunkHolder)this.updatingChunkMap.get(l);
   }

   @Nullable
   protected ChunkHolder getVisibleChunkIfPresent(long l) {
      return (ChunkHolder)this.visibleChunkMap.get(l);
   }

   protected IntSupplier getChunkQueueLevel(long l) {
      return () -> {
         ChunkHolder var3 = this.getVisibleChunkIfPresent(l);
         return var3 == null?ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1:Math.min(var3.getQueueLevel(), ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1);
      };
   }

   public String getChunkDebugData(ChunkPos chunkPos) {
      ChunkHolder var2 = this.getVisibleChunkIfPresent(chunkPos.toLong());
      if(var2 == null) {
         return "null";
      } else {
         String var3 = var2.getTicketLevel() + "\n";
         ChunkStatus var4 = var2.getLastAvailableStatus();
         ChunkAccess var5 = var2.getLastAvailable();
         if(var4 != null) {
            var3 = var3 + "St: §" + var4.getIndex() + var4 + '§' + "r\n";
         }

         if(var5 != null) {
            var3 = var3 + "Ch: §" + var5.getStatus().getIndex() + var5.getStatus() + '§' + "r\n";
         }

         ChunkHolder.FullChunkStatus var6 = var2.getFullStatus();
         var3 = var3 + "§" + var6.ordinal() + var6;
         return var3 + '§' + "r";
      }
   }

   private CompletableFuture getChunkRangeFuture(ChunkPos chunkPos, int var2, IntFunction intFunction) {
      List<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var4 = Lists.newArrayList();
      int var5 = chunkPos.x;
      int var6 = chunkPos.z;

      for(int var7 = -var2; var7 <= var2; ++var7) {
         for(int var8 = -var2; var8 <= var2; ++var8) {
            int var9 = Math.max(Math.abs(var8), Math.abs(var7));
            final ChunkPos var10 = new ChunkPos(var5 + var8, var6 + var7);
            long var11 = var10.toLong();
            ChunkHolder var13 = this.getUpdatingChunkIfPresent(var11);
            if(var13 == null) {
               return CompletableFuture.completedFuture(Either.right(new ChunkHolder.ChunkLoadingFailure() {
                  public String toString() {
                     return "Unloaded " + var10.toString();
                  }
               }));
            }

            ChunkStatus var14 = (ChunkStatus)intFunction.apply(var9);
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var15 = var13.getOrScheduleFuture(var14, this);
            var4.add(var15);
         }
      }

      CompletableFuture<List<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var7 = Util.sequence(var4);
      return var7.thenApply((list) -> {
         List<ChunkAccess> list = Lists.newArrayList();
         final int var6 = 0;

         for(final Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> var8 : list) {
            Optional<ChunkAccess> var9 = var8.left();
            if(!var9.isPresent()) {
               return Either.right(new ChunkHolder.ChunkLoadingFailure() {
                  public String toString() {
                     return "Unloaded " + new ChunkPos(var5 + var6 % (var2 * 2 + 1), var6 + var6 / (var2 * 2 + 1)) + " " + ((ChunkHolder.ChunkLoadingFailure)var8.right().get()).toString();
                  }
               });
            }

            list.add(var9.get());
            ++var6;
         }

         return Either.left(list);
      });
   }

   public CompletableFuture getEntityTickingRangeFuture(ChunkPos chunkPos) {
      return this.getChunkRangeFuture(chunkPos, 2, (i) -> {
         return ChunkStatus.FULL;
      }).thenApplyAsync((either) -> {
         return either.mapLeft((list) -> {
            return (LevelChunk)list.get(list.size() / 2);
         });
      }, this.mainThreadExecutor);
   }

   @Nullable
   private ChunkHolder updateChunkScheduling(long var1, int var3, @Nullable ChunkHolder var4, int var5) {
      if(var5 > MAX_CHUNK_DISTANCE && var3 > MAX_CHUNK_DISTANCE) {
         return var4;
      } else {
         if(var4 != null) {
            var4.setTicketLevel(var3);
         }

         if(var4 != null) {
            if(var3 > MAX_CHUNK_DISTANCE) {
               this.toDrop.add(var1);
            } else {
               this.toDrop.remove(var1);
            }
         }

         if(var3 <= MAX_CHUNK_DISTANCE && var4 == null) {
            var4 = (ChunkHolder)this.pendingUnloads.remove(var1);
            if(var4 != null) {
               var4.setTicketLevel(var3);
            } else {
               var4 = new ChunkHolder(new ChunkPos(var1), var3, this.lightEngine, this.queueSorter, this);
            }

            this.updatingChunkMap.put(var1, var4);
            this.modified = true;
         }

         return var4;
      }
   }

   public void close() throws IOException {
      this.queueSorter.close();
      this.poiManager.close();
      super.close();
   }

   protected void saveAllChunks(boolean b) {
      if(b) {
         List<ChunkHolder> var2 = (List)this.visibleChunkMap.values().stream().filter(ChunkHolder::wasAccessibleSinceLastSave).peek(ChunkHolder::refreshAccessibility).collect(Collectors.toList());
         MutableBoolean var3 = new MutableBoolean();

         while(true) {
            var3.setFalse();
            var2.stream().map((chunkHolder) -> {
               CompletableFuture<ChunkAccess> var2;
               while(true) {
                  var2 = chunkHolder.getChunkToSave();
                  this.mainThreadExecutor.managedBlock(var2::isDone);
                  if(var2 == chunkHolder.getChunkToSave()) {
                     break;
                  }
               }

               return (ChunkAccess)var2.join();
            }).filter((chunkAccess) -> {
               return chunkAccess instanceof ImposterProtoChunk || chunkAccess instanceof LevelChunk;
            }).filter(this::save).forEach((chunkAccess) -> {
               var3.setTrue();
            });
            if(!var3.isTrue()) {
               break;
            }
         }

         this.processUnloads(() -> {
            return true;
         });
         LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.storageFolder.getName());
      } else {
         this.visibleChunkMap.values().stream().filter(ChunkHolder::wasAccessibleSinceLastSave).forEach((chunkHolder) -> {
            ChunkAccess var2 = (ChunkAccess)chunkHolder.getChunkToSave().getNow((Object)null);
            if(var2 instanceof ImposterProtoChunk || var2 instanceof LevelChunk) {
               this.save(var2);
               chunkHolder.refreshAccessibility();
            }

         });
      }

   }

   protected void tick(BooleanSupplier booleanSupplier) {
      ProfilerFiller var2 = this.level.getProfiler();
      var2.push("poi");
      this.poiManager.tick(booleanSupplier);
      var2.popPush("chunk_unload");
      if(!this.level.noSave()) {
         this.processUnloads(booleanSupplier);
      }

      var2.pop();
   }

   private void processUnloads(BooleanSupplier booleanSupplier) {
      LongIterator var2 = this.toDrop.iterator();

      for(int var3 = 0; var2.hasNext() && (booleanSupplier.getAsBoolean() || var3 < 200 || this.toDrop.size() > 2000); var2.remove()) {
         long var4 = var2.nextLong();
         ChunkHolder var6 = (ChunkHolder)this.updatingChunkMap.remove(var4);
         if(var6 != null) {
            this.pendingUnloads.put(var4, var6);
            this.modified = true;
            ++var3;
            this.scheduleUnload(var4, var6);
         }
      }

      Runnable var4;
      while((booleanSupplier.getAsBoolean() || this.unloadQueue.size() > 2000) && (var4 = (Runnable)this.unloadQueue.poll()) != null) {
         var4.run();
      }

   }

   private void scheduleUnload(long var1, ChunkHolder chunkHolder) {
      CompletableFuture<ChunkAccess> var4 = chunkHolder.getChunkToSave();
      Consumer var10001 = (chunkAccess) -> {
         CompletableFuture<ChunkAccess> completableFuture = chunkHolder.getChunkToSave();
         if(completableFuture != var4) {
            this.scheduleUnload(var1, chunkHolder);
         } else {
            if(this.pendingUnloads.remove(var1, chunkHolder) && chunkAccess != null) {
               if(chunkAccess instanceof LevelChunk) {
                  ((LevelChunk)chunkAccess).setLoaded(false);
               }

               this.save(chunkAccess);
               if(this.entitiesInLevel.remove(var1) && chunkAccess instanceof LevelChunk) {
                  LevelChunk var7 = (LevelChunk)chunkAccess;
                  this.level.unload(var7);
               }

               this.lightEngine.updateChunkStatus(chunkAccess.getPos());
               this.lightEngine.tryScheduleUpdate();
               this.progressListener.onStatusChange(chunkAccess.getPos(), (ChunkStatus)null);
            }

         }
      };
      Queue var10002 = this.unloadQueue;
      this.unloadQueue.getClass();
      var4.thenAcceptAsync(var10001, var10002::add).whenComplete((void, throwable) -> {
         if(throwable != null) {
            LOGGER.error("Failed to save chunk " + chunkHolder.getPos(), throwable);
         }

      });
   }

   protected boolean promoteChunkMap() {
      if(!this.modified) {
         return false;
      } else {
         this.visibleChunkMap = this.updatingChunkMap.clone();
         this.modified = false;
         return true;
      }
   }

   public CompletableFuture schedule(ChunkHolder chunkHolder, ChunkStatus chunkStatus) {
      ChunkPos var3 = chunkHolder.getPos();
      if(chunkStatus == ChunkStatus.EMPTY) {
         return this.scheduleChunkLoad(var3);
      } else {
         CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var4 = chunkHolder.getOrScheduleFuture(chunkStatus.getParent(), this);
         return var4.thenComposeAsync((either) -> {
            Optional<ChunkAccess> var5 = either.left();
            if(!var5.isPresent()) {
               return CompletableFuture.completedFuture(either);
            } else {
               if(chunkStatus == ChunkStatus.LIGHT) {
                  this.distanceManager.addTicket(TicketType.LIGHT, var3, 33 + ChunkStatus.getDistance(ChunkStatus.FEATURES), var3);
               }

               ChunkAccess var6 = (ChunkAccess)var5.get();
               if(var6.getStatus().isOrAfter(chunkStatus)) {
                  CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var7;
                  if(chunkStatus == ChunkStatus.LIGHT) {
                     var7 = this.scheduleChunkGeneration(chunkHolder, chunkStatus);
                  } else {
                     var7 = chunkStatus.load(this.level, this.structureManager, this.lightEngine, (chunkAccess) -> {
                        return this.protoChunkToFullChunk(chunkHolder);
                     }, var6);
                  }

                  this.progressListener.onStatusChange(var3, chunkStatus);
                  return var7;
               } else {
                  return this.scheduleChunkGeneration(chunkHolder, chunkStatus);
               }
            }
         }, this.mainThreadExecutor);
      }
   }

   private CompletableFuture scheduleChunkLoad(ChunkPos chunkPos) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            CompoundTag var2 = this.readChunk(chunkPos);
            if(var2 != null) {
               boolean var3 = var2.contains("Level", 10) && var2.getCompound("Level").contains("Status", 8);
               if(var3) {
                  ChunkAccess var4 = ChunkSerializer.read(this.level, this.structureManager, this.poiManager, chunkPos, var2);
                  var4.setLastSaveTime(this.level.getGameTime());
                  return Either.left(var4);
               }

               LOGGER.error("Chunk file at {} is missing level data, skipping", chunkPos);
            }
         } catch (ReportedException var5) {
            Throwable var3 = var5.getCause();
            if(!(var3 instanceof IOException)) {
               throw var5;
            }

            LOGGER.error("Couldn\'t load chunk {}", chunkPos, var3);
         } catch (Exception var6) {
            LOGGER.error("Couldn\'t load chunk {}", chunkPos, var6);
         }

         return Either.left(new ProtoChunk(chunkPos, UpgradeData.EMPTY));
      }, this.mainThreadExecutor);
   }

   private CompletableFuture scheduleChunkGeneration(ChunkHolder chunkHolder, ChunkStatus chunkStatus) {
      ChunkPos var3 = chunkHolder.getPos();
      CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> var4 = this.getChunkRangeFuture(var3, chunkStatus.getRange(), (var2) -> {
         return this.getDependencyStatus(chunkStatus, var2);
      });
      return var4.thenComposeAsync((either) -> {
         return (CompletableFuture)either.map((list) -> {
            try {
               CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completableFuture = chunkStatus.generate(this.level, this.generator, this.structureManager, this.lightEngine, (chunkAccess) -> {
                  return this.protoChunkToFullChunk(chunkHolder);
               }, list);
               this.progressListener.onStatusChange(var3, chunkStatus);
               return completableFuture;
            } catch (Exception var8) {
               CrashReport var6 = CrashReport.forThrowable(var8, "Exception generating new chunk");
               CrashReportCategory var7 = var6.addCategory("Chunk to be generated");
               var7.setDetail("Location", (Object)String.format("%d,%d", new Object[]{Integer.valueOf(var3.x), Integer.valueOf(var3.z)}));
               var7.setDetail("Position hash", (Object)Long.valueOf(ChunkPos.asLong(var3.x, var3.z)));
               var7.setDetail("Generator", (Object)this.generator);
               throw new ReportedException(var6);
            }
         }, (chunkHolder$ChunkLoadingFailure) -> {
            this.releaseLightTicket(var3);
            return CompletableFuture.completedFuture(Either.right(chunkHolder$ChunkLoadingFailure));
         });
      }, (runnable) -> {
         this.worldgenMailbox.tell(ChunkTaskPriorityQueueSorter.message(chunkHolder, runnable));
      });
   }

   protected void releaseLightTicket(ChunkPos chunkPos) {
      this.mainThreadExecutor.tell(Util.name(() -> {
         this.distanceManager.removeTicket(TicketType.LIGHT, chunkPos, 33 + ChunkStatus.getDistance(ChunkStatus.FEATURES), chunkPos);
      }, () -> {
         return "release light ticket " + chunkPos;
      }));
   }

   private ChunkStatus getDependencyStatus(ChunkStatus var1, int var2) {
      ChunkStatus var3;
      if(var2 == 0) {
         var3 = var1.getParent();
      } else {
         var3 = ChunkStatus.getStatus(ChunkStatus.getDistance(var1) + var2);
      }

      return var3;
   }

   private CompletableFuture protoChunkToFullChunk(ChunkHolder chunkHolder) {
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completableFuture = chunkHolder.getFutureIfPresentUnchecked(ChunkStatus.FULL.getParent());
      return completableFuture.thenApplyAsync((var2) -> {
         ChunkStatus var3 = ChunkHolder.getStatus(chunkHolder.getTicketLevel());
         return !var3.isOrAfter(ChunkStatus.FULL)?ChunkHolder.UNLOADED_CHUNK:var2.mapLeft((var2) -> {
            ChunkPos var3 = chunkHolder.getPos();
            LevelChunk var4;
            if(var2 instanceof ImposterProtoChunk) {
               var4 = ((ImposterProtoChunk)var2).getWrapped();
            } else {
               var4 = new LevelChunk(this.level, (ProtoChunk)var2);
               chunkHolder.replaceProtoChunk(new ImposterProtoChunk(var4));
            }

            var4.setFullStatus(() -> {
               return ChunkHolder.getFullChunkStatus(chunkHolder.getTicketLevel());
            });
            var4.runPostLoad();
            if(this.entitiesInLevel.add(var3.toLong())) {
               var4.setLoaded(true);
               this.level.addAllPendingBlockEntities(var4.getBlockEntities().values());
               List<Entity> var5 = null;
               ClassInstanceMultiMap[] var6 = var4.getEntitySections();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  for(Entity var11 : var6[var8]) {
                     if(!(var11 instanceof Player) && !this.level.loadFromChunk(var11)) {
                        if(var5 == null) {
                           var5 = Lists.newArrayList(new Entity[]{var11});
                        } else {
                           var5.add(var11);
                        }
                     }
                  }
               }

               if(var5 != null) {
                  var5.forEach(var4::removeEntity);
               }
            }

            return var4;
         });
      }, (runnable) -> {
         ProcessorHandle var10000 = this.mainThreadMailbox;
         long var10002 = chunkHolder.getPos().toLong();
         chunkHolder.getClass();
         var10000.tell(ChunkTaskPriorityQueueSorter.message(runnable, var10002, chunkHolder::getTicketLevel));
      });
   }

   public CompletableFuture postProcess(ChunkHolder chunkHolder) {
      ChunkPos var2 = chunkHolder.getPos();
      CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> var3 = this.getChunkRangeFuture(var2, 1, (i) -> {
         return ChunkStatus.FULL;
      });
      CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> var4 = var3.thenApplyAsync((either) -> {
         return either.flatMap((list) -> {
            LevelChunk var1 = (LevelChunk)list.get(list.size() / 2);
            var1.postProcessGeneration();
            return Either.left(var1);
         });
      }, (runnable) -> {
         this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(chunkHolder, runnable));
      });
      var4.thenAcceptAsync((either) -> {
         either.mapLeft((levelChunk) -> {
            this.tickingGenerated.getAndIncrement();
            Packet<?>[] vars3 = new Packet[2];
            this.getPlayers(var2, false).forEach((serverPlayer) -> {
               this.playerLoadedChunk(serverPlayer, vars3, levelChunk);
            });
            return Either.left(levelChunk);
         });
      }, (runnable) -> {
         this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(chunkHolder, runnable));
      });
      return var4;
   }

   public CompletableFuture unpackTicks(ChunkHolder chunkHolder) {
      return chunkHolder.getOrScheduleFuture(ChunkStatus.FULL, this).thenApplyAsync((either) -> {
         return either.mapLeft((chunkAccess) -> {
            LevelChunk levelChunk = (LevelChunk)chunkAccess;
            levelChunk.unpackTicks();
            return levelChunk;
         });
      }, (runnable) -> {
         this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(chunkHolder, runnable));
      });
   }

   public int getTickingGenerated() {
      return this.tickingGenerated.get();
   }

   private boolean save(ChunkAccess chunkAccess) {
      this.poiManager.flush(chunkAccess.getPos());
      if(!chunkAccess.isUnsaved()) {
         return false;
      } else {
         try {
            this.level.checkSession();
         } catch (LevelConflictException var6) {
            LOGGER.error("Couldn\'t save chunk; already in use by another instance of Minecraft?", var6);
            return false;
         }

         chunkAccess.setLastSaveTime(this.level.getGameTime());
         chunkAccess.setUnsaved(false);
         ChunkPos var2 = chunkAccess.getPos();

         try {
            ChunkStatus var3 = chunkAccess.getStatus();
            if(var3.getChunkType() != ChunkStatus.ChunkType.LEVELCHUNK) {
               CompoundTag var4 = this.readChunk(var2);
               if(var4 != null && ChunkSerializer.getChunkTypeFromTag(var4) == ChunkStatus.ChunkType.LEVELCHUNK) {
                  return false;
               }

               if(var3 == ChunkStatus.EMPTY && chunkAccess.getAllStarts().values().stream().noneMatch(StructureStart::isValid)) {
                  return false;
               }
            }

            CompoundTag var4 = ChunkSerializer.write(this.level, chunkAccess);
            this.write(var2, var4);
            return true;
         } catch (Exception var5) {
            LOGGER.error("Failed to save chunk {},{}", Integer.valueOf(var2.x), Integer.valueOf(var2.z), var5);
            return false;
         }
      }
   }

   protected void setViewDistance(int viewDistance) {
      int var2 = Mth.clamp(viewDistance + 1, 3, 33);
      if(var2 != this.viewDistance) {
         int var3 = this.viewDistance;
         this.viewDistance = var2;
         this.distanceManager.updatePlayerTickets(this.viewDistance);
         ObjectIterator var4 = this.updatingChunkMap.values().iterator();

         while(var4.hasNext()) {
            ChunkHolder var5 = (ChunkHolder)var4.next();
            ChunkPos var6 = var5.getPos();
            Packet<?>[] vars7 = new Packet[2];
            this.getPlayers(var6, false).forEach((serverPlayer) -> {
               int var5 = checkerboardDistance(var6, serverPlayer, true);
               boolean var6 = var5 <= var3;
               boolean var7 = var5 <= this.viewDistance;
               this.updateChunkTracking(serverPlayer, var6, vars7, var6, var7);
            });
         }
      }

   }

   protected void updateChunkTracking(ServerPlayer serverPlayer, ChunkPos chunkPos, Packet[] packets, boolean var4, boolean var5) {
      if(serverPlayer.level == this.level) {
         if(var5 && !var4) {
            ChunkHolder var6 = this.getVisibleChunkIfPresent(chunkPos.toLong());
            if(var6 != null) {
               LevelChunk var7 = var6.getTickingChunk();
               if(var7 != null) {
                  this.playerLoadedChunk(serverPlayer, packets, var7);
               }

               DebugPackets.sendPoiPacketsForChunk(this.level, chunkPos);
            }
         }

         if(!var5 && var4) {
            serverPlayer.untrackChunk(chunkPos);
         }

      }
   }

   public int size() {
      return this.visibleChunkMap.size();
   }

   protected ChunkMap.DistanceManager getDistanceManager() {
      return this.distanceManager;
   }

   protected Iterable getChunks() {
      return Iterables.unmodifiableIterable(this.visibleChunkMap.values());
   }

   void dumpChunks(Writer writer) throws IOException {
      CsvOutput var2 = CsvOutput.builder().addColumn("x").addColumn("z").addColumn("level").addColumn("in_memory").addColumn("status").addColumn("full_status").addColumn("accessible_ready").addColumn("ticking_ready").addColumn("entity_ticking_ready").addColumn("ticket").addColumn("spawning").addColumn("entity_count").addColumn("block_entity_count").build(writer);
      ObjectBidirectionalIterator var3 = this.visibleChunkMap.long2ObjectEntrySet().iterator();

      while(var3.hasNext()) {
         Entry<ChunkHolder> var4 = (Entry)var3.next();
         ChunkPos var5 = new ChunkPos(var4.getLongKey());
         ChunkHolder var6 = (ChunkHolder)var4.getValue();
         Optional<ChunkAccess> var7 = Optional.ofNullable(var6.getLastAvailable());
         Optional<LevelChunk> var8 = var7.flatMap((chunkAccess) -> {
            return chunkAccess instanceof LevelChunk?Optional.of((LevelChunk)chunkAccess):Optional.empty();
         });
         var2.writeRow(new Object[]{Integer.valueOf(var5.x), Integer.valueOf(var5.z), Integer.valueOf(var6.getTicketLevel()), Boolean.valueOf(var7.isPresent()), var7.map(ChunkAccess::getStatus).orElse((Object)null), var8.map(LevelChunk::getFullStatus).orElse((Object)null), printFuture(var6.getFullChunkFuture()), printFuture(var6.getTickingChunkFuture()), printFuture(var6.getEntityTickingChunkFuture()), this.distanceManager.getTicketDebugString(var4.getLongKey()), Boolean.valueOf(!this.noPlayersCloseForSpawning(var5)), var8.map((levelChunk) -> {
            return Integer.valueOf(Stream.of(levelChunk.getEntitySections()).mapToInt(ClassInstanceMultiMap::size).sum());
         }).orElse(Integer.valueOf(0)), var8.map((levelChunk) -> {
            return Integer.valueOf(levelChunk.getBlockEntities().size());
         }).orElse(Integer.valueOf(0))});
      }

   }

   private static String printFuture(CompletableFuture completableFuture) {
      try {
         Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> var1 = (Either)completableFuture.getNow((Object)null);
         return var1 != null?(String)var1.map((levelChunk) -> {
            return "done";
         }, (chunkHolder$ChunkLoadingFailure) -> {
            return "unloaded";
         }):"not completed";
      } catch (CompletionException var2) {
         return "failed " + var2.getCause().getMessage();
      } catch (CancellationException var3) {
         return "cancelled";
      }
   }

   @Nullable
   private CompoundTag readChunk(ChunkPos chunkPos) throws IOException {
      CompoundTag compoundTag = this.read(chunkPos);
      return compoundTag == null?null:this.upgradeChunkTag(this.level.getDimension().getType(), this.overworldDataStorage, compoundTag);
   }

   boolean noPlayersCloseForSpawning(ChunkPos chunkPos) {
      long var2 = chunkPos.toLong();
      return !this.distanceManager.hasPlayersNearby(var2)?true:this.playerMap.getPlayers(var2).noneMatch((serverPlayer) -> {
         return !serverPlayer.isSpectator() && euclideanDistanceSquared(chunkPos, serverPlayer) < 16384.0D;
      });
   }

   private boolean skipPlayer(ServerPlayer serverPlayer) {
      return serverPlayer.isSpectator() && !this.level.getGameRules().getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS);
   }

   void updatePlayerStatus(ServerPlayer serverPlayer, boolean var2) {
      boolean var3 = this.skipPlayer(serverPlayer);
      boolean var4 = this.playerMap.ignoredOrUnknown(serverPlayer);
      int var5 = Mth.floor(serverPlayer.x) >> 4;
      int var6 = Mth.floor(serverPlayer.z) >> 4;
      if(var2) {
         this.playerMap.addPlayer(ChunkPos.asLong(var5, var6), serverPlayer, var3);
         this.updatePlayerPos(serverPlayer);
         if(!var3) {
            this.distanceManager.addPlayer(SectionPos.of((Entity)serverPlayer), serverPlayer);
         }
      } else {
         SectionPos var7 = serverPlayer.getLastSectionPos();
         this.playerMap.removePlayer(var7.chunk().toLong(), serverPlayer);
         if(!var4) {
            this.distanceManager.removePlayer(var7, serverPlayer);
         }
      }

      for(int var7 = var5 - this.viewDistance; var7 <= var5 + this.viewDistance; ++var7) {
         for(int var8 = var6 - this.viewDistance; var8 <= var6 + this.viewDistance; ++var8) {
            ChunkPos var9 = new ChunkPos(var7, var8);
            this.updateChunkTracking(serverPlayer, var9, new Packet[2], !var2, var2);
         }
      }

   }

   private SectionPos updatePlayerPos(ServerPlayer serverPlayer) {
      SectionPos sectionPos = SectionPos.of((Entity)serverPlayer);
      serverPlayer.setLastSectionPos(sectionPos);
      serverPlayer.connection.send(new ClientboundSetChunkCacheCenterPacket(sectionPos.x(), sectionPos.z()));
      return sectionPos;
   }

   public void move(ServerPlayer serverPlayer) {
      ObjectIterator var2 = this.entityMap.values().iterator();

      while(var2.hasNext()) {
         ChunkMap.TrackedEntity var3 = (ChunkMap.TrackedEntity)var2.next();
         if(var3.entity == serverPlayer) {
            var3.updatePlayers(this.level.players());
         } else {
            var3.updatePlayer(serverPlayer);
         }
      }

      int var2 = Mth.floor(serverPlayer.x) >> 4;
      int var3 = Mth.floor(serverPlayer.z) >> 4;
      SectionPos var4 = serverPlayer.getLastSectionPos();
      SectionPos var5 = SectionPos.of((Entity)serverPlayer);
      long var6 = var4.chunk().toLong();
      long var8 = var5.chunk().toLong();
      boolean var10 = this.playerMap.ignored(serverPlayer);
      boolean var11 = this.skipPlayer(serverPlayer);
      boolean var12 = var4.asLong() != var5.asLong();
      if(var12 || var10 != var11) {
         this.updatePlayerPos(serverPlayer);
         if(!var10) {
            this.distanceManager.removePlayer(var4, serverPlayer);
         }

         if(!var11) {
            this.distanceManager.addPlayer(var5, serverPlayer);
         }

         if(!var10 && var11) {
            this.playerMap.ignorePlayer(serverPlayer);
         }

         if(var10 && !var11) {
            this.playerMap.unIgnorePlayer(serverPlayer);
         }

         if(var6 != var8) {
            this.playerMap.updatePlayer(var6, var8, serverPlayer);
         }
      }

      int var13 = var4.x();
      int var14 = var4.z();
      if(Math.abs(var13 - var2) <= this.viewDistance * 2 && Math.abs(var14 - var3) <= this.viewDistance * 2) {
         int var15 = Math.min(var2, var13) - this.viewDistance;
         int var16 = Math.min(var3, var14) - this.viewDistance;
         int var17 = Math.max(var2, var13) + this.viewDistance;
         int var18 = Math.max(var3, var14) + this.viewDistance;

         for(int var19 = var15; var19 <= var17; ++var19) {
            for(int var20 = var16; var20 <= var18; ++var20) {
               ChunkPos var21 = new ChunkPos(var19, var20);
               boolean var22 = checkerboardDistance(var21, var13, var14) <= this.viewDistance;
               boolean var23 = checkerboardDistance(var21, var2, var3) <= this.viewDistance;
               this.updateChunkTracking(serverPlayer, var21, new Packet[2], var22, var23);
            }
         }
      } else {
         for(int var15 = var13 - this.viewDistance; var15 <= var13 + this.viewDistance; ++var15) {
            for(int var16 = var14 - this.viewDistance; var16 <= var14 + this.viewDistance; ++var16) {
               ChunkPos var17 = new ChunkPos(var15, var16);
               boolean var18 = true;
               boolean var19 = false;
               this.updateChunkTracking(serverPlayer, var17, new Packet[2], true, false);
            }
         }

         for(int var15 = var2 - this.viewDistance; var15 <= var2 + this.viewDistance; ++var15) {
            for(int var16 = var3 - this.viewDistance; var16 <= var3 + this.viewDistance; ++var16) {
               ChunkPos var17 = new ChunkPos(var15, var16);
               boolean var18 = false;
               boolean var19 = true;
               this.updateChunkTracking(serverPlayer, var17, new Packet[2], false, true);
            }
         }
      }

   }

   public Stream getPlayers(ChunkPos chunkPos, boolean var2) {
      return this.playerMap.getPlayers(chunkPos.toLong()).filter((serverPlayer) -> {
         int var4 = checkerboardDistance(chunkPos, serverPlayer, true);
         return var4 > this.viewDistance?false:!var2 || var4 == this.viewDistance;
      });
   }

   protected void addEntity(Entity entity) {
      if(!(entity instanceof EnderDragonPart)) {
         if(!(entity instanceof LightningBolt)) {
            EntityType<?> var2 = entity.getType();
            int var3 = var2.chunkRange() * 16;
            int var4 = var2.updateInterval();
            if(this.entityMap.containsKey(entity.getId())) {
               throw new IllegalStateException("Entity is already tracked!");
            } else {
               ChunkMap.TrackedEntity var5 = new ChunkMap.TrackedEntity(entity, var3, var4, var2.trackDeltas());
               this.entityMap.put(entity.getId(), var5);
               var5.updatePlayers(this.level.players());
               if(entity instanceof ServerPlayer) {
                  ServerPlayer var6 = (ServerPlayer)entity;
                  this.updatePlayerStatus(var6, true);
                  ObjectIterator var7 = this.entityMap.values().iterator();

                  while(var7.hasNext()) {
                     ChunkMap.TrackedEntity var8 = (ChunkMap.TrackedEntity)var7.next();
                     if(var8.entity != var6) {
                        var8.updatePlayer(var6);
                     }
                  }
               }

            }
         }
      }
   }

   protected void removeEntity(Entity entity) {
      if(entity instanceof ServerPlayer) {
         ServerPlayer var2 = (ServerPlayer)entity;
         this.updatePlayerStatus(var2, false);
         ObjectIterator var3 = this.entityMap.values().iterator();

         while(var3.hasNext()) {
            ChunkMap.TrackedEntity var4 = (ChunkMap.TrackedEntity)var3.next();
            var4.removePlayer(var2);
         }
      }

      ChunkMap.TrackedEntity var2 = (ChunkMap.TrackedEntity)this.entityMap.remove(entity.getId());
      if(var2 != null) {
         var2.broadcastRemoved();
      }

   }

   protected void tick() {
      List<ServerPlayer> var1 = Lists.newArrayList();
      List<ServerPlayer> var2 = this.level.players();

      ChunkMap.TrackedEntity var4;
      for(ObjectIterator var3 = this.entityMap.values().iterator(); var3.hasNext(); var4.serverEntity.sendChanges()) {
         var4 = (ChunkMap.TrackedEntity)var3.next();
         SectionPos var5 = var4.lastSectionPos;
         SectionPos var6 = SectionPos.of(var4.entity);
         if(!Objects.equals(var5, var6)) {
            var4.updatePlayers(var2);
            Entity var7 = var4.entity;
            if(var7 instanceof ServerPlayer) {
               var1.add((ServerPlayer)var7);
            }

            var4.lastSectionPos = var6;
         }
      }

      ObjectIterator var8 = this.entityMap.values().iterator();

      while(var8.hasNext()) {
         var4 = (ChunkMap.TrackedEntity)var8.next();
         var4.updatePlayers(var1);
      }

   }

   protected void broadcast(Entity entity, Packet packet) {
      ChunkMap.TrackedEntity var3 = (ChunkMap.TrackedEntity)this.entityMap.get(entity.getId());
      if(var3 != null) {
         var3.broadcast(packet);
      }

   }

   protected void broadcastAndSend(Entity entity, Packet packet) {
      ChunkMap.TrackedEntity var3 = (ChunkMap.TrackedEntity)this.entityMap.get(entity.getId());
      if(var3 != null) {
         var3.broadcastAndSend(packet);
      }

   }

   private void playerLoadedChunk(ServerPlayer serverPlayer, Packet[] packets, LevelChunk levelChunk) {
      if(packets[0] == null) {
         packets[0] = new ClientboundLevelChunkPacket(levelChunk, '\uffff');
         packets[1] = new ClientboundLightUpdatePacket(levelChunk.getPos(), this.lightEngine);
      }

      serverPlayer.trackChunk(levelChunk.getPos(), packets[0], packets[1]);
      DebugPackets.sendPoiPacketsForChunk(this.level, levelChunk.getPos());
      List<Entity> var4 = Lists.newArrayList();
      List<Entity> var5 = Lists.newArrayList();
      ObjectIterator var6 = this.entityMap.values().iterator();

      while(var6.hasNext()) {
         ChunkMap.TrackedEntity var7 = (ChunkMap.TrackedEntity)var6.next();
         Entity var8 = var7.entity;
         if(var8 != serverPlayer && var8.xChunk == levelChunk.getPos().x && var8.zChunk == levelChunk.getPos().z) {
            var7.updatePlayer(serverPlayer);
            if(var8 instanceof Mob && ((Mob)var8).getLeashHolder() != null) {
               var4.add(var8);
            }

            if(!var8.getPassengers().isEmpty()) {
               var5.add(var8);
            }
         }
      }

      if(!var4.isEmpty()) {
         for(Entity var7 : var4) {
            serverPlayer.connection.send(new ClientboundSetEntityLinkPacket(var7, ((Mob)var7).getLeashHolder()));
         }
      }

      if(!var5.isEmpty()) {
         for(Entity var7 : var5) {
            serverPlayer.connection.send(new ClientboundSetPassengersPacket(var7));
         }
      }

   }

   protected PoiManager getPoiManager() {
      return this.poiManager;
   }

   public CompletableFuture packTicks(LevelChunk levelChunk) {
      return this.mainThreadExecutor.submit(() -> {
         levelChunk.packTicks(this.level);
      });
   }

   // $FF: synthetic method
   static ChunkHolder access$400(ChunkMap chunkMap, long var1, int var3, ChunkHolder var4, int var5) {
      return chunkMap.updateChunkScheduling(var1, var3, var4, var5);
   }

   class DistanceManager extends DistanceManager {
      protected DistanceManager(Executor var2, Executor var3) {
         super(var2, var3);
      }

      protected boolean isChunkToRemove(long l) {
         return ChunkMap.this.toDrop.contains(l);
      }

      @Nullable
      protected ChunkHolder getChunk(long l) {
         return ChunkMap.this.getUpdatingChunkIfPresent(l);
      }

      @Nullable
      protected ChunkHolder updateChunkScheduling(long var1, int var3, @Nullable ChunkHolder var4, int var5) {
         return ChunkMap.access$400(ChunkMap.this, var1, var3, var4, var5);
      }
   }

   class TrackedEntity {
      private final ServerEntity serverEntity;
      private final Entity entity;
      private final int range;
      private SectionPos lastSectionPos;
      private final Set seenBy = Sets.newHashSet();

      public TrackedEntity(Entity entity, int range, int var4, boolean var5) {
         this.serverEntity = new ServerEntity(ChunkMap.this.level, entity, var4, var5, this::broadcast);
         this.entity = entity;
         this.range = range;
         this.lastSectionPos = SectionPos.of(entity);
      }

      public boolean equals(Object object) {
         return object instanceof ChunkMap.TrackedEntity?((ChunkMap.TrackedEntity)object).entity.getId() == this.entity.getId():false;
      }

      public int hashCode() {
         return this.entity.getId();
      }

      public void broadcast(Packet packet) {
         for(ServerPlayer var3 : this.seenBy) {
            var3.connection.send(packet);
         }

      }

      public void broadcastAndSend(Packet packet) {
         this.broadcast(packet);
         if(this.entity instanceof ServerPlayer) {
            ((ServerPlayer)this.entity).connection.send(packet);
         }

      }

      public void broadcastRemoved() {
         for(ServerPlayer var2 : this.seenBy) {
            this.serverEntity.removePairing(var2);
         }

      }

      public void removePlayer(ServerPlayer serverPlayer) {
         if(this.seenBy.remove(serverPlayer)) {
            this.serverEntity.removePairing(serverPlayer);
         }

      }

      public void updatePlayer(ServerPlayer serverPlayer) {
         if(serverPlayer != this.entity) {
            Vec3 var2 = (new Vec3(serverPlayer.x, serverPlayer.y, serverPlayer.z)).subtract(this.serverEntity.sentPos());
            int var3 = Math.min(this.range, (ChunkMap.this.viewDistance - 1) * 16);
            boolean var4 = var2.x >= (double)(-var3) && var2.x <= (double)var3 && var2.z >= (double)(-var3) && var2.z <= (double)var3 && this.entity.broadcastToPlayer(serverPlayer);
            if(var4) {
               boolean var5 = this.entity.forcedLoading;
               if(!var5) {
                  ChunkPos var6 = new ChunkPos(this.entity.xChunk, this.entity.zChunk);
                  ChunkHolder var7 = ChunkMap.this.getVisibleChunkIfPresent(var6.toLong());
                  if(var7 != null && var7.getTickingChunk() != null) {
                     var5 = ChunkMap.checkerboardDistance(var6, serverPlayer, false) <= ChunkMap.this.viewDistance;
                  }
               }

               if(var5 && this.seenBy.add(serverPlayer)) {
                  this.serverEntity.addPairing(serverPlayer);
               }
            } else if(this.seenBy.remove(serverPlayer)) {
               this.serverEntity.removePairing(serverPlayer);
            }

         }
      }

      public void updatePlayers(List list) {
         for(ServerPlayer var3 : list) {
            this.updatePlayer(var3);
         }

      }
   }
}
