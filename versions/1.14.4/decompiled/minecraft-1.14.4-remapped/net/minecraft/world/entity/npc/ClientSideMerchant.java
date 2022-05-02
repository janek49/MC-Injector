package net.minecraft.world.entity.npc;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

public class ClientSideMerchant implements Merchant {
   private final MerchantContainer container;
   private final Player source;
   private MerchantOffers offers = new MerchantOffers();
   private int xp;

   public ClientSideMerchant(Player source) {
      this.source = source;
      this.container = new MerchantContainer(this);
   }

   @Nullable
   public Player getTradingPlayer() {
      return this.source;
   }

   public void setTradingPlayer(@Nullable Player tradingPlayer) {
   }

   public MerchantOffers getOffers() {
      return this.offers;
   }

   public void overrideOffers(@Nullable MerchantOffers offers) {
      this.offers = offers;
   }

   public void notifyTrade(MerchantOffer merchantOffer) {
      merchantOffer.increaseUses();
   }

   public void notifyTradeUpdated(ItemStack itemStack) {
   }

   public Level getLevel() {
      return this.source.level;
   }

   public int getVillagerXp() {
      return this.xp;
   }

   public void overrideXp(int xp) {
      this.xp = xp;
   }

   public boolean showProgressBar() {
      return true;
   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }
}
