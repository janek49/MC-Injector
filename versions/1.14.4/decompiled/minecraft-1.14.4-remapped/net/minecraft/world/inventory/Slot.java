package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Slot {
   private final int slot;
   public final Container container;
   public int index;
   public int x;
   public int y;

   public Slot(Container container, int slot, int x, int y) {
      this.container = container;
      this.slot = slot;
      this.x = x;
      this.y = y;
   }

   public void onQuickCraft(ItemStack var1, ItemStack var2) {
      int var3 = var2.getCount() - var1.getCount();
      if(var3 > 0) {
         this.onQuickCraft(var2, var3);
      }

   }

   protected void onQuickCraft(ItemStack itemStack, int var2) {
   }

   protected void onSwapCraft(int i) {
   }

   protected void checkTakeAchievements(ItemStack itemStack) {
   }

   public ItemStack onTake(Player player, ItemStack var2) {
      this.setChanged();
      return var2;
   }

   public boolean mayPlace(ItemStack itemStack) {
      return true;
   }

   public ItemStack getItem() {
      return this.container.getItem(this.slot);
   }

   public boolean hasItem() {
      return !this.getItem().isEmpty();
   }

   public void set(ItemStack itemStack) {
      this.container.setItem(this.slot, itemStack);
      this.setChanged();
   }

   public void setChanged() {
      this.container.setChanged();
   }

   public int getMaxStackSize() {
      return this.container.getMaxStackSize();
   }

   public int getMaxStackSize(ItemStack itemStack) {
      return this.getMaxStackSize();
   }

   @Nullable
   public String getNoItemIcon() {
      return null;
   }

   public ItemStack remove(int i) {
      return this.container.removeItem(this.slot, i);
   }

   public boolean mayPickup(Player player) {
      return true;
   }

   public boolean isActive() {
      return true;
   }
}
