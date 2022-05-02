package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Serializable;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class PoiSection implements Serializable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Short2ObjectMap records = new Short2ObjectOpenHashMap();
   private final Map byType = Maps.newHashMap();
   private final Runnable setDirty;
   private boolean isValid;

   public PoiSection(Runnable setDirty) {
      this.setDirty = setDirty;
      this.isValid = true;
   }

   public PoiSection(Runnable setDirty, Dynamic dynamic) {
      this.setDirty = setDirty;

      try {
         this.isValid = dynamic.get("Valid").asBoolean(false);
         dynamic.get("Records").asStream().forEach((dynamic) -> {
            this.add(new PoiRecord(dynamic, setDirty));
         });
      } catch (Exception var4) {
         LOGGER.error("Failed to load POI chunk", var4);
         this.clear();
         this.isValid = false;
      }

   }

   public Stream getRecords(Predicate predicate, PoiManager.Occupancy poiManager$Occupancy) {
      return this.byType.entrySet().stream().filter((map$Entry) -> {
         return predicate.test(map$Entry.getKey());
      }).flatMap((map$Entry) -> {
         return ((Set)map$Entry.getValue()).stream();
      }).filter(poiManager$Occupancy.getTest());
   }

   public void add(BlockPos blockPos, PoiType poiType) {
      if(this.add(new PoiRecord(blockPos, poiType, this.setDirty))) {
         LOGGER.debug("Added POI of type {} @ {}", new Supplier[]{() -> {
            return poiType;
         }, () -> {
            return blockPos;
         }});
         this.setDirty.run();
      }

   }

   private boolean add(PoiRecord poiRecord) {
      BlockPos var2 = poiRecord.getPos();
      PoiType var3 = poiRecord.getPoiType();
      short var4 = SectionPos.sectionRelativePos(var2);
      PoiRecord var5 = (PoiRecord)this.records.get(var4);
      if(var5 != null) {
         if(var3.equals(var5.getPoiType())) {
            return false;
         } else {
            throw new IllegalStateException("POI data mismatch: already registered at " + var2);
         }
      } else {
         this.records.put(var4, poiRecord);
         ((Set)this.byType.computeIfAbsent(var3, (poiType) -> {
            return Sets.newHashSet();
         })).add(poiRecord);
         return true;
      }
   }

   public void remove(BlockPos blockPos) {
      PoiRecord var2 = (PoiRecord)this.records.remove(SectionPos.sectionRelativePos(blockPos));
      if(var2 == null) {
         LOGGER.error("POI data mismatch: never registered at " + blockPos);
      } else {
         ((Set)this.byType.get(var2.getPoiType())).remove(var2);
         LOGGER.debug("Removed POI of type {} @ {}", new Supplier[]{var2::getPoiType, var2::getPos});
         this.setDirty.run();
      }
   }

   public boolean release(BlockPos blockPos) {
      PoiRecord var2 = (PoiRecord)this.records.get(SectionPos.sectionRelativePos(blockPos));
      if(var2 == null) {
         throw new IllegalStateException("POI never registered at " + blockPos);
      } else {
         boolean var3 = var2.releaseTicket();
         this.setDirty.run();
         return var3;
      }
   }

   public boolean exists(BlockPos blockPos, Predicate predicate) {
      short var3 = SectionPos.sectionRelativePos(blockPos);
      PoiRecord var4 = (PoiRecord)this.records.get(var3);
      return var4 != null && predicate.test(var4.getPoiType());
   }

   public Optional getType(BlockPos blockPos) {
      short var2 = SectionPos.sectionRelativePos(blockPos);
      PoiRecord var3 = (PoiRecord)this.records.get(var2);
      return var3 != null?Optional.of(var3.getPoiType()):Optional.empty();
   }

   public Object serialize(DynamicOps dynamicOps) {
      T object = dynamicOps.createList(this.records.values().stream().map((poiRecord) -> {
         return poiRecord.serialize(dynamicOps);
      }));
      return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("Records"), object, dynamicOps.createString("Valid"), dynamicOps.createBoolean(this.isValid)));
   }

   public void refresh(Consumer consumer) {
      if(!this.isValid) {
         Short2ObjectMap<PoiRecord> var2 = new Short2ObjectOpenHashMap(this.records);
         this.clear();
         consumer.accept((blockPos, poiType) -> {
            short var4 = SectionPos.sectionRelativePos(blockPos);
            PoiRecord var5 = (PoiRecord)var2.computeIfAbsent(var4, (var3) -> {
               return new PoiRecord(blockPos, poiType, this.setDirty);
            });
            this.add(var5);
         });
         this.isValid = true;
         this.setDirty.run();
      }

   }

   private void clear() {
      this.records.clear();
      this.byType.clear();
   }
}
