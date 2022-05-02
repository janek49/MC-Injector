package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class CentralSpikedFeature extends Feature {
   protected final BlockState blockState;

   public CentralSpikedFeature(Function function, BlockState blockState) {
      super(function);
      this.blockState = blockState;
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      int var6 = 0;

      for(int var7 = 0; var7 < 64; ++var7) {
         BlockPos var8 = blockPos.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if(levelAccessor.isEmptyBlock(var8) && levelAccessor.getBlockState(var8.below()).getBlock() == Blocks.GRASS_BLOCK) {
            levelAccessor.setBlock(var8, this.blockState, 2);
            ++var6;
         }
      }

      return var6 > 0;
   }
}
