package net.minecraft.world.level.block;

import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndPortalFrameBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty HAS_EYE = BlockStateProperties.EYE;
   protected static final VoxelShape BASE_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);
   protected static final VoxelShape EYE_SHAPE = Block.box(4.0D, 13.0D, 4.0D, 12.0D, 16.0D, 12.0D);
   protected static final VoxelShape FULL_SHAPE = Shapes.or(BASE_SHAPE, EYE_SHAPE);
   private static BlockPattern portalShape;

   public EndPortalFrameBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(HAS_EYE, Boolean.valueOf(false)));
   }

   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return true;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return ((Boolean)blockState.getValue(HAS_EYE)).booleanValue()?FULL_SHAPE:BASE_SHAPE;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite())).setValue(HAS_EYE, Boolean.valueOf(false));
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      return ((Boolean)blockState.getValue(HAS_EYE)).booleanValue()?15:0;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, HAS_EYE});
   }

   public static BlockPattern getOrCreatePortalShape() {
      if(portalShape == null) {
         portalShape = BlockPatternBuilder.start().aisle(new String[]{"?vvv?", ">???<", ">???<", ">???<", "?^^^?"}).where('?', BlockInWorld.hasState(BlockStatePredicate.ANY)).where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).where(HAS_EYE, Predicates.equalTo(Boolean.valueOf(true))).where(FACING, Predicates.equalTo(Direction.SOUTH)))).where('>', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).where(HAS_EYE, Predicates.equalTo(Boolean.valueOf(true))).where(FACING, Predicates.equalTo(Direction.WEST)))).where('v', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).where(HAS_EYE, Predicates.equalTo(Boolean.valueOf(true))).where(FACING, Predicates.equalTo(Direction.NORTH)))).where('<', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).where(HAS_EYE, Predicates.equalTo(Boolean.valueOf(true))).where(FACING, Predicates.equalTo(Direction.EAST)))).build();
      }

      return portalShape;
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
