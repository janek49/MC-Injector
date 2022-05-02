package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ShulkerBoxColoring extends CustomRecipe {
   public ShulkerBoxColoring(ResourceLocation resourceLocation) {
      super(resourceLocation);
   }

   public boolean matches(CraftingContainer craftingContainer, Level level) {
      int var3 = 0;
      int var4 = 0;

      for(int var5 = 0; var5 < craftingContainer.getContainerSize(); ++var5) {
         ItemStack var6 = craftingContainer.getItem(var5);
         if(!var6.isEmpty()) {
            if(Block.byItem(var6.getItem()) instanceof ShulkerBoxBlock) {
               ++var3;
            } else {
               if(!(var6.getItem() instanceof DyeItem)) {
                  return false;
               }

               ++var4;
            }

            if(var4 > 1 || var3 > 1) {
               return false;
            }
         }
      }

      return var3 == 1 && var4 == 1;
   }

   public ItemStack assemble(CraftingContainer craftingContainer) {
      ItemStack itemStack = ItemStack.EMPTY;
      DyeItem var3 = (DyeItem)Items.WHITE_DYE;

      for(int var4 = 0; var4 < craftingContainer.getContainerSize(); ++var4) {
         ItemStack var5 = craftingContainer.getItem(var4);
         if(!var5.isEmpty()) {
            Item var6 = var5.getItem();
            if(Block.byItem(var6) instanceof ShulkerBoxBlock) {
               itemStack = var5;
            } else if(var6 instanceof DyeItem) {
               var3 = (DyeItem)var6;
            }
         }
      }

      ItemStack var4 = ShulkerBoxBlock.getColoredItemStack(var3.getDyeColor());
      if(itemStack.hasTag()) {
         var4.setTag(itemStack.getTag().copy());
      }

      return var4;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.SHULKER_BOX_COLORING;
   }
}
