package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrassPathBlock extends Block {
   protected static final VoxelShape SHAPE = FarmBlock.SHAPE;

   protected GrassPathBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return !this.defaultBlockState().canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())?Block.pushEntitiesUp(this.defaultBlockState(), Blocks.DIRT.defaultBlockState(), blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos()):super.getStateForPlacement(blockPlaceContext);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(direction == Direction.UP && !var1.canSurvive(levelAccessor, var5)) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, 1);
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      FarmBlock.turnToDirt(blockState, level, blockPos);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockState blockState = levelReader.getBlockState(blockPos.above());
      return !blockState.getMaterial().isSolid() || blockState.getBlock() instanceof FenceGateBlock;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
