package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class FireworkStarItem extends Item {
   public FireworkStarItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      CompoundTag var5 = itemStack.getTagElement("Explosion");
      if(var5 != null) {
         appendHoverText(var5, list);
      }

   }

   public static void appendHoverText(CompoundTag compoundTag, List list) {
      FireworkRocketItem.Shape var2 = FireworkRocketItem.Shape.byId(compoundTag.getByte("Type"));
      list.add((new TranslatableComponent("item.minecraft.firework_star.shape." + var2.getName(), new Object[0])).withStyle(ChatFormatting.GRAY));
      int[] vars3 = compoundTag.getIntArray("Colors");
      if(vars3.length > 0) {
         list.add(appendColors((new TextComponent("")).withStyle(ChatFormatting.GRAY), vars3));
      }

      int[] vars4 = compoundTag.getIntArray("FadeColors");
      if(vars4.length > 0) {
         list.add(appendColors((new TranslatableComponent("item.minecraft.firework_star.fade_to", new Object[0])).append(" ").withStyle(ChatFormatting.GRAY), vars4));
      }

      if(compoundTag.getBoolean("Trail")) {
         list.add((new TranslatableComponent("item.minecraft.firework_star.trail", new Object[0])).withStyle(ChatFormatting.GRAY));
      }

      if(compoundTag.getBoolean("Flicker")) {
         list.add((new TranslatableComponent("item.minecraft.firework_star.flicker", new Object[0])).withStyle(ChatFormatting.GRAY));
      }

   }

   private static Component appendColors(Component var0, int[] ints) {
      for(int var2 = 0; var2 < ints.length; ++var2) {
         if(var2 > 0) {
            var0.append(", ");
         }

         var0.append(getColorName(ints[var2]));
      }

      return var0;
   }

   private static Component getColorName(int i) {
      DyeColor var1 = DyeColor.byFireworkColor(i);
      return var1 == null?new TranslatableComponent("item.minecraft.firework_star.custom_color", new Object[0]):new TranslatableComponent("item.minecraft.firework_star." + var1.getName(), new Object[0]);
   }
}
