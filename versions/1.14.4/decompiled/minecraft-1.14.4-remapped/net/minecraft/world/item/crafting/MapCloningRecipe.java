package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class MapCloningRecipe extends CustomRecipe {
   public MapCloningRecipe(ResourceLocation resourceLocation) {
      super(resourceLocation);
   }

   public boolean matches(CraftingContainer craftingContainer, Level level) {
      int var3 = 0;
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < craftingContainer.getContainerSize(); ++var5) {
         ItemStack var6 = craftingContainer.getItem(var5);
         if(!var6.isEmpty()) {
            if(var6.getItem() == Items.FILLED_MAP) {
               if(!var4.isEmpty()) {
                  return false;
               }

               var4 = var6;
            } else {
               if(var6.getItem() != Items.MAP) {
                  return false;
               }

               ++var3;
            }
         }
      }

      return !var4.isEmpty() && var3 > 0;
   }

   public ItemStack assemble(CraftingContainer craftingContainer) {
      int var2 = 0;
      ItemStack var3 = ItemStack.EMPTY;

      for(int var4 = 0; var4 < craftingContainer.getContainerSize(); ++var4) {
         ItemStack var5 = craftingContainer.getItem(var4);
         if(!var5.isEmpty()) {
            if(var5.getItem() == Items.FILLED_MAP) {
               if(!var3.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               var3 = var5;
            } else {
               if(var5.getItem() != Items.MAP) {
                  return ItemStack.EMPTY;
               }

               ++var2;
            }
         }
      }

      if(!var3.isEmpty() && var2 >= 1) {
         ItemStack var4 = var3.copy();
         var4.setCount(var2 + 1);
         return var4;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 3 && var2 >= 3;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.MAP_CLONING;
   }
}
