package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KelpBlock extends Block implements LiquidBlockContainer {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

   protected KelpBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      FluidState var2 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
      return var2.is(FluidTags.WATER) && var2.getAmount() == 8?this.getStateForPlacement((LevelAccessor)blockPlaceContext.getLevel()):null;
   }

   public BlockState getStateForPlacement(LevelAccessor levelAccessor) {
      return (BlockState)this.defaultBlockState().setValue(AGE, Integer.valueOf(levelAccessor.getRandom().nextInt(25)));
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public FluidState getFluidState(BlockState blockState) {
      return Fluids.WATER.getSource(false);
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!blockState.canSurvive(level, blockPos)) {
         level.destroyBlock(blockPos, true);
      } else {
         BlockPos blockPos = blockPos.above();
         BlockState var6 = level.getBlockState(blockPos);
         if(var6.getBlock() == Blocks.WATER && ((Integer)blockState.getValue(AGE)).intValue() < 25 && random.nextDouble() < 0.14D) {
            level.setBlockAndUpdate(blockPos, (BlockState)blockState.cycle(AGE));
         }

      }
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      BlockState var5 = levelReader.getBlockState(blockPos);
      Block var6 = var5.getBlock();
      return var6 == Blocks.MAGMA_BLOCK?false:var6 == this || var6 == Blocks.KELP_PLANT || var5.isFaceSturdy(levelReader, blockPos, Direction.UP);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(!var1.canSurvive(levelAccessor, var5)) {
         if(direction == Direction.DOWN) {
            return Blocks.AIR.defaultBlockState();
         }

         levelAccessor.getBlockTicks().scheduleTick(var5, this, 1);
      }

      if(direction == Direction.UP && var3.getBlock() == this) {
         return Blocks.KELP_PLANT.defaultBlockState();
      } else {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AGE});
   }

   public boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
      return false;
   }

   public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
      return false;
   }
}
