package net.minecraft.world.entity.animal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Pufferfish extends AbstractFish {
   private static final EntityDataAccessor PUFF_STATE = SynchedEntityData.defineId(Pufferfish.class, EntityDataSerializers.INT);
   private int inflateCounter;
   private int deflateTimer;
   private static final Predicate NO_SPECTATORS_AND_NO_WATER_MOB = (livingEntity) -> {
      return livingEntity == null?false:(!(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player)livingEntity).isCreative()?livingEntity.getMobType() != MobType.WATER:false);
   };

   public Pufferfish(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(PUFF_STATE, Integer.valueOf(0));
   }

   public int getPuffState() {
      return ((Integer)this.entityData.get(PUFF_STATE)).intValue();
   }

   public void setPuffState(int puffState) {
      this.entityData.set(PUFF_STATE, Integer.valueOf(puffState));
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      if(PUFF_STATE.equals(entityDataAccessor)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(entityDataAccessor);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("PuffState", this.getPuffState());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setPuffState(compoundTag.getInt("PuffState"));
   }

   protected ItemStack getBucketItemStack() {
      return new ItemStack(Items.PUFFERFISH_BUCKET);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new Pufferfish.PufferfishPuffGoal(this));
   }

   public void tick() {
      if(!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
         if(this.inflateCounter > 0) {
            if(this.getPuffState() == 0) {
               this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
               this.setPuffState(1);
            } else if(this.inflateCounter > 40 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
               this.setPuffState(2);
            }

            ++this.inflateCounter;
         } else if(this.getPuffState() != 0) {
            if(this.deflateTimer > 60 && this.getPuffState() == 2) {
               this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
               this.setPuffState(1);
            } else if(this.deflateTimer > 100 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
               this.setPuffState(0);
            }

            ++this.deflateTimer;
         }
      }

      super.tick();
   }

   public void aiStep() {
      super.aiStep();
      if(this.isAlive() && this.getPuffState() > 0) {
         for(Mob var3 : this.level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(0.3D), NO_SPECTATORS_AND_NO_WATER_MOB)) {
            if(var3.isAlive()) {
               this.touch(var3);
            }
         }
      }

   }

   private void touch(Mob mob) {
      int var2 = this.getPuffState();
      if(mob.hurt(DamageSource.mobAttack(this), (float)(1 + var2))) {
         mob.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * var2, 0));
         this.playSound(SoundEvents.PUFFER_FISH_STING, 1.0F, 1.0F);
      }

   }

   public void playerTouch(Player player) {
      int var2 = this.getPuffState();
      if(player instanceof ServerPlayer && var2 > 0 && player.hurt(DamageSource.mobAttack(this), (float)(1 + var2))) {
         ((ServerPlayer)player).connection.send(new ClientboundGameEventPacket(9, 0.0F));
         player.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * var2, 0));
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PUFFER_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PUFFER_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.PUFFER_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.PUFFER_FISH_FLOP;
   }

   public EntityDimensions getDimensions(Pose pose) {
      return super.getDimensions(pose).scale(getScale(this.getPuffState()));
   }

   private static float getScale(int i) {
      switch(i) {
      case 0:
         return 0.5F;
      case 1:
         return 0.7F;
      default:
         return 1.0F;
      }
   }

   static class PufferfishPuffGoal extends Goal {
      private final Pufferfish fish;

      public PufferfishPuffGoal(Pufferfish fish) {
         this.fish = fish;
      }

      public boolean canUse() {
         List<LivingEntity> var1 = this.fish.level.getEntitiesOfClass(LivingEntity.class, this.fish.getBoundingBox().inflate(2.0D), Pufferfish.NO_SPECTATORS_AND_NO_WATER_MOB);
         return !var1.isEmpty();
      }

      public void start() {
         this.fish.inflateCounter = 1;
         this.fish.deflateTimer = 0;
      }

      public void stop() {
         this.fish.inflateCounter = 0;
      }

      public boolean canContinueToUse() {
         List<LivingEntity> var1 = this.fish.level.getEntitiesOfClass(LivingEntity.class, this.fish.getBoundingBox().inflate(2.0D), Pufferfish.NO_SPECTATORS_AND_NO_WATER_MOB);
         return !var1.isEmpty();
      }
   }
}
