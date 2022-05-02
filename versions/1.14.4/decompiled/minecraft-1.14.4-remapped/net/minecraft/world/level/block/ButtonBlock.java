package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class ButtonBlock extends FaceAttachedHorizontalDirectionalBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   protected static final VoxelShape CEILING_AABB_X = Block.box(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
   protected static final VoxelShape CEILING_AABB_Z = Block.box(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
   protected static final VoxelShape FLOOR_AABB_X = Block.box(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
   protected static final VoxelShape FLOOR_AABB_Z = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
   protected static final VoxelShape WEST_AABB = Block.box(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
   protected static final VoxelShape PRESSED_CEILING_AABB_X = Block.box(6.0D, 15.0D, 5.0D, 10.0D, 16.0D, 11.0D);
   protected static final VoxelShape PRESSED_CEILING_AABB_Z = Block.box(5.0D, 15.0D, 6.0D, 11.0D, 16.0D, 10.0D);
   protected static final VoxelShape PRESSED_FLOOR_AABB_X = Block.box(6.0D, 0.0D, 5.0D, 10.0D, 1.0D, 11.0D);
   protected static final VoxelShape PRESSED_FLOOR_AABB_Z = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 1.0D, 10.0D);
   protected static final VoxelShape PRESSED_NORTH_AABB = Block.box(5.0D, 6.0D, 15.0D, 11.0D, 10.0D, 16.0D);
   protected static final VoxelShape PRESSED_SOUTH_AABB = Block.box(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 1.0D);
   protected static final VoxelShape PRESSED_WEST_AABB = Block.box(15.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
   protected static final VoxelShape PRESSED_EAST_AABB = Block.box(0.0D, 6.0D, 5.0D, 1.0D, 10.0D, 11.0D);
   private final boolean sensitive;

   protected ButtonBlock(boolean sensitive, Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, Boolean.valueOf(false))).setValue(FACE, AttachFace.WALL));
      this.sensitive = sensitive;
   }

   public int getTickDelay(LevelReader levelReader) {
      return this.sensitive?30:20;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      Direction var5 = (Direction)blockState.getValue(FACING);
      boolean var6 = ((Boolean)blockState.getValue(POWERED)).booleanValue();
      switch((AttachFace)blockState.getValue(FACE)) {
      case FLOOR:
         if(var5.getAxis() == Direction.Axis.X) {
            return var6?PRESSED_FLOOR_AABB_X:FLOOR_AABB_X;
         }

         return var6?PRESSED_FLOOR_AABB_Z:FLOOR_AABB_Z;
      case WALL:
         switch(var5) {
         case EAST:
            return var6?PRESSED_EAST_AABB:EAST_AABB;
         case WEST:
            return var6?PRESSED_WEST_AABB:WEST_AABB;
         case SOUTH:
            return var6?PRESSED_SOUTH_AABB:SOUTH_AABB;
         case NORTH:
         default:
            return var6?PRESSED_NORTH_AABB:NORTH_AABB;
         }
      case CEILING:
      default:
         return var5.getAxis() == Direction.Axis.X?(var6?PRESSED_CEILING_AABB_X:CEILING_AABB_X):(var6?PRESSED_CEILING_AABB_Z:CEILING_AABB_Z);
      }
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(((Boolean)blockState.getValue(POWERED)).booleanValue()) {
         return true;
      } else {
         level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(true)), 3);
         this.playSound(player, level, blockPos, true);
         this.updateNeighbours(blockState, level, blockPos);
         level.getBlockTicks().scheduleTick(blockPos, this, this.getTickDelay(level));
         return true;
      }
   }

   protected void playSound(@Nullable Player player, LevelAccessor levelAccessor, BlockPos blockPos, boolean var4) {
      levelAccessor.playSound(var4?player:null, blockPos, this.getSound(var4), SoundSource.BLOCKS, 0.3F, var4?0.6F:0.5F);
   }

   protected abstract SoundEvent getSound(boolean var1);

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(!var5 && var1.getBlock() != var4.getBlock()) {
         if(((Boolean)var1.getValue(POWERED)).booleanValue()) {
            this.updateNeighbours(var1, level, blockPos);
         }

         super.onRemove(var1, level, blockPos, var4, var5);
      }
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return ((Boolean)blockState.getValue(POWERED)).booleanValue()?15:0;
   }

   public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return ((Boolean)blockState.getValue(POWERED)).booleanValue() && getConnectedDirection(blockState) == direction?15:0;
   }

   public boolean isSignalSource(BlockState blockState) {
      return true;
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!level.isClientSide && ((Boolean)blockState.getValue(POWERED)).booleanValue()) {
         if(this.sensitive) {
            this.checkPressed(blockState, level, blockPos);
         } else {
            level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(false)), 3);
            this.updateNeighbours(blockState, level, blockPos);
            this.playSound((Player)null, level, blockPos, false);
         }

      }
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if(!level.isClientSide && this.sensitive && !((Boolean)blockState.getValue(POWERED)).booleanValue()) {
         this.checkPressed(blockState, level, blockPos);
      }
   }

   private void checkPressed(BlockState blockState, Level level, BlockPos blockPos) {
      List<? extends Entity> var4 = level.getEntitiesOfClass(AbstractArrow.class, blockState.getShape(level, blockPos).bounds().move(blockPos));
      boolean var5 = !var4.isEmpty();
      boolean var6 = ((Boolean)blockState.getValue(POWERED)).booleanValue();
      if(var5 != var6) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(var5)), 3);
         this.updateNeighbours(blockState, level, blockPos);
         this.playSound((Player)null, level, blockPos, var5);
      }

      if(var5) {
         level.getBlockTicks().scheduleTick(new BlockPos(blockPos), this, this.getTickDelay(level));
      }

   }

   private void updateNeighbours(BlockState blockState, Level level, BlockPos blockPos) {
      level.updateNeighborsAt(blockPos, this);
      level.updateNeighborsAt(blockPos.relative(getConnectedDirection(blockState).getOpposite()), this);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, POWERED, FACE});
   }
}
