package net.minecraft.world.level.levelgen.feature;

import java.util.function.Function;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.BlockPileFeature;

public class SnowBlockPileFeature extends BlockPileFeature {
   public SnowBlockPileFeature(Function function) {
      super(function);
   }

   protected BlockState getBlockState(LevelAccessor levelAccessor) {
      return Blocks.SNOW_BLOCK.defaultBlockState();
   }
}
