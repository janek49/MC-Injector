package net.minecraft.world.level.block.grower;

import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.MegaPineTreeFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.SpruceFeature;

public class SpruceTreeGrower extends AbstractMegaTreeGrower {
   @Nullable
   protected AbstractTreeFeature getFeature(Random random) {
      return new SpruceFeature(NoneFeatureConfiguration::deserialize, true);
   }

   @Nullable
   protected AbstractTreeFeature getMegaFeature(Random random) {
      return new MegaPineTreeFeature(NoneFeatureConfiguration::deserialize, false, random.nextBoolean());
   }
}
