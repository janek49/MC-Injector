package net.minecraft.world.entity.projectile;

import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LlamaSpit extends Entity implements Projectile {
   public Llama owner;
   private CompoundTag ownerTag;

   public LlamaSpit(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public LlamaSpit(Level level, Llama owner) {
      this(EntityType.LLAMA_SPIT, level);
      this.owner = owner;
      this.setPos(owner.x - (double)(owner.getBbWidth() + 1.0F) * 0.5D * (double)Mth.sin(owner.yBodyRot * 0.017453292F), owner.y + (double)owner.getEyeHeight() - 0.10000000149011612D, owner.z + (double)(owner.getBbWidth() + 1.0F) * 0.5D * (double)Mth.cos(owner.yBodyRot * 0.017453292F));
   }

   public LlamaSpit(Level level, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(EntityType.LLAMA_SPIT, level);
      this.setPos(var2, var4, var6);

      for(int var14 = 0; var14 < 7; ++var14) {
         double var15 = 0.4D + 0.1D * (double)var14;
         level.addParticle(ParticleTypes.SPIT, var2, var4, var6, var8 * var15, var10, var12 * var15);
      }

      this.setDeltaMovement(var8, var10, var12);
   }

   public void tick() {
      super.tick();
      if(this.ownerTag != null) {
         this.restoreOwnerFromSave();
      }

      Vec3 var1 = this.getDeltaMovement();
      HitResult var2 = ProjectileUtil.getHitResult(this, this.getBoundingBox().expandTowards(var1).inflate(1.0D), (entity) -> {
         return !entity.isSpectator() && entity != this.owner;
      }, ClipContext.Block.OUTLINE, true);
      if(var2 != null) {
         this.onHit(var2);
      }

      this.x += var1.x;
      this.y += var1.y;
      this.z += var1.z;
      float var3 = Mth.sqrt(getHorizontalDistanceSqr(var1));
      this.yRot = (float)(Mth.atan2(var1.x, var1.z) * 57.2957763671875D);

      for(this.xRot = (float)(Mth.atan2(var1.y, (double)var3) * 57.2957763671875D); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
         ;
      }

      while(this.xRot - this.xRotO >= 180.0F) {
         this.xRotO += 360.0F;
      }

      while(this.yRot - this.yRotO < -180.0F) {
         this.yRotO -= 360.0F;
      }

      while(this.yRot - this.yRotO >= 180.0F) {
         this.yRotO += 360.0F;
      }

      this.xRot = Mth.lerp(0.2F, this.xRotO, this.xRot);
      this.yRot = Mth.lerp(0.2F, this.yRotO, this.yRot);
      float var4 = 0.99F;
      float var5 = 0.06F;
      if(!this.level.containsMaterial(this.getBoundingBox(), Material.AIR)) {
         this.remove();
      } else if(this.isInWaterOrBubble()) {
         this.remove();
      } else {
         this.setDeltaMovement(var1.scale(0.9900000095367432D));
         if(!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.05999999865889549D, 0.0D));
         }

         this.setPos(this.x, this.y, this.z);
      }
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
      if(this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float var7 = Mth.sqrt(var1 * var1 + var5 * var5);
         this.xRot = (float)(Mth.atan2(var3, (double)var7) * 57.2957763671875D);
         this.yRot = (float)(Mth.atan2(var1, var5) * 57.2957763671875D);
         this.xRotO = this.xRot;
         this.yRotO = this.yRot;
         this.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
      }

   }

   public void shoot(double var1, double var3, double var5, float var7, float var8) {
      Vec3 var9 = (new Vec3(var1, var3, var5)).normalize().add(this.random.nextGaussian() * 0.007499999832361937D * (double)var8, this.random.nextGaussian() * 0.007499999832361937D * (double)var8, this.random.nextGaussian() * 0.007499999832361937D * (double)var8).scale((double)var7);
      this.setDeltaMovement(var9);
      float var10 = Mth.sqrt(getHorizontalDistanceSqr(var9));
      this.yRot = (float)(Mth.atan2(var9.x, var5) * 57.2957763671875D);
      this.xRot = (float)(Mth.atan2(var9.y, (double)var10) * 57.2957763671875D);
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   public void onHit(HitResult hitResult) {
      HitResult.Type var2 = hitResult.getType();
      if(var2 == HitResult.Type.ENTITY && this.owner != null) {
         ((EntityHitResult)hitResult).getEntity().hurt(DamageSource.indirectMobAttack(this, this.owner).setProjectile(), 1.0F);
      } else if(var2 == HitResult.Type.BLOCK && !this.level.isClientSide) {
         this.remove();
      }

   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      if(compoundTag.contains("Owner", 10)) {
         this.ownerTag = compoundTag.getCompound("Owner");
      }

   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      if(this.owner != null) {
         CompoundTag compoundTag = new CompoundTag();
         UUID var3 = this.owner.getUUID();
         compoundTag.putUUID("OwnerUUID", var3);
         compoundTag.put("Owner", compoundTag);
      }

   }

   private void restoreOwnerFromSave() {
      if(this.ownerTag != null && this.ownerTag.hasUUID("OwnerUUID")) {
         UUID var1 = this.ownerTag.getUUID("OwnerUUID");

         for(Llama var4 : this.level.getEntitiesOfClass(Llama.class, this.getBoundingBox().inflate(15.0D))) {
            if(var4.getUUID().equals(var1)) {
               this.owner = var4;
               break;
            }
         }
      }

      this.ownerTag = null;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
