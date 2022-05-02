package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;

public class JigsawJunction {
   private final int sourceX;
   private final int sourceGroundY;
   private final int sourceZ;
   private final int deltaY;
   private final StructureTemplatePool.Projection destProjection;

   public JigsawJunction(int sourceX, int sourceGroundY, int sourceZ, int deltaY, StructureTemplatePool.Projection destProjection) {
      this.sourceX = sourceX;
      this.sourceGroundY = sourceGroundY;
      this.sourceZ = sourceZ;
      this.deltaY = deltaY;
      this.destProjection = destProjection;
   }

   public int getSourceX() {
      return this.sourceX;
   }

   public int getSourceGroundY() {
      return this.sourceGroundY;
   }

   public int getSourceZ() {
      return this.sourceZ;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      Builder<T, T> var2 = ImmutableMap.builder();
      var2.put(dynamicOps.createString("source_x"), dynamicOps.createInt(this.sourceX)).put(dynamicOps.createString("source_ground_y"), dynamicOps.createInt(this.sourceGroundY)).put(dynamicOps.createString("source_z"), dynamicOps.createInt(this.sourceZ)).put(dynamicOps.createString("delta_y"), dynamicOps.createInt(this.deltaY)).put(dynamicOps.createString("dest_proj"), dynamicOps.createString(this.destProjection.getName()));
      return new Dynamic(dynamicOps, dynamicOps.createMap(var2.build()));
   }

   public static JigsawJunction deserialize(Dynamic dynamic) {
      return new JigsawJunction(dynamic.get("source_x").asInt(0), dynamic.get("source_ground_y").asInt(0), dynamic.get("source_z").asInt(0), dynamic.get("delta_y").asInt(0), StructureTemplatePool.Projection.byName(dynamic.get("dest_proj").asString("")));
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         JigsawJunction var2 = (JigsawJunction)object;
         return this.sourceX != var2.sourceX?false:(this.sourceZ != var2.sourceZ?false:(this.deltaY != var2.deltaY?false:this.destProjection == var2.destProjection));
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.sourceX;
      var1 = 31 * var1 + this.sourceGroundY;
      var1 = 31 * var1 + this.sourceZ;
      var1 = 31 * var1 + this.deltaY;
      var1 = 31 * var1 + this.destProjection.hashCode();
      return var1;
   }

   public String toString() {
      return "JigsawJunction{sourceX=" + this.sourceX + ", sourceGroundY=" + this.sourceGroundY + ", sourceZ=" + this.sourceZ + ", deltaY=" + this.deltaY + ", destProjection=" + this.destProjection + '}';
   }
}
