package net.minecraft.world.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class MerchantMenu extends AbstractContainerMenu {
   private final Merchant trader;
   private final MerchantContainer tradeContainer;
   private int merchantLevel;
   private boolean showProgressBar;
   private boolean canRestock;

   public MerchantMenu(int var1, Inventory inventory) {
      this(var1, inventory, new ClientSideMerchant(inventory.player));
   }

   public MerchantMenu(int var1, Inventory inventory, Merchant trader) {
      super(MenuType.MERCHANT, var1);
      this.trader = trader;
      this.tradeContainer = new MerchantContainer(trader);
      this.addSlot(new Slot(this.tradeContainer, 0, 136, 37));
      this.addSlot(new Slot(this.tradeContainer, 1, 162, 37));
      this.addSlot(new MerchantResultSlot(inventory.player, trader, this.tradeContainer, 2, 220, 37));

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(inventory, var5 + var4 * 9 + 9, 108 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(int var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(inventory, var4, 108 + var4 * 18, 142));
      }

   }

   public void setShowProgressBar(boolean showProgressBar) {
      this.showProgressBar = showProgressBar;
   }

   public void slotsChanged(Container container) {
      this.tradeContainer.updateSellItem();
      super.slotsChanged(container);
   }

   public void setSelectionHint(int selectionHint) {
      this.tradeContainer.setSelectionHint(selectionHint);
   }

   public boolean stillValid(Player player) {
      return this.trader.getTradingPlayer() == player;
   }

   public int getTraderXp() {
      return this.trader.getVillagerXp();
   }

   public int getFutureTraderXp() {
      return this.tradeContainer.getFutureXp();
   }

   public void setXp(int xp) {
      this.trader.overrideXp(xp);
   }

   public int getTraderLevel() {
      return this.merchantLevel;
   }

   public void setMerchantLevel(int merchantLevel) {
      this.merchantLevel = merchantLevel;
   }

   public void setCanRestock(boolean canRestock) {
      this.canRestock = canRestock;
   }

   public boolean canRestock() {
      return this.canRestock;
   }

   public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
      return false;
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         itemStack = var5.copy();
         if(var2 == 2) {
            if(!this.moveItemStackTo(var5, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, itemStack);
            this.playTradeSound();
         } else if(var2 != 0 && var2 != 1) {
            if(var2 >= 3 && var2 < 30) {
               if(!this.moveItemStackTo(var5, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if(var2 >= 30 && var2 < 39 && !this.moveItemStackTo(var5, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if(!this.moveItemStackTo(var5, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if(var5.isEmpty()) {
            var4.set(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if(var5.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(player, var5);
      }

      return itemStack;
   }

   private void playTradeSound() {
      if(!this.trader.getLevel().isClientSide) {
         Entity var1 = (Entity)this.trader;
         this.trader.getLevel().playLocalSound(var1.x, var1.y, var1.z, this.trader.getNotifyTradeSound(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
      }

   }

   public void removed(Player player) {
      super.removed(player);
      this.trader.setTradingPlayer((Player)null);
      if(!this.trader.getLevel().isClientSide) {
         if(!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected()) {
            ItemStack var2 = this.tradeContainer.removeItemNoUpdate(0);
            if(!var2.isEmpty()) {
               player.drop(var2, false);
            }

            var2 = this.tradeContainer.removeItemNoUpdate(1);
            if(!var2.isEmpty()) {
               player.drop(var2, false);
            }
         } else {
            player.inventory.placeItemBackInInventory(player.level, this.tradeContainer.removeItemNoUpdate(0));
            player.inventory.placeItemBackInInventory(player.level, this.tradeContainer.removeItemNoUpdate(1));
         }

      }
   }

   public void tryMoveItems(int i) {
      if(this.getOffers().size() > i) {
         ItemStack var2 = this.tradeContainer.getItem(0);
         if(!var2.isEmpty()) {
            if(!this.moveItemStackTo(var2, 3, 39, true)) {
               return;
            }

            this.tradeContainer.setItem(0, var2);
         }

         ItemStack var3 = this.tradeContainer.getItem(1);
         if(!var3.isEmpty()) {
            if(!this.moveItemStackTo(var3, 3, 39, true)) {
               return;
            }

            this.tradeContainer.setItem(1, var3);
         }

         if(this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
            ItemStack var4 = ((MerchantOffer)this.getOffers().get(i)).getCostA();
            this.moveFromInventoryToPaymentSlot(0, var4);
            ItemStack var5 = ((MerchantOffer)this.getOffers().get(i)).getCostB();
            this.moveFromInventoryToPaymentSlot(1, var5);
         }

      }
   }

   private void moveFromInventoryToPaymentSlot(int var1, ItemStack itemStack) {
      if(!itemStack.isEmpty()) {
         for(int var3 = 3; var3 < 39; ++var3) {
            ItemStack var4 = ((Slot)this.slots.get(var3)).getItem();
            if(!var4.isEmpty() && this.isSameItem(itemStack, var4)) {
               ItemStack var5 = this.tradeContainer.getItem(var1);
               int var6 = var5.isEmpty()?0:var5.getCount();
               int var7 = Math.min(itemStack.getMaxStackSize() - var6, var4.getCount());
               ItemStack var8 = var4.copy();
               int var9 = var6 + var7;
               var4.shrink(var7);
               var8.setCount(var9);
               this.tradeContainer.setItem(var1, var8);
               if(var9 >= itemStack.getMaxStackSize()) {
                  break;
               }
            }
         }
      }

   }

   private boolean isSameItem(ItemStack var1, ItemStack var2) {
      return var1.getItem() == var2.getItem() && ItemStack.tagMatches(var1, var2);
   }

   public void setOffers(MerchantOffers offers) {
      this.trader.overrideOffers(offers);
   }

   public MerchantOffers getOffers() {
      return this.trader.getOffers();
   }

   public boolean showProgressBar() {
      return this.showProgressBar;
   }
}
