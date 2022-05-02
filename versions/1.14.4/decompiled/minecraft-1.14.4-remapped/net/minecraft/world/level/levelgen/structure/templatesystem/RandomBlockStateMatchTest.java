package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class RandomBlockStateMatchTest extends RuleTest {
   private final BlockState blockState;
   private final float probability;

   public RandomBlockStateMatchTest(BlockState blockState, float probability) {
      this.blockState = blockState;
      this.probability = probability;
   }

   public RandomBlockStateMatchTest(Dynamic dynamic) {
      this(BlockState.deserialize(dynamic.get("blockstate").orElseEmptyMap()), dynamic.get("probability").asFloat(1.0F));
   }

   public boolean test(BlockState blockState, Random random) {
      return blockState == this.blockState && random.nextFloat() < this.probability;
   }

   protected RuleTestType getType() {
      return RuleTestType.RANDOM_BLOCKSTATE_TEST;
   }

   protected Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("blockstate"), BlockState.serialize(dynamicOps, this.blockState).getValue(), dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
   }
}
