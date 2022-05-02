package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class CoralBlock extends Block {
   private final Block deadBlock;

   public CoralBlock(Block deadBlock, Block.Properties block$Properties) {
      super(block$Properties);
      this.deadBlock = deadBlock;
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!this.scanForWater(level, blockPos)) {
         level.setBlock(blockPos, this.deadBlock.defaultBlockState(), 2);
      }

   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(!this.scanForWater(levelAccessor, var5)) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, 60 + levelAccessor.getRandom().nextInt(40));
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   protected boolean scanForWater(BlockGetter blockGetter, BlockPos blockPos) {
      for(Direction var6 : Direction.values()) {
         FluidState var7 = blockGetter.getFluidState(blockPos.relative(var6));
         if(var7.is(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      if(!this.scanForWater(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())) {
         blockPlaceContext.getLevel().getBlockTicks().scheduleTick(blockPlaceContext.getClickedPos(), this, 60 + blockPlaceContext.getLevel().getRandom().nextInt(40));
      }

      return this.defaultBlockState();
   }
}
