package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class VinesFeature extends Feature {
   private static final Direction[] DIRECTIONS = Direction.values();

   public VinesFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos(blockPos);

      for(int var7 = blockPos.getY(); var7 < 256; ++var7) {
         var6.set((Vec3i)blockPos);
         var6.move(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
         var6.setY(var7);
         if(levelAccessor.isEmptyBlock(var6)) {
            for(Direction var11 : DIRECTIONS) {
               if(var11 != Direction.DOWN && VineBlock.isAcceptableNeighbour(levelAccessor, var6, var11)) {
                  levelAccessor.setBlock(var6, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(var11), Boolean.valueOf(true)), 2);
                  break;
               }
            }
         }
      }

      return true;
   }
}
