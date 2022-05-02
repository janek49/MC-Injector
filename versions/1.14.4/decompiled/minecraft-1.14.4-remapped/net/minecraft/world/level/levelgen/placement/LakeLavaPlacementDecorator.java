package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.LakeChanceDecoratorConfig;

public class LakeLavaPlacementDecorator extends FeatureDecorator {
   public LakeLavaPlacementDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, LakeChanceDecoratorConfig lakeChanceDecoratorConfig, BlockPos blockPos) {
      if(random.nextInt(lakeChanceDecoratorConfig.chance / 10) == 0) {
         int var6 = random.nextInt(16);
         int var7 = random.nextInt(random.nextInt(chunkGenerator.getGenDepth() - 8) + 8);
         int var8 = random.nextInt(16);
         if(var7 < levelAccessor.getSeaLevel() || random.nextInt(lakeChanceDecoratorConfig.chance / 8) == 0) {
            return Stream.of(blockPos.offset(var6, var7, var8));
         }
      }

      return Stream.empty();
   }
}
