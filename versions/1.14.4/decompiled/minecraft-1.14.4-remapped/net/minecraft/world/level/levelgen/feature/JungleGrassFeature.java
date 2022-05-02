package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class JungleGrassFeature extends Feature {
   public JungleGrassFeature(Function function) {
      super(function);
   }

   public BlockState getState(Random random) {
      return random.nextInt(4) == 0?Blocks.FERN.defaultBlockState():Blocks.GRASS.defaultBlockState();
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      BlockState var6 = this.getState(random);

      for(BlockState var7 = levelAccessor.getBlockState(blockPos); (var7.isAir() || var7.is(BlockTags.LEAVES)) && blockPos.getY() > 0; var7 = levelAccessor.getBlockState(blockPos)) {
         blockPos = blockPos.below();
      }

      int var8 = 0;

      for(int var9 = 0; var9 < 128; ++var9) {
         BlockPos var10 = blockPos.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if(levelAccessor.isEmptyBlock(var10) && levelAccessor.getBlockState(var10.below()).getBlock() != Blocks.PODZOL && var6.canSurvive(levelAccessor, var10)) {
            levelAccessor.setBlock(var10, var6, 2);
            ++var8;
         }
      }

      return var8 > 0;
   }
}
