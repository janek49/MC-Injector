package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class StandingSignBlock extends SignBlock {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;

   public StandingSignBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, Integer.valueOf(0))).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return levelReader.getBlockState(blockPos.below()).getMaterial().isSolid();
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      FluidState var2 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(ROTATION, Integer.valueOf(Mth.floor((double)((180.0F + blockPlaceContext.getRotation()) * 16.0F / 360.0F) + 0.5D) & 15))).setValue(WATERLOGGED, Boolean.valueOf(var2.getType() == Fluids.WATER));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction == Direction.DOWN && !this.canSurvive(var1, levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(ROTATION, Integer.valueOf(rotation.rotate(((Integer)var1.getValue(ROTATION)).intValue(), 16)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return (BlockState)var1.setValue(ROTATION, Integer.valueOf(mirror.mirror(((Integer)var1.getValue(ROTATION)).intValue(), 16)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{ROTATION, WATERLOGGED});
   }
}
