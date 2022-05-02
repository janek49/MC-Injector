package net.minecraft.world.entity.projectile;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class ThrowableProjectile extends Entity implements Projectile {
   private int xBlock;
   private int yBlock;
   private int zBlock;
   protected boolean inGround;
   public int shakeTime;
   protected LivingEntity owner;
   private UUID ownerId;
   private Entity entityToIgnore;
   private int timeToIgnore;

   protected ThrowableProjectile(EntityType entityType, Level level) {
      super(entityType, level);
      this.xBlock = -1;
      this.yBlock = -1;
      this.zBlock = -1;
   }

   protected ThrowableProjectile(EntityType entityType, double var2, double var4, double var6, Level level) {
      this(entityType, level);
      this.setPos(var2, var4, var6);
   }

   protected ThrowableProjectile(EntityType entityType, LivingEntity owner, Level level) {
      this(entityType, owner.x, owner.y + (double)owner.getEyeHeight() - 0.10000000149011612D, owner.z, level);
      this.owner = owner;
      this.ownerId = owner.getUUID();
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      double var3 = this.getBoundingBox().getSize() * 4.0D;
      if(Double.isNaN(var3)) {
         var3 = 4.0D;
      }

      var3 = var3 * 64.0D;
      return d < var3 * var3;
   }

   public void shootFromRotation(Entity entity, float var2, float var3, float var4, float var5, float var6) {
      float var7 = -Mth.sin(var3 * 0.017453292F) * Mth.cos(var2 * 0.017453292F);
      float var8 = -Mth.sin((var2 + var4) * 0.017453292F);
      float var9 = Mth.cos(var3 * 0.017453292F) * Mth.cos(var2 * 0.017453292F);
      this.shoot((double)var7, (double)var8, (double)var9, var5, var6);
      Vec3 var10 = entity.getDeltaMovement();
      this.setDeltaMovement(this.getDeltaMovement().add(var10.x, entity.onGround?0.0D:var10.y, var10.z));
   }

   public void shoot(double var1, double var3, double var5, float var7, float var8) {
      Vec3 var9 = (new Vec3(var1, var3, var5)).normalize().add(this.random.nextGaussian() * 0.007499999832361937D * (double)var8, this.random.nextGaussian() * 0.007499999832361937D * (double)var8, this.random.nextGaussian() * 0.007499999832361937D * (double)var8).scale((double)var7);
      this.setDeltaMovement(var9);
      float var10 = Mth.sqrt(getHorizontalDistanceSqr(var9));
      this.yRot = (float)(Mth.atan2(var9.x, var9.z) * 57.2957763671875D);
      this.xRot = (float)(Mth.atan2(var9.y, (double)var10) * 57.2957763671875D);
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
      if(this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float var7 = Mth.sqrt(var1 * var1 + var5 * var5);
         this.yRot = (float)(Mth.atan2(var1, var5) * 57.2957763671875D);
         this.xRot = (float)(Mth.atan2(var3, (double)var7) * 57.2957763671875D);
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
      }

   }

   public void tick() {
      this.xOld = this.x;
      this.yOld = this.y;
      this.zOld = this.z;
      super.tick();
      if(this.shakeTime > 0) {
         --this.shakeTime;
      }

      if(this.inGround) {
         this.inGround = false;
         this.setDeltaMovement(this.getDeltaMovement().multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
      }

      AABB var1 = this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D);

      for(Entity var3 : this.level.getEntities((Entity)this, var1, (entity) -> {
         return !entity.isSpectator() && entity.isPickable();
      })) {
         if(var3 == this.entityToIgnore) {
            ++this.timeToIgnore;
            break;
         }

         if(this.owner != null && this.tickCount < 2 && this.entityToIgnore == null) {
            this.entityToIgnore = var3;
            this.timeToIgnore = 3;
            break;
         }
      }

      HitResult var2 = ProjectileUtil.getHitResult(this, var1, (entity) -> {
         return !entity.isSpectator() && entity.isPickable() && entity != this.entityToIgnore;
      }, ClipContext.Block.OUTLINE, true);
      if(this.entityToIgnore != null && this.timeToIgnore-- <= 0) {
         this.entityToIgnore = null;
      }

      if(var2.getType() != HitResult.Type.MISS) {
         if(var2.getType() == HitResult.Type.BLOCK && this.level.getBlockState(((BlockHitResult)var2).getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
            this.handleInsidePortal(((BlockHitResult)var2).getBlockPos());
         } else {
            this.onHit(var2);
         }
      }

      Vec3 var3 = this.getDeltaMovement();
      this.x += var3.x;
      this.y += var3.y;
      this.z += var3.z;
      float var4 = Mth.sqrt(getHorizontalDistanceSqr(var3));
      this.yRot = (float)(Mth.atan2(var3.x, var3.z) * 57.2957763671875D);

      for(this.xRot = (float)(Mth.atan2(var3.y, (double)var4) * 57.2957763671875D); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
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
      float var5;
      if(this.isInWater()) {
         for(int var6 = 0; var6 < 4; ++var6) {
            float var7 = 0.25F;
            this.level.addParticle(ParticleTypes.BUBBLE, this.x - var3.x * 0.25D, this.y - var3.y * 0.25D, this.z - var3.z * 0.25D, var3.x, var3.y, var3.z);
         }

         var5 = 0.8F;
      } else {
         var5 = 0.99F;
      }

      this.setDeltaMovement(var3.scale((double)var5));
      if(!this.isNoGravity()) {
         Vec3 var6 = this.getDeltaMovement();
         this.setDeltaMovement(var6.x, var6.y - (double)this.getGravity(), var6.z);
      }

      this.setPos(this.x, this.y, this.z);
   }

   protected float getGravity() {
      return 0.03F;
   }

   protected abstract void onHit(HitResult var1);

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putInt("xTile", this.xBlock);
      compoundTag.putInt("yTile", this.yBlock);
      compoundTag.putInt("zTile", this.zBlock);
      compoundTag.putByte("shake", (byte)this.shakeTime);
      compoundTag.putByte("inGround", (byte)(this.inGround?1:0));
      if(this.ownerId != null) {
         compoundTag.put("owner", NbtUtils.createUUIDTag(this.ownerId));
      }

   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      this.xBlock = compoundTag.getInt("xTile");
      this.yBlock = compoundTag.getInt("yTile");
      this.zBlock = compoundTag.getInt("zTile");
      this.shakeTime = compoundTag.getByte("shake") & 255;
      this.inGround = compoundTag.getByte("inGround") == 1;
      this.owner = null;
      if(compoundTag.contains("owner", 10)) {
         this.ownerId = NbtUtils.loadUUIDTag(compoundTag.getCompound("owner"));
      }

   }

   @Nullable
   public LivingEntity getOwner() {
      if(this.owner == null && this.ownerId != null && this.level instanceof ServerLevel) {
         Entity var1 = ((ServerLevel)this.level).getEntity(this.ownerId);
         if(var1 instanceof LivingEntity) {
            this.owner = (LivingEntity)var1;
         } else {
            this.ownerId = null;
         }
      }

      return this.owner;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
