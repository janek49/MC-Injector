package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class ObserverBlock extends DirectionalBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public ObserverBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.SOUTH)).setValue(POWERED, Boolean.valueOf(false)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, POWERED});
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(((Boolean)blockState.getValue(POWERED)).booleanValue()) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(false)), 2);
      } else {
         level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(true)), 2);
         level.getBlockTicks().scheduleTick(blockPos, this, 2);
      }

      this.updateNeighborsInFront(level, blockPos, blockState);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(var1.getValue(FACING) == direction && !((Boolean)var1.getValue(POWERED)).booleanValue()) {
         this.startSignal(levelAccessor, var5);
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   private void startSignal(LevelAccessor levelAccessor, BlockPos blockPos) {
      if(!levelAccessor.isClientSide() && !levelAccessor.getBlockTicks().hasScheduledTick(blockPos, this)) {
         levelAccessor.getBlockTicks().scheduleTick(blockPos, this, 2);
      }

   }

   protected void updateNeighborsInFront(Level level, BlockPos blockPos, BlockState blockState) {
      Direction var4 = (Direction)blockState.getValue(FACING);
      BlockPos var5 = blockPos.relative(var4.getOpposite());
      level.neighborChanged(var5, this, blockPos);
      level.updateNeighborsAtExceptFromFacing(var5, this, var4);
   }

   public boolean isSignalSource(BlockState blockState) {
      return true;
   }

   public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return blockState.getSignal(blockGetter, blockPos, direction);
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return ((Boolean)blockState.getValue(POWERED)).booleanValue() && blockState.getValue(FACING) == direction?15:0;
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         if(!level.isClientSide() && ((Boolean)var1.getValue(POWERED)).booleanValue() && !level.getBlockTicks().hasScheduledTick(blockPos, this)) {
            BlockState var6 = (BlockState)var1.setValue(POWERED, Boolean.valueOf(false));
            level.setBlock(blockPos, var6, 18);
            this.updateNeighborsInFront(level, blockPos, var6);
         }

      }
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         if(!level.isClientSide && ((Boolean)var1.getValue(POWERED)).booleanValue() && level.getBlockTicks().hasScheduledTick(blockPos, this)) {
            this.updateNeighborsInFront(level, blockPos, (BlockState)var1.setValue(POWERED, Boolean.valueOf(false)));
         }

      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getNearestLookingDirection().getOpposite().getOpposite());
   }
}
