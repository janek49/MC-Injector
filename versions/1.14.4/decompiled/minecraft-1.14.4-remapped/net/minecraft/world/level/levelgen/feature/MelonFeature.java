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

public class MelonFeature extends Feature {
   public MelonFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      for(int var6 = 0; var6 < 64; ++var6) {
         BlockPos var7 = blockPos.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         BlockState var8 = Blocks.MELON.defaultBlockState();
         if(levelAccessor.getBlockState(var7).getMaterial().isReplaceable() && levelAccessor.getBlockState(var7.below()).getBlock() == Blocks.GRASS_BLOCK) {
            levelAccessor.setBlock(var7, var8, 2);
         }
      }

      return true;
   }
}
