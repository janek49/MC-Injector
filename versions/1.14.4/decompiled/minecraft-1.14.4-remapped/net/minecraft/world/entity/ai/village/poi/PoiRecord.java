package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Serializable;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class PoiRecord implements Serializable {
   private final BlockPos pos;
   private final PoiType poiType;
   private int freeTickets;
   private final Runnable setDirty;

   private PoiRecord(BlockPos blockPos, PoiType poiType, int freeTickets, Runnable setDirty) {
      this.pos = blockPos.immutable();
      this.poiType = poiType;
      this.freeTickets = freeTickets;
      this.setDirty = setDirty;
   }

   public PoiRecord(BlockPos blockPos, PoiType poiType, Runnable runnable) {
      this(blockPos, poiType, poiType.getMaxTickets(), runnable);
   }

   public PoiRecord(Dynamic dynamic, Runnable runnable) {
      this((BlockPos)dynamic.get("pos").map(BlockPos::deserialize).orElse(new BlockPos(0, 0, 0)), (PoiType)Registry.POINT_OF_INTEREST_TYPE.get(new ResourceLocation(dynamic.get("type").asString(""))), dynamic.get("free_tickets").asInt(0), runnable);
   }

   public Object serialize(DynamicOps dynamicOps) {
      return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("pos"), this.pos.serialize(dynamicOps), dynamicOps.createString("type"), dynamicOps.createString(Registry.POINT_OF_INTEREST_TYPE.getKey(this.poiType).toString()), dynamicOps.createString("free_tickets"), dynamicOps.createInt(this.freeTickets)));
   }

   protected boolean acquireTicket() {
      if(this.freeTickets <= 0) {
         return false;
      } else {
         --this.freeTickets;
         this.setDirty.run();
         return true;
      }
   }

   protected boolean releaseTicket() {
      if(this.freeTickets >= this.poiType.getMaxTickets()) {
         return false;
      } else {
         ++this.freeTickets;
         this.setDirty.run();
         return true;
      }
   }

   public boolean hasSpace() {
      return this.freeTickets > 0;
   }

   public boolean isOccupied() {
      return this.freeTickets != this.poiType.getMaxTickets();
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public PoiType getPoiType() {
      return this.poiType;
   }

   public boolean equals(Object object) {
      return this == object?true:(object != null && this.getClass() == object.getClass()?Objects.equals(this.pos, ((PoiRecord)object).pos):false);
   }

   public int hashCode() {
      return this.pos.hashCode();
   }
}
