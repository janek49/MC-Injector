package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SnowLayerBlock extends Block {
   public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
   protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[]{Shapes.empty(), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

   protected SnowLayerBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LAYERS, Integer.valueOf(1)));
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      switch(pathComputationType) {
      case LAND:
         return ((Integer)blockState.getValue(LAYERS)).intValue() < 5;
      case WATER:
         return false;
      case AIR:
         return false;
      default:
         return false;
      }
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE_BY_LAYER[((Integer)blockState.getValue(LAYERS)).intValue()];
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE_BY_LAYER[((Integer)blockState.getValue(LAYERS)).intValue() - 1];
   }

   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return true;
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockState blockState = levelReader.getBlockState(blockPos.below());
      Block var5 = blockState.getBlock();
      return var5 != Blocks.ICE && var5 != Blocks.PACKED_ICE && var5 != Blocks.BARRIER?Block.isFaceFull(blockState.getCollisionShape(levelReader, blockPos.below()), Direction.UP) || var5 == this && ((Integer)blockState.getValue(LAYERS)).intValue() == 8:false;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(level.getBrightness(LightLayer.BLOCK, blockPos) > 11) {
         dropResources(blockState, level, blockPos);
         level.removeBlock(blockPos, false);
      }

   }

   public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
      int var3 = ((Integer)blockState.getValue(LAYERS)).intValue();
      return blockPlaceContext.getItemInHand().getItem() == this.asItem() && var3 < 8?(blockPlaceContext.replacingClickedOnBlock()?blockPlaceContext.getClickedFace() == Direction.UP:true):var3 == 1;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
      if(blockState.getBlock() == this) {
         int var3 = ((Integer)blockState.getValue(LAYERS)).intValue();
         return (BlockState)blockState.setValue(LAYERS, Integer.valueOf(Math.min(8, var3 + 1)));
      } else {
         return super.getStateForPlacement(blockPlaceContext);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{LAYERS});
   }
}
