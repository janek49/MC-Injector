package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class FaceAttachedHorizontalDirectionalBlock extends HorizontalDirectionalBlock {
   public static final EnumProperty FACE = BlockStateProperties.ATTACH_FACE;

   protected FaceAttachedHorizontalDirectionalBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return canAttach(levelReader, blockPos, getConnectedDirection(blockState).getOpposite());
   }

   public static boolean canAttach(LevelReader levelReader, BlockPos blockPos, Direction direction) {
      BlockPos blockPos = blockPos.relative(direction);
      return levelReader.getBlockState(blockPos).isFaceSturdy(levelReader, blockPos, direction.getOpposite());
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      for(Direction var5 : blockPlaceContext.getNearestLookingDirections()) {
         BlockState var6;
         if(var5.getAxis() == Direction.Axis.Y) {
            var6 = (BlockState)((BlockState)this.defaultBlockState().setValue(FACE, var5 == Direction.UP?AttachFace.CEILING:AttachFace.FLOOR)).setValue(FACING, blockPlaceContext.getHorizontalDirection());
         } else {
            var6 = (BlockState)((BlockState)this.defaultBlockState().setValue(FACE, AttachFace.WALL)).setValue(FACING, var5.getOpposite());
         }

         if(var6.canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())) {
            return var6;
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return getConnectedDirection(var1).getOpposite() == direction && !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   protected static Direction getConnectedDirection(BlockState blockState) {
      switch((AttachFace)blockState.getValue(FACE)) {
      case CEILING:
         return Direction.DOWN;
      case FLOOR:
         return Direction.UP;
      default:
         return (Direction)blockState.getValue(FACING);
      }
   }
}
