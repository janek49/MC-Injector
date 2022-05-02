package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public abstract class BlockPileFeature extends Feature {
   public BlockPileFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      if(blockPos.getY() < 5) {
         return false;
      } else {
         int var6 = 2 + random.nextInt(2);
         int var7 = 2 + random.nextInt(2);

         for(BlockPos var9 : BlockPos.betweenClosed(blockPos.offset(-var6, 0, -var7), blockPos.offset(var6, 1, var7))) {
            int var10 = blockPos.getX() - var9.getX();
            int var11 = blockPos.getZ() - var9.getZ();
            if((float)(var10 * var10 + var11 * var11) <= random.nextFloat() * 10.0F - random.nextFloat() * 6.0F) {
               this.tryPlaceBlock(levelAccessor, var9, random);
            } else if((double)random.nextFloat() < 0.031D) {
               this.tryPlaceBlock(levelAccessor, var9, random);
            }
         }

         return true;
      }
   }

   private boolean mayPlaceOn(LevelAccessor levelAccessor, BlockPos blockPos, Random random) {
      BlockPos blockPos = blockPos.below();
      BlockState var5 = levelAccessor.getBlockState(blockPos);
      return var5.getBlock() == Blocks.GRASS_PATH?random.nextBoolean():var5.isFaceSturdy(levelAccessor, blockPos, Direction.UP);
   }

   private void tryPlaceBlock(LevelAccessor levelAccessor, BlockPos blockPos, Random random) {
      if(levelAccessor.isEmptyBlock(blockPos) && this.mayPlaceOn(levelAccessor, blockPos, random)) {
         levelAccessor.setBlock(blockPos, this.getBlockState(levelAccessor), 4);
      }

   }

   protected abstract BlockState getBlockState(LevelAccessor var1);
}
