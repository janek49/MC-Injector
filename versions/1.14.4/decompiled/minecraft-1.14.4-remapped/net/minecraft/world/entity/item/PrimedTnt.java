package net.minecraft.world.entity.item;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class PrimedTnt extends Entity {
   private static final EntityDataAccessor DATA_FUSE_ID = SynchedEntityData.defineId(PrimedTnt.class, EntityDataSerializers.INT);
   @Nullable
   private LivingEntity owner;
   private int life;

   public PrimedTnt(EntityType entityType, Level level) {
      super(entityType, level);
      this.life = 80;
      this.blocksBuilding = true;
   }

   public PrimedTnt(Level level, double xo, double yo, double zo, @Nullable LivingEntity owner) {
      this(EntityType.TNT, level);
      this.setPos(xo, yo, zo);
      double var9 = level.random.nextDouble() * 6.2831854820251465D;
      this.setDeltaMovement(-Math.sin(var9) * 0.02D, 0.20000000298023224D, -Math.cos(var9) * 0.02D);
      this.setFuse(80);
      this.xo = xo;
      this.yo = yo;
      this.zo = zo;
      this.owner = owner;
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_FUSE_ID, Integer.valueOf(80));
   }

   protected boolean makeStepSound() {
      return false;
   }

   public boolean isPickable() {
      return !this.removed;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(!this.isNoGravity()) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
      if(this.onGround) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
      }

      --this.life;
      if(this.life <= 0) {
         this.remove();
         if(!this.level.isClientSide) {
            this.explode();
         }
      } else {
         this.updateInWaterState();
         this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y + 0.5D, this.z, 0.0D, 0.0D, 0.0D);
      }

   }

   private void explode() {
      float var1 = 4.0F;
      this.level.explode(this, this.x, this.y + (double)(this.getBbHeight() / 16.0F), this.z, 4.0F, Explosion.BlockInteraction.BREAK);
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putShort("Fuse", (short)this.getLife());
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      this.setFuse(compoundTag.getShort("Fuse"));
   }

   @Nullable
   public LivingEntity getOwner() {
      return this.owner;
   }

   protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 0.0F;
   }

   public void setFuse(int fuse) {
      this.entityData.set(DATA_FUSE_ID, Integer.valueOf(fuse));
      this.life = fuse;
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      if(DATA_FUSE_ID.equals(entityDataAccessor)) {
         this.life = this.getFuse();
      }

   }

   public int getFuse() {
      return ((Integer)this.entityData.get(DATA_FUSE_ID)).intValue();
   }

   public int getLife() {
      return this.life;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
