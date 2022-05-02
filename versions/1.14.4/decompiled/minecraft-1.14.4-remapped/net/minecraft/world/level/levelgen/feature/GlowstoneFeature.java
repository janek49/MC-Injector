package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class GlowstoneFeature extends Feature {
   public GlowstoneFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      if(!levelAccessor.isEmptyBlock(blockPos)) {
         return false;
      } else if(levelAccessor.getBlockState(blockPos.above()).getBlock() != Blocks.NETHERRACK) {
         return false;
      } else {
         levelAccessor.setBlock(blockPos, Blocks.GLOWSTONE.defaultBlockState(), 2);

         for(int var6 = 0; var6 < 1500; ++var6) {
            BlockPos var7 = blockPos.offset(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
            if(levelAccessor.getBlockState(var7).isAir()) {
               int var8 = 0;

               for(Direction var12 : Direction.values()) {
                  if(levelAccessor.getBlockState(var7.relative(var12)).getBlock() == Blocks.GLOWSTONE) {
                     ++var8;
                  }

                  if(var8 > 1) {
                     break;
                  }
               }

               if(var8 == 1) {
                  levelAccessor.setBlock(var7, Blocks.GLOWSTONE.defaultBlockState(), 2);
               }
            }
         }

         return true;
      }
   }
}
