package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;

public class ForestFlowerFeature extends FlowerFeature {
   private static final Block[] flowers = new Block[]{Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY};

   public ForestFlowerFeature(Function function) {
      super(function);
   }

   public BlockState getRandomFlower(Random random, BlockPos blockPos) {
      double var3 = Mth.clamp((1.0D + Biome.BIOME_INFO_NOISE.getValue((double)blockPos.getX() / 48.0D, (double)blockPos.getZ() / 48.0D)) / 2.0D, 0.0D, 0.9999D);
      Block var5 = flowers[(int)(var3 * (double)flowers.length)];
      return var5 == Blocks.BLUE_ORCHID?Blocks.POPPY.defaultBlockState():var5.defaultBlockState();
   }
}
