package net.minecraft.world.level.block.grower;

import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.BirchFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class BirchTreeGrower extends AbstractTreeGrower {
   @Nullable
   protected AbstractTreeFeature getFeature(Random random) {
      return new BirchFeature(NoneFeatureConfiguration::deserialize, true, false);
   }
}
