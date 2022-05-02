package net.minecraft.world.entity.ai.village.poi;

import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.SectionTracker;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiSection;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.SectionStorage;

public class PoiManager extends SectionStorage {
   private final PoiManager.DistanceTracker distanceTracker = new PoiManager.DistanceTracker();

   public PoiManager(File file, DataFixer dataFixer) {
      super(file, PoiSection::<init>, PoiSection::<init>, dataFixer, DataFixTypes.POI_CHUNK);
   }

   public void add(BlockPos blockPos, PoiType poiType) {
      ((PoiSection)this.getOrCreate(SectionPos.of(blockPos).asLong())).add(blockPos, poiType);
   }

   public void remove(BlockPos blockPos) {
      ((PoiSection)this.getOrCreate(SectionPos.of(blockPos).asLong())).remove(blockPos);
   }

   public long getCountInRange(Predicate predicate, BlockPos blockPos, int var3, PoiManager.Occupancy poiManager$Occupancy) {
      return this.getInRange(predicate, blockPos, var3, poiManager$Occupancy).count();
   }

   public Stream getInRange(Predicate predicate, BlockPos blockPos, int var3, PoiManager.Occupancy poiManager$Occupancy) {
      int var5 = var3 * var3;
      return ChunkPos.rangeClosed(new ChunkPos(blockPos), Math.floorDiv(var3, 16)).flatMap((chunkPos) -> {
         return this.getInChunk(predicate, chunkPos, poiManager$Occupancy).filter((poiRecord) -> {
            return poiRecord.getPos().distSqr(blockPos) <= (double)var5;
         });
      });
   }

   public Stream getInChunk(Predicate predicate, ChunkPos chunkPos, PoiManager.Occupancy poiManager$Occupancy) {
      return IntStream.range(0, 16).boxed().flatMap((integer) -> {
         return this.getInSection(predicate, SectionPos.of(chunkPos, integer.intValue()).asLong(), poiManager$Occupancy);
      });
   }

   private Stream getInSection(Predicate predicate, long var2, PoiManager.Occupancy poiManager$Occupancy) {
      return (Stream)this.getOrLoad(var2).map((poiSection) -> {
         return poiSection.getRecords(predicate, poiManager$Occupancy);
      }).orElseGet(Stream::empty);
   }

   public Stream findAll(Predicate var1, Predicate var2, BlockPos blockPos, int var4, PoiManager.Occupancy poiManager$Occupancy) {
      return this.getInRange(var1, blockPos, var4, poiManager$Occupancy).map(PoiRecord::getPos).filter(var2);
   }

   public Optional find(Predicate var1, Predicate var2, BlockPos blockPos, int var4, PoiManager.Occupancy poiManager$Occupancy) {
      return this.findAll(var1, var2, blockPos, var4, poiManager$Occupancy).findFirst();
   }

   public Optional findClosest(Predicate var1, Predicate var2, BlockPos blockPos, int var4, PoiManager.Occupancy poiManager$Occupancy) {
      return this.getInRange(var1, blockPos, var4, poiManager$Occupancy).map(PoiRecord::getPos).sorted(Comparator.comparingDouble((var1) -> {
         return var1.distSqr(blockPos);
      })).filter(var2).findFirst();
   }

   public Optional take(Predicate var1, Predicate var2, BlockPos blockPos, int var4) {
      return this.getInRange(var1, blockPos, var4, PoiManager.Occupancy.HAS_SPACE).filter((poiRecord) -> {
         return var2.test(poiRecord.getPos());
      }).findFirst().map((poiRecord) -> {
         poiRecord.acquireTicket();
         return poiRecord.getPos();
      });
   }

   public Optional getRandom(Predicate var1, Predicate var2, PoiManager.Occupancy poiManager$Occupancy, BlockPos blockPos, int var5, Random random) {
      List<PoiRecord> var7 = (List)this.getInRange(var1, blockPos, var5, poiManager$Occupancy).collect(Collectors.toList());
      Collections.shuffle(var7, random);
      return var7.stream().filter((poiRecord) -> {
         return var2.test(poiRecord.getPos());
      }).findFirst().map(PoiRecord::getPos);
   }

   public boolean release(BlockPos blockPos) {
      return ((PoiSection)this.getOrCreate(SectionPos.of(blockPos).asLong())).release(blockPos);
   }

