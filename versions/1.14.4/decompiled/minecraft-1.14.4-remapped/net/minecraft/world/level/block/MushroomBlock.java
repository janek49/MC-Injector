package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MushroomBlock extends BushBlock implements BonemealableBlock {
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

   public MushroomBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(random.nextInt(25) == 0) {
         int var5 = 5;
         int var6 = 4;

         for(BlockPos var8 : BlockPos.betweenClosed(blockPos.offset(-4, -1, -4), blockPos.offset(4, 1, 4))) {
            if(level.getBlockState(var8).getBlock() == this) {
               --var5;
               if(var5 <= 0) {
                  return;
               }
            }
         }

         BlockPos var7 = blockPos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

         for(int var8 = 0; var8 < 4; ++var8) {
            if(level.isEmptyBlock(var7) && blockState.canSurvive(level, var7)) {
               blockPos = var7;
            }

            var7 = blockPos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
         }

         if(level.isEmptyBlock(var7) && blockState.canSurvive(level, var7)) {
            level.setBlock(var7, blockState, 2);
         }
      }

   }

   protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.isSolidRender(blockGetter, blockPos);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      BlockState var5 = levelReader.getBlockState(blockPos);
      Block var6 = var5.getBlock();
      return var6 != Blocks.MYCELIUM && var6 != Blocks.PODZOL?levelReader.getRawBrightness(blockPos, 0) < 13 && this.mayPlaceOn(var5, levelReader, blockPos):true;
   }

   public boolean growMushroom(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Random random) {
      levelAccessor.removeBlock(blockPos, false);
      Feature<HugeMushroomFeatureConfig> var5 = null;
      if(this == Blocks.BROWN_MUSHROOM) {
         var5 = Feature.HUGE_BROWN_MUSHROOM;
      } else if(this == Blocks.RED_MUSHROOM) {
         var5 = Feature.HUGE_RED_MUSHROOM;
      }

      if(var5 != null && var5.place(levelAccessor, levelAccessor.getChunkSource().getGenerator(), random, blockPos, new HugeMushroomFeatureConfig(true))) {
         return true;
      } else {
         levelAccessor.setBlock(blockPos, blockState, 3);
         return false;
      }
   }

   public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean var4) {
      return true;
   }

   public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      return (double)random.nextFloat() < 0.4D;
   }

   public void performBonemeal(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      this.growMushroom(level, blockPos, blockState, random);
   }

   public boolean hasPostProcess(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return true;
   }
}
