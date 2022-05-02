package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class SnowAndFreezeFeature extends Feature {
   public SnowAndFreezeFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      for(int var8 = 0; var8 < 16; ++var8) {
         for(int var9 = 0; var9 < 16; ++var9) {
            int var10 = blockPos.getX() + var8;
            int var11 = blockPos.getZ() + var9;
            int var12 = levelAccessor.getHeight(Heightmap.Types.MOTION_BLOCKING, var10, var11);
            var6.set(var10, var12, var11);
            var7.set((Vec3i)var6).move(Direction.DOWN, 1);
            Biome var13 = levelAccessor.getBiome(var6);
            if(var13.shouldFreeze(levelAccessor, var7, false)) {
               levelAccessor.setBlock(var7, Blocks.ICE.defaultBlockState(), 2);
            }

            if(var13.shouldSnow(levelAccessor, var6)) {
               levelAccessor.setBlock(var6, Blocks.SNOW.defaultBlockState(), 2);
               BlockState var14 = levelAccessor.getBlockState(var7);
               if(var14.hasProperty(SnowyDirtBlock.SNOWY)) {
                  levelAccessor.setBlock(var7, (BlockState)var14.setValue(SnowyDirtBlock.SNOWY, Boolean.valueOf(true)), 2);
               }
            }
         }
      }

      return true;
   }
}
