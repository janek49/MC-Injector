package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public interface Recipe {
   boolean matches(Container var1, Level var2);

   ItemStack assemble(Container var1);

   boolean canCraftInDimensions(int var1, int var2);

   ItemStack getResultItem();

   default NonNullList getRemainingItems(Container container) {
      NonNullList<ItemStack> nonNullList = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

      for(int var3 = 0; var3 < nonNullList.size(); ++var3) {
         Item var4 = container.getItem(var3).getItem();
         if(var4.hasCraftingRemainingItem()) {
            nonNullList.set(var3, new ItemStack(var4.getCraftingRemainingItem()));
         }
      }

      return nonNullList;
   }

   default NonNullList getIngredients() {
      return NonNullList.create();
   }

   default boolean isSpecial() {
      return false;
   }

   default String getGroup() {
      return "";
   }

   default ItemStack getToastSymbol() {
      return new ItemStack(Blocks.CRAFTING_TABLE);
   }

   ResourceLocation getId();

   RecipeSerializer getSerializer();

   RecipeType getType();
}
