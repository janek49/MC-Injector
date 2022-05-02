package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HopperBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.FACING_HOPPER;
   public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
   private static final VoxelShape TOP = Block.box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape FUNNEL = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
   private static final VoxelShape CONVEX_BASE = Shapes.or(FUNNEL, TOP);
   private static final VoxelShape BASE = Shapes.join(CONVEX_BASE, Hopper.INSIDE, BooleanOp.ONLY_FIRST);
   private static final VoxelShape DOWN_SHAPE = Shapes.or(BASE, Block.box(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
   private static final VoxelShape EAST_SHAPE = Shapes.or(BASE, Block.box(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
   private static final VoxelShape NORTH_SHAPE = Shapes.or(BASE, Block.box(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
   private static final VoxelShape SOUTH_SHAPE = Shapes.or(BASE, Block.box(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
   private static final VoxelShape WEST_SHAPE = Shapes.or(BASE, Block.box(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
   private static final VoxelShape DOWN_INTERACTION_SHAPE = Hopper.INSIDE;
   private static final VoxelShape EAST_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
   private static final VoxelShape NORTH_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
   private static final VoxelShape SOUTH_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
   private static final VoxelShape WEST_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));

   public HopperBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.DOWN)).setValue(ENABLED, Boolean.valueOf(true)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      switch((Direction)blockState.getValue(FACING)) {
      case DOWN:
         return DOWN_SHAPE;
      case NORTH:
         return NORTH_SHAPE;
      case SOUTH:
         return SOUTH_SHAPE;
      case WEST:
         return WEST_SHAPE;
      case EAST:
         return EAST_SHAPE;
      default:
         return BASE;
      }
   }

   public VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      switch((Direction)blockState.getValue(FACING)) {
      case DOWN:
         return DOWN_INTERACTION_SHAPE;
      case NORTH:
         return NORTH_INTERACTION_SHAPE;
      case SOUTH:
         return SOUTH_INTERACTION_SHAPE;
      case WEST:
         return WEST_INTERACTION_SHAPE;
      case EAST:
         return EAST_INTERACTION_SHAPE;
      default:
         return Hopper.INSIDE;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      Direction var2 = blockPlaceContext.getClickedFace().getOpposite();
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var2.getAxis() == Direction.Axis.Y?Direction.DOWN:var2)).setValue(ENABLED, Boolean.valueOf(true));
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new HopperBlockEntity();
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      if(itemStack.hasCustomHoverName()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof HopperBlockEntity) {
            ((HopperBlockEntity)var6).setCustomName(itemStack.getHoverName());
         }
      }

   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var4.getBlock() != var1.getBlock()) {
         this.checkPoweredState(level, blockPos, var1);
      }
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(level.isClientSide) {
         return true;
      } else {
         BlockEntity var7 = level.getBlockEntity(blockPos);
         if(var7 instanceof HopperBlockEntity) {
            player.openMenu((HopperBlockEntity)var7);
            player.awardStat(Stats.INSPECT_HOPPER);
         }

         return true;
      }
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      this.checkPoweredState(level, var3, blockState);
   }

   private void checkPoweredState(Level level, BlockPos blockPos, BlockState blockState) {
      boolean var4 = !level.hasNeighborSignal(blockPos);
      if(var4 != ((Boolean)blockState.getValue(ENABLED)).booleanValue()) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(ENABLED, Boolean.valueOf(var4)), 4);
      }

   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof HopperBlockEntity) {
            Containers.dropContents(level, (BlockPos)blockPos, (Container)((HopperBlockEntity)var6));
            level.updateNeighbourForOutputSignal(blockPos, this);
         }

         super.onRemove(var1, level, blockPos, var4, var5);
      }
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos));
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT_MIPPED;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, ENABLED});
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      BlockEntity var5 = level.getBlockEntity(blockPos);
      if(var5 instanceof HopperBlockEntity) {
         ((HopperBlockEntity)var5).entityInside(entity);
      }

   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
