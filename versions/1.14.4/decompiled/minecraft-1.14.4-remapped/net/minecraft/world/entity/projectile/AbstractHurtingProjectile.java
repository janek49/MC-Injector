package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHurtingProjectile extends Entity {
   public LivingEntity owner;
   private int life;
   private int flightTime;
   public double xPower;
   public double yPower;
   public double zPower;

   protected AbstractHurtingProjectile(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public AbstractHurtingProjectile(EntityType entityType, double var2, double var4, double var6, double var8, double var10, double var12, Level level) {
      this(entityType, level);
      this.moveTo(var2, var4, var6, this.yRot, this.xRot);
      this.setPos(var2, var4, var6);
      double var15 = (double)Mth.sqrt(var8 * var8 + var10 * var10 + var12 * var12);
      this.xPower = var8 / var15 * 0.1D;
      this.yPower = var10 / var15 * 0.1D;
      this.zPower = var12 / var15 * 0.1D;
   }

   public AbstractHurtingProjectile(EntityType entityType, LivingEntity owner, double var3, double var5, double var7, Level level) {
      this(entityType, level);
      this.owner = owner;
      this.moveTo(owner.x, owner.y, owner.z, owner.yRot, owner.xRot);
      this.setPos(this.x, this.y, this.z);
      this.setDeltaMovement(Vec3.ZERO);
      var3 = var3 + this.random.nextGaussian() * 0.4D;
      var5 = var5 + this.random.nextGaussian() * 0.4D;
      var7 = var7 + this.random.nextGaussian() * 0.4D;
      double var10 = (double)Mth.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
      this.xPower = var3 / var10 * 0.1D;
      this.yPower = var5 / var10 * 0.1D;
      this.zPower = var7 / var10 * 0.1D;
   }

   protected void defineSynchedData() {
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      double var3 = this.getBoundingBox().getSize() * 4.0D;
      if(Double.isNaN(var3)) {
         var3 = 4.0D;
      }

      var3 = var3 * 64.0D;
      return d < var3 * var3;
   }

   public void tick() {
      if(this.level.isClientSide || (this.owner == null || !this.owner.removed) && this.level.hasChunkAt(new BlockPos(this))) {
         super.tick();
         if(this.shouldBurn()) {
            this.setSecondsOnFire(1);
         }

         ++this.flightTime;
         HitResult var1 = ProjectileUtil.forwardsRaycast(this, true, this.flightTime >= 25, this.owner, ClipContext.Block.COLLIDER);
         if(var1.getType() != HitResult.Type.MISS) {
            this.onHit(var1);
         }

         Vec3 var2 = this.getDeltaMovement();
         this.x += var2.x;
         this.y += var2.y;
         this.z += var2.z;
         ProjectileUtil.rotateTowardsMovement(this, 0.2F);
         float var3 = this.getInertia();
         if(this.isInWater()) {
            for(int var4 = 0; var4 < 4; ++var4) {
               float var5 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, this.x - var2.x * 0.25D, this.y - var2.y * 0.25D, this.z - var2.z * 0.25D, var2.x, var2.y, var2.z);
            }

            var3 = 0.8F;
         }

         this.setDeltaMovement(var2.add(this.xPower, this.yPower, this.zPower).scale((double)var3));
         this.level.addParticle(this.getTrailParticle(), this.x, this.y + 0.5D, this.z, 0.0D, 0.0D, 0.0D);
         this.setPos(this.x, this.y, this.z);
      } else {
         this.remove();
      }
   }

   protected boolean shouldBurn() {
      return true;
   }

   protected ParticleOptions getTrailParticle() {
      return ParticleTypes.SMOKE;
   }

   protected float getInertia() {
      return 0.95F;
   }

   protected abstract void onHit(HitResult var1);

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      Vec3 var2 = this.getDeltaMovement();
      compoundTag.put("direction", this.newDoubleList(new double[]{var2.x, var2.y, var2.z}));
      compoundTag.put("power", this.newDoubleList(new double[]{this.xPower, this.yPower, this.zPower}));
      compoundTag.putInt("life", this.life);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      if(compoundTag.contains("power", 9)) {
         ListTag var2 = compoundTag.getList("power", 6);
         if(var2.size() == 3) {
            this.xPower = var2.getDouble(0);
            this.yPower = var2.getDouble(1);
            this.zPower = var2.getDouble(2);
         }
      }

      this.life = compoundTag.getInt("life");
      if(compoundTag.contains("direction", 9) && compoundTag.getList("direction", 6).size() == 3) {
         ListTag var2 = compoundTag.getList("direction", 6);
         this.setDeltaMovement(var2.getDouble(0), var2.getDouble(1), var2.getDouble(2));
      } else {
         this.remove();
      }

   }

   public boolean isPickable() {
      return true;
   }

   public float getPickRadius() {
      return 1.0F;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else {
         this.markHurt();
         if(damageSource.getEntity() != null) {
            Vec3 var3 = damageSource.getEntity().getLookAngle();
            this.setDeltaMovement(var3);
            this.xPower = var3.x * 0.1D;
            this.yPower = var3.y * 0.1D;
            this.zPower = var3.z * 0.1D;
            if(damageSource.getEntity() instanceof LivingEntity) {
               this.owner = (LivingEntity)damageSource.getEntity();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public float getBrightness() {
      return 1.0F;
   }

   public int getLightColor() {
      return 15728880;
   }

   public Packet getAddEntityPacket() {
      int var1 = this.owner == null?0:this.owner.getId();
      return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.x, this.y, this.z, this.xRot, this.yRot, this.getType(), var1, new Vec3(this.xPower, this.yPower, this.zPower));
   }
}
