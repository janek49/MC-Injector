package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class ShieldItem extends Item {
   public ShieldItem(Item.Properties item$Properties) {
      super(item$Properties);
      this.addProperty(new ResourceLocation("blocking"), (itemStack, level, livingEntity) -> {
         return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack?1.0F:0.0F;
      });
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public String getDescriptionId(ItemStack itemStack) {
      return itemStack.getTagElement("BlockEntityTag") != null?this.getDescriptionId() + '.' + getColor(itemStack).getName():super.getDescriptionId(itemStack);
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      BannerItem.appendHoverTextFromBannerBlockEntityTag(itemStack, list);
   }

   public UseAnim getUseAnimation(ItemStack itemStack) {
      return UseAnim.BLOCK;
   }

   public int getUseDuration(ItemStack itemStack) {
      return 72000;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      player.startUsingItem(interactionHand);
      return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return ItemTags.PLANKS.contains(var2.getItem()) || super.isValidRepairItem(var1, var2);
   }

   public static DyeColor getColor(ItemStack itemStack) {
      return DyeColor.byId(itemStack.getOrCreateTagElement("BlockEntityTag").getInt("Base"));
   }
}
