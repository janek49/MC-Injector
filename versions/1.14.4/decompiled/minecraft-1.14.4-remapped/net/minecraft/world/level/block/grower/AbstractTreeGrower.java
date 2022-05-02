package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public abstract class AbstractTreeGrower {
   @Nullable
   protected abstract AbstractTreeFeature getFeature(Random var1);

   public boolean growTree(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Random random) {
      AbstractTreeFeature<NoneFeatureConfiguration> var5 = this.getFeature(random);
      if(var5 == null) {
         return false;
      } else {
         levelAccessor.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 4);
         if(var5.place(levelAccessor, levelAccessor.getChunkSource().getGenerator(), random, blockPos, FeatureConfiguration.NONE)) {
            return true;
         } else {
            levelAccessor.setBlock(blockPos, blockState, 4);
            return false;
         }
      }
   }
}
