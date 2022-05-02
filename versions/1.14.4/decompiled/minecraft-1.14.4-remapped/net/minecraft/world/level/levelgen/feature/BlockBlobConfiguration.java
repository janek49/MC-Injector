package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.function.Function;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class BlockBlobConfiguration implements FeatureConfiguration {
   public final BlockState state;
   public final int startRadius;

   public BlockBlobConfiguration(BlockState state, int startRadius) {
      this.state = state;
      this.startRadius = startRadius;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue(), dynamicOps.createString("start_radius"), dynamicOps.createInt(this.startRadius))));
   }

   public static BlockBlobConfiguration deserialize(Dynamic dynamic) {
      BlockState var1 = (BlockState)dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      int var2 = dynamic.get("start_radius").asInt(0);
      return new BlockBlobConfiguration(var1, var2);
   }
}
