package net.minecraft.world.item;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ShearsItem extends Item {
   public ShearsItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
      if(!level.isClientSide) {
         itemStack.hurtAndBreak(1, livingEntity, (livingEntity) -> {
            livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
         });
      }

      Block var6 = blockState.getBlock();
      return !blockState.is(BlockTags.LEAVES) && var6 != Blocks.COBWEB && var6 != Blocks.GRASS && var6 != Blocks.FERN && var6 != Blocks.DEAD_BUSH && var6 != Blocks.VINE && var6 != Blocks.TRIPWIRE && !var6.is(BlockTags.WOOL)?super.mineBlock(itemStack, level, blockState, blockPos, livingEntity):true;
   }

   public boolean canDestroySpecial(BlockState blockState) {
      Block var2 = blockState.getBlock();
      return var2 == Blocks.COBWEB || var2 == Blocks.REDSTONE_WIRE || var2 == Blocks.TRIPWIRE;
   }

   public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
      Block var3 = blockState.getBlock();
      return var3 != Blocks.COBWEB && !blockState.is(BlockTags.LEAVES)?(var3.is(BlockTags.WOOL)?5.0F:super.getDestroySpeed(itemStack, blockState)):15.0F;
   }
}
