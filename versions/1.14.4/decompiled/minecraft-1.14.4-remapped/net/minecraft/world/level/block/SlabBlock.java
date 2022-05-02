package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlabBlock extends Block implements SimpleWaterloggedBlock {
   public static final EnumProperty TYPE = BlockStateProperties.SLAB_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   protected static final VoxelShape TOP_AABB = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

   public SlabBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return blockState.getValue(TYPE) != SlabType.DOUBLE;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{TYPE, WATERLOGGED});
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      SlabType var5 = (SlabType)blockState.getValue(TYPE);
      switch(var5) {
      case DOUBLE:
         return Shapes.block();
      case TOP:
         return TOP_AABB;
      default:
         return BOTTOM_AABB;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockPos var2 = blockPlaceContext.getClickedPos();
      BlockState var3 = blockPlaceContext.getLevel().getBlockState(var2);
      if(var3.getBlock() == this) {
         return (BlockState)((BlockState)var3.setValue(TYPE, SlabType.DOUBLE)).setValue(WATERLOGGED, Boolean.valueOf(false));
      } else {
         FluidState var4 = blockPlaceContext.getLevel().getFluidState(var2);
         BlockState var5 = (BlockState)((BlockState)this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM)).setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
         Direction var6 = blockPlaceContext.getClickedFace();
         return var6 != Direction.DOWN && (var6 == Direction.UP || blockPlaceContext.getClickLocation().y - (double)var2.getY() <= 0.5D)?var5:(BlockState)var5.setValue(TYPE, SlabType.TOP);
      }
   }

   public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
      ItemStack var3 = blockPlaceContext.getItemInHand();
      SlabType var4 = (SlabType)blockState.getValue(TYPE);
      if(var4 != SlabType.DOUBLE && var3.getItem() == this.asItem()) {
         if(blockPlaceContext.replacingClickedOnBlock()) {
            boolean var5 = blockPlaceContext.getClickLocation().y - (double)blockPlaceContext.getClickedPos().getY() > 0.5D;
            Direction var6 = blockPlaceContext.getClickedFace();
            return var4 == SlabType.BOTTOM?var6 == Direction.UP || var5 && var6.getAxis().isHorizontal():var6 == Direction.DOWN || !var5 && var6.getAxis().isHorizontal();
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public FluidState getFluidState(BlockState blockState) {
      return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()?Fluids.WATER.getSource(false):super.getFluidState(blockState);
   }

   public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
      return blockState.getValue(TYPE) != SlabType.DOUBLE?super.placeLiquid(levelAccessor, blockPos, blockState, fluidState):false;
   }

   public boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
      return blockState.getValue(TYPE) != SlabType.DOUBLE?super.canPlaceLiquid(blockGetter, blockPos, blockState, fluid):false;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      switch(pathComputationType) {
      case LAND:
         return false;
      case WATER:
         return blockGetter.getFluidState(blockPos).is(FluidTags.WATER);
      case AIR:
         return false;
      default:
         return false;
      }
   }
}
