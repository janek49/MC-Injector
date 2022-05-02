package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class ResultContainer implements Container, RecipeHolder {
   private final NonNullList itemStacks = NonNullList.withSize(1, ItemStack.EMPTY);
   private Recipe recipeUsed;

   public int getContainerSize() {
      return 1;
   }

   public boolean isEmpty() {
      for(ItemStack var2 : this.itemStacks) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int i) {
      return (ItemStack)this.itemStacks.get(0);
   }

   public ItemStack removeItem(int var1, int var2) {
      return ContainerHelper.takeItem(this.itemStacks, 0);
   }

   public ItemStack removeItemNoUpdate(int i) {
      return ContainerHelper.takeItem(this.itemStacks, 0);
   }

   public void setItem(int var1, ItemStack itemStack) {
      this.itemStacks.set(0, itemStack);
   }

   public void setChanged() {
   }

   public boolean stillValid(Player player) {
      return true;
   }

   public void clearContent() {
      this.itemStacks.clear();
   }

   public void setRecipeUsed(@Nullable Recipe recipeUsed) {
      this.recipeUsed = recipeUsed;
   }

   @Nullable
   public Recipe getRecipeUsed() {
      return this.recipeUsed;
   }
}
