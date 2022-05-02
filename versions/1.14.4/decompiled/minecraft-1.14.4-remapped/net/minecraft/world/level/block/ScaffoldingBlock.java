package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ScaffoldingBlock extends Block implements SimpleWaterloggedBlock {
   private static final VoxelShape STABLE_SHAPE;
   private static final VoxelShape UNSTABLE_SHAPE;
   private static final VoxelShape UNSTABLE_SHAPE_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   private static final VoxelShape BELOW_BLOCK = Shapes.block().move(0.0D, -1.0D, 0.0D);
   public static final IntegerProperty DISTANCE = BlockStateProperties.STABILITY_DISTANCE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

   protected ScaffoldingBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, Integer.valueOf(7))).setValue(WATERLOGGED, Boolean.valueOf(false))).setValue(BOTTOM, Boolean.valueOf(false)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{DISTANCE, WATERLOGGED, BOTTOM});
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return !collisionContext.isHoldingItem(blockState.getBlock().asItem())?(((Boolean)blockState.getValue(BOTTOM)).booleanValue()?UNSTABLE_SHAPE:STABLE_SHAPE):Shapes.block();
   }

   public VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return Shapes.block();
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
      return blockPlaceContext.getItemInHand().getItem() == this.asItem();
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockPos var2 = blockPlaceContext.getClickedPos();
      Level var3 = blockPlaceContext.getLevel();
      int var4 = getDistance(var3, var2);
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(var3.getFluidState(var2).getType() == Fluids.WATER))).setValue(DISTANCE, Integer.valueOf(var4))).setValue(BOTTOM, Boolean.valueOf(this.isBottom(var3, var2, var4)));
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(!level.isClientSide) {
         level.getBlockTicks().scheduleTick(blockPos, this, 1);
      }

   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      if(!levelAccessor.isClientSide()) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, 1);
      }

      return var1;
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      int var5 = getDistance(level, blockPos);
      BlockState var6 = (BlockState)((BlockState)blockState.setValue(DISTANCE, Integer.valueOf(var5))).setValue(BOTTOM, Boolean.valueOf(this.isBottom(level, blockPos, var5)));
      if(((Integer)var6.getValue(DISTANCE)).intValue() == 7) {
         if(((Integer)blockState.getValue(DISTANCE)).intValue() == 7) {
            level.addFreshEntity(new FallingBlockEntity(level, (double)blockPos.getX() + 0.5D, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5D, (BlockState)var6.setValue(WATERLOGGED, Boolean.valueOf(false))));
         } else {
            level.destroyBlock(blockPos, true);
         }
      } else if(blockState != var6) {
         level.setBlock(blockPos, var6, 3);
      }

   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return getDistance(levelReader, blockPos) < 7;
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return collisionContext.isAbove(Shapes.block(), blockPos, true) && !collisionContext.isSneaking()?STABLE_SHAPE:(((Integer)blockState.getValue(DISTANCE)).intValue() != 0 && ((Boolean)blockState.getValue(BOTTOM)).booleanValue() && collisionContext.isAbove(BELOW_BLOCK, blockPos, true)?UNSTABLE_SHAPE_BOTTOM:Shapes.empty());
   }

   public FluidState getFluidState(BlockState blockState) {
      return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()?Fluids.WATER.getSource(false):super.getFluidState(blockState);
   }

   private boolean isBottom(BlockGetter blockGetter, BlockPos blockPos, int var3) {
      return var3 > 0 && blockGetter.getBlockState(blockPos.below()).getBlock() != this;
   }

   public static int getDistance(BlockGetter blockGetter, BlockPos blockPos) {
      BlockPos.MutableBlockPos var2 = (new BlockPos.MutableBlockPos(blockPos)).move(Direction.DOWN);
      BlockState var3 = blockGetter.getBlockState(var2);
      int var4 = 7;
      if(var3.getBlock() == Blocks.SCAFFOLDING) {
         var4 = ((Integer)var3.getValue(DISTANCE)).intValue();
      } else if(var3.isFaceSturdy(blockGetter, var2, Direction.UP)) {
         return 0;
      }

      for(Direction var6 : Direction.Plane.HORIZONTAL) {
         BlockState var7 = blockGetter.getBlockState(var2.set((Vec3i)blockPos).move(var6));
         if(var7.getBlock() == Blocks.SCAFFOLDING) {
            var4 = Math.min(var4, ((Integer)var7.getValue(DISTANCE)).intValue() + 1);
            if(var4 == 1) {
               break;
            }
         }
      }

      return var4;
   }

   static {
      VoxelShape var0 = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      VoxelShape var1 = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
      VoxelShape var2 = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
      VoxelShape var3 = Block.box(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);
      VoxelShape var4 = Block.box(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
      STABLE_SHAPE = Shapes.or(var0, new VoxelShape[]{var1, var2, var3, var4});
      VoxelShape var5 = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 2.0D, 16.0D);
      VoxelShape var6 = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
      VoxelShape var7 = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 2.0D, 16.0D);
      VoxelShape var8 = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 2.0D);
      UNSTABLE_SHAPE = Shapes.or(UNSTABLE_SHAPE_BOTTOM, new VoxelShape[]{STABLE_SHAPE, var6, var5, var8, var7});
   }
}
