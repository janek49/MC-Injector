package net.minecraft.world.item.trading;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

public interface Merchant {
   void setTradingPlayer(@Nullable Player var1);

   @Nullable
   Player getTradingPlayer();

   MerchantOffers getOffers();

   void overrideOffers(@Nullable MerchantOffers var1);

   void notifyTrade(MerchantOffer var1);

   void notifyTradeUpdated(ItemStack var1);

   Level getLevel();

   int getVillagerXp();

   void overrideXp(int var1);

   boolean showProgressBar();

   SoundEvent getNotifyTradeSound();

   default boolean canRestock() {
      return false;
   }

   default void openTradingScreen(Player player, Component component, int var3) {
      OptionalInt var4 = player.openMenu(new SimpleMenuProvider((var1, inventory, player) -> {
         return new MerchantMenu(var1, inventory, this);
      }, component));
      if(var4.isPresent()) {
         MerchantOffers var5 = this.getOffers();
         if(!var5.isEmpty()) {
            player.sendMerchantOffers(var4.getAsInt(), var5, var3, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
         }
      }

   }
}
