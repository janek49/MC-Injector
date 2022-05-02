package net.minecraft.world.level.block.grower;

import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.BigTreeFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.TreeFeature;

public class OakTreeGrower extends AbstractTreeGrower {
   @Nullable
   protected AbstractTreeFeature getFeature(Random random) {
      return (AbstractTreeFeature)(random.nextInt(10) == 0?new BigTreeFeature(NoneFeatureConfiguration::deserialize, true):new TreeFeature(NoneFeatureConfiguration::deserialize, true));
   }
}
