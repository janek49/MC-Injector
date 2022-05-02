package net.minecraft.world.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EvokerFangs extends Entity {
   private int warmupDelayTicks;
   private boolean sentSpikeEvent;
   private int lifeTicks;
   private boolean clientSideAttackStarted;
   private LivingEntity owner;
   private UUID ownerUUID;

   public EvokerFangs(EntityType entityType, Level level) {
      super(entityType, level);
      this.lifeTicks = 22;
   }

   public EvokerFangs(Level level, double var2, double var4, double var6, float var8, int warmupDelayTicks, LivingEntity owner) {
      this(EntityType.EVOKER_FANGS, level);
      this.warmupDelayTicks = warmupDelayTicks;
      this.setOwner(owner);
      this.yRot = var8 * 57.295776F;
      this.setPos(var2, var4, var6);
   }

   protected void defineSynchedData() {
   }

   public void setOwner(@Nullable LivingEntity owner) {
      this.owner = owner;
      this.ownerUUID = owner == null?null:owner.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if(this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
         Entity var1 = ((ServerLevel)this.level).getEntity(this.ownerUUID);
         if(var1 instanceof LivingEntity) {
            this.owner = (LivingEntity)var1;
         }
      }

      return this.owner;
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      this.warmupDelayTicks = compoundTag.getInt("Warmup");
      if(compoundTag.hasUUID("OwnerUUID")) {
         this.ownerUUID = compoundTag.getUUID("OwnerUUID");
      }

   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putInt("Warmup", this.warmupDelayTicks);
      if(this.ownerUUID != null) {
         compoundTag.putUUID("OwnerUUID", this.ownerUUID);
      }

   }

   public void tick() {
      super.tick();
      if(this.level.isClientSide) {
         if(this.clientSideAttackStarted) {
            --this.lifeTicks;
            if(this.lifeTicks == 14) {
               for(int var1 = 0; var1 < 12; ++var1) {
                  double var2 = this.x + (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.getBbWidth() * 0.5D;
                  double var4 = this.y + 0.05D + this.random.nextDouble();
                  double var6 = this.z + (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.getBbWidth() * 0.5D;
                  double var8 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  double var10 = 0.3D + this.random.nextDouble() * 0.3D;
                  double var12 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  this.level.addParticle(ParticleTypes.CRIT, var2, var4 + 1.0D, var6, var8, var10, var12);
               }
            }
         }
      } else if(--this.warmupDelayTicks < 0) {
         if(this.warmupDelayTicks == -8) {
            for(LivingEntity var3 : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
               this.dealDamageTo(var3);
            }
         }

         if(!this.sentSpikeEvent) {
            this.level.broadcastEntityEvent(this, (byte)4);
            this.sentSpikeEvent = true;
         }

         if(--this.lifeTicks < 0) {
            this.remove();
         }
      }

   }

   private void dealDamageTo(LivingEntity livingEntity) {
      LivingEntity livingEntity = this.getOwner();
      if(livingEntity.isAlive() && !livingEntity.isInvulnerable() && livingEntity != livingEntity) {
         if(livingEntity == null) {
            livingEntity.hurt(DamageSource.MAGIC, 6.0F);
         } else {
            if(livingEntity.isAlliedTo(livingEntity)) {
               return;
            }

            livingEntity.hurt(DamageSource.indirectMagic(this, livingEntity), 6.0F);
         }

      }
   }

   public void handleEntityEvent(byte b) {
      super.handleEntityEvent(b);
      if(b == 4) {
         this.clientSideAttackStarted = true;
         if(!this.isSilent()) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.EVOKER_FANGS_ATTACK, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
         }
      }

   }

   public float getAnimationProgress(float f) {
      if(!this.clientSideAttackStarted) {
         return 0.0F;
      } else {
         int var2 = this.lifeTicks - 2;
         return var2 <= 0?1.0F:1.0F - ((float)var2 - f) / 20.0F;
      }
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
