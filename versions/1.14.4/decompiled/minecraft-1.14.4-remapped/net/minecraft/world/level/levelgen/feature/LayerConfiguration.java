package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.function.Function;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class LayerConfiguration implements FeatureConfiguration {
   public final int height;
   public final BlockState state;

   public LayerConfiguration(int height, BlockState state) {
      this.height = height;
      this.state = state;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("height"), dynamicOps.createInt(this.height), dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue())));
   }

   public static LayerConfiguration deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("height").asInt(0);
      BlockState var2 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      return new LayerConfiguration(var1, var2);
   }
}
