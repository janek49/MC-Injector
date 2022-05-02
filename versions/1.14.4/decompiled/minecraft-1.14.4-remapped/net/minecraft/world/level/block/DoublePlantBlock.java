package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class DoublePlantBlock extends BushBlock {
   public static final EnumProperty HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

   public DoublePlantBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      DoubleBlockHalf var7 = (DoubleBlockHalf)var1.getValue(HALF);
      return direction.getAxis() != Direction.Axis.Y || var7 == DoubleBlockHalf.LOWER != (direction == Direction.UP) || var3.getBlock() == this && var3.getValue(HALF) != var7?(var7 == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6)):Blocks.AIR.defaultBlockState();
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockPos var2 = blockPlaceContext.getClickedPos();
      return var2.getY() < 255 && blockPlaceContext.getLevel().getBlockState(var2.above()).canBeReplaced(blockPlaceContext)?super.getStateForPlacement(blockPlaceContext):null;
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      level.setBlock(blockPos.above(), (BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), 3);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      if(blockState.getValue(HALF) != DoubleBlockHalf.UPPER) {
         return super.canSurvive(blockState, levelReader, blockPos);
      } else {
         BlockState blockState = levelReader.getBlockState(blockPos.below());
         return blockState.getBlock() == this && blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
      }
   }

   public void placeAt(LevelAccessor levelAccessor, BlockPos blockPos, int var3) {
      levelAccessor.setBlock(blockPos, (BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER), var3);
      levelAccessor.setBlock(blockPos.above(), (BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), var3);
   }

   public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
      super.playerDestroy(level, player, blockPos, Blocks.AIR.defaultBlockState(), blockEntity, itemStack);
   }

   public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      DoubleBlockHalf var5 = (DoubleBlockHalf)blockState.getValue(HALF);
      BlockPos var6 = var5 == DoubleBlockHalf.LOWER?blockPos.above():blockPos.below();
      BlockState var7 = level.getBlockState(var6);
      if(var7.getBlock() == this && var7.getValue(HALF) != var5) {
         level.setBlock(var6, Blocks.AIR.defaultBlockState(), 35);
         level.levelEvent(player, 2001, var6, Block.getId(var7));
         if(!level.isClientSide && !player.isCreative()) {
            dropResources(blockState, level, blockPos, (BlockEntity)null, player, player.getMainHandItem());
            dropResources(var7, level, var6, (BlockEntity)null, player, player.getMainHandItem());
         }
      }

      super.playerWillDestroy(level, blockPos, blockState, player);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{HALF});
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public long getSeed(BlockState blockState, BlockPos blockPos) {
      return Mth.getSeed(blockPos.getX(), blockPos.below(blockState.getValue(HALF) == DoubleBlockHalf.LOWER?0:1).getY(), blockPos.getZ());
   }
}
