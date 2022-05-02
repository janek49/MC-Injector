package net.minecraft.world.inventory;

import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;

public class MerchantResultSlot extends Slot {
   private final MerchantContainer slots;
   private final Player player;
   private int removeCount;
   private final Merchant merchant;

   public MerchantResultSlot(Player player, Merchant merchant, MerchantContainer slots, int var4, int var5, int var6) {
      super(slots, var4, var5, var6);
      this.player = player;
      this.merchant = merchant;
      this.slots = slots;
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

   protected void checkTakeAchievements(ItemStack itemStack) {
      itemStack.onCraftedBy(this.player.level, this.player, this.removeCount);
      this.removeCount = 0;
   }

   public ItemStack onTake(Player player, ItemStack var2) {
      this.checkTakeAchievements(var2);
      MerchantOffer var3 = this.slots.getActiveOffer();
      if(var3 != null) {
         ItemStack var4 = this.slots.getItem(0);
         ItemStack var5 = this.slots.getItem(1);
         if(var3.take(var4, var5) || var3.take(var5, var4)) {
            this.merchant.notifyTrade(var3);
            player.awardStat(Stats.TRADED_WITH_VILLAGER);
            this.slots.setItem(0, var4);
            this.slots.setItem(1, var5);
         }

         this.merchant.overrideXp(this.merchant.getVillagerXp() + var3.getXp());
      }

      return var2;
   }
}
