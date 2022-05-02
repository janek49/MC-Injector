package net.minecraft.world.entity;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SitGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;

public abstract class TamableAnimal extends Animal {
   protected static final EntityDataAccessor DATA_FLAGS_ID = SynchedEntityData.defineId(TamableAnimal.class, EntityDataSerializers.BYTE);
   protected static final EntityDataAccessor DATA_OWNERUUID_ID = SynchedEntityData.defineId(TamableAnimal.class, EntityDataSerializers.OPTIONAL_UUID);
   protected SitGoal sitGoal;

   protected TamableAnimal(EntityType entityType, Level level) {
      super(entityType, level);
      this.reassessTameGoals();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, Byte.valueOf((byte)0));
      this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      if(this.getOwnerUUID() == null) {
         compoundTag.putString("OwnerUUID", "");
      } else {
         compoundTag.putString("OwnerUUID", this.getOwnerUUID().toString());
      }

      compoundTag.putBoolean("Sitting", this.isSitting());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      String var2;
      if(compoundTag.contains("OwnerUUID", 8)) {
         var2 = compoundTag.getString("OwnerUUID");
      } else {
         String var3 = compoundTag.getString("Owner");
         var2 = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), var3);
      }

      if(!var2.isEmpty()) {
         try {
            this.setOwnerUUID(UUID.fromString(var2));
            this.setTame(true);
         } catch (Throwable var4) {
            this.setTame(false);
         }
      }

      if(this.sitGoal != null) {
         this.sitGoal.wantToSit(compoundTag.getBoolean("Sitting"));
      }

      this.setSitting(compoundTag.getBoolean("Sitting"));
   }

   public boolean canBeLeashed(Player player) {
      return !this.isLeashed();
   }

   protected void spawnTamingParticles(boolean b) {
      ParticleOptions var2 = ParticleTypes.HEART;
      if(!b) {
         var2 = ParticleTypes.SMOKE;
      }

      for(int var3 = 0; var3 < 7; ++var3) {
         double var4 = this.random.nextGaussian() * 0.02D;
         double var6 = this.random.nextGaussian() * 0.02D;
         double var8 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(var2, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), this.y + 0.5D + (double)(this.random.nextFloat() * this.getBbHeight()), this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), var4, var6, var8);
      }

   }

   public void handleEntityEvent(byte b) {
      if(b == 7) {
         this.spawnTamingParticles(true);
      } else if(b == 6) {
         this.spawnTamingParticles(false);
      } else {
         super.handleEntityEvent(b);
      }

   }

   public boolean isTame() {
      return (((Byte)this.entityData.get(DATA_FLAGS_ID)).byteValue() & 4) != 0;
   }

   public void setTame(boolean tame) {
      byte var2 = ((Byte)this.entityData.get(DATA_FLAGS_ID)).byteValue();
      if(tame) {
         this.entityData.set(DATA_FLAGS_ID, Byte.valueOf((byte)(var2 | 4)));
      } else {
         this.entityData.set(DATA_FLAGS_ID, Byte.valueOf((byte)(var2 & -5)));
      }

      this.reassessTameGoals();
   }

   protected void reassessTameGoals() {
   }

   public boolean isSitting() {
      return (((Byte)this.entityData.get(DATA_FLAGS_ID)).byteValue() & 1) != 0;
   }

   public void setSitting(boolean sitting) {
      byte var2 = ((Byte)this.entityData.get(DATA_FLAGS_ID)).byteValue();
      if(sitting) {
         this.entityData.set(DATA_FLAGS_ID, Byte.valueOf((byte)(var2 | 1)));
      } else {
         this.entityData.set(DATA_FLAGS_ID, Byte.valueOf((byte)(var2 & -2)));
      }

   }

   @Nullable
   public UUID getOwnerUUID() {
      return (UUID)((Optional)this.entityData.get(DATA_OWNERUUID_ID)).orElse((Object)null);
   }

   public void setOwnerUUID(@Nullable UUID ownerUUID) {
      this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(ownerUUID));
   }

   public void tame(Player player) {
      this.setTame(true);
      this.setOwnerUUID(player.getUUID());
      if(player instanceof ServerPlayer) {
         CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
      }

   }

   @Nullable
   public LivingEntity getOwner() {
      try {
         UUID var1 = this.getOwnerUUID();
         return var1 == null?null:this.level.getPlayerByUUID(var1);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public boolean canAttack(LivingEntity livingEntity) {
      return this.isOwnedBy(livingEntity)?false:super.canAttack(livingEntity);
   }

   public boolean isOwnedBy(LivingEntity livingEntity) {
      return livingEntity == this.getOwner();
   }

   public SitGoal getSitGoal() {
      return this.sitGoal;
   }

   public boolean wantsToAttack(LivingEntity var1, LivingEntity var2) {
      return true;
   }

   public Team getTeam() {
      if(this.isTame()) {
         LivingEntity var1 = this.getOwner();
         if(var1 != null) {
            return var1.getTeam();
         }
      }

      return super.getTeam();
   }

   public boolean isAlliedTo(Entity entity) {
      if(this.isTame()) {
         LivingEntity var2 = this.getOwner();
         if(entity == var2) {
            return true;
         }

         if(var2 != null) {
            return var2.isAlliedTo(entity);
         }
      }

      return super.isAlliedTo(entity);
   }

   public void die(DamageSource damageSource) {
      if(!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
         this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage());
      }

      super.die(damageSource);
   }
}
