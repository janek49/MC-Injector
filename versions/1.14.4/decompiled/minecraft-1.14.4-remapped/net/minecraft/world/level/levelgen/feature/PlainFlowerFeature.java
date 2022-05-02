package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;

public class PlainFlowerFeature extends FlowerFeature {
   public PlainFlowerFeature(Function function) {
      super(function);
   }

   public BlockState getRandomFlower(Random random, BlockPos blockPos) {
      double var3 = Biome.BIOME_INFO_NOISE.getValue((double)blockPos.getX() / 200.0D, (double)blockPos.getZ() / 200.0D);
      if(var3 < -0.8D) {
         int var5 = random.nextInt(4);
         switch(var5) {
         case 0:
            return Blocks.ORANGE_TULIP.defaultBlockState();
         case 1:
            return Blocks.RED_TULIP.defaultBlockState();
         case 2:
            return Blocks.PINK_TULIP.defaultBlockState();
         case 3:
         default:
            return Blocks.WHITE_TULIP.defaultBlockState();
         }
      } else if(random.nextInt(3) > 0) {
         int var5 = random.nextInt(4);
         switch(var5) {
         case 0:
            return Blocks.POPPY.defaultBlockState();
         case 1:
            return Blocks.AZURE_BLUET.defaultBlockState();
         case 2:
            return Blocks.OXEYE_DAISY.defaultBlockState();
         case 3:
         default:
            return Blocks.CORNFLOWER.defaultBlockState();
         }
      } else {
         return Blocks.DANDELION.defaultBlockState();
      }
   }
}
