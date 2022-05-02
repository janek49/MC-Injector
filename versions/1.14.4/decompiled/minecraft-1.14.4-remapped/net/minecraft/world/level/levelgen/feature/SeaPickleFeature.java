package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.CountFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

public class SeaPickleFeature extends Feature {
   public SeaPickleFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, CountFeatureConfiguration countFeatureConfiguration) {
      int var6 = 0;

      for(int var7 = 0; var7 < countFeatureConfiguration.count; ++var7) {
         int var8 = random.nextInt(8) - random.nextInt(8);
         int var9 = random.nextInt(8) - random.nextInt(8);
         int var10 = levelAccessor.getHeight(Heightmap.Types.OCEAN_FLOOR, blockPos.getX() + var8, blockPos.getZ() + var9);
         BlockPos var11 = new BlockPos(blockPos.getX() + var8, var10, blockPos.getZ() + var9);
         BlockState var12 = (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, Integer.valueOf(random.nextInt(4) + 1));
         if(levelAccessor.getBlockState(var11).getBlock() == Blocks.WATER && var12.canSurvive(levelAccessor, var11)) {
            levelAccessor.setBlock(var11, var12, 2);
            ++var6;
         }
      }

      return var6 > 0;
   }
}
