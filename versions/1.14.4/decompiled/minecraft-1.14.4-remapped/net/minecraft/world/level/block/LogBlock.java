package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;

public class LogBlock extends RotatedPillarBlock {
   private final MaterialColor woodMaterialColor;

   public LogBlock(MaterialColor woodMaterialColor, Block.Properties block$Properties) {
      super(block$Properties);
      this.woodMaterialColor = woodMaterialColor;
   }

   public MaterialColor getMapColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.getValue(AXIS) == Direction.Axis.Y?this.woodMaterialColor:this.materialColor;
   }
}
