package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class DiodeBlock extends HorizontalDirectionalBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   protected DiodeBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return canSupportRigidBlock(levelReader, blockPos.below());
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!this.isLocked(level, blockPos, blockState)) {
         boolean var5 = ((Boolean)blockState.getValue(POWERED)).booleanValue();
         boolean var6 = this.shouldTurnOn(level, blockPos, blockState);
         if(var5 && !var6) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(false)), 2);
         } else if(!var5) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(true)), 2);
            if(!var6) {
               level.getBlockTicks().scheduleTick(blockPos, this, this.getDelay(blockState), TickPriority.HIGH);
            }
         }

      }
   }

   public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return blockState.getSignal(blockGetter, blockPos, direction);
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return !((Boolean)blockState.getValue(POWERED)).booleanValue()?0:(blockState.getValue(FACING) == direction?this.getOutputSignal(blockGetter, blockPos, blockState):0);
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(blockState.canSurvive(level, var3)) {
         this.checkTickOnNeighbor(level, var3, blockState);
      } else {
         BlockEntity var7 = this.isEntityBlock()?level.getBlockEntity(var3):null;
         dropResources(blockState, level, var3, var7);
         level.removeBlock(var3, false);

         for(Direction var11 : Direction.values()) {
            level.updateNeighborsAt(var3.relative(var11), this);
         }

      }
   }

   protected void checkTickOnNeighbor(Level level, BlockPos blockPos, BlockState blockState) {
      if(!this.isLocked(level, blockPos, blockState)) {
         boolean var4 = ((Boolean)blockState.getValue(POWERED)).booleanValue();
         boolean var5 = this.shouldTurnOn(level, blockPos, blockState);
         if(var4 != var5 && !level.getBlockTicks().willTickThisTick(blockPos, this)) {
            TickPriority var6 = TickPriority.HIGH;
            if(this.shouldPrioritize(level, blockPos, blockState)) {
               var6 = TickPriority.EXTREMELY_HIGH;
            } else if(var4) {
               var6 = TickPriority.VERY_HIGH;
            }

            level.getBlockTicks().scheduleTick(blockPos, this, this.getDelay(blockState), var6);
         }

      }
   }

   public boolean isLocked(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
      return false;
   }

   protected boolean shouldTurnOn(Level level, BlockPos blockPos, BlockState blockState) {
      return this.getInputSignal(level, blockPos, blockState) > 0;
   }

   protected int getInputSignal(Level level, BlockPos blockPos, BlockState blockState) {
      Direction var4 = (Direction)blockState.getValue(FACING);
      BlockPos var5 = blockPos.relative(var4);
      int var6 = level.getSignal(var5, var4);
      if(var6 >= 15) {
         return var6;
      } else {
         BlockState var7 = level.getBlockState(var5);
         return Math.max(var6, var7.getBlock() == Blocks.REDSTONE_WIRE?((Integer)var7.getValue(RedStoneWireBlock.POWER)).intValue():0);
      }
   }

   protected int getAlternateSignal(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
      Direction var4 = (Direction)blockState.getValue(FACING);
      Direction var5 = var4.getClockWise();
      Direction var6 = var4.getCounterClockWise();
      return Math.max(this.getAlternateSignalAt(levelReader, blockPos.relative(var5), var5), this.getAlternateSignalAt(levelReader, blockPos.relative(var6), var6));
   }

   protected int getAlternateSignalAt(LevelReader levelReader, BlockPos blockPos, Direction direction) {
      BlockState var4 = levelReader.getBlockState(blockPos);
      Block var5 = var4.getBlock();
      return this.isAlternateInput(var4)?(var5 == Blocks.REDSTONE_BLOCK?15:(var5 == Blocks.REDSTONE_WIRE?((Integer)var4.getValue(RedStoneWireBlock.POWER)).intValue():levelReader.getDirectSignal(blockPos, direction))):0;
   }

   public boolean isSignalSource(BlockState blockState) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      if(this.shouldTurnOn(level, blockPos, blockState)) {
         level.getBlockTicks().scheduleTick(blockPos, this, 1);
      }

   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      this.updateNeighborsInFront(level, blockPos, var1);
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(!var5 && var1.getBlock() != var4.getBlock()) {
         super.onRemove(var1, level, blockPos, var4, var5);
         this.updateNeighborsInFront(level, blockPos, var1);
      }
   }

   protected void updateNeighborsInFront(Level level, BlockPos blockPos, BlockState blockState) {
      Direction var4 = (Direction)blockState.getValue(FACING);
      BlockPos var5 = blockPos.relative(var4.getOpposite());
      level.neighborChanged(var5, this, blockPos);
      level.updateNeighborsAtExceptFromFacing(var5, this, var4);
   }

   protected boolean isAlternateInput(BlockState blockState) {
      return blockState.isSignalSource();
   }

   protected int getOutputSignal(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return 15;
   }

   public static boolean isDiode(BlockState blockState) {
      return blockState.getBlock() instanceof DiodeBlock;
   }

   public boolean shouldPrioritize(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      Direction var4 = ((Direction)blockState.getValue(FACING)).getOpposite();
      BlockState var5 = blockGetter.getBlockState(blockPos.relative(var4));
      return isDiode(var5) && var5.getValue(FACING) != var4;
   }

   protected abstract int getDelay(BlockState var1);

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public boolean canOcclude(BlockState blockState) {
      return true;
   }
}
