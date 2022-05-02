package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TippedArrowRecipe extends CustomRecipe {
   public TippedArrowRecipe(ResourceLocation resourceLocation) {
      super(resourceLocation);
   }

   public boolean matches(CraftingContainer craftingContainer, Level level) {
      if(craftingContainer.getWidth() == 3 && craftingContainer.getHeight() == 3) {
         for(int var3 = 0; var3 < craftingContainer.getWidth(); ++var3) {
            for(int var4 = 0; var4 < craftingContainer.getHeight(); ++var4) {
               ItemStack var5 = craftingContainer.getItem(var3 + var4 * craftingContainer.getWidth());
               if(var5.isEmpty()) {
                  return false;
               }

               Item var6 = var5.getItem();
               if(var3 == 1 && var4 == 1) {
                  if(var6 != Items.LINGERING_POTION) {
                     return false;
                  }
               } else if(var6 != Items.ARROW) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingContainer craftingContainer) {
      ItemStack itemStack = craftingContainer.getItem(1 + craftingContainer.getWidth());
      if(itemStack.getItem() != Items.LINGERING_POTION) {
         return ItemStack.EMPTY;
      } else {
         ItemStack var3 = new ItemStack(Items.TIPPED_ARROW, 8);
         PotionUtils.setPotion(var3, PotionUtils.getPotion(itemStack));
         PotionUtils.setCustomEffects(var3, PotionUtils.getCustomEffects(itemStack));
         return var3;
      }
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 2 && var2 >= 2;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.TIPPED_ARROW;
   }
}
