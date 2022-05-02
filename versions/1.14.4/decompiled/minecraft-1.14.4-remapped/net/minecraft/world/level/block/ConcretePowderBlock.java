package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ConcretePowderBlock extends FallingBlock {
   private final BlockState concrete;

   public ConcretePowderBlock(Block block, Block.Properties block$Properties) {
      super(block$Properties);
      this.concrete = block.defaultBlockState();
   }

   public void onLand(Level level, BlockPos blockPos, BlockState var3, BlockState var4) {
      if(canSolidify(var4)) {
         level.setBlock(blockPos, this.concrete, 3);
      }

   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockGetter var2 = blockPlaceContext.getLevel();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      return !canSolidify(var2.getBlockState(var3)) && !touchesLiquid(var2, var3)?super.getStateForPlacement(blockPlaceContext):this.concrete;
   }

   private static boolean touchesLiquid(BlockGetter blockGetter, BlockPos blockPos) {
      boolean var2 = false;
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos(blockPos);

      for(Direction var7 : Direction.values()) {
         BlockState var8 = blockGetter.getBlockState(var3);
         if(var7 != Direction.DOWN || canSolidify(var8)) {
            var3.set((Vec3i)blockPos).move(var7);
            var8 = blockGetter.getBlockState(var3);
            if(canSolidify(var8) && !var8.isFaceSturdy(blockGetter, blockPos, var7.getOpposite())) {
               var2 = true;
               break;
            }
         }
      }

      return var2;
   }

   private static boolean canSolidify(BlockState blockState) {
      return blockState.getFluidState().is(FluidTags.WATER);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return touchesLiquid(levelAccessor, var5)?this.concrete:super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }
}
