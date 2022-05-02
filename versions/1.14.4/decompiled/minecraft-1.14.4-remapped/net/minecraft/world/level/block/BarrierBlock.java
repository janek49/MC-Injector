package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class BarrierBlock extends Block {
   protected BarrierBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return true;
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.INVISIBLE;
   }

   public boolean canOcclude(BlockState blockState) {
      return false;
   }

   public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return 1.0F;
   }

   public boolean isValidSpawn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType entityType) {
      return false;
   }
}
