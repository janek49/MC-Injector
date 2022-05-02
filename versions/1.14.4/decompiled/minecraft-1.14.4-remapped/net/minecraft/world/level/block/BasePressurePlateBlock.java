package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BasePressurePlateBlock extends Block {
   protected static final VoxelShape PRESSED_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
   protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
   protected static final AABB TOUCH_AABB = new AABB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

   protected BasePressurePlateBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return this.getSignalForState(blockState) > 0?PRESSED_AABB:AABB;
   }

   public int getTickDelay(LevelReader levelReader) {
      return 20;
   }

   public boolean isPossibleToRespawnInThis() {
      return true;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction == Direction.DOWN && !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      return canSupportRigidBlock(levelReader, blockPos) || canSupportCenter(levelReader, blockPos, Direction.UP);
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!level.isClientSide) {
         int var5 = this.getSignalForState(blockState);
         if(var5 > 0) {
            this.checkPressed(level, blockPos, blockState, var5);
         }

      }
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if(!level.isClientSide) {
         int var5 = this.getSignalForState(blockState);
         if(var5 == 0) {
            this.checkPressed(level, blockPos, blockState, var5);
         }

      }
   }

   protected void checkPressed(Level level, BlockPos blockPos, BlockState blockState, int var4) {
      int var5 = this.getSignalStrength(level, blockPos);
      boolean var6 = var4 > 0;
      boolean var7 = var5 > 0;
      if(var4 != var5) {
         BlockState var8 = this.setSignalForState(blockState, var5);
         level.setBlock(blockPos, var8, 2);
         this.updateNeighbours(level, blockPos);
         level.setBlocksDirty(blockPos, blockState, var8);
      }

      if(!var7 && var6) {
         this.playOffSound(level, blockPos);
      } else if(var7 && !var6) {
         this.playOnSound(level, blockPos);
      }

      if(var7) {
         level.getBlockTicks().scheduleTick(new BlockPos(blockPos), this, this.getTickDelay(level));
      }

   }

   protected abstract void playOnSound(LevelAccessor var1, BlockPos var2);

   protected abstract void playOffSound(LevelAccessor var1, BlockPos var2);

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(!var5 && var1.getBlock() != var4.getBlock()) {
         if(this.getSignalForState(var1) > 0) {
            this.updateNeighbours(level, blockPos);
         }

         super.onRemove(var1, level, blockPos, var4, var5);
      }
   }

   protected void updateNeighbours(Level level, BlockPos blockPos) {
      level.updateNeighborsAt(blockPos, this);
      level.updateNeighborsAt(blockPos.below(), this);
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return this.getSignalForState(blockState);
   }

   public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return direction == Direction.UP?this.getSignalForState(blockState):0;
   }

   public boolean isSignalSource(BlockState blockState) {
      return true;
   }

   public PushReaction getPistonPushReaction(BlockState blockState) {
      return PushReaction.DESTROY;
   }

   protected abstract int getSignalStrength(Level var1, BlockPos var2);

   protected abstract int getSignalForState(BlockState var1);

   protected abstract BlockState setSignalForState(BlockState var1, int var2);
}
