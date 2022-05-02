package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class ResultSlot extends Slot {
   private final CraftingContainer craftSlots;
   private final Player player;
   private int removeCount;

   public ResultSlot(Player player, CraftingContainer craftSlots, Container container, int var4, int var5, int var6) {
      super(container, var4, var5, var6);
      this.player = player;
      this.craftSlots = craftSlots;
   }

   public boolean mayPlace(ItemStack itemStack) {
      return false;
   }

   public ItemStack remove(int i) {
      if(this.hasItem()) {
         this.removeCount += Math.min(i, this.getItem().getCount());
      }

      return super.remove(i);
   }

   protected void onQuickCraft(ItemStack itemStack, int var2) {
      this.removeCount += var2;
      this.checkTakeAchievements(itemStack);
   }

   protected void onSwapCraft(int i) {
      this.removeCount += i;
   }

   protected void checkTakeAchievements(ItemStack itemStack) {
      if(this.removeCount > 0) {
         itemStack.onCraftedBy(this.player.level, this.player, this.removeCount);
      }

      if(this.container instanceof RecipeHolder) {
         ((RecipeHolder)this.container).awardAndReset(this.player);
      }

      this.removeCount = 0;
   }

   public ItemStack onTake(Player player, ItemStack var2) {
      this.checkTakeAchievements(var2);
      NonNullList<ItemStack> var3 = player.level.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, this.craftSlots, player.level);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         ItemStack var5 = this.craftSlots.getItem(var4);
         ItemStack var6 = (ItemStack)var3.get(var4);
         if(!var5.isEmpty()) {
            this.craftSlots.removeItem(var4, 1);
            var5 = this.craftSlots.getItem(var4);
         }

         if(!var6.isEmpty()) {
            if(var5.isEmpty()) {
               this.craftSlots.setItem(var4, var6);
            } else if(ItemStack.isSame(var5, var6) && ItemStack.tagMatches(var5, var6)) {
               var6.grow(var5.getCount());
               this.craftSlots.setItem(var4, var6);
            } else if(!this.player.inventory.add(var6)) {
               this.player.drop(var6, false);
            }
         }
      }

      return var2;
   }
}
