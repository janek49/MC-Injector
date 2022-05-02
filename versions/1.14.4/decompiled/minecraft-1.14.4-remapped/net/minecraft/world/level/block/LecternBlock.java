package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LecternBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty HAS_BOOK = BlockStateProperties.HAS_BOOK;
   public static final VoxelShape SHAPE_BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   public static final VoxelShape SHAPE_POST = Block.box(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
   public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE, SHAPE_POST);
   public static final VoxelShape SHAPE_TOP_PLATE = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
   public static final VoxelShape SHAPE_COLLISION = Shapes.or(SHAPE_COMMON, SHAPE_TOP_PLATE);
   public static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), new VoxelShape[]{Block.box(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.box(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), SHAPE_COMMON});
   public static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), new VoxelShape[]{Block.box(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.box(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), SHAPE_COMMON});
   public static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(15.0D, 10.0D, 0.0D, 10.666667D, 14.0D, 16.0D), new VoxelShape[]{Block.box(10.666667D, 12.0D, 0.0D, 6.333333D, 16.0D, 16.0D), Block.box(6.333333D, 14.0D, 0.0D, 2.0D, 18.0D, 16.0D), SHAPE_COMMON});
   public static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(0.0D, 10.0D, 15.0D, 16.0D, 14.0D, 10.666667D), new VoxelShape[]{Block.box(0.0D, 12.0D, 10.666667D, 16.0D, 16.0D, 6.333333D), Block.box(0.0D, 14.0D, 6.333333D, 16.0D, 18.0D, 2.0D), SHAPE_COMMON});

   protected LecternBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, Boolean.valueOf(false))).setValue(HAS_BOOK, Boolean.valueOf(false)));
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return SHAPE_COMMON;
   }

   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE_COLLISION;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      switch((Direction)blockState.getValue(FACING)) {
      case NORTH:
         return SHAPE_NORTH;
      case SOUTH:
         return SHAPE_SOUTH;
      case EAST:
         return SHAPE_EAST;
      case WEST:
         return SHAPE_WEST;
      default:
         return SHAPE_COMMON;
      }
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, POWERED, HAS_BOOK});
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new LecternBlockEntity();
   }

   public static boolean tryPlaceBook(Level level, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
      if(!((Boolean)blockState.getValue(HAS_BOOK)).booleanValue()) {
         if(!level.isClientSide) {
            placeBook(level, blockPos, blockState, itemStack);
         }

         return true;
      } else {
         return false;
      }
   }

   private static void placeBook(Level level, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
      BlockEntity var4 = level.getBlockEntity(blockPos);
      if(var4 instanceof LecternBlockEntity) {
         LecternBlockEntity var5 = (LecternBlockEntity)var4;
         var5.setBook(itemStack.split(1));
         resetBookState(level, blockPos, blockState, true);
         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   public static void resetBookState(Level level, BlockPos blockPos, BlockState blockState, boolean var3) {
      level.setBlock(blockPos, (BlockState)((BlockState)blockState.setValue(POWERED, Boolean.valueOf(false))).setValue(HAS_BOOK, Boolean.valueOf(var3)), 3);
      updateBelow(level, blockPos, blockState);
   }

   public static void signalPageChange(Level level, BlockPos blockPos, BlockState blockState) {
      changePowered(level, blockPos, blockState, true);
      level.getBlockTicks().scheduleTick(blockPos, blockState.getBlock(), 2);
      level.levelEvent(1043, blockPos, 0);
   }

   private static void changePowered(Level level, BlockPos blockPos, BlockState blockState, boolean var3) {
      level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(var3)), 3);
      updateBelow(level, blockPos, blockState);
   }

   private static void updateBelow(Level level, BlockPos blockPos, BlockState blockState) {
      level.updateNeighborsAt(blockPos.below(), blockState.getBlock());
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!level.isClientSide) {
         changePowered(level, blockPos, blockState, false);
      }
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         if(((Boolean)var1.getValue(HAS_BOOK)).booleanValue()) {
            this.popBook(var1, level, blockPos);
         }

         if(((Boolean)var1.getValue(POWERED)).booleanValue()) {
            level.updateNeighborsAt(blockPos.below(), this);
         }

         super.onRemove(var1, level, blockPos, var4, var5);
      }
   }

   private void popBook(BlockState blockState, Level level, BlockPos blockPos) {
      BlockEntity var4 = level.getBlockEntity(blockPos);
      if(var4 instanceof LecternBlockEntity) {
         LecternBlockEntity var5 = (LecternBlockEntity)var4;
         Direction var6 = (Direction)blockState.getValue(FACING);
         ItemStack var7 = var5.getBook().copy();
         float var8 = 0.25F * (float)var6.getStepX();
         float var9 = 0.25F * (float)var6.getStepZ();
         ItemEntity var10 = new ItemEntity(level, (double)blockPos.getX() + 0.5D + (double)var8, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5D + (double)var9, var7);
         var10.setDefaultPickUpDelay();
         level.addFreshEntity(var10);
         var5.clearContent();
      }

   }

   public boolean isSignalSource(BlockState blockState) {
      return true;
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return ((Boolean)blockState.getValue(POWERED)).booleanValue()?15:0;
   }

   public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return direction == Direction.UP && ((Boolean)blockState.getValue(POWERED)).booleanValue()?15:0;
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      if(((Boolean)blockState.getValue(HAS_BOOK)).booleanValue()) {
         BlockEntity var4 = level.getBlockEntity(blockPos);
         if(var4 instanceof LecternBlockEntity) {
            return ((LecternBlockEntity)var4).getRedstoneSignal();
         }
      }

      return 0;
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(((Boolean)blockState.getValue(HAS_BOOK)).booleanValue()) {
         if(!level.isClientSide) {
            this.openScreen(level, blockPos, player);
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
      return !((Boolean)blockState.getValue(HAS_BOOK)).booleanValue()?null:super.getMenuProvider(blockState, level, blockPos);
   }

   private void openScreen(Level level, BlockPos blockPos, Player player) {
      BlockEntity var4 = level.getBlockEntity(blockPos);
      if(var4 instanceof LecternBlockEntity) {
         player.openMenu((LecternBlockEntity)var4);
         player.awardStat(Stats.INTERACT_WITH_LECTERN);
      }

   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
