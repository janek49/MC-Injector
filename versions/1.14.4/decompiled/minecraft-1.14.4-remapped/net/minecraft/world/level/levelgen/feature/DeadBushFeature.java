package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class DeadBushFeature extends Feature {
   private static final DeadBushBlock DEAD_BUSH = (DeadBushBlock)Blocks.DEAD_BUSH;

   public DeadBushFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      for(BlockState var6 = levelAccessor.getBlockState(blockPos); (var6.isAir() || var6.is(BlockTags.LEAVES)) && blockPos.getY() > 0; var6 = levelAccessor.getBlockState(blockPos)) {
         blockPos = blockPos.below();
      }

      BlockState var7 = DEAD_BUSH.defaultBlockState();

      for(int var8 = 0; var8 < 4; ++var8) {
         BlockPos var9 = blockPos.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if(levelAccessor.isEmptyBlock(var9) && var7.canSurvive(levelAccessor, var9)) {
            levelAccessor.setBlock(var9, var7, 2);
         }
      }

      return true;
   }
}
