package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;

public class BambooFeature extends Feature {
   private static final BlockState BAMBOO_TRUNK = (BlockState)((BlockState)((BlockState)Blocks.BAMBOO.defaultBlockState().setValue(BambooBlock.AGE, Integer.valueOf(1))).setValue(BambooBlock.LEAVES, BambooLeaves.NONE)).setValue(BambooBlock.STAGE, Integer.valueOf(0));
   private static final BlockState BAMBOO_FINAL_LARGE = (BlockState)((BlockState)BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.LARGE)).setValue(BambooBlock.STAGE, Integer.valueOf(1));
   private static final BlockState BAMBOO_TOP_LARGE = (BlockState)BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.LARGE);
   private static final BlockState BAMBOO_TOP_SMALL = (BlockState)BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.SMALL);

   public BambooFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
      int var6 = 0;
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos(blockPos);
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos(blockPos);
      if(levelAccessor.isEmptyBlock(var7)) {
         if(Blocks.BAMBOO.defaultBlockState().canSurvive(levelAccessor, var7)) {
            int var9 = random.nextInt(12) + 5;
            if(random.nextFloat() < probabilityFeatureConfiguration.probability) {
               int var10 = random.nextInt(4) + 1;

               for(int var11 = blockPos.getX() - var10; var11 <= blockPos.getX() + var10; ++var11) {
                  for(int var12 = blockPos.getZ() - var10; var12 <= blockPos.getZ() + var10; ++var12) {
                     int var13 = var11 - blockPos.getX();
                     int var14 = var12 - blockPos.getZ();
                     if(var13 * var13 + var14 * var14 <= var10 * var10) {
                        var8.set(var11, levelAccessor.getHeight(Heightmap.Types.WORLD_SURFACE, var11, var12) - 1, var12);
                        if(levelAccessor.getBlockState(var8).getBlock().is(BlockTags.DIRT_LIKE)) {
                           levelAccessor.setBlock(var8, Blocks.PODZOL.defaultBlockState(), 2);
                        }
                     }
                  }
               }
            }

            for(int var10 = 0; var10 < var9 && levelAccessor.isEmptyBlock(var7); ++var10) {
               levelAccessor.setBlock(var7, BAMBOO_TRUNK, 2);
               var7.move(Direction.UP, 1);
            }

            if(var7.getY() - blockPos.getY() >= 3) {
               levelAccessor.setBlock(var7, BAMBOO_FINAL_LARGE, 2);
               levelAccessor.setBlock(var7.move(Direction.DOWN, 1), BAMBOO_TOP_LARGE, 2);
               levelAccessor.setBlock(var7.move(Direction.DOWN, 1), BAMBOO_TOP_SMALL, 2);
            }
         }

         ++var6;
      }

      return var6 > 0;
   }
}
