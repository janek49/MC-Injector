package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorNoiseCountFactor implements DecoratorConfiguration {
   public final int noiseToCountRatio;
   public final double noiseFactor;
   public final double noiseOffset;
   public final Heightmap.Types heightmap;

   public DecoratorNoiseCountFactor(int noiseToCountRatio, double noiseFactor, double noiseOffset, Heightmap.Types heightmap) {
      this.noiseToCountRatio = noiseToCountRatio;
      this.noiseFactor = noiseFactor;
      this.noiseOffset = noiseOffset;
      this.heightmap = heightmap;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("noise_to_count_ratio"), dynamicOps.createInt(this.noiseToCountRatio), dynamicOps.createString("noise_factor"), dynamicOps.createDouble(this.noiseFactor), dynamicOps.createString("noise_offset"), dynamicOps.createDouble(this.noiseOffset), dynamicOps.createString("heightmap"), dynamicOps.createString(this.heightmap.getSerializationKey()))));
   }

   public static DecoratorNoiseCountFactor deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("noise_to_count_ratio").asInt(10);
      double var2 = dynamic.get("noise_factor").asDouble(80.0D);
      double var4 = dynamic.get("noise_offset").asDouble(0.0D);
      Heightmap.Types var6 = Heightmap.Types.getFromKey(dynamic.get("heightmap").asString("OCEAN_FLOOR_WG"));
      return new DecoratorNoiseCountFactor(var1, var2, var4, var6);
   }
}
