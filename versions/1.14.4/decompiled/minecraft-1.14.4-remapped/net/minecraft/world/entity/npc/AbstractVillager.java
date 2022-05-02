package net.minecraft.world.entity.npc;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public abstract class AbstractVillager extends AgableMob implements Npc, Merchant {
   private static final EntityDataAccessor DATA_UNHAPPY_COUNTER = SynchedEntityData.defineId(AbstractVillager.class, EntityDataSerializers.INT);
   @Nullable
   private Player tradingPlayer;
   @Nullable
   protected MerchantOffers offers;
   private final SimpleContainer inventory = new SimpleContainer(8);

   public AbstractVillager(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public int getUnhappyCounter() {
      return ((Integer)this.entityData.get(DATA_UNHAPPY_COUNTER)).intValue();
   }

   public void setUnhappyCounter(int unhappyCounter) {
      this.entityData.set(DATA_UNHAPPY_COUNTER, Integer.valueOf(unhappyCounter));
   }

   public int getVillagerXp() {
      return 0;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return this.isBaby()?0.81F:1.62F;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_UNHAPPY_COUNTER, Integer.valueOf(0));
   }

   public void setTradingPlayer(@Nullable Player tradingPlayer) {
      this.tradingPlayer = tradingPlayer;
   }

   @Nullable
   public Player getTradingPlayer() {
      return this.tradingPlayer;
   }

   public boolean isTrading() {
      return this.tradingPlayer != null;
   }

   public MerchantOffers getOffers() {
      if(this.offers == null) {
         this.offers = new MerchantOffers();
         this.updateTrades();
      }

      return this.offers;
   }

   public void overrideOffers(@Nullable MerchantOffers merchantOffers) {
   }

   public void overrideXp(int i) {
   }

   public void notifyTrade(MerchantOffer merchantOffer) {
      merchantOffer.increaseUses();
      this.ambientSoundTime = -this.getAmbientSoundInterval();
      this.rewardTradeXp(merchantOffer);
      if(this.tradingPlayer instanceof ServerPlayer) {
         CriteriaTriggers.TRADE.trigger((ServerPlayer)this.tradingPlayer, this, merchantOffer.getResult());
      }

   }

   protected abstract void rewardTradeXp(MerchantOffer var1);

   public boolean showProgressBar() {
      return true;
   }

   public void notifyTradeUpdated(ItemStack itemStack) {
      if(!this.level.isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
         this.ambientSoundTime = -this.getAmbientSoundInterval();
         this.playSound(this.getTradeUpdatedSound(!itemStack.isEmpty()), this.getSoundVolume(), this.getVoicePitch());
      }

   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }

   protected SoundEvent getTradeUpdatedSound(boolean b) {
      return b?SoundEvents.VILLAGER_YES:SoundEvents.VILLAGER_NO;
   }

   public void playCelebrateSound() {
      this.playSound(SoundEvents.VILLAGER_CELEBRATE, this.getSoundVolume(), this.getVoicePitch());
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      MerchantOffers var2 = this.getOffers();
      if(!var2.isEmpty()) {
         compoundTag.put("Offers", var2.createTag());
      }

      ListTag var3 = new ListTag();

      for(int var4 = 0; var4 < this.inventory.getContainerSize(); ++var4) {
         ItemStack var5 = this.inventory.getItem(var4);
         if(!var5.isEmpty()) {
            var3.add(var5.save(new CompoundTag()));
         }
      }

      compoundTag.put("Inventory", var3);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("Offers", 10)) {
         this.offers = new MerchantOffers(compoundTag.getCompound("Offers"));
      }

      ListTag var2 = compoundTag.getList("Inventory", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ItemStack var4 = ItemStack.of(var2.getCompound(var3));
         if(!var4.isEmpty()) {
            this.inventory.addItem(var4);
         }
      }

   }

   @Nullable
   public Entity changeDimension(DimensionType dimensionType) {
      this.stopTrading();
      return super.changeDimension(dimensionType);
   }

   protected void stopTrading() {
      this.setTradingPlayer((Player)null);
   }

   public void die(DamageSource damageSource) {
      super.die(damageSource);
      this.stopTrading();
   }

   protected void addParticlesAroundSelf(ParticleOptions particleOptions) {
      for(int var2 = 0; var2 < 5; ++var2) {
         double var3 = this.random.nextGaussian() * 0.02D;
         double var5 = this.random.nextGaussian() * 0.02D;
         double var7 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(particleOptions, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), this.y + 1.0D + (double)(this.random.nextFloat() * this.getBbHeight()), this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), var3, var5, var7);
      }

   }

   public boolean canBeLeashed(Player player) {
      return false;
   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   public boolean setSlot(int var1, ItemStack itemStack) {
      if(super.setSlot(var1, itemStack)) {
         return true;
      } else {
         int var3 = var1 - 300;
         if(var3 >= 0 && var3 < this.inventory.getContainerSize()) {
            this.inventory.setItem(var3, itemStack);
            return true;
         } else {
            return false;
         }
      }
   }

   public Level getLevel() {
      return this.level;
   }

   protected abstract void updateTrades();

   protected void addOffersFromItemListings(MerchantOffers merchantOffers, VillagerTrades.ItemListing[] villagerTrades$ItemListings, int var3) {
      Set<Integer> var4 = Sets.newHashSet();
      if(villagerTrades$ItemListings.length > var3) {
         while(((Set)var4).size() < var3) {
            var4.add(Integer.valueOf(this.random.nextInt(villagerTrades$ItemListings.length)));
         }
      } else {
         for(int var5 = 0; var5 < villagerTrades$ItemListings.length; ++var5) {
            var4.add(Integer.valueOf(var5));
         }
      }

      for(Integer var6 : var4) {
         VillagerTrades.ItemListing var7 = villagerTrades$ItemListings[var6.intValue()];
         MerchantOffer var8 = var7.getOffer(this, this.random);
         if(var8 != null) {
            merchantOffers.add(var8);
         }
      }

   }
}
