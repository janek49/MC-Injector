package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class AlwaysTrueTest extends RuleTest {
   public static final AlwaysTrueTest INSTANCE = new AlwaysTrueTest();

   public boolean test(BlockState blockState, Random random) {
      return true;
   }

   protected RuleTestType getType() {
      return RuleTestType.ALWAYS_TRUE_TEST;
   }

   protected Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.emptyMap());
   }
}
