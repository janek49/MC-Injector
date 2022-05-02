package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBannerBlock extends BaseEntityBlock {
   private final DyeColor color;

   protected AbstractBannerBlock(DyeColor color, Block.Properties block$Properties) {
      super(block$Properties);
      this.color = color;
   }

   public boolean isPossibleToRespawnInThis() {
      return true;
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new BannerBlockEntity(this.color);
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
      if(itemStack.hasCustomHoverName()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof BannerBlockEntity) {
            ((BannerBlockEntity)var6).setCustomName(itemStack.getHoverName());
         }
      }

   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      BlockEntity var4 = blockGetter.getBlockEntity(blockPos);
      return var4 instanceof BannerBlockEntity?((BannerBlockEntity)var4).getItem(blockState):super.getCloneItemStack(blockGetter, blockPos, blockState);
   }

   public DyeColor getColor() {
      return this.color;
   }
}
