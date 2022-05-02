package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndRodBlock extends DirectionalBlock {
   protected static final VoxelShape Y_AXIS_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

   protected EndRodBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return (BlockState)var1.setValue(FACING, mirror.mirror((Direction)var1.getValue(FACING)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      switch(((Direction)blockState.getValue(FACING)).getAxis()) {
      case X:
      default:
         return X_AXIS_AABB;
      case Z:
         return Z_AXIS_AABB;
      case Y:
         return Y_AXIS_AABB;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      Direction var2 = blockPlaceContext.getClickedFace();
      BlockState var3 = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos().relative(var2.getOpposite()));
      return var3.getBlock() == this && var3.getValue(FACING) == var2?(BlockState)this.defaultBlockState().setValue(FACING, var2.getOpposite()):(BlockState)this.defaultBlockState().setValue(FACING, var2);
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      Direction var5 = (Direction)blockState.getValue(FACING);
      double var6 = (double)blockPos.getX() + 0.55D - (double)(random.nextFloat() * 0.1F);
      double var8 = (double)blockPos.getY() + 0.55D - (double)(random.nextFloat() * 0.1F);
      double var10 = (double)blockPos.getZ() + 0.55D - (double)(random.nextFloat() * 0.1F);
      double var12 = (double)(0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F);
      if(random.nextInt(5) == 0) {
         level.addParticle(ParticleTypes.END_ROD, var6 + (double)var5.getStepX() * var12, var8 + (double)var5.getStepY() * var12, var10 + (double)var5.getStepZ() * var12, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D);
      }

   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING});
   }

   public PushReaction getPistonPushReaction(BlockState blockState) {
      return PushReaction.NORMAL;
   }
}
