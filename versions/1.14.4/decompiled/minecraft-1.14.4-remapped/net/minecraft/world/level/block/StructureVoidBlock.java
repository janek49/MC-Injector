package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StructureVoidBlock extends Block {
   private static final VoxelShape SHAPE = Block.box(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

   protected StructureVoidBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.INVISIBLE;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return 1.0F;
   }

   public PushReaction getPistonPushReaction(BlockState blockState) {
      return PushReaction.DESTROY;
   }
}
