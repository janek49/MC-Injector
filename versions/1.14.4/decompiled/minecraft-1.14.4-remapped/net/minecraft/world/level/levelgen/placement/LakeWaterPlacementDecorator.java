package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.LakeChanceDecoratorConfig;

public class LakeWaterPlacementDecorator extends FeatureDecorator {
   public LakeWaterPlacementDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, LakeChanceDecoratorConfig lakeChanceDecoratorConfig, BlockPos blockPos) {
      if(random.nextInt(lakeChanceDecoratorConfig.chance) == 0) {
         int var6 = random.nextInt(16);
         int var7 = random.nextInt(chunkGenerator.getGenDepth());
         int var8 = random.nextInt(16);
         return Stream.of(blockPos.offset(var6, var7, var8));
      } else {
         return Stream.empty();
      }
   }
}
