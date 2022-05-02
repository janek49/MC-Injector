package net.minecraft.world.level.levelgen.feature;

import java.util.function.Function;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.BlockPileFeature;

public class IceBlockPileFeature extends BlockPileFeature {
   public IceBlockPileFeature(Function function) {
      super(function);
   }

   protected BlockState getBlockState(LevelAccessor levelAccessor) {
      return levelAccessor.getRandom().nextInt(7) == 0?Blocks.BLUE_ICE.defaultBlockState():Blocks.PACKED_ICE.defaultBlockState();
   }
}
