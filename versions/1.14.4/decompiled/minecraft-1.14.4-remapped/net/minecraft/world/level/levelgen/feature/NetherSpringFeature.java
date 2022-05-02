package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HellSpringConfiguration;
import net.minecraft.world.level.material.Fluids;

public class NetherSpringFeature extends Feature {
   private static final BlockState NETHERRACK = Blocks.NETHERRACK.defaultBlockState();

   public NetherSpringFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, HellSpringConfiguration hellSpringConfiguration) {
      if(levelAccessor.getBlockState(blockPos.above()) != NETHERRACK) {
         return false;
      } else if(!levelAccessor.getBlockState(blockPos).isAir() && levelAccessor.getBlockState(blockPos) != NETHERRACK) {
         return false;
      } else {
         int var6 = 0;
         if(levelAccessor.getBlockState(blockPos.west()) == NETHERRACK) {
            ++var6;
         }

         if(levelAccessor.getBlockState(blockPos.east()) == NETHERRACK) {
            ++var6;
         }

         if(levelAccessor.getBlockState(blockPos.north()) == NETHERRACK) {
            ++var6;
         }

         if(levelAccessor.getBlockState(blockPos.south()) == NETHERRACK) {
            ++var6;
         }

         if(levelAccessor.getBlockState(blockPos.below()) == NETHERRACK) {
            ++var6;
         }

         int var7 = 0;
         if(levelAccessor.isEmptyBlock(blockPos.west())) {
            ++var7;
         }

         if(levelAccessor.isEmptyBlock(blockPos.east())) {
            ++var7;
         }

         if(levelAccessor.isEmptyBlock(blockPos.north())) {
            ++var7;
         }

         if(levelAccessor.isEmptyBlock(blockPos.south())) {
            ++var7;
         }

         if(levelAccessor.isEmptyBlock(blockPos.below())) {
            ++var7;
         }

         if(!hellSpringConfiguration.insideRock && var6 == 4 && var7 == 1 || var6 == 5) {
            levelAccessor.setBlock(blockPos, Blocks.LAVA.defaultBlockState(), 2);
            levelAccessor.getLiquidTicks().scheduleTick(blockPos, Fluids.LAVA, 0);
         }

         return true;
      }
   }
}
