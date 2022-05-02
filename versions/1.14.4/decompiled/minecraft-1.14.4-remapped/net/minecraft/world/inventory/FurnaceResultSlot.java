package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class FurnaceResultSlot extends Slot {
   private final Player player;
   private int removeCount;

   public FurnaceResultSlot(Player player, Container container, int var3, int var4, int var5) {
      super(container, var3, var4, var5);
      this.player = player;
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

   public ItemStack onTake(Player player, ItemStack var2) {
      this.checkTakeAchievements(var2);
      super.onTake(player, var2);
      return var2;
   }

   protected void onQuickCraft(ItemStack itemStack, int var2) {
      this.removeCount += var2;
      this.checkTakeAchievements(itemStack);
   }

   protected void checkTakeAchievements(ItemStack itemStack) {
      itemStack.onCraftedBy(this.player.level, this.player, this.removeCount);
      if(!this.player.level.isClientSide && this.container instanceof AbstractFurnaceBlockEntity) {
         ((AbstractFurnaceBlockEntity)this.container).awardResetAndExperience(this.player);
      }

      this.removeCount = 0;
   }
}
