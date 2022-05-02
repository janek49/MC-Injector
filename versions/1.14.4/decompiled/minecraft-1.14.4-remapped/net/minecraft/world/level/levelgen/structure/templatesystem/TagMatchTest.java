package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class TagMatchTest extends RuleTest {
   private final Tag tag;

   public TagMatchTest(Tag tag) {
      this.tag = tag;
   }

   public TagMatchTest(Dynamic dynamic) {
      this(BlockTags.getAllTags().getTag(new ResourceLocation(dynamic.get("tag").asString(""))));
   }

   public boolean test(BlockState blockState, Random random) {
      return blockState.is(this.tag);
   }

   protected RuleTestType getType() {
      return RuleTestType.TAG_TEST;
   }

   protected Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("tag"), dynamicOps.createString(this.tag.getId().toString()))));
   }
}
