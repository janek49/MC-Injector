package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

public class EnchantedBookItem extends Item {
   public EnchantedBookItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public boolean isFoil(ItemStack itemStack) {
      return true;
   }

   public boolean isEnchantable(ItemStack itemStack) {
      return false;
   }

   public static ListTag getEnchantments(ItemStack itemStack) {
      CompoundTag var1 = itemStack.getTag();
      return var1 != null?var1.getList("StoredEnchantments", 10):new ListTag();
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      super.appendHoverText(itemStack, level, list, tooltipFlag);
      ItemStack.appendEnchantmentNames(list, getEnchantments(itemStack));
   }

   public static void addEnchantment(ItemStack itemStack, EnchantmentInstance enchantmentInstance) {
      ListTag var2 = getEnchantments(itemStack);
      boolean var3 = true;
      ResourceLocation var4 = Registry.ENCHANTMENT.getKey(enchantmentInstance.enchantment);

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         CompoundTag var6 = var2.getCompound(var5);
         ResourceLocation var7 = ResourceLocation.tryParse(var6.getString("id"));
         if(var7 != null && var7.equals(var4)) {
            if(var6.getInt("lvl") < enchantmentInstance.level) {
               var6.putShort("lvl", (short)enchantmentInstance.level);
            }

            var3 = false;
            break;
         }
      }

      if(var3) {
         CompoundTag var5 = new CompoundTag();
         var5.putString("id", String.valueOf(var4));
         var5.putShort("lvl", (short)enchantmentInstance.level);
         var2.add(var5);
      }

      itemStack.getOrCreateTag().put("StoredEnchantments", var2);
   }

   public static ItemStack createForEnchantment(EnchantmentInstance enchantmentInstance) {
      ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
      addEnchantment(itemStack, enchantmentInstance);
      return itemStack;
   }

   public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList nonNullList) {
      if(creativeModeTab == CreativeModeTab.TAB_SEARCH) {
         for(Enchantment var4 : Registry.ENCHANTMENT) {
            if(var4.category != null) {
               for(int var5 = var4.getMinLevel(); var5 <= var4.getMaxLevel(); ++var5) {
                  nonNullList.add(createForEnchantment(new EnchantmentInstance(var4, var5)));
               }
            }
         }
      } else if(creativeModeTab.getEnchantmentCategories().length != 0) {
         for(Enchantment var4 : Registry.ENCHANTMENT) {
            if(creativeModeTab.hasEnchantmentCategory(var4.category)) {
               nonNullList.add(createForEnchantment(new EnchantmentInstance(var4, var4.getMaxLevel())));
            }
         }
      }

   }
}
