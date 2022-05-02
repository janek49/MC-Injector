package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class VillageConfiguration implements FeatureConfiguration {
   public final ResourceLocation startPool;
   public final int size;

   public VillageConfiguration(String string, int size) {
      this.startPool = new ResourceLocation(string);
      this.size = size;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("start_pool"), dynamicOps.createString(this.startPool.toString()), dynamicOps.createString("size"), dynamicOps.createInt(this.size))));
   }

   public static VillageConfiguration deserialize(Dynamic dynamic) {
      String var1 = dynamic.get("start_pool").asString("");
      int var2 = dynamic.get("size").asInt(6);
      return new VillageConfiguration(var1, var2);
   }
}
