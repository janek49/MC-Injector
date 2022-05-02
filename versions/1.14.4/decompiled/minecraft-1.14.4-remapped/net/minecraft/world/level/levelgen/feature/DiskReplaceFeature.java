package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

public class DiskReplaceFeature extends Feature {
   public DiskReplaceFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DiskConfiguration diskConfiguration) {
      if(!levelAccessor.getFluidState(blockPos).is(FluidTags.WATER)) {
         return false;
      } else {
         int var6 = 0;
         int var7 = random.nextInt(diskConfiguration.radius - 2) + 2;

         for(int var8 = blockPos.getX() - var7; var8 <= blockPos.getX() + var7; ++var8) {
            for(int var9 = blockPos.getZ() - var7; var9 <= blockPos.getZ() + var7; ++var9) {
               int var10 = var8 - blockPos.getX();
               int var11 = var9 - blockPos.getZ();
               if(var10 * var10 + var11 * var11 <= var7 * var7) {
                  for(int var12 = blockPos.getY() - diskConfiguration.ySize; var12 <= blockPos.getY() + diskConfiguration.ySize; ++var12) {
                     BlockPos var13 = new BlockPos(var8, var12, var9);
                     BlockState var14 = levelAccessor.getBlockState(var13);

                     for(BlockState var16 : diskConfiguration.targets) {
                        if(var16.getBlock() == var14.getBlock()) {
                           levelAccessor.setBlock(var13, diskConfiguration.state, 2);
                           ++var6;
                           break;
                        }
                     }
                  }
               }
            }
         }

         return var6 > 0;
      }
   }
}
