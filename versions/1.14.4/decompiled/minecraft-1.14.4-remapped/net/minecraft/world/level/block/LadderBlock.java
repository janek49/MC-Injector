package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LadderBlock extends Block implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);

   protected LadderBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      switch((Direction)blockState.getValue(FACING)) {
      case NORTH:
         return NORTH_AABB;
      case SOUTH:
         return SOUTH_AABB;
      case WEST:
         return WEST_AABB;
      case EAST:
      default:
         return EAST_AABB;
      }
   }

   private boolean canAttachTo(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      BlockState var4 = blockGetter.getBlockState(blockPos);
      return !var4.isSignalSource() && var4.isFaceSturdy(blockGetter, blockPos, direction);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      Direction var4 = (Direction)blockState.getValue(FACING);
      return this.canAttachTo(levelReader, blockPos.relative(var4.getOpposite()), var4);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(direction.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(levelAccessor, var5)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
            levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
         }

         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      if(!blockPlaceContext.replacingClickedOnBlock()) {
         BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos().relative(blockPlaceContext.getClickedFace().getOpposite()));
         if(blockState.getBlock() == this && blockState.getValue(FACING) == blockPlaceContext.getClickedFace()) {
            return null;
         }
      }

      BlockState blockState = this.defaultBlockState();
      LevelReader var3 = blockPlaceContext.getLevel();
      BlockPos var4 = blockPlaceContext.getClickedPos();
      FluidState var5 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());

      for(Direction var9 : blockPlaceContext.getNearestLookingDirections()) {
         if(var9.getAxis().isHorizontal()) {
            blockState = (BlockState)blockState.setValue(FACING, var9.getOpposite());
            if(blockState.canSurvive(var3, var4)) {
               return (BlockState)blockState.setValue(WATERLOGGED, Boolean.valueOf(var5.getType() == Fluids.WATER));
            }
         }
      }

      return null;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, WATERLOGGED});
   }

   public FluidState getFluidState(BlockState blockState) {
      return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()?Fluids.WATER.getSource(false):super.getFluidState(blockState);
   }
}
