package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Serializable;
import net.minecraft.world.level.dimension.DimensionType;

public final class GlobalPos implements Serializable {
   private final DimensionType dimension;
   private final BlockPos pos;

   private GlobalPos(DimensionType dimension, BlockPos pos) {
      this.dimension = dimension;
      this.pos = pos;
   }

   public static GlobalPos of(DimensionType dimensionType, BlockPos blockPos) {
      return new GlobalPos(dimensionType, blockPos);
   }

   public static GlobalPos of(Dynamic dynamic) {
      return (GlobalPos)dynamic.get("dimension").map(DimensionType::of).flatMap((dimensionType) -> {
         return dynamic.get("pos").map(BlockPos::deserialize).map((blockPos) -> {
            return new GlobalPos(dimensionType, blockPos);
         });
      }).orElseThrow(() -> {
         return new IllegalArgumentException("Could not parse GlobalPos");
      });
   }

   public DimensionType dimension() {
      return this.dimension;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         GlobalPos var2 = (GlobalPos)object;
         return Objects.equals(this.dimension, var2.dimension) && Objects.equals(this.pos, var2.pos);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.dimension, this.pos});
   }

   public Object serialize(DynamicOps dynamicOps) {
      return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("dimension"), this.dimension.serialize(dynamicOps), dynamicOps.createString("pos"), this.pos.serialize(dynamicOps)));
   }

   public String toString() {
      return this.dimension.toString() + " " + this.pos;
   }
}
