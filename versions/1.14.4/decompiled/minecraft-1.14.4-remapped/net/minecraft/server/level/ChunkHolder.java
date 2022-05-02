package net.minecraft.server.level;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundChunkBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ChunkHolder {
   public static final Either UNLOADED_CHUNK = Either.right(ChunkHolder.ChunkLoadingFailure.UNLOADED);
   public static final CompletableFuture UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
   public static final Either UNLOADED_LEVEL_CHUNK = Either.right(ChunkHolder.ChunkLoadingFailure.UNLOADED);
   private static final CompletableFuture UNLOADED_LEVEL_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_LEVEL_CHUNK);
   private static final List CHUNK_STATUSES = ChunkStatus.getStatusList();
   private static final ChunkHolder.FullChunkStatus[] FULL_CHUNK_STATUSES = ChunkHolder.FullChunkStatus.values();
   private final AtomicReferenceArray futures;
   private volatile CompletableFuture fullChunkFuture;
   private volatile CompletableFuture tickingChunkFuture;
   private volatile CompletableFuture entityTickingChunkFuture;
   private CompletableFuture chunkToSave;
   private int oldTicketLevel;
   private int ticketLevel;
   private int queueLevel;
   private final ChunkPos pos;
   private final short[] changedBlocks;
   private int changes;
   private int changedSectionFilter;
   private int sectionsToForceSendLightFor;
   private int blockChangedLightSectionFilter;
   private int skyChangedLightSectionFilter;
   private final LevelLightEngine lightEngine;
   private final ChunkHolder.LevelChangeListener onLevelChange;
   private final ChunkHolder.PlayerProvider playerProvider;
   private boolean wasAccessibleSinceLastSave;

   public ChunkHolder(ChunkPos pos, int ticketLevel, LevelLightEngine lightEngine, ChunkHolder.LevelChangeListener onLevelChange, ChunkHolder.PlayerProvider playerProvider) {
      this.futures = new AtomicReferenceArray(CHUNK_STATUSES.size());
      this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      this.entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      this.chunkToSave = CompletableFuture.completedFuture((Object)null);
      this.changedBlocks = new short[64];
      this.pos = pos;
      this.lightEngine = lightEngine;
      this.onLevelChange = onLevelChange;
      this.playerProvider = playerProvider;
      this.oldTicketLevel = ChunkMap.MAX_CHUNK_DISTANCE + 1;
      this.ticketLevel = this.oldTicketLevel;
      this.queueLevel = this.oldTicketLevel;
      this.setTicketLevel(ticketLevel);
   }

   public CompletableFuture getFutureIfPresentUnchecked(ChunkStatus chunkStatus) {
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completableFuture = (CompletableFuture)this.futures.get(chunkStatus.getIndex());
      return completableFuture == null?UNLOADED_CHUNK_FUTURE:completableFuture;
   }

   public CompletableFuture getFutureIfPresent(ChunkStatus chunkStatus) {
      return getStatus(this.ticketLevel).isOrAfter(chunkStatus)?this.getFutureIfPresentUnchecked(chunkStatus):UNLOADED_CHUNK_FUTURE;
   }

   public CompletableFuture getTickingChunkFuture() {
      return this.tickingChunkFuture;
   }

   public CompletableFuture getEntityTickingChunkFuture() {
      return this.entityTickingChunkFuture;
   }

   public CompletableFuture getFullChunkFuture() {
      return this.fullChunkFuture;
   }

   @Nullable
   public LevelChunk getTickingChunk() {
      CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> var1 = this.getTickingChunkFuture();
      Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> var2 = (Either)var1.getNow((Object)null);
      return var2 == null?null:(LevelChunk)var2.left().orElse((Object)null);
   }

   @Nullable
   public ChunkStatus getLastAvailableStatus() {
      for(int var1 = CHUNK_STATUSES.size() - 1; var1 >= 0; --var1) {
         ChunkStatus var2 = (ChunkStatus)CHUNK_STATUSES.get(var1);
         CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var3 = this.getFutureIfPresentUnchecked(var2);
         if(((Either)var3.getNow(UNLOADED_CHUNK)).left().isPresent()) {
            return var2;
         }
      }

      return null;
   }

   @Nullable
   public ChunkAccess getLastAvailable() {
      for(int var1 = CHUNK_STATUSES.size() - 1; var1 >= 0; --var1) {
         ChunkStatus var2 = (ChunkStatus)CHUNK_STATUSES.get(var1);
         CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var3 = this.getFutureIfPresentUnchecked(var2);
         if(!var3.isCompletedExceptionally()) {
            Optional<ChunkAccess> var4 = ((Either)var3.getNow(UNLOADED_CHUNK)).left();
            if(var4.isPresent()) {
               return (ChunkAccess)var4.get();
            }
         }
      }

      return null;
   }

   public CompletableFuture getChunkToSave() {
      return this.chunkToSave;
   }

   public void blockChanged(int var1, int var2, int var3) {
      LevelChunk var4 = this.getTickingChunk();
      if(var4 != null) {
         this.changedSectionFilter |= 1 << (var2 >> 4);
         if(this.changes < 64) {
            short var5 = (short)(var1 << 12 | var3 << 8 | var2);

            for(int var6 = 0; var6 < this.changes; ++var6) {
               if(this.changedBlocks[var6] == var5) {
                  return;
               }
            }

            this.changedBlocks[this.changes++] = var5;
         }

      }
   }

   public void sectionLightChanged(LightLayer lightLayer, int var2) {
      LevelChunk var3 = this.getTickingChunk();
      if(var3 != null) {
         var3.setUnsaved(true);
         if(lightLayer == LightLayer.SKY) {
            this.skyChangedLightSectionFilter |= 1 << var2 - -1;
         } else {
            this.blockChangedLightSectionFilter |= 1 << var2 - -1;
         }

      }
   }

   public void broadcastChanges(LevelChunk levelChunk) {
      if(this.changes != 0 || this.skyChangedLightSectionFilter != 0 || this.blockChangedLightSectionFilter != 0) {
         Level var2 = levelChunk.getLevel();
         if(this.changes == 64) {
            this.sectionsToForceSendLightFor = -1;
         }

         if(this.skyChangedLightSectionFilter != 0 || this.blockChangedLightSectionFilter != 0) {
            this.broadcast(new ClientboundLightUpdatePacket(levelChunk.getPos(), this.lightEngine, this.skyChangedLightSectionFilter & ~this.sectionsToForceSendLightFor, this.blockChangedLightSectionFilter & ~this.sectionsToForceSendLightFor), true);
            int var3 = this.skyChangedLightSectionFilter & this.sectionsToForceSendLightFor;
            int var4 = this.blockChangedLightSectionFilter & this.sectionsToForceSendLightFor;
            if(var3 != 0 || var4 != 0) {
               this.broadcast(new ClientboundLightUpdatePacket(levelChunk.getPos(), this.lightEngine, var3, var4), false);
            }

            this.skyChangedLightSectionFilter = 0;
            this.blockChangedLightSectionFilter = 0;
            this.sectionsToForceSendLightFor &= ~(this.skyChangedLightSectionFilter & this.blockChangedLightSectionFilter);
         }

         if(this.changes == 1) {
            int var3 = (this.changedBlocks[0] >> 12 & 15) + this.pos.x * 16;
            int var4 = this.changedBlocks[0] & 255;
            int var5 = (this.changedBlocks[0] >> 8 & 15) + this.pos.z * 16;
            BlockPos var6 = new BlockPos(var3, var4, var5);
            this.broadcast(new ClientboundBlockUpdatePacket(var2, var6), false);
            if(var2.getBlockState(var6).getBlock().isEntityBlock()) {
               this.broadcastBlockEntity(var2, var6);
            }
         } else if(this.changes == 64) {
            this.broadcast(new ClientboundLevelChunkPacket(levelChunk, this.changedSectionFilter), false);
         } else if(this.changes != 0) {
            this.broadcast(new ClientboundChunkBlocksUpdatePacket(this.changes, this.changedBlocks, levelChunk), false);

            for(int var3 = 0; var3 < this.changes; ++var3) {
               int var4 = (this.changedBlocks[var3] >> 12 & 15) + this.pos.x * 16;
               int var5 = this.changedBlocks[var3] & 255;
               int var6 = (this.changedBlocks[var3] >> 8 & 15) + this.pos.z * 16;
               BlockPos var7 = new BlockPos(var4, var5, var6);
               if(var2.getBlockState(var7).getBlock().isEntityBlock()) {
                  this.broadcastBlockEntity(var2, var7);
               }
            }
         }

         this.changes = 0;
         this.changedSectionFilter = 0;
      }
   }

   private void broadcastBlockEntity(Level level, BlockPos blockPos) {
      BlockEntity var3 = level.getBlockEntity(blockPos);
      if(var3 != null) {
         ClientboundBlockEntityDataPacket var4 = var3.getUpdatePacket();
         if(var4 != null) {
            this.broadcast(var4, false);
         }
      }

   }

   private void broadcast(Packet packet, boolean var2) {
      this.playerProvider.getPlayers(this.pos, var2).forEach((serverPlayer) -> {
         serverPlayer.connection.send(packet);
      });
   }

   public CompletableFuture getOrScheduleFuture(ChunkStatus chunkStatus, ChunkMap chunkMap) {
      int var3 = chunkStatus.getIndex();
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var4 = (CompletableFuture)this.futures.get(var3);
      if(var4 != null) {
         Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> var5 = (Either)var4.getNow((Object)null);
         if(var5 == null || var5.left().isPresent()) {
            return var4;
         }
      }

      if(getStatus(this.ticketLevel).isOrAfter(chunkStatus)) {
         CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var5 = chunkMap.schedule(this, chunkStatus);
         this.updateChunkToSave(var5);
         this.futures.set(var3, var5);
         return var5;
      } else {
         return var4 == null?UNLOADED_CHUNK_FUTURE:var4;
      }
   }

   private void updateChunkToSave(CompletableFuture completableFuture) {
      this.chunkToSave = this.chunkToSave.thenCombine(completableFuture, (var0, either) -> {
         return (ChunkAccess)either.map((chunkAccess) -> {
            return chunkAccess;
         }, (chunkHolder$ChunkLoadingFailure) -> {
            return var0;
         });
      });
   }

   public ChunkHolder.FullChunkStatus getFullStatus() {
      return getFullChunkStatus(this.ticketLevel);
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public int getTicketLevel() {
      return this.ticketLevel;
   }

   public int getQueueLevel() {
      return this.queueLevel;
   }

   private void setQueueLevel(int queueLevel) {
      this.queueLevel = queueLevel;
   }

   public void setTicketLevel(int ticketLevel) {
      this.ticketLevel = ticketLevel;
   }

   protected void updateFutures(ChunkMap chunkMap) {
      ChunkStatus var2 = getStatus(this.oldTicketLevel);
      ChunkStatus var3 = getStatus(this.ticketLevel);
      boolean var4 = this.oldTicketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
      boolean var5 = this.ticketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
      ChunkHolder.FullChunkStatus var6 = getFullChunkStatus(this.oldTicketLevel);
      ChunkHolder.FullChunkStatus var7 = getFullChunkStatus(this.ticketLevel);
      if(var4) {
         Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> var8 = Either.right(new ChunkHolder.ChunkLoadingFailure() {
            public String toString() {
               return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
            }
         });

         for(int var9 = var5?var3.getIndex() + 1:0; var9 <= var2.getIndex(); ++var9) {
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var10 = (CompletableFuture)this.futures.get(var9);
            if(var10 != null) {
               var10.complete(var8);
            } else {
               this.futures.set(var9, CompletableFuture.completedFuture(var8));
            }
         }
      }

      boolean var8 = var6.isOrAfter(ChunkHolder.FullChunkStatus.BORDER);
      boolean var9 = var7.isOrAfter(ChunkHolder.FullChunkStatus.BORDER);
      this.wasAccessibleSinceLastSave |= var9;
      if(!var8 && var9) {
         this.fullChunkFuture = chunkMap.unpackTicks(this);
         this.updateChunkToSave(this.fullChunkFuture);
      }

      if(var8 && !var9) {
         CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> var10 = this.fullChunkFuture;
         this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
         this.updateChunkToSave(var10.thenApply((var1) -> {
            chunkMap.getClass();
            return var1.ifLeft(chunkMap::packTicks);
         }));
      }

      boolean var10 = var6.isOrAfter(ChunkHolder.FullChunkStatus.TICKING);
      boolean var11 = var7.isOrAfter(ChunkHolder.FullChunkStatus.TICKING);
      if(!var10 && var11) {
         this.tickingChunkFuture = chunkMap.postProcess(this);
         this.updateChunkToSave(this.tickingChunkFuture);
      }

      if(var10 && !var11) {
         this.tickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      boolean var12 = var6.isOrAfter(ChunkHolder.FullChunkStatus.ENTITY_TICKING);
      boolean var13 = var7.isOrAfter(ChunkHolder.FullChunkStatus.ENTITY_TICKING);
      if(!var12 && var13) {
         if(this.entityTickingChunkFuture != UNLOADED_LEVEL_CHUNK_FUTURE) {
            throw new IllegalStateException();
         }

         this.entityTickingChunkFuture = chunkMap.getEntityTickingRangeFuture(this.pos);
         this.updateChunkToSave(this.entityTickingChunkFuture);
      }

      if(var12 && !var13) {
         this.entityTickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      this.onLevelChange.onLevelChange(this.pos, this::getQueueLevel, this.ticketLevel, this::setQueueLevel);
      this.oldTicketLevel = this.ticketLevel;
   }

   public static ChunkStatus getStatus(int i) {
      return i < 33?ChunkStatus.FULL:ChunkStatus.getStatus(i - 33);
   }

   public static ChunkHolder.FullChunkStatus getFullChunkStatus(int i) {
      return FULL_CHUNK_STATUSES[Mth.clamp(33 - i + 1, 0, FULL_CHUNK_STATUSES.length - 1)];
   }

   public boolean wasAccessibleSinceLastSave() {
      return this.wasAccessibleSinceLastSave;
   }

   public void refreshAccessibility() {
      this.wasAccessibleSinceLastSave = getFullChunkStatus(this.ticketLevel).isOrAfter(ChunkHolder.FullChunkStatus.BORDER);
   }

   public void replaceProtoChunk(ImposterProtoChunk imposterProtoChunk) {
      for(int var2 = 0; var2 < this.futures.length(); ++var2) {
         CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> var3 = (CompletableFuture)this.futures.get(var2);
         if(var3 != null) {
            Optional<ChunkAccess> var4 = ((Either)var3.getNow(UNLOADED_CHUNK)).left();
            if(var4.isPresent() && var4.get() instanceof ProtoChunk) {
               this.futures.set(var2, CompletableFuture.completedFuture(Either.left(imposterProtoChunk)));
            }
         }
      }

      this.updateChunkToSave(CompletableFuture.completedFuture(Either.left(imposterProtoChunk.getWrapped())));
   }

   public interface ChunkLoadingFailure {
      ChunkHolder.ChunkLoadingFailure UNLOADED = new ChunkHolder.ChunkLoadingFailure() {
         public String toString() {
            return "UNLOADED";
         }
      };
   }

   public static enum FullChunkStatus {
      INACCESSIBLE,
      BORDER,
      TICKING,
      ENTITY_TICKING;

      public boolean isOrAfter(ChunkHolder.FullChunkStatus chunkHolder$FullChunkStatus) {
         return this.ordinal() >= chunkHolder$FullChunkStatus.ordinal();
      }
   }

   public interface LevelChangeListener {
      void onLevelChange(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4);
   }

   public interface PlayerProvider {
      Stream getPlayers(ChunkPos var1, boolean var2);
   }
}
