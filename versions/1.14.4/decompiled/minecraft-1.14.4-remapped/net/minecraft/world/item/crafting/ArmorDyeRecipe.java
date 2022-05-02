package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ArmorDyeRecipe extends CustomRecipe {
   public ArmorDyeRecipe(ResourceLocation resourceLocation) {
      super(resourceLocation);
   }

   public boolean matches(CraftingContainer craftingContainer, Level level) {
      ItemStack var3 = ItemStack.EMPTY;
      List<ItemStack> var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < craftingContainer.getContainerSize(); ++var5) {
         ItemStack var6 = craftingContainer.getItem(var5);
         if(!var6.isEmpty()) {
            if(var6.getItem() instanceof DyeableLeatherItem) {
               if(!var3.isEmpty()) {
                  return false;
               }

               var3 = var6;
            } else {
               if(!(var6.getItem() instanceof DyeItem)) {
                  return false;
               }

               var4.add(var6);
            }
         }
      }

      return !var3.isEmpty() && !var4.isEmpty();
   }

   public ItemStack assemble(CraftingContainer craftingContainer) {
      List<DyeItem> var2 = Lists.newArrayList();
      ItemStack var3 = ItemStack.EMPTY;

      for(int var4 = 0; var4 < craftingContainer.getContainerSize(); ++var4) {
         ItemStack var5 = craftingContainer.getItem(var4);
         if(!var5.isEmpty()) {
            Item var6 = var5.getItem();
            if(var6 instanceof DyeableLeatherItem) {
               if(!var3.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               var3 = var5.copy();
            } else {
               if(!(var6 instanceof DyeItem)) {
                  return ItemStack.EMPTY;
               }

               var2.add((DyeItem)var6);
            }
         }
      }

      if(!var3.isEmpty() && !var2.isEmpty()) {
         return DyeableLeatherItem.dyeArmor(var3, var2);
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.ARMOR_DYE;
   }
}
