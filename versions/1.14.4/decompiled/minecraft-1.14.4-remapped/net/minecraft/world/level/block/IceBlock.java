package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;

public class IceBlock extends HalfTransparentBlock {
   public IceBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
      super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
      if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
         if(level.dimension.isUltraWarm()) {
            level.removeBlock(blockPos, false);
            return;
         }

         Material var7 = level.getBlockState(blockPos.below()).getMaterial();
         if(var7.blocksMotion() || var7.isLiquid()) {
            level.setBlockAndUpdate(blockPos, Blocks.WATER.defaultBlockState());
         }
      }

   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(level.getBrightness(LightLayer.BLOCK, blockPos) > 11 - blockState.getLightBlock(level, blockPos)) {
         this.melt(blockState, level, blockPos);
      }

   }

   protected void melt(BlockState blockState, Level level, BlockPos blockPos) {
      if(level.dimension.isUltraWarm()) {
         level.removeBlock(blockPos, false);
      } else {
         level.setBlockAndUpdate(blockPos, Blocks.WATER.defaultBlockState());
         level.neighborChanged(blockPos, Blocks.WATER, blockPos);
      }
   }

   public PushReaction getPistonPushReaction(BlockState blockState) {
      return PushReaction.NORMAL;
   }

   public boolean isValidSpawn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType entityType) {
      return entityType == EntityType.POLAR_BEAR;
   }
}
