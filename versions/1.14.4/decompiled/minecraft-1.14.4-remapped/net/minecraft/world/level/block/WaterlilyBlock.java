package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaterlilyBlock extends BushBlock {
   protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

   protected WaterlilyBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      super.entityInside(blockState, level, blockPos, entity);
      if(entity instanceof Boat) {
         level.destroyBlock(new BlockPos(blockPos), true);
      }

   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return AABB;
   }

   protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      FluidState var4 = blockGetter.getFluidState(blockPos);
      return var4.getType() == Fluids.WATER || blockState.getMaterial() == Material.ICE;
   }
}
