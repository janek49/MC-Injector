package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;

public class GrassBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {
   public GrassBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean var4) {
      return blockGetter.getBlockState(blockPos.above()).isAir();
   }

   public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      return true;
   }

   public void performBonemeal(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      BlockPos blockPos = blockPos.above();
      BlockState var6 = Blocks.GRASS.defaultBlockState();

      for(int var7 = 0; var7 < 128; ++var7) {
         BlockPos var8 = blockPos;
         int var9 = 0;

         while(true) {
            if(var9 >= var7 / 16) {
               BlockState var9 = level.getBlockState(var8);
               if(var9.getBlock() == var6.getBlock() && random.nextInt(10) == 0) {
                  ((BonemealableBlock)var6.getBlock()).performBonemeal(level, random, var8, var9);
               }

               if(!var9.isAir()) {
                  break;
               }

               BlockState var10;
               if(random.nextInt(8) == 0) {
                  List<ConfiguredFeature<?>> var11 = level.getBiome(var8).getFlowerFeatures();
                  if(var11.isEmpty()) {
                     break;
                  }

                  var10 = ((FlowerFeature)((DecoratedFeatureConfiguration)((ConfiguredFeature)var11.get(0)).config).feature.feature).getRandomFlower(random, var8);
               } else {
                  var10 = var6;
               }

               if(var10.canSurvive(level, var8)) {
                  level.setBlock(var8, var10, 3);
               }
               break;
            }

            var8 = var8.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
            if(level.getBlockState(var8.below()).getBlock() != this || level.getBlockState(var8).isCollisionShapeFullBlock(level, var8)) {
               break;
            }

            ++var9;
         }
      }

   }

   public boolean canOcclude(BlockState blockState) {
      return true;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT_MIPPED;
   }
}
