package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BlockBlobConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

public class BlockBlobFeature extends Feature {
   public BlockBlobFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BlockBlobConfiguration blockBlobConfiguration) {
      while(true) {
         label0: {
            if(blockPos.getY() > 3) {
               if(levelAccessor.isEmptyBlock(blockPos.below())) {
                  break label0;
               }

               Block var6 = levelAccessor.getBlockState(blockPos.below()).getBlock();
               if(var6 != Blocks.GRASS_BLOCK && !Block.equalsDirt(var6) && !Block.equalsStone(var6)) {
                  break label0;
               }
            }

            if(blockPos.getY() <= 3) {
               return false;
            }

            int var6 = blockBlobConfiguration.startRadius;

            for(int var7 = 0; var6 >= 0 && var7 < 3; ++var7) {
               int var8 = var6 + random.nextInt(2);
               int var9 = var6 + random.nextInt(2);
               int var10 = var6 + random.nextInt(2);
               float var11 = (float)(var8 + var9 + var10) * 0.333F + 0.5F;

               for(BlockPos var13 : BlockPos.betweenClosed(blockPos.offset(-var8, -var9, -var10), blockPos.offset(var8, var9, var10))) {
                  if(var13.distSqr(blockPos) <= (double)(var11 * var11)) {
                     levelAccessor.setBlock(var13, blockBlobConfiguration.state, 4);
                  }
               }

               blockPos = blockPos.offset(-(var6 + 1) + random.nextInt(2 + var6 * 2), 0 - random.nextInt(2), -(var6 + 1) + random.nextInt(2 + var6 * 2));
            }

            return true;
         }

         blockPos = blockPos.below();
      }
   }
}
