package net.minecraft.server.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.server.level.ChunkTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DistanceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getDistance(ChunkStatus.FULL) - 2;
   private final Long2ObjectMap playersPerChunk = new Long2ObjectOpenHashMap();
   private final Long2ObjectOpenHashMap tickets = new Long2ObjectOpenHashMap();
   private final DistanceManager.ChunkTicketTracker ticketTracker = new DistanceManager.ChunkTicketTracker();
   private final DistanceManager.FixedPlayerDistanceChunkTracker naturalSpawnChunkCounter = new DistanceManager.FixedPlayerDistanceChunkTracker(8);
   private final DistanceManager.PlayerTicketTracker playerTicketManager = new DistanceManager.PlayerTicketTracker(33);
   private final Set chunksToUpdateFutures = Sets.newHashSet();
   private final ChunkTaskPriorityQueueSorter ticketThrottler;
   private final ProcessorHandle ticketThrottlerInput;
   private final ProcessorHandle ticketThrottlerReleaser;
   private final LongSet ticketsToRelease = new LongOpenHashSet();
   private final Executor mainThreadExecutor;
   private long ticketTickCounter;

   protected DistanceManager(Executor var1, Executor mainThreadExecutor) {
      mainThreadExecutor.getClass();
      ProcessorHandle<Runnable> var3 = ProcessorHandle.of("player ticket throttler", mainThreadExecutor::execute);
      ChunkTaskPriorityQueueSorter var4 = new ChunkTaskPriorityQueueSorter(ImmutableList.of(var3), var1, 4);
      this.ticketThrottler = var4;
      this.ticketThrottlerInput = var4.getProcessor(var3, true);
      this.ticketThrottlerReleaser = var4.getReleaseProcessor(var3);
      this.mainThreadExecutor = mainThreadExecutor;
   }

   protected void purgeStaleTickets() {
      ++this.ticketTickCounter;
      ObjectIterator<Entry<ObjectSortedSet<Ticket<?>>>> var1 = this.tickets.long2ObjectEntrySet().fastIterator();

      while(var1.hasNext()) {
         Entry<ObjectSortedSet<Ticket<?>>> var2 = (Entry)var1.next();
         if(((ObjectSortedSet)var2.getValue()).removeIf((ticket) -> {
            return ticket.timedOut(this.ticketTickCounter);
         })) {
            this.ticketTracker.update(var2.getLongKey(), this.getTicketLevelAt((ObjectSortedSet)var2.getValue()), false);
         }

         if(((ObjectSortedSet)var2.getValue()).isEmpty()) {
            var1.remove();
         }
      }

   }

   private int getTicketLevelAt(ObjectSortedSet objectSortedSet) {
      ObjectBidirectionalIterator<Ticket<?>> var2 = objectSortedSet.iterator();
      return var2.hasNext()?((Ticket)var2.next()).getTicketLevel():ChunkMap.MAX_CHUNK_DISTANCE + 1;
   }

   protected abstract boolean isChunkToRemove(long var1);

   @Nullable
   protected abstract ChunkHolder getChunk(long var1);

   @Nullable
   protected abstract ChunkHolder updateChunkScheduling(long var1, int var3, @Nullable ChunkHolder var4, int var5);

   public boolean runAllUpdates(ChunkMap chunkMap) {
      this.naturalSpawnChunkCounter.runAllUpdates();
      this.playerTicketManager.runAllUpdates();
      int var2 = Integer.MAX_VALUE - this.ticketTracker.runDistnaceUpdates(Integer.MAX_VALUE);
      boolean var3 = var2 != 0;
      if(var3) {
         ;
      }

      if(!this.chunksToUpdateFutures.isEmpty()) {
         this.chunksToUpdateFutures.forEach((chunkHolder) -> {
            chunkHolder.updateFutures(chunkMap);
         });
         this.chunksToUpdateFutures.clear();
         return true;
      } else {
         if(!this.ticketsToRelease.isEmpty()) {
            LongIterator var4 = this.ticketsToRelease.iterator();

            while(var4.hasNext()) {
               long var5 = var4.nextLong();
               if(this.getTickets(var5).stream().anyMatch((ticket) -> {
                  return ticket.getType() == TicketType.PLAYER;
               })) {
                  ChunkHolder var7 = chunkMap.getUpdatingChunkIfPresent(var5);
                  if(var7 == null) {
                     throw new IllegalStateException();
                  }

                  CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> var8 = var7.getEntityTickingChunkFuture();
                  var8.thenAccept((either) -> {
                     this.mainThreadExecutor.execute(() -> {
                        this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                        }, var5, false));
                     });
                  });
               }
            }

            this.ticketsToRelease.clear();
         }

         return var3;
      }
   }

   private void addTicket(long var1, Ticket ticket) {
      ObjectSortedSet<Ticket<?>> var4 = this.getTickets(var1);
      ObjectBidirectionalIterator<Ticket<?>> var6 = var4.iterator();
      int var5;
      if(var6.hasNext()) {
         var5 = ((Ticket)var6.next()).getTicketLevel();
      } else {
         var5 = ChunkMap.MAX_CHUNK_DISTANCE + 1;
      }

      if(var4.add(ticket)) {
         ;
      }

      if(ticket.getTicketLevel() < var5) {
         this.ticketTracker.update(var1, ticket.getTicketLevel(), true);
      }

   }

   private void removeTicket(long var1, Ticket ticket) {
      ObjectSortedSet<Ticket<?>> var4 = this.getTickets(var1);
      if(var4.remove(ticket)) {
         ;
      }

      if(var4.isEmpty()) {
         this.tickets.remove(var1);
      }

      this.ticketTracker.update(var1, this.getTicketLevelAt(var4), false);
   }

   public void addTicket(TicketType ticketType, ChunkPos chunkPos, int var3, Object object) {
      this.addTicket(chunkPos.toLong(), new Ticket(ticketType, var3, object, this.ticketTickCounter));
   }

   public void removeTicket(TicketType ticketType, ChunkPos chunkPos, int var3, Object object) {
      Ticket<T> var5 = new Ticket(ticketType, var3, object, this.ticketTickCounter);
      this.removeTicket(chunkPos.toLong(), var5);
   }

   public void addRegionTicket(TicketType ticketType, ChunkPos chunkPos, int var3, Object object) {
      this.addTicket(chunkPos.toLong(), new Ticket(ticketType, 33 - var3, object, this.ticketTickCounter));
   }

   public void removeRegionTicket(TicketType ticketType, ChunkPos chunkPos, int var3, Object object) {
      Ticket<T> var5 = new Ticket(ticketType, 33 - var3, object, this.ticketTickCounter);
      this.removeTicket(chunkPos.toLong(), var5);
   }

   private ObjectSortedSet getTickets(long l) {
      return (ObjectSortedSet)this.tickets.computeIfAbsent(l, (l) -> {
         return new ObjectAVLTreeSet();
      });
   }

   protected void updateChunkForced(ChunkPos chunkPos, boolean var2) {
      Ticket<ChunkPos> var3 = new Ticket(TicketType.FORCED, 31, chunkPos, this.ticketTickCounter);
      if(var2) {
         this.addTicket(chunkPos.toLong(), var3);
      } else {
         this.removeTicket(chunkPos.toLong(), var3);
      }

   }

   public void addPlayer(SectionPos sectionPos, ServerPlayer serverPlayer) {
      long var3 = sectionPos.chunk().toLong();
      ((ObjectSet)this.playersPerChunk.computeIfAbsent(var3, (l) -> {
         return new ObjectOpenHashSet();
      })).add(serverPlayer);
      this.naturalSpawnChunkCounter.update(var3, 0, true);
      this.playerTicketManager.update(var3, 0, true);
   }

   public void removePlayer(SectionPos sectionPos, ServerPlayer serverPlayer) {
      long var3 = sectionPos.chunk().toLong();
      ObjectSet<ServerPlayer> var5 = (ObjectSet)this.playersPerChunk.get(var3);
      var5.remove(serverPlayer);
      if(var5.isEmpty()) {
         this.playersPerChunk.remove(var3);
         this.naturalSpawnChunkCounter.update(var3, Integer.MAX_VALUE, false);
         this.playerTicketManager.update(var3, Integer.MAX_VALUE, false);
      }

   }

   protected String getTicketDebugString(long l) {
      ObjectSortedSet<Ticket<?>> var3 = (ObjectSortedSet)this.tickets.get(l);
      String var4;
      if(var3 != null && !var3.isEmpty()) {
         var4 = ((Ticket)var3.first()).toString();
      } else {
         var4 = "no_ticket";
      }

      return var4;
   }

   protected void updatePlayerTickets(int i) {
      this.playerTicketManager.updateViewDistance(i);
   }

   public int getNaturalSpawnChunkCount() {
      this.naturalSpawnChunkCounter.runAllUpdates();
      return this.naturalSpawnChunkCounter.chunks.size();
   }

   public boolean hasPlayersNearby(long l) {
      this.naturalSpawnChunkCounter.runAllUpdates();
      return this.naturalSpawnChunkCounter.chunks.containsKey(l);
   }

   public String getDebugStatus() {
      return this.ticketThrottler.getDebugStatus();
   }

   // $FF: synthetic method
   static void access$800(DistanceManager distanceManager, long var1, Ticket ticket) {
      distanceManager.removeTicket(var1, ticket);
   }

   // $FF: synthetic method
   static void access$900(DistanceManager distanceManager, long var1, Ticket ticket) {
      distanceManager.addTicket(var1, ticket);
   }

   class ChunkTicketTracker extends ChunkTracker {
      public ChunkTicketTracker() {
         super(ChunkMap.MAX_CHUNK_DISTANCE + 2, 16, 256);
      }

      protected int getLevelFromSource(long l) {
         ObjectSortedSet<Ticket<?>> var3 = (ObjectSortedSet)DistanceManager.this.tickets.get(l);
         if(var3 == null) {
            return Integer.MAX_VALUE;
         } else {
            ObjectBidirectionalIterator<Ticket<?>> var4 = var3.iterator();
            return !var4.hasNext()?Integer.MAX_VALUE:((Ticket)var4.next()).getTicketLevel();
         }
      }

      protected int getLevel(long l) {
         if(!DistanceManager.this.isChunkToRemove(l)) {
            ChunkHolder var3 = DistanceManager.this.getChunk(l);
            if(var3 != null) {
               return var3.getTicketLevel();
            }
         }

         return ChunkMap.MAX_CHUNK_DISTANCE + 1;
      }

      protected void setLevel(long var1, int var3) {
         ChunkHolder var4 = DistanceManager.this.getChunk(var1);
         int var5 = var4 == null?ChunkMap.MAX_CHUNK_DISTANCE + 1:var4.getTicketLevel();
         if(var5 != var3) {
            var4 = DistanceManager.this.updateChunkScheduling(var1, var3, var4, var5);
            if(var4 != null) {
               DistanceManager.this.chunksToUpdateFutures.add(var4);
            }

         }
      }

      public int runDistnaceUpdates(int i) {
         return this.runUpdates(i);
      }
   }

   class FixedPlayerDistanceChunkTracker extends ChunkTracker {
      protected final Long2ByteMap chunks = new Long2ByteOpenHashMap();
      protected final int maxDistance;

      protected FixedPlayerDistanceChunkTracker(int maxDistance) {
         super(maxDistance + 2, 16, 256);
         this.maxDistance = maxDistance;
         this.chunks.defaultReturnValue((byte)(maxDistance + 2));
      }

      protected int getLevel(long l) {
         return this.chunks.get(l);
      }

      protected void setLevel(long var1, int var3) {
         byte var4;
         if(var3 > this.maxDistance) {
            var4 = this.chunks.remove(var1);
         } else {
            var4 = this.chunks.put(var1, (byte)var3);
         }

         this.onLevelChange(var1, var4, var3);
      }

      protected void onLevelChange(long var1, int var3, int var4) {
      }

      protected int getLevelFromSource(long l) {
         return this.havePlayer(l)?0:Integer.MAX_VALUE;
      }

      private boolean havePlayer(long l) {
         ObjectSet<ServerPlayer> var3 = (ObjectSet)DistanceManager.this.playersPerChunk.get(l);
         return var3 != null && !var3.isEmpty();
      }

      public void runAllUpdates() {
         this.runUpdates(Integer.MAX_VALUE);
      }
   }

   class PlayerTicketTracker extends DistanceManager.FixedPlayerDistanceChunkTracker {
      private int viewDistance = 0;
      private final Long2IntMap queueLevels = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
      private final LongSet toUpdate = new LongOpenHashSet();

      protected PlayerTicketTracker(int var2) {
         super(var2);
         this.queueLevels.defaultReturnValue(var2 + 2);
      }

      protected void onLevelChange(long var1, int var3, int var4) {
         this.toUpdate.add(var1);
      }

      public void updateViewDistance(int viewDistance) {
         ObjectIterator var2 = this.chunks.long2ByteEntrySet().iterator();

         while(var2.hasNext()) {
            it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry var3 = (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry)var2.next();
            byte var4 = var3.getByteValue();
            long var5 = var3.getLongKey();
            this.onLevelChange(var5, var4, this.haveTicketFor(var4), var4 <= viewDistance - 2);
         }

         this.viewDistance = viewDistance;
      }

      private void onLevelChange(long var1, int var3, boolean var4, boolean var5) {
         if(var4 != var5) {
            Ticket<?> var6 = new Ticket(TicketType.PLAYER, DistanceManager.PLAYER_TICKET_LEVEL, new ChunkPos(var1), DistanceManager.this.ticketTickCounter);
            if(var5) {
               DistanceManager.this.ticketThrottlerInput.tell(ChunkTaskPriorityQueueSorter.message(() -> {
                  DistanceManager.this.mainThreadExecutor.execute(() -> {
                     if(this.haveTicketFor(this.getLevel(var1))) {
                        DistanceManager.access$900(DistanceManager.this, var1, var6);
                        DistanceManager.this.ticketsToRelease.add(var1);
                     } else {
                        DistanceManager.this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                        }, var1, false));
                     }

                  });
               }, var1, () -> {
                  return var3;
               }));
            } else {
               DistanceManager.this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                  DistanceManager.this.mainThreadExecutor.execute(() -> {
                     DistanceManager.access$800(DistanceManager.this, var1, var6);
                  });
               }, var1, true));
            }
         }

      }

      public void runAllUpdates() {
         super.runAllUpdates();
         if(!this.toUpdate.isEmpty()) {
            LongIterator var1 = this.toUpdate.iterator();

            while(var1.hasNext()) {
               long var2 = var1.nextLong();
               int var4 = this.queueLevels.get(var2);
               int var5 = this.getLevel(var2);
               if(var4 != var5) {
                  DistanceManager.this.ticketThrottler.onLevelChange(new ChunkPos(var2), () -> {
                     return this.queueLevels.get(var2);
                  }, var5, (var3) -> {
                     if(var3 >= this.queueLevels.defaultReturnValue()) {
                        this.queueLevels.remove(var2);
                     } else {
                        this.queueLevels.put(var2, var3);
                     }

                  });
                  this.onLevelChange(var2, var5, this.haveTicketFor(var4), this.haveTicketFor(var5));
               }
            }

            this.toUpdate.clear();
         }

      }

      private boolean haveTicketFor(int i) {
         return i <= this.viewDistance - 2;
      }
   }
}
