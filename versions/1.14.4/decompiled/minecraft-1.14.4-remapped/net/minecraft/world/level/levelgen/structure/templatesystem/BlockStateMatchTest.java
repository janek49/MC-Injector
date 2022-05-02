package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class BlockStateMatchTest extends RuleTest {
   private final BlockState blockState;

   public BlockStateMatchTest(BlockState blockState) {
      this.blockState = blockState;
   }

   public BlockStateMatchTest(Dynamic dynamic) {
      this(BlockState.deserialize(dynamic.get("blockstate").orElseEmptyMap()));
   }

   public boolean test(BlockState blockState, Random random) {
      return blockState == this.blockState;
   }

   protected RuleTestType getType() {
      return RuleTestType.BLOCKSTATE_TEST;
   }

   protected Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("blockstate"), BlockState.serialize(dynamicOps, this.blockState).getValue())));
   }
}
