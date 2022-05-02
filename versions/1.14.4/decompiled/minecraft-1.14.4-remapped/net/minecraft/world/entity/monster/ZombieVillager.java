package net.minecraft.world.entity.monster;

import com.mojang.datafixers.Dynamic;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ZombieVillager extends Zombie implements VillagerDataHolder {
   private static final EntityDataAccessor DATA_CONVERTING_ID = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor DATA_VILLAGER_DATA = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.VILLAGER_DATA);
   private int villagerConversionTime;
   private UUID conversionStarter;
   private Tag gossips;
   private CompoundTag tradeOffers;
   private int villagerXp;

   public ZombieVillager(EntityType entityType, Level level) {
      super(entityType, level);
      this.setVillagerData(this.getVillagerData().setProfession((VillagerProfession)Registry.VILLAGER_PROFESSION.getRandom(this.random)));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_CONVERTING_ID, Boolean.valueOf(false));
      this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.put("VillagerData", (Tag)this.getVillagerData().serialize(NbtOps.INSTANCE));
      if(this.tradeOffers != null) {
         compoundTag.put("Offers", this.tradeOffers);
      }

      if(this.gossips != null) {
         compoundTag.put("Gossips", this.gossips);
      }

      compoundTag.putInt("ConversionTime", this.isConverting()?this.villagerConversionTime:-1);
      if(this.conversionStarter != null) {
         compoundTag.putUUID("ConversionPlayer", this.conversionStarter);
      }

      compoundTag.putInt("Xp", this.villagerXp);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("VillagerData", 10)) {
         this.setVillagerData(new VillagerData(new Dynamic(NbtOps.INSTANCE, compoundTag.get("VillagerData"))));
      }

      if(compoundTag.contains("Offers", 10)) {
         this.tradeOffers = compoundTag.getCompound("Offers");
      }

      if(compoundTag.contains("Gossips", 10)) {
         this.gossips = compoundTag.getList("Gossips", 10);
      }

      if(compoundTag.contains("ConversionTime", 99) && compoundTag.getInt("ConversionTime") > -1) {
         this.startConverting(compoundTag.hasUUID("ConversionPlayer")?compoundTag.getUUID("ConversionPlayer"):null, compoundTag.getInt("ConversionTime"));
      }

      if(compoundTag.contains("Xp", 3)) {
         this.villagerXp = compoundTag.getInt("Xp");
      }

   }

   public void tick() {
      if(!this.level.isClientSide && this.isAlive() && this.isConverting()) {
         int var1 = this.getConversionProgress();
         this.villagerConversionTime -= var1;
         if(this.villagerConversionTime <= 0) {
            this.finishConversion((ServerLevel)this.level);
         }
      }

      super.tick();
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      if(var3.getItem() == Items.GOLDEN_APPLE && this.hasEffect(MobEffects.WEAKNESS)) {
         if(!player.abilities.instabuild) {
            var3.shrink(1);
         }

         if(!this.level.isClientSide) {
            this.startConverting(player.getUUID(), this.random.nextInt(2401) + 3600);
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean convertsInWater() {
      return false;
   }

   public boolean removeWhenFarAway(double d) {
      return !this.isConverting();
   }

   public boolean isConverting() {
      return ((Boolean)this.getEntityData().get(DATA_CONVERTING_ID)).booleanValue();
   }

   private void startConverting(@Nullable UUID conversionStarter, int villagerConversionTime) {
      this.conversionStarter = conversionStarter;
      this.villagerConversionTime = villagerConversionTime;
      this.getEntityData().set(DATA_CONVERTING_ID, Boolean.valueOf(true));
      this.removeEffect(MobEffects.WEAKNESS);
      this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, villagerConversionTime, Math.min(this.level.getDifficulty().getId() - 1, 0)));
      this.level.broadcastEntityEvent(this, (byte)16);
   }

   public void handleEntityEvent(byte b) {
      if(b == 16) {
         if(!this.isSilent()) {
            this.level.playLocalSound(this.x + 0.5D, this.y + 0.5D, this.z + 0.5D, SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
         }

      } else {
         super.handleEntityEvent(b);
      }
   }

   private void finishConversion(ServerLevel serverLevel) {
      Villager var2 = (Villager)EntityType.VILLAGER.create(serverLevel);
      var2.copyPosition(this);
      var2.setVillagerData(this.getVillagerData());
      if(this.gossips != null) {
         var2.setGossips(this.gossips);
      }

      if(this.tradeOffers != null) {
         var2.setOffers(new MerchantOffers(this.tradeOffers));
      }

      var2.setVillagerXp(this.villagerXp);
      var2.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(new BlockPos(var2)), MobSpawnType.CONVERSION, (SpawnGroupData)null, (CompoundTag)null);
      if(this.isBaby()) {
         var2.setAge(-24000);
      }

      this.remove();
      var2.setNoAi(this.isNoAi());
      if(this.hasCustomName()) {
         var2.setCustomName(this.getCustomName());
         var2.setCustomNameVisible(this.isCustomNameVisible());
      }

      serverLevel.addFreshEntity(var2);
      if(this.conversionStarter != null) {
         Player var3 = serverLevel.getPlayerByUUID(this.conversionStarter);
         if(var3 instanceof ServerPlayer) {
            CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer)var3, this, var2);
            serverLevel.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, var3, var2);
         }
      }

      var2.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
      serverLevel.levelEvent((Player)null, 1027, new BlockPos(this), 0);
   }

   private int getConversionProgress() {
      int var1 = 1;
      if(this.random.nextFloat() < 0.01F) {
         int var2 = 0;
         BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();

         for(int var4 = (int)this.x - 4; var4 < (int)this.x + 4 && var2 < 14; ++var4) {
            for(int var5 = (int)this.y - 4; var5 < (int)this.y + 4 && var2 < 14; ++var5) {
               for(int var6 = (int)this.z - 4; var6 < (int)this.z + 4 && var2 < 14; ++var6) {
                  Block var7 = this.level.getBlockState(var3.set(var4, var5, var6)).getBlock();
                  if(var7 == Blocks.IRON_BARS || var7 instanceof BedBlock) {
                     if(this.random.nextFloat() < 0.3F) {
                        ++var1;
                     }

                     ++var2;
                  }
               }
            }
         }
      }

      return var1;
   }

   protected float getVoicePitch() {
      return this.isBaby()?(this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F:(this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
   }

   public SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.ZOMBIE_VILLAGER_HURT;
   }

   public SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_VILLAGER_DEATH;
   }

   public SoundEvent getStepSound() {
      return SoundEvents.ZOMBIE_VILLAGER_STEP;
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   public void setTradeOffers(CompoundTag tradeOffers) {
      this.tradeOffers = tradeOffers;
   }

   public void setGossips(Tag gossips) {
      this.gossips = gossips;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(levelAccessor.getBiome(new BlockPos(this)))));
      return super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
   }

   public void setVillagerData(VillagerData villagerData) {
      VillagerData villagerData = this.getVillagerData();
      if(villagerData.getProfession() != villagerData.getProfession()) {
         this.tradeOffers = null;
      }

      this.entityData.set(DATA_VILLAGER_DATA, villagerData);
   }

   public VillagerData getVillagerData() {
      return (VillagerData)this.entityData.get(DATA_VILLAGER_DATA);
   }

   public void setVillagerXp(int villagerXp) {
      this.villagerXp = villagerXp;
   }
}
