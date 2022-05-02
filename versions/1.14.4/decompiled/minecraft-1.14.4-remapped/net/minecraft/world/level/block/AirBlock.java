package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AirBlock extends Block {
   protected AirBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.INVISIBLE;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return Shapes.empty();
   }

   public boolean isAir(BlockState blockState) {
      return true;
   }
}
