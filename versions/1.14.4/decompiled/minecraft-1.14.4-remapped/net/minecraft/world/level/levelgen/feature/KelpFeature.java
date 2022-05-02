package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class KelpFeature extends Feature {
   public KelpFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      int var6 = 0;
      int var7 = levelAccessor.getHeight(Heightmap.Types.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
      BlockPos var8 = new BlockPos(blockPos.getX(), var7, blockPos.getZ());
      if(levelAccessor.getBlockState(var8).getBlock() == Blocks.WATER) {
         BlockState var9 = Blocks.KELP.defaultBlockState();
         BlockState var10 = Blocks.KELP_PLANT.defaultBlockState();
         int var11 = 1 + random.nextInt(10);

         for(int var12 = 0; var12 <= var11; ++var12) {
            if(levelAccessor.getBlockState(var8).getBlock() == Blocks.WATER && levelAccessor.getBlockState(var8.above()).getBlock() == Blocks.WATER && var10.canSurvive(levelAccessor, var8)) {
               if(var12 == var11) {
                  levelAccessor.setBlock(var8, (BlockState)var9.setValue(KelpBlock.AGE, Integer.valueOf(random.nextInt(23))), 2);
                  ++var6;
               } else {
                  levelAccessor.setBlock(var8, var10, 2);
               }
            } else if(var12 > 0) {
               BlockPos var13 = var8.below();
               if(var9.canSurvive(levelAccessor, var13) && levelAccessor.getBlockState(var13.below()).getBlock() != Blocks.KELP) {
                  levelAccessor.setBlock(var13, (BlockState)var9.setValue(KelpBlock.AGE, Integer.valueOf(random.nextInt(23))), 2);
                  ++var6;
               }
               break;
            }

            var8 = var8.above();
         }
      }

      return var6 > 0;
   }
}
