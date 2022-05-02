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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Lantern extends Block {
   public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
   protected static final VoxelShape AABB = Shapes.or(Block.box(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 11.0D), Block.box(6.0D, 7.0D, 6.0D, 10.0D, 9.0D, 10.0D));
   protected static final VoxelShape HANGING_AABB = Shapes.or(Block.box(5.0D, 1.0D, 5.0D, 11.0D, 8.0D, 11.0D), Block.box(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D));

   public Lantern(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HANGING, Boolean.valueOf(false)));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      for(Direction var5 : blockPlaceContext.getNearestLookingDirections()) {
         if(var5.getAxis() == Direction.Axis.Y) {
            BlockState var6 = (BlockState)this.defaultBlockState().setValue(HANGING, Boolean.valueOf(var5 == Direction.UP));
            if(var6.canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())) {
               return var6;
            }
         }
      }

      return null;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return ((Boolean)blockState.getValue(HANGING)).booleanValue()?HANGING_AABB:AABB;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{HANGING});
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      Direction var4 = getConnectedDirection(blockState).getOpposite();
      return Block.canSupportCenter(levelReader, blockPos.relative(var4), var4.getOpposite());
   }

   protected static Direction getConnectedDirection(BlockState blockState) {
      return ((Boolean)blockState.getValue(HANGING)).booleanValue()?Direction.DOWN:Direction.UP;
   }

   public PushReaction getPistonPushReaction(BlockState blockState) {
      return PushReaction.DESTROY;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return getConnectedDirection(var1).getOpposite() == direction && !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
