package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;

public class OceanRuinConfiguration implements FeatureConfiguration {
   public final OceanRuinFeature.Type biomeTemp;
   public final float largeProbability;
   public final float clusterProbability;

   public OceanRuinConfiguration(OceanRuinFeature.Type biomeTemp, float largeProbability, float clusterProbability) {
      this.biomeTemp = biomeTemp;
      this.largeProbability = largeProbability;
      this.clusterProbability = clusterProbability;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("biome_temp"), dynamicOps.createString(this.biomeTemp.getName()), dynamicOps.createString("large_probability"), dynamicOps.createFloat(this.largeProbability), dynamicOps.createString("cluster_probability"), dynamicOps.createFloat(this.clusterProbability))));
   }

   public static OceanRuinConfiguration deserialize(Dynamic dynamic) {
      OceanRuinFeature.Type var1 = OceanRuinFeature.Type.byName(dynamic.get("biome_temp").asString(""));
      float var2 = dynamic.get("large_probability").asFloat(0.0F);
      float var3 = dynamic.get("cluster_probability").asFloat(0.0F);
      return new OceanRuinConfiguration(var1, var2, var3);
   }
}
