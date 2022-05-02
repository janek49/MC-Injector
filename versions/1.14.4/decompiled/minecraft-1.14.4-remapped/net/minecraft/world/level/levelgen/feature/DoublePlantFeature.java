package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.DoublePlantConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

public class DoublePlantFeature extends Feature {
   public DoublePlantFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DoublePlantConfiguration doublePlantConfiguration) {
      boolean var6 = false;

      for(int var7 = 0; var7 < 64; ++var7) {
         BlockPos var8 = blockPos.offset(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
         if(levelAccessor.isEmptyBlock(var8) && var8.getY() < 254 && doublePlantConfiguration.state.canSurvive(levelAccessor, var8)) {
            ((DoublePlantBlock)doublePlantConfiguration.state.getBlock()).placeAt(levelAccessor, var8, 2);
            var6 = true;
         }
      }

      return var6;
   }
}
