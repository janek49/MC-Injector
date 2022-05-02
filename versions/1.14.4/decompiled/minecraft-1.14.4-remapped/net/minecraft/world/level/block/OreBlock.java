package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class OreBlock extends Block {
   public OreBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   protected int xpOnDrop(Random random) {
      return this == Blocks.COAL_ORE?Mth.nextInt(random, 0, 2):(this == Blocks.DIAMOND_ORE?Mth.nextInt(random, 3, 7):(this == Blocks.EMERALD_ORE?Mth.nextInt(random, 3, 7):(this == Blocks.LAPIS_ORE?Mth.nextInt(random, 2, 5):(this == Blocks.NETHER_QUARTZ_ORE?Mth.nextInt(random, 2, 5):0))));
   }

   public void spawnAfterBreak(BlockState blockState, Level level, BlockPos blockPos, ItemStack itemStack) {
      super.spawnAfterBreak(blockState, level, blockPos, itemStack);
      if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
         int var5 = this.xpOnDrop(level.random);
         if(var5 > 0) {
            this.popExperience(level, blockPos, var5);
         }
      }

   }
}
