package net.minecraft.world.level.levelgen.feature;

import java.util.function.Function;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.BlockPileFeature;

public class PumpkinBlockPileFeature extends BlockPileFeature {
   public PumpkinBlockPileFeature(Function function) {
      super(function);
   }

   protected BlockState getBlockState(LevelAccessor levelAccessor) {
      return levelAccessor.getRandom().nextFloat() < 0.95F?Blocks.PUMPKIN.defaultBlockState():Blocks.JACK_O_LANTERN.defaultBlockState();
   }
}
