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

public class WaterlilyFeature extends Feature {
   public WaterlilyFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      BlockPos var7;
      for(BlockPos blockPos = blockPos; blockPos.getY() > 0; blockPos = var7) {
         var7 = blockPos.below();
         if(!levelAccessor.isEmptyBlock(var7)) {
            break;
         }
      }

      for(int var7 = 0; var7 < 10; ++var7) {
         BlockPos var8 = blockPos.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         BlockState var9 = Blocks.LILY_PAD.defaultBlockState();
         if(levelAccessor.isEmptyBlock(var8) && var9.canSurvive(levelAccessor, var8)) {
            levelAccessor.setBlock(var8, var9, 2);
         }
      }

      return true;
   }
}
