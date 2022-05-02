package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public abstract class AbstractMegaTreeGrower extends AbstractTreeGrower {
   public boolean growTree(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Random random) {
      for(int var5 = 0; var5 >= -1; --var5) {
         for(int var6 = 0; var6 >= -1; --var6) {
            if(isTwoByTwoSapling(blockState, levelAccessor, blockPos, var5, var6)) {
               return this.placeMega(levelAccessor, blockPos, blockState, random, var5, var6);
            }
         }
      }

      return super.growTree(levelAccessor, blockPos, blockState, random);
   }

   @Nullable
   protected abstract AbstractTreeFeature getMegaFeature(Random var1);

   public boolean placeMega(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Random random, int var5, int var6) {
      AbstractTreeFeature<NoneFeatureConfiguration> var7 = this.getMegaFeature(random);
      if(var7 == null) {
         return false;
      } else {
         BlockState var8 = Blocks.AIR.defaultBlockState();
         levelAccessor.setBlock(blockPos.offset(var5, 0, var6), var8, 4);
         levelAccessor.setBlock(blockPos.offset(var5 + 1, 0, var6), var8, 4);
         levelAccessor.setBlock(blockPos.offset(var5, 0, var6 + 1), var8, 4);
         levelAccessor.setBlock(blockPos.offset(var5 + 1, 0, var6 + 1), var8, 4);
         if(var7.place(levelAccessor, levelAccessor.getChunkSource().getGenerator(), random, blockPos.offset(var5, 0, var6), FeatureConfiguration.NONE)) {
            return true;
         } else {
            levelAccessor.setBlock(blockPos.offset(var5, 0, var6), blockState, 4);
            levelAccessor.setBlock(blockPos.offset(var5 + 1, 0, var6), blockState, 4);
            levelAccessor.setBlock(blockPos.offset(var5, 0, var6 + 1), blockState, 4);
            levelAccessor.setBlock(blockPos.offset(var5 + 1, 0, var6 + 1), blockState, 4);
            return false;
         }
      }
   }

   public static boolean isTwoByTwoSapling(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, int var3, int var4) {
      Block var5 = blockState.getBlock();
      return var5 == blockGetter.getBlockState(blockPos.offset(var3, 0, var4)).getBlock() && var5 == blockGetter.getBlockState(blockPos.offset(var3 + 1, 0, var4)).getBlock() && var5 == blockGetter.getBlockState(blockPos.offset(var3, 0, var4 + 1)).getBlock() && var5 == blockGetter.getBlockState(blockPos.offset(var3 + 1, 0, var4 + 1)).getBlock();
   }
}
