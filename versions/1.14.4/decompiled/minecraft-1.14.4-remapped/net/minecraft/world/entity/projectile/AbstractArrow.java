package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractArrow extends Entity implements Projectile {
   private static final EntityDataAccessor ID_FLAGS = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
   protected static final EntityDataAccessor DATA_OWNERUUID_ID = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor PIERCE_LEVEL = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
   @Nullable
   private BlockState lastState;
   protected boolean inGround;
   protected int inGroundTime;
   public AbstractArrow.Pickup pickup;
   public int shakeTime;
   public UUID ownerUUID;
   private int life;
   private int flightTime;
   private double baseDamage;
   private int knockback;
   private SoundEvent soundEvent;
   private IntOpenHashSet piercingIgnoreEntityIds;
   private List piercedAndKilledEntities;

   protected AbstractArrow(EntityType entityType, Level level) {
      super(entityType, level);
      this.pickup = AbstractArrow.Pickup.DISALLOWED;
      this.baseDamage = 2.0D;
      this.soundEvent = this.getDefaultHitGroundSoundEvent();
   }

   protected AbstractArrow(EntityType entityType, double var2, double var4, double var6, Level level) {
      this(entityType, level);
      this.setPos(var2, var4, var6);
   }

   protected AbstractArrow(EntityType entityType, LivingEntity owner, Level level) {
      this(entityType, owner.x, owner.y + (double)owner.getEyeHeight() - 0.10000000149011612D, owner.z, level);
      this.setOwner(owner);
      if(owner instanceof Player) {
         this.pickup = AbstractArrow.Pickup.ALLOWED;
      }

   }

   public void setSoundEvent(SoundEvent soundEvent) {
      this.soundEvent = soundEvent;
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      double var3 = this.getBoundingBox().getSize() * 10.0D;
      if(Double.isNaN(var3)) {
         var3 = 1.0D;
      }

      var3 = var3 * 64.0D * getViewScale();
      return d < var3 * var3;
   }

   protected void defineSynchedData() {
      this.entityData.define(ID_FLAGS, Byte.valueOf((byte)0));
      this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
      this.entityData.define(PIERCE_LEVEL, Byte.valueOf((byte)0));
   }

   public void shootFromRotation(Entity entity, float var2, float var3, float var4, float var5, float var6) {
      float var7 = -Mth.sin(var3 * 0.017453292F) * Mth.cos(var2 * 0.017453292F);
      float var8 = -Mth.sin(var2 * 0.017453292F);
      float var9 = Mth.cos(var3 * 0.017453292F) * Mth.cos(var2 * 0.017453292F);
      this.shoot((double)var7, (double)var8, (double)var9, var5, var6);
      this.setDeltaMovement(this.getDeltaMovement().add(entity.getDeltaMovement().x, entity.onGround?0.0D:entity.getDeltaMovement().y, entity.getDeltaMovement().z));
   }

   public void shoot(double var1, double var3, double var5, float var7, float var8) {
      Vec3 var9 = (new Vec3(var1, var3, var5)).normalize().add(this.random.nextGaussian() * 0.007499999832361937D * (double)var8, this.random.nextGaussian() * 0.007499999832361937D * (double)var8, this.random.nextGaussian() * 0.007499999832361937D * (double)var8).scale((double)var7);
      this.setDeltaMovement(var9);
      float var10 = Mth.sqrt(getHorizontalDistanceSqr(var9));
      this.yRot = (float)(Mth.atan2(var9.x, var9.z) * 57.2957763671875D);
      this.xRot = (float)(Mth.atan2(var9.y, (double)var10) * 57.2957763671875D);
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
      this.life = 0;
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.setPos(var1, var3, var5);
      this.setRot(var7, var8);
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
         this.life = 0;
      }

   }

   public void tick() {
      super.tick();
      boolean var1 = this.isNoPhysics();
      Vec3 var2 = this.getDeltaMovement();
      if(this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float var3 = Mth.sqrt(getHorizontalDistanceSqr(var2));
         this.yRot = (float)(Mth.atan2(var2.x, var2.z) * 57.2957763671875D);
         this.xRot = (float)(Mth.atan2(var2.y, (double)var3) * 57.2957763671875D);
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
      }

      BlockPos var3 = new BlockPos(this.x, this.y, this.z);
      BlockState var4 = this.level.getBlockState(var3);
      if(!var4.isAir() && !var1) {
         VoxelShape var5 = var4.getCollisionShape(this.level, var3);
         if(!var5.isEmpty()) {
            for(AABB var7 : var5.toAabbs()) {
               if(var7.move(var3).contains(new Vec3(this.x, this.y, this.z))) {
                  this.inGround = true;
                  break;
               }
            }
         }
      }

      if(this.shakeTime > 0) {
         --this.shakeTime;
      }

      if(this.isInWaterOrRain()) {
         this.clearFire();
      }

      if(this.inGround && !var1) {
         if(this.lastState != var4 && this.level.noCollision(this.getBoundingBox().inflate(0.06D))) {
            this.inGround = false;
            this.setDeltaMovement(var2.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
            this.life = 0;
            this.flightTime = 0;
         } else if(!this.level.isClientSide) {
            this.checkDespawn();
         }

         ++this.inGroundTime;
      } else {
         this.inGroundTime = 0;
         ++this.flightTime;
         Vec3 var5 = new Vec3(this.x, this.y, this.z);
         Vec3 var6 = var5.add(var2);
         HitResult var7 = this.level.clip(new ClipContext(var5, var6, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
         if(var7.getType() != HitResult.Type.MISS) {
            var6 = var7.getLocation();
         }

         while(!this.removed) {
            EntityHitResult var8 = this.findHitEntity(var5, var6);
            if(var8 != null) {
               var7 = var8;
            }

            if(var7 != null && var7.getType() == HitResult.Type.ENTITY) {
               Entity var9 = ((EntityHitResult)var7).getEntity();
               Entity var10 = this.getOwner();
               if(var9 instanceof Player && var10 instanceof Player && !((Player)var10).canHarmPlayer((Player)var9)) {
                  var7 = null;
                  var8 = null;
               }
            }

            if(var7 != null && !var1) {
               this.onHit(var7);
               this.hasImpulse = true;
            }

            if(var8 == null || this.getPierceLevel() <= 0) {
               break;
            }

            var7 = null;
         }

         var2 = this.getDeltaMovement();
         double var8 = var2.x;
         double var10 = var2.y;
         double var12 = var2.z;
         if(this.isCritArrow()) {
            for(int var14 = 0; var14 < 4; ++var14) {
               this.level.addParticle(ParticleTypes.CRIT, this.x + var8 * (double)var14 / 4.0D, this.y + var10 * (double)var14 / 4.0D, this.z + var12 * (double)var14 / 4.0D, -var8, -var10 + 0.2D, -var12);
            }
         }

         this.x += var8;
         this.y += var10;
         this.z += var12;
         float var14 = Mth.sqrt(getHorizontalDistanceSqr(var2));
         if(var1) {
            this.yRot = (float)(Mth.atan2(-var8, -var12) * 57.2957763671875D);
         } else {
            this.yRot = (float)(Mth.atan2(var8, var12) * 57.2957763671875D);
         }

         for(this.xRot = (float)(Mth.atan2(var10, (double)var14) * 57.2957763671875D); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
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
         float var15 = 0.99F;
         float var16 = 0.05F;
         if(this.isInWater()) {
            for(int var17 = 0; var17 < 4; ++var17) {
               float var18 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, this.x - var8 * 0.25D, this.y - var10 * 0.25D, this.z - var12 * 0.25D, var8, var10, var12);
            }

            var15 = this.getWaterInertia();
         }

         this.setDeltaMovement(var2.scale((double)var15));
         if(!this.isNoGravity() && !var1) {
            Vec3 var17 = this.getDeltaMovement();
            this.setDeltaMovement(var17.x, var17.y - 0.05000000074505806D, var17.z);
         }

         this.setPos(this.x, this.y, this.z);
         this.checkInsideBlocks();
      }
   }

   protected void checkDespawn() {
      ++this.life;
      if(this.life >= 1200) {
         this.remove();
      }

   }

   protected void onHit(HitResult hitResult) {
      HitResult.Type var2 = hitResult.getType();
      if(var2 == HitResult.Type.ENTITY) {
         this.onHitEntity((EntityHitResult)hitResult);
      } else if(var2 == HitResult.Type.BLOCK) {
         BlockHitResult var3 = (BlockHitResult)hitResult;
         BlockState var4 = this.level.getBlockState(var3.getBlockPos());
         this.lastState = var4;
         Vec3 var5 = var3.getLocation().subtract(this.x, this.y, this.z);
         this.setDeltaMovement(var5);
         Vec3 var6 = var5.normalize().scale(0.05000000074505806D);
         this.x -= var6.x;
         this.y -= var6.y;
         this.z -= var6.z;
         this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         this.inGround = true;
         this.shakeTime = 7;
         this.setCritArrow(false);
         this.setPierceLevel((byte)0);
         this.setSoundEvent(SoundEvents.ARROW_HIT);
         this.setShotFromCrossbow(false);
         this.resetPiercedEntities();
         var4.onProjectileHit(this.level, var4, var3, this);
      }

   }

   private void resetPiercedEntities() {
      if(this.piercedAndKilledEntities != null) {
         this.piercedAndKilledEntities.clear();
      }

      if(this.piercingIgnoreEntityIds != null) {
         this.piercingIgnoreEntityIds.clear();
      }

   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      Entity var2 = entityHitResult.getEntity();
      float var3 = (float)this.getDeltaMovement().length();
      int var4 = Mth.ceil(Math.max((double)var3 * this.baseDamage, 0.0D));
      if(this.getPierceLevel() > 0) {
         if(this.piercingIgnoreEntityIds == null) {
            this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
         }

         if(this.piercedAndKilledEntities == null) {
            this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
         }

         if(this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
            this.remove();
            return;
         }

         this.piercingIgnoreEntityIds.add(var2.getId());
      }

      if(this.isCritArrow()) {
         var4 += this.random.nextInt(var4 / 2 + 2);
      }

      Entity var6 = this.getOwner();
      DamageSource var5;
      if(var6 == null) {
         var5 = DamageSource.arrow(this, this);
      } else {
         var5 = DamageSource.arrow(this, var6);
         if(var6 instanceof LivingEntity) {
            ((LivingEntity)var6).setLastHurtMob(var2);
         }
      }

      int var7 = var2.getRemainingFireTicks();
      if(this.isOnFire() && !(var2 instanceof EnderMan)) {
         var2.setSecondsOnFire(5);
      }

      if(var2.hurt(var5, (float)var4)) {
         if(var2 instanceof LivingEntity) {
            LivingEntity var8 = (LivingEntity)var2;
            if(!this.level.isClientSide && this.getPierceLevel() <= 0) {
               var8.setArrowCount(var8.getArrowCount() + 1);
            }

            if(this.knockback > 0) {
               Vec3 var9 = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockback * 0.6D);
               if(var9.lengthSqr() > 0.0D) {
                  var8.push(var9.x, 0.1D, var9.z);
               }
            }

            if(!this.level.isClientSide && var6 instanceof LivingEntity) {
               EnchantmentHelper.doPostHurtEffects(var8, var6);
               EnchantmentHelper.doPostDamageEffects((LivingEntity)var6, var8);
            }

            this.doPostHurtEffects(var8);
            if(var6 != null && var8 != var6 && var8 instanceof Player && var6 instanceof ServerPlayer) {
               ((ServerPlayer)var6).connection.send(new ClientboundGameEventPacket(6, 0.0F));
            }

            if(!var2.isAlive() && this.piercedAndKilledEntities != null) {
               this.piercedAndKilledEntities.add(var8);
            }

            if(!this.level.isClientSide && var6 instanceof ServerPlayer) {
               ServerPlayer var9 = (ServerPlayer)var6;
               if(this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
                  CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(var9, this.piercedAndKilledEntities, this.piercedAndKilledEntities.size());
               } else if(!var2.isAlive() && this.shotFromCrossbow()) {
                  CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(var9, Arrays.asList(new Entity[]{var2}), 0);
               }
            }
         }

         this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         if(this.getPierceLevel() <= 0 && !(var2 instanceof EnderMan)) {
            this.remove();
         }
      } else {
         var2.setRemainingFireTicks(var7);
         this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
         this.yRot += 180.0F;
         this.yRotO += 180.0F;
         this.flightTime = 0;
         if(!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if(this.pickup == AbstractArrow.Pickup.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.remove();
         }
      }

   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.ARROW_HIT;
   }

   protected final SoundEvent getHitGroundSoundEvent() {
      return this.soundEvent;
   }

   protected void doPostHurtEffects(LivingEntity livingEntity) {
   }

   @Nullable
   protected EntityHitResult findHitEntity(Vec3 var1, Vec3 var2) {
      return ProjectileUtil.getHitResult(this.level, this, var1, var2, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (entity) -> {
         return !entity.isSpectator() && entity.isAlive() && entity.isPickable() && (entity != this.getOwner() || this.flightTime >= 5) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId()));
      });
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putShort("life", (short)this.life);
      if(this.lastState != null) {
         compoundTag.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
      }

      compoundTag.putByte("shake", (byte)this.shakeTime);
      compoundTag.putByte("inGround", (byte)(this.inGround?1:0));
      compoundTag.putByte("pickup", (byte)this.pickup.ordinal());
      compoundTag.putDouble("damage", this.baseDamage);
      compoundTag.putBoolean("crit", this.isCritArrow());
      compoundTag.putByte("PierceLevel", this.getPierceLevel());
      if(this.ownerUUID != null) {
         compoundTag.putUUID("OwnerUUID", this.ownerUUID);
      }

      compoundTag.putString("SoundEvent", Registry.SOUND_EVENT.getKey(this.soundEvent).toString());
      compoundTag.putBoolean("ShotFromCrossbow", this.shotFromCrossbow());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      this.life = compoundTag.getShort("life");
      if(compoundTag.contains("inBlockState", 10)) {
         this.lastState = NbtUtils.readBlockState(compoundTag.getCompound("inBlockState"));
      }

      this.shakeTime = compoundTag.getByte("shake") & 255;
      this.inGround = compoundTag.getByte("inGround") == 1;
      if(compoundTag.contains("damage", 99)) {
         this.baseDamage = compoundTag.getDouble("damage");
      }

      if(compoundTag.contains("pickup", 99)) {
         this.pickup = AbstractArrow.Pickup.byOrdinal(compoundTag.getByte("pickup"));
      } else if(compoundTag.contains("player", 99)) {
         this.pickup = compoundTag.getBoolean("player")?AbstractArrow.Pickup.ALLOWED:AbstractArrow.Pickup.DISALLOWED;
      }

      this.setCritArrow(compoundTag.getBoolean("crit"));
      this.setPierceLevel(compoundTag.getByte("PierceLevel"));
      if(compoundTag.hasUUID("OwnerUUID")) {
         this.ownerUUID = compoundTag.getUUID("OwnerUUID");
      }

      if(compoundTag.contains("SoundEvent", 8)) {
         this.soundEvent = (SoundEvent)Registry.SOUND_EVENT.getOptional(new ResourceLocation(compoundTag.getString("SoundEvent"))).orElse(this.getDefaultHitGroundSoundEvent());
      }

      this.setShotFromCrossbow(compoundTag.getBoolean("ShotFromCrossbow"));
   }

   public void setOwner(@Nullable Entity owner) {
      this.ownerUUID = owner == null?null:owner.getUUID();
      if(owner instanceof Player) {
         this.pickup = ((Player)owner).abilities.instabuild?AbstractArrow.Pickup.CREATIVE_ONLY:AbstractArrow.Pickup.ALLOWED;
      }

   }

   @Nullable
   public Entity getOwner() {
      return this.ownerUUID != null && this.level instanceof ServerLevel?((ServerLevel)this.level).getEntity(this.ownerUUID):null;
   }

   public void playerTouch(Player player) {
      if(!this.level.isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
         boolean var2 = this.pickup == AbstractArrow.Pickup.ALLOWED || this.pickup == AbstractArrow.Pickup.CREATIVE_ONLY && player.abilities.instabuild || this.isNoPhysics() && this.getOwner().getUUID() == player.getUUID();
         if(this.pickup == AbstractArrow.Pickup.ALLOWED && !player.inventory.add(this.getPickupItem())) {
            var2 = false;
         }

         if(var2) {
            player.take(this, 1);
            this.remove();
         }

      }
   }

   protected abstract ItemStack getPickupItem();

   protected boolean makeStepSound() {
      return false;
   }

   public void setBaseDamage(double baseDamage) {
      this.baseDamage = baseDamage;
   }

   public double getBaseDamage() {
      return this.baseDamage;
   }

   public void setKnockback(int knockback) {
      this.knockback = knockback;
   }

   public boolean isAttackable() {
      return false;
   }

   protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 0.0F;
   }

   public void setCritArrow(boolean critArrow) {
      this.setFlag(1, critArrow);
   }

   public void setPierceLevel(byte pierceLevel) {
      this.entityData.set(PIERCE_LEVEL, Byte.valueOf(pierceLevel));
   }

   private void setFlag(int var1, boolean var2) {
      byte var3 = ((Byte)this.entityData.get(ID_FLAGS)).byteValue();
      if(var2) {
         this.entityData.set(ID_FLAGS, Byte.valueOf((byte)(var3 | var1)));
      } else {
         this.entityData.set(ID_FLAGS, Byte.valueOf((byte)(var3 & ~var1)));
      }

   }

   public boolean isCritArrow() {
      byte var1 = ((Byte)this.entityData.get(ID_FLAGS)).byteValue();
      return (var1 & 1) != 0;
   }

   public boolean shotFromCrossbow() {
      byte var1 = ((Byte)this.entityData.get(ID_FLAGS)).byteValue();
      return (var1 & 4) != 0;
   }

   public byte getPierceLevel() {
      return ((Byte)this.entityData.get(PIERCE_LEVEL)).byteValue();
   }

   public void setEnchantmentEffectsFromEntity(LivingEntity livingEntity, float var2) {
      int var3 = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, livingEntity);
      int var4 = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, livingEntity);
      this.setBaseDamage((double)(var2 * 2.0F) + this.random.nextGaussian() * 0.25D + (double)((float)this.level.getDifficulty().getId() * 0.11F));
      if(var3 > 0) {
         this.setBaseDamage(this.getBaseDamage() + (double)var3 * 0.5D + 0.5D);
      }

      if(var4 > 0) {
         this.setKnockback(var4);
      }

      if(EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, livingEntity) > 0) {
         this.setSecondsOnFire(100);
      }

   }

   protected float getWaterInertia() {
      return 0.6F;
   }

   public void setNoPhysics(boolean noPhysics) {
      this.noPhysics = noPhysics;
      this.setFlag(2, noPhysics);
   }

   public boolean isNoPhysics() {
      return !this.level.isClientSide?this.noPhysics:(((Byte)this.entityData.get(ID_FLAGS)).byteValue() & 2) != 0;
   }

   public void setShotFromCrossbow(boolean shotFromCrossbow) {
      this.setFlag(4, shotFromCrossbow);
   }

   public Packet getAddEntityPacket() {
      Entity var1 = this.getOwner();
      return new ClientboundAddEntityPacket(this, var1 == null?0:var1.getId());
   }

   public static enum Pickup {
      DISALLOWED,
      ALLOWED,
      CREATIVE_ONLY;

      public static AbstractArrow.Pickup byOrdinal(int ordinal) {
         if(ordinal < 0 || ordinal > values().length) {
            ordinal = 0;
         }

         return values()[ordinal];
      }
   }
}
