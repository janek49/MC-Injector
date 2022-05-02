package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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

public class LeverBlock extends FaceAttachedHorizontalDirectionalBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
   protected static final VoxelShape WEST_AABB = Block.box(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
   protected static final VoxelShape UP_AABB_Z = Block.box(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
   protected static final VoxelShape UP_AABB_X = Block.box(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
   protected static final VoxelShape DOWN_AABB_Z = Block.box(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
   protected static final VoxelShape DOWN_AABB_X = Block.box(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);

   protected LeverBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, Boolean.valueOf(false))).setValue(FACE, AttachFace.WALL));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      switch((AttachFace)blockState.getValue(FACE)) {
      case FLOOR:
         switch(((Direction)blockState.getValue(FACING)).getAxis()) {
         case X:
            return UP_AABB_X;
         case Z:
         default:
            return UP_AABB_Z;
         }
      case WALL:
         switch((Direction)blockState.getValue(FACING)) {
         case EAST:
            return EAST_AABB;
         case WEST:
            return WEST_AABB;
         case SOUTH:
            return SOUTH_AABB;
         case NORTH:
         default:
            return NORTH_AABB;
         }
      case CEILING:
      default:
         switch(((Direction)blockState.getValue(FACING)).getAxis()) {
         case X:
            return DOWN_AABB_X;
         case Z:
         default:
            return DOWN_AABB_Z;
         }
      }
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      blockState = (BlockState)blockState.cycle(POWERED);
      boolean var7 = ((Boolean)blockState.getValue(POWERED)).booleanValue();
      if(level.isClientSide) {
         if(var7) {
            makeParticle(blockState, level, blockPos, 1.0F);
         }

         return true;
      } else {
         level.setBlock(blockPos, blockState, 3);
         float var8 = var7?0.6F:0.5F;
         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, var8);
         this.updateNeighbours(blockState, level, blockPos);
         return true;
      }
   }

   private static void makeParticle(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, float var3) {
      Direction var4 = ((Direction)blockState.getValue(FACING)).getOpposite();
      Direction var5 = getConnectedDirection(blockState).getOpposite();
      double var6 = (double)blockPos.getX() + 0.5D + 0.1D * (double)var4.getStepX() + 0.2D * (double)var5.getStepX();
      double var8 = (double)blockPos.getY() + 0.5D + 0.1D * (double)var4.getStepY() + 0.2D * (double)var5.getStepY();
      double var10 = (double)blockPos.getZ() + 0.5D + 0.1D * (double)var4.getStepZ() + 0.2D * (double)var5.getStepZ();
      levelAccessor.addParticle(new DustParticleOptions(1.0F, 0.0F, 0.0F, var3), var6, var8, var10, 0.0D, 0.0D, 0.0D);
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(((Boolean)blockState.getValue(POWERED)).booleanValue() && random.nextFloat() < 0.25F) {
         makeParticle(blockState, level, blockPos, 0.5F);
      }

   }

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

   private void updateNeighbours(BlockState blockState, Level level, BlockPos blockPos) {
      level.updateNeighborsAt(blockPos, this);
      level.updateNeighborsAt(blockPos.relative(getConnectedDirection(blockState).getOpposite()), this);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACE, FACING, POWERED});
   }
}
