package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.MonsterRoomPlacementConfiguration;

public class MonsterRoomPlacementDecorator extends FeatureDecorator {
   public MonsterRoomPlacementDecorator(Function function) {
      super(function);
   }

   public Stream getPositions(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, MonsterRoomPlacementConfiguration monsterRoomPlacementConfiguration, BlockPos blockPos) {
      int var6 = monsterRoomPlacementConfiguration.chance;
      return IntStream.range(0, var6).mapToObj((var3) -> {
         int var4 = random.nextInt(16);
         int var5 = random.nextInt(chunkGenerator.getGenDepth());
         int var6 = random.nextInt(16);
         return blockPos.offset(var4, var5, var6);
      });
   }
}
