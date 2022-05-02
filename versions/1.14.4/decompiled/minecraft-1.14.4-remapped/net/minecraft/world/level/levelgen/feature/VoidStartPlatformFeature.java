package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class VoidStartPlatformFeature extends Feature {
   private static final BlockPos PLATFORM_ORIGIN = new BlockPos(8, 3, 8);
   private static final ChunkPos PLATFORM_ORIGIN_CHUNK = new ChunkPos(PLATFORM_ORIGIN);

   public VoidStartPlatformFeature(Function function) {
      super(function);
   }

   private static int checkerboardDistance(int var0, int var1, int var2, int var3) {
      return Math.max(Math.abs(var0 - var2), Math.abs(var1 - var3));
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      ChunkPos var6 = new ChunkPos(blockPos);
      if(checkerboardDistance(var6.x, var6.z, PLATFORM_ORIGIN_CHUNK.x, PLATFORM_ORIGIN_CHUNK.z) > 1) {
         return true;
      } else {
         BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

         for(int var8 = var6.getMinBlockZ(); var8 <= var6.getMaxBlockZ(); ++var8) {
            for(int var9 = var6.getMinBlockX(); var9 <= var6.getMaxBlockX(); ++var9) {
               if(checkerboardDistance(PLATFORM_ORIGIN.getX(), PLATFORM_ORIGIN.getZ(), var9, var8) <= 16) {
                  var7.set(var9, PLATFORM_ORIGIN.getY(), var8);
                  if(var7.equals(PLATFORM_ORIGIN)) {
                     levelAccessor.setBlock(var7, Blocks.COBBLESTONE.defaultBlockState(), 2);
                  } else {
                     levelAccessor.setBlock(var7, Blocks.STONE.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }
}
