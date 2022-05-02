package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class ChorusPlantBlock extends PipeBlock {
   protected ChorusPlantBlock(Block.Properties block$Properties) {
      super(0.3125F, block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, Boolean.valueOf(false))).setValue(EAST, Boolean.valueOf(false))).setValue(SOUTH, Boolean.valueOf(false))).setValue(WEST, Boolean.valueOf(false))).setValue(UP, Boolean.valueOf(false))).setValue(DOWN, Boolean.valueOf(false)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return this.getStateForPlacement(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos());
   }

   public BlockState getStateForPlacement(BlockGetter blockGetter, BlockPos blockPos) {
      Block var3 = blockGetter.getBlockState(blockPos.below()).getBlock();
      Block var4 = blockGetter.getBlockState(blockPos.above()).getBlock();
      Block var5 = blockGetter.getBlockState(blockPos.north()).getBlock();
      Block var6 = blockGetter.getBlockState(blockPos.east()).getBlock();
      Block var7 = blockGetter.getBlockState(blockPos.south()).getBlock();
      Block var8 = blockGetter.getBlockState(blockPos.west()).getBlock();
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(DOWN, Boolean.valueOf(var3 == this || var3 == Blocks.CHORUS_FLOWER || var3 == Blocks.END_STONE))).setValue(UP, Boolean.valueOf(var4 == this || var4 == Blocks.CHORUS_FLOWER))).setValue(NORTH, Boolean.valueOf(var5 == this || var5 == Blocks.CHORUS_FLOWER))).setValue(EAST, Boolean.valueOf(var6 == this || var6 == Blocks.CHORUS_FLOWER))).setValue(SOUTH, Boolean.valueOf(var7 == this || var7 == Blocks.CHORUS_FLOWER))).setValue(WEST, Boolean.valueOf(var8 == this || var8 == Blocks.CHORUS_FLOWER));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(!var1.canSurvive(levelAccessor, var5)) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, 1);
         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      } else {
         Block var7 = var3.getBlock();
         boolean var8 = var7 == this || var7 == Blocks.CHORUS_FLOWER || direction == Direction.DOWN && var7 == Blocks.END_STONE;
         return (BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(direction), Boolean.valueOf(var8));
      }
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!blockState.canSurvive(level, blockPos)) {
         level.destroyBlock(blockPos, true);
      }

   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockState blockState = levelReader.getBlockState(blockPos.below());
      boolean var5 = !levelReader.getBlockState(blockPos.above()).isAir() && !blockState.isAir();

      for(Direction var7 : Direction.Plane.HORIZONTAL) {
         BlockPos var8 = blockPos.relative(var7);
         Block var9 = levelReader.getBlockState(var8).getBlock();
         if(var9 == this) {
            if(var5) {
               return false;
            }

            Block var10 = levelReader.getBlockState(var8.below()).getBlock();
            if(var10 == this || var10 == Blocks.END_STONE) {
               return true;
            }
         }
      }

      Block var6 = blockState.getBlock();
      return var6 == this || var6 == Blocks.END_STONE;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{NORTH, EAST, SOUTH, WEST, UP, DOWN});
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