   public boolean exists(BlockPos blockPos, Predicate predicate) {
      return ((Boolean)this.getOrLoad(SectionPos.of(blockPos).asLong()).map((poiSection) -> {
         return Boolean.valueOf(poiSection.exists(blockPos, predicate));
      }).orElse(Boolean.valueOf(false))).booleanValue();
   }

   public Optional getType(BlockPos blockPos) {
      PoiSection var2 = (PoiSection)this.getOrCreate(SectionPos.of(blockPos).asLong());
      return var2.getType(blockPos);
   }

   public int sectionsToVillage(SectionPos sectionPos) {
      this.distanceTracker.runAllUpdates();
      return this.distanceTracker.getLevel(sectionPos.asLong());
   }

   private boolean isVillageCenter(long l) {
      Optional<PoiSection> var3 = this.get(l);
      return var3 == null?false:((Boolean)var3.map((poiSection) -> {
         return Boolean.valueOf(poiSection.getRecords(PoiType.ALL, PoiManager.Occupancy.IS_OCCUPIED).count() > 0L);
      }).orElse(Boolean.valueOf(false))).booleanValue();
   }

   public void tick(BooleanSupplier booleanSupplier) {
      super.tick(booleanSupplier);
      this.distanceTracker.runAllUpdates();
   }

   protected void setDirty(long dirty) {
      super.setDirty(dirty);
      this.distanceTracker.update(dirty, this.distanceTracker.getLevelFromSource(dirty), false);
   }

   protected void onSectionLoad(long l) {
      this.distanceTracker.update(l, this.distanceTracker.getLevelFromSource(l), false);
   }

   public void checkConsistencyWithBlocks(ChunkPos chunkPos, LevelChunkSection levelChunkSection) {
      SectionPos var3 = SectionPos.of(chunkPos, levelChunkSection.bottomBlockY() >> 4);
      Util.ifElse(this.getOrLoad(var3.asLong()), (poiSection) -> {
         poiSection.refresh((biConsumer) -> {
            if(mayHavePoi(levelChunkSection)) {
               this.updateFromSection(levelChunkSection, var3, biConsumer);
            }

         });
      }, () -> {
         if(mayHavePoi(levelChunkSection)) {
            PoiSection var3 = (PoiSection)this.getOrCreate(var3.asLong());
            this.updateFromSection(levelChunkSection, var3, var3::add);
         }

      });
   }

   private static boolean mayHavePoi(LevelChunkSection levelChunkSection) {
      Stream var10000 = PoiType.allPoiStates();
      levelChunkSection.getClass();
      return var10000.anyMatch(levelChunkSection::maybeHas);
   }

   private void updateFromSection(LevelChunkSection levelChunkSection, SectionPos sectionPos, BiConsumer biConsumer) {
      sectionPos.blocksInside().forEach((blockPos) -> {
         BlockState var3 = levelChunkSection.getBlockState(SectionPos.sectionRelative(blockPos.getX()), SectionPos.sectionRelative(blockPos.getY()), SectionPos.sectionRelative(blockPos.getZ()));
         PoiType.forState(var3).ifPresent((poiType) -> {
            biConsumer.accept(blockPos, poiType);
         });
      });
   }

   final class DistanceTracker extends SectionTracker {
      private final Long2ByteMap levels = new Long2ByteOpenHashMap();

      protected DistanceTracker() {
         super(7, 16, 256);
         this.levels.defaultReturnValue((byte)7);
      }

      protected int getLevelFromSource(long l) {
         return PoiManager.this.isVillageCenter(l)?0:7;
      }

      protected int getLevel(long l) {
         return this.levels.get(l);
      }

      protected void setLevel(long var1, int var3) {
         if(var3 > 6) {
            this.levels.remove(var1);
         } else {
            this.levels.put(var1, (byte)var3);
         }

      }

      public void runAllUpdates() {
         super.runUpdates(Integer.MAX_VALUE);
      }
   }

   public static enum Occupancy {
      HAS_SPACE(PoiRecord::hasSpace),
      IS_OCCUPIED(PoiRecord::isOccupied),
      ANY((poiRecord) -> {
         return true;
      });

      private final Predicate test;

      private Occupancy(Predicate test) {
         this.test = test;
      }

      public Predicate getTest() {
         return this.test;
      }
   }
}
