package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.DecoratorChance;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class IcebergPlacementDecorator extends FeatureDecorator {
   public IcebergPlacementDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, DecoratorChance decoratorChance, BlockPos blockPos) {
      if(random.nextFloat() < 1.0F / (float)decoratorChance.chance) {
         int var6 = random.nextInt(8) + 4;
         int var7 = random.nextInt(8) + 4;
         return Stream.of(levelAccessor.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos.offset(var6, 0, var7)));
      } else {
         return Stream.empty();
      }
   }
}
