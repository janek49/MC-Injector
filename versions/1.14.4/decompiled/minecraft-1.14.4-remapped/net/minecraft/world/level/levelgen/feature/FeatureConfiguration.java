package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public interface FeatureConfiguration {
   NoneFeatureConfiguration NONE = new NoneFeatureConfiguration();

   Dynamic serialize(DynamicOps var1);
}
