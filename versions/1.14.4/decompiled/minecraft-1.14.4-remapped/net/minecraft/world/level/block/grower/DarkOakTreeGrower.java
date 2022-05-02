package net.minecraft.world.level.block.grower;

import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.DarkOakFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class DarkOakTreeGrower extends AbstractMegaTreeGrower {
   @Nullable
   protected AbstractTreeFeature getFeature(Random random) {
      return null;
   }

   @Nullable
   protected AbstractTreeFeature getMegaFeature(Random random) {
      return new DarkOakFeature(NoneFeatureConfiguration::deserialize, true);
   }
}
