package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class BlockMatchTest extends RuleTest {
   private final Block block;

   public BlockMatchTest(Block block) {
      this.block = block;
   }

   public BlockMatchTest(Dynamic dynamic) {
      this((Block)Registry.BLOCK.get(new ResourceLocation(dynamic.get("block").asString(""))));
   }

   public boolean test(BlockState blockState, Random random) {
      return blockState.getBlock() == this.block;
   }

   protected RuleTestType getType() {
      return RuleTestType.BLOCK_TEST;
   }

   protected Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("block"), dynamicOps.createString(Registry.BLOCK.getKey(this.block).toString()))));
   }
}
