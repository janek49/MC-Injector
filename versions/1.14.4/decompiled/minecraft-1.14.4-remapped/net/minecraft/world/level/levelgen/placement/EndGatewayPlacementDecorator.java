package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class EndGatewayPlacementDecorator extends FeatureDecorator {
   public EndGatewayPlacementDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, NoneDecoratorConfiguration noneDecoratorConfiguration, BlockPos blockPos) {
      if(random.nextInt(700) == 0) {
         int var6 = random.nextInt(16);
         int var7 = random.nextInt(16);
         int var8 = levelAccessor.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos.offset(var6, 0, var7)).getY();
         if(var8 > 0) {
            int var9 = var8 + 3 + random.nextInt(7);
            return Stream.of(blockPos.offset(var6, var9, var7));
         }
      }

      return Stream.empty();
   }
}
