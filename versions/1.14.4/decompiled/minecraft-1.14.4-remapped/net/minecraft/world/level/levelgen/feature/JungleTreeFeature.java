package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;

public class JungleTreeFeature extends TreeFeature {
   public JungleTreeFeature(Function function, boolean var2, int var3, BlockState var4, BlockState var5, boolean var6) {
      super(function, var2, var3, var4, var5, var6);
   }

   protected int getTreeHeight(Random random) {
      return this.baseHeight + random.nextInt(7);
   }
}
