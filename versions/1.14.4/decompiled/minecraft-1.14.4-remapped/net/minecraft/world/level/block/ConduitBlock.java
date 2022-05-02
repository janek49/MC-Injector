package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConduitBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

   public ConduitBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, Boolean.valueOf(true)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{WATERLOGGED});
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new ConduitBlockEntity();
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
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

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
      if(itemStack.hasCustomHoverName()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof BeaconBlockEntity) {
            ((BeaconBlockEntity)var6).setCustomName(itemStack.getHoverName());
         }
      }

   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      FluidState var2 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(var2.is(FluidTags.WATER) && var2.getAmount() == 8));
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
