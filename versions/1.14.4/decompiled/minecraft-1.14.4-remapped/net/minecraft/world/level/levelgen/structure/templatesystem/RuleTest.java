package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public abstract class RuleTest {
   public abstract boolean test(BlockState var1, Random var2);

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.mergeInto(this.getDynamic(dynamicOps).getValue(), dynamicOps.createString("predicate_type"), dynamicOps.createString(Registry.RULE_TEST.getKey(this.getType()).toString())));
   }

   protected abstract RuleTestType getType();

   protected abstract Dynamic getDynamic(DynamicOps var1);
}
