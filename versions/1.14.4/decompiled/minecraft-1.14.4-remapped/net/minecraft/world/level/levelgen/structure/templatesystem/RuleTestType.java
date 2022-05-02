package net.minecraft.world.level.levelgen.structure.templatesystem;

import net.minecraft.core.Registry;
import net.minecraft.util.Deserializer;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public interface RuleTestType extends Deserializer {
   RuleTestType ALWAYS_TRUE_TEST = register("always_true", (dynamic) -> {
      return AlwaysTrueTest.INSTANCE;
   });
   RuleTestType BLOCK_TEST = register("block_match", BlockMatchTest::<init>);
   RuleTestType BLOCKSTATE_TEST = register("blockstate_match", BlockStateMatchTest::<init>);
   RuleTestType TAG_TEST = register("tag_match", TagMatchTest::<init>);
   RuleTestType RANDOM_BLOCK_TEST = register("random_block_match", RandomBlockMatchTest::<init>);
   RuleTestType RANDOM_BLOCKSTATE_TEST = register("random_blockstate_match", RandomBlockStateMatchTest::<init>);

   static default RuleTestType register(String string, RuleTestType var1) {
      return (RuleTestType)Registry.register(Registry.RULE_TEST, (String)string, var1);
   }
}
