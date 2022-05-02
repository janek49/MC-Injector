package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;

public abstract class CustomRecipe implements CraftingRecipe {
   private final ResourceLocation id;

   public CustomRecipe(ResourceLocation id) {
      this.id = id;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean isSpecial() {
      return true;
   }

   public ItemStack getResultItem() {
      return ItemStack.EMPTY;
   }
}
