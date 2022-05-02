package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrapDoorBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty HALF = BlockStateProperties.HALF;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_OPEN_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
   protected static final VoxelShape TOP_AABB = Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);

   protected TrapDoorBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, Boolean.valueOf(false))).setValue(HALF, Half.BOTTOM)).setValue(POWERED, Boolean.valueOf(false))).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      if(!((Boolean)blockState.getValue(OPEN)).booleanValue()) {
         return blockState.getValue(HALF) == Half.TOP?TOP_AABB:BOTTOM_AABB;
      } else {
         switch((Direction)blockState.getValue(FACING)) {
         case NORTH:
         default:
            return NORTH_OPEN_AABB;
         case SOUTH:
            return SOUTH_OPEN_AABB;
         case WEST:
            return WEST_OPEN_AABB;
         case EAST:
            return EAST_OPEN_AABB;
         }
      }
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      switch(pathComputationType) {
      case LAND:
         return ((Boolean)blockState.getValue(OPEN)).booleanValue();
      case WATER:
         return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue();
      case AIR:
         return ((Boolean)blockState.getValue(OPEN)).booleanValue();
      default:
         return false;
      }
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(this.material == Material.METAL) {
         return false;
      } else {
         blockState = (BlockState)blockState.cycle(OPEN);
         level.setBlock(blockPos, blockState, 2);
         if(((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()) {
            level.getLiquidTicks().scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
         }

         this.playSound(player, level, blockPos, ((Boolean)blockState.getValue(OPEN)).booleanValue());
         return true;
      }
   }

   protected void playSound(@Nullable Player player, Level level, BlockPos blockPos, boolean var4) {
      if(var4) {
         int var5 = this.material == Material.METAL?1037:1007;
         level.levelEvent(player, var5, blockPos, 0);
      } else {
         int var5 = this.material == Material.METAL?1036:1013;
         level.levelEvent(player, var5, blockPos, 0);
      }

   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(!level.isClientSide) {
         boolean var7 = level.hasNeighborSignal(var3);
         if(var7 != ((Boolean)blockState.getValue(POWERED)).booleanValue()) {
            if(((Boolean)blockState.getValue(OPEN)).booleanValue() != var7) {
               blockState = (BlockState)blockState.setValue(OPEN, Boolean.valueOf(var7));
               this.playSound((Player)null, level, var3, var7);
            }

            level.setBlock(var3, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(var7)), 2);
            if(((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()) {
               level.getLiquidTicks().scheduleTick(var3, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }
         }

      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = this.defaultBlockState();
      FluidState var3 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
      Direction var4 = blockPlaceContext.getClickedFace();
      if(!blockPlaceContext.replacingClickedOnBlock() && var4.getAxis().isHorizontal()) {
         blockState = (BlockState)((BlockState)blockState.setValue(FACING, var4)).setValue(HALF, blockPlaceContext.getClickLocation().y - (double)blockPlaceContext.getClickedPos().getY() > 0.5D?Half.TOP:Half.BOTTOM);
      } else {
         blockState = (BlockState)((BlockState)blockState.setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite())).setValue(HALF, var4 == Direction.UP?Half.BOTTOM:Half.TOP);
      }

      if(blockPlaceContext.getLevel().hasNeighborSignal(blockPlaceContext.getClickedPos())) {
         blockState = (BlockState)((BlockState)blockState.setValue(OPEN, Boolean.valueOf(true))).setValue(POWERED, Boolean.valueOf(true));
      }

      return (BlockState)blockState.setValue(WATERLOGGED, Boolean.valueOf(var3.getType() == Fluids.WATER));
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, OPEN, HALF, POWERED, WATERLOGGED});
   }

   public FluidState getFluidState(BlockState blockState) {
      return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()?Fluids.WATER.getSource(false):super.getFluidState(blockState);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean isValidSpawn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType entityType) {
      return false;
   }
}
