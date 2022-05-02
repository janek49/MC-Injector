package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.Queue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;

public class SpongeBlock extends Block {
   protected SpongeBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var4.getBlock() != var1.getBlock()) {
         this.tryAbsorbWater(level, blockPos);
      }
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      this.tryAbsorbWater(level, var3);
      super.neighborChanged(blockState, level, var3, block, var5, var6);
   }

   protected void tryAbsorbWater(Level level, BlockPos blockPos) {
      if(this.removeWaterBreadthFirstSearch(level, blockPos)) {
         level.setBlock(blockPos, Blocks.WET_SPONGE.defaultBlockState(), 2);
         level.levelEvent(2001, blockPos, Block.getId(Blocks.WATER.defaultBlockState()));
      }

   }

   private boolean removeWaterBreadthFirstSearch(Level level, BlockPos blockPos) {
      Queue<Tuple<BlockPos, Integer>> var3 = Lists.newLinkedList();
      var3.add(new Tuple(blockPos, Integer.valueOf(0)));
      int var4 = 0;

      while(!((Queue)var3).isEmpty()) {
         Tuple<BlockPos, Integer> var5 = (Tuple)var3.poll();
         BlockPos var6 = (BlockPos)var5.getA();
         int var7 = ((Integer)var5.getB()).intValue();

         for(Direction var11 : Direction.values()) {
            BlockPos var12 = var6.relative(var11);
            BlockState var13 = level.getBlockState(var12);
            FluidState var14 = level.getFluidState(var12);
            Material var15 = var13.getMaterial();
            if(var14.is(FluidTags.WATER)) {
               if(var13.getBlock() instanceof BucketPickup && ((BucketPickup)var13.getBlock()).takeLiquid(level, var12, var13) != Fluids.EMPTY) {
                  ++var4;
                  if(var7 < 6) {
                     var3.add(new Tuple(var12, Integer.valueOf(var7 + 1)));
                  }
               } else if(var13.getBlock() instanceof LiquidBlock) {
                  level.setBlock(var12, Blocks.AIR.defaultBlockState(), 3);
                  ++var4;
                  if(var7 < 6) {
                     var3.add(new Tuple(var12, Integer.valueOf(var7 + 1)));
                  }
               } else if(var15 == Material.WATER_PLANT || var15 == Material.REPLACEABLE_WATER_PLANT) {
                  BlockEntity var16 = var13.getBlock().isEntityBlock()?level.getBlockEntity(var12):null;
                  dropResources(var13, level, var12, var16);
                  level.setBlock(var12, Blocks.AIR.defaultBlockState(), 3);
                  ++var4;
                  if(var7 < 6) {
                     var3.add(new Tuple(var12, Integer.valueOf(var7 + 1)));
                  }
               }
            }
         }

         if(var4 > 64) {
            break;
         }
      }

      return var4 > 0;
   }
}
