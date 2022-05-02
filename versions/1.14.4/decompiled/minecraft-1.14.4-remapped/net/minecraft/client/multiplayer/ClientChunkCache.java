package net.minecraft.client.multiplayer;

import com.fox2code.repacker.ClientJarOnly;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class ClientChunkCache extends ChunkSource {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LevelChunk emptyChunk;
   private final LevelLightEngine lightEngine;
   private volatile ClientChunkCache.Storage storage;
   private final MultiPlayerLevel level;

   public ClientChunkCache(MultiPlayerLevel level, int var2) {
      this.level = level;
      this.emptyChunk = new EmptyLevelChunk(level, new ChunkPos(0, 0));
      this.lightEngine = new LevelLightEngine(this, true, level.getDimension().isHasSkyLight());
      this.storage = new ClientChunkCache.Storage(calculateStorageRange(var2));
   }

   public LevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   private static boolean isValidChunk(@Nullable LevelChunk levelChunk, int var1, int var2) {
      if(levelChunk == null) {
         return false;
      } else {
         ChunkPos var3 = levelChunk.getPos();
         return var3.x == var1 && var3.z == var2;
      }
   }

   public void drop(int var1, int var2) {
      if(this.storage.inRange(var1, var2)) {
         int var3 = this.storage.getIndex(var1, var2);
         LevelChunk var4 = this.storage.getChunk(var3);
         if(isValidChunk(var4, var1, var2)) {
            this.storage.replace(var3, var4, (LevelChunk)null);
         }

      }
   }

   @Nullable
   public LevelChunk getChunk(int var1, int var2, ChunkStatus chunkStatus, boolean var4) {
      if(this.storage.inRange(var1, var2)) {
         LevelChunk levelChunk = this.storage.getChunk(this.storage.getIndex(var1, var2));
         if(isValidChunk(levelChunk, var1, var2)) {
            return levelChunk;
         }
      }

      return var4?this.emptyChunk:null;
   }

   public BlockGetter getLevel() {
      return this.level;
   }

   @Nullable
   public LevelChunk replaceWithPacketData(Level level, int var2, int var3, FriendlyByteBuf friendlyByteBuf, CompoundTag compoundTag, int var6, boolean var7) {
      if(!this.storage.inRange(var2, var3)) {
         LOGGER.warn("Ignoring chunk since it\'s not in the view range: {}, {}", Integer.valueOf(var2), Integer.valueOf(var3));
         return null;
      } else {
         int var8 = this.storage.getIndex(var2, var3);
         LevelChunk var9 = (LevelChunk)this.storage.chunks.get(var8);
         if(!isValidChunk(var9, var2, var3)) {
            if(!var7) {
               LOGGER.warn("Ignoring chunk since we don\'t have complete data: {}, {}", Integer.valueOf(var2), Integer.valueOf(var3));
               return null;
            }

            var9 = new LevelChunk(level, new ChunkPos(var2, var3), new Biome[256]);
            var9.replaceWithPacketData(friendlyByteBuf, compoundTag, var6, var7);
            this.storage.replace(var8, var9);
         } else {
            var9.replaceWithPacketData(friendlyByteBuf, compoundTag, var6, var7);
         }

         LevelChunkSection[] vars10 = var9.getSections();
         LevelLightEngine var11 = this.getLightEngine();
         var11.enableLightSources(new ChunkPos(var2, var3), true);

         for(int var12 = 0; var12 < vars10.length; ++var12) {
            LevelChunkSection var13 = vars10[var12];
            var11.updateSectionStatus(SectionPos.of(var2, var12, var3), LevelChunkSection.isEmpty(var13));
         }

         return var9;
      }
   }

   public void tick(BooleanSupplier booleanSupplier) {
   }

   public void updateViewCenter(int var1, int var2) {
      this.storage.viewCenterX = var1;
      this.storage.viewCenterZ = var2;
   }

   public void updateViewRadius(int i) {
      int var2 = this.storage.chunkRadius;
      int var3 = calculateStorageRange(i);
      if(var2 != var3) {
         ClientChunkCache.Storage var4 = new ClientChunkCache.Storage(var3);
         var4.viewCenterX = this.storage.viewCenterX;
         var4.viewCenterZ = this.storage.viewCenterZ;

         for(int var5 = 0; var5 < this.storage.chunks.length(); ++var5) {
            LevelChunk var6 = (LevelChunk)this.storage.chunks.get(var5);
            if(var6 != null) {
               ChunkPos var7 = var6.getPos();
               if(var4.inRange(var7.x, var7.z)) {
                  var4.replace(var4.getIndex(var7.x, var7.z), var6);
               }
            }
         }

         this.storage = var4;
      }

   }

   private static int calculateStorageRange(int i) {
      return Math.max(2, i) + 3;
   }

   public String gatherStats() {
      return "Client Chunk Cache: " + this.storage.chunks.length() + ", " + this.getLoadedChunksCount();
   }

   public ChunkGenerator getGenerator() {
      return null;
   }

   public int getLoadedChunksCount() {
      return this.storage.chunkCount;
   }

   public void onLightUpdate(LightLayer lightLayer, SectionPos sectionPos) {
      Minecraft.getInstance().levelRenderer.setSectionDirty(sectionPos.x(), sectionPos.y(), sectionPos.z());
   }

   public boolean isTickingChunk(BlockPos blockPos) {
      return this.hasChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
   }

   public boolean isEntityTickingChunk(ChunkPos chunkPos) {
      return this.hasChunk(chunkPos.x, chunkPos.z);
   }

   public boolean isEntityTickingChunk(Entity entity) {
      return this.hasChunk(Mth.floor(entity.x) >> 4, Mth.floor(entity.z) >> 4);
   }

   // $FF: synthetic method
   @Nullable
   public ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
      return this.getChunk(var1, var2, var3, var4);
   }

   @ClientJarOnly
   final class Storage {
      private final AtomicReferenceArray chunks;
      private final int chunkRadius;
      private final int viewRange;
      private volatile int viewCenterX;
      private volatile int viewCenterZ;
      private int chunkCount;

      private Storage(int chunkRadius) {
         this.chunkRadius = chunkRadius;
         this.viewRange = chunkRadius * 2 + 1;
         this.chunks = new AtomicReferenceArray(this.viewRange * this.viewRange);
      }

      private int getIndex(int var1, int var2) {
         return Math.floorMod(var2, this.viewRange) * this.viewRange + Math.floorMod(var1, this.viewRange);
      }

      protected void replace(int var1, @Nullable LevelChunk levelChunkx) {
         LevelChunk levelChunk = (LevelChunk)this.chunks.getAndSet(var1, levelChunkx);
         if(levelChunk != null) {
            --this.chunkCount;
            ClientChunkCache.this.level.unload(levelChunk);
         }

         if(levelChunkx != null) {
            ++this.chunkCount;
         }

      }

      protected LevelChunk replace(int var1, LevelChunk var2, @Nullable LevelChunk var3) {
         if(this.chunks.compareAndSet(var1, var2, var3) && var3 == null) {
            --this.chunkCount;
         }

         ClientChunkCache.this.level.unload(var2);
         return var2;
      }

      private boolean inRange(int var1, int var2) {
         return Math.abs(var1 - this.viewCenterX) <= this.chunkRadius && Math.abs(var2 - this.viewCenterZ) <= this.chunkRadius;
      }

      @Nullable
      protected LevelChunk getChunk(int i) {
         return (LevelChunk)this.chunks.get(i);
      }
   }
}
