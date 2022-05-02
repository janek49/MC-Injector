package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class FrostedIceBlock extends IceBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

   public FrostedIceBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, Integer.valueOf(0)));
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if((random.nextInt(3) == 0 || this.fewerNeigboursThan(level, blockPos, 4)) && level.getMaxLocalRawBrightness(blockPos) > 11 - ((Integer)blockState.getValue(AGE)).intValue() - blockState.getLightBlock(level, blockPos) && this.slightlyMelt(blockState, level, blockPos)) {
         BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var6 = null;

         try {
            for(Direction var10 : Direction.values()) {
               var5.set((Vec3i)blockPos).move(var10);
               BlockState var11 = level.getBlockState(var5);
               if(var11.getBlock() == this && !this.slightlyMelt(var11, level, var5)) {
                  level.getBlockTicks().scheduleTick(var5, this, Mth.nextInt(random, 20, 40));
               }
            }
         } catch (Throwable var19) {
            var6 = var19;
            throw var19;
         } finally {
            if(var5 != null) {
               if(var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var18) {
                     var6.addSuppressed(var18);
                  }
               } else {
                  var5.close();
               }
            }

         }

      } else {
         level.getBlockTicks().scheduleTick(blockPos, this, Mth.nextInt(random, 20, 40));
      }
   }

   private boolean slightlyMelt(BlockState blockState, Level level, BlockPos blockPos) {
      int var4 = ((Integer)blockState.getValue(AGE)).intValue();
      if(var4 < 3) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(AGE, Integer.valueOf(var4 + 1)), 2);
         return false;
      } else {
         this.melt(blockState, level, blockPos);
         return true;
      }
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(block == this && this.fewerNeigboursThan(level, var3, 2)) {
         this.melt(blockState, level, var3);
      }

      super.neighborChanged(blockState, level, var3, block, var5, var6);
   }

   private boolean fewerNeigboursThan(BlockGetter blockGetter, BlockPos blockPos, int var3) {
      int var4 = 0;
      BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var6 = null;

      try {
         for(Direction var10 : Direction.values()) {
            var5.set((Vec3i)blockPos).move(var10);
            if(blockGetter.getBlockState(var5).getBlock() == this) {
               ++var4;
               if(var4 >= var3) {
                  boolean var11 = false;
                  return var11;
               }
            }
         }

         return true;
      } catch (Throwable var21) {
         var6 = var21;
         throw var21;
      } finally {
         if(var5 != null) {
            if(var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var20) {
                  var6.addSuppressed(var20);
               }
            } else {
               var5.close();
            }
         }

      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AGE});
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return ItemStack.EMPTY;
   }
}
