package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;

public interface DecoratorConfiguration {
   NoneDecoratorConfiguration NONE = new NoneDecoratorConfiguration();

   Dynamic serialize(DynamicOps var1);
}
