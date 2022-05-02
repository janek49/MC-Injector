package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownTrident extends AbstractArrow {
   private static final EntityDataAccessor ID_LOYALTY = SynchedEntityData.defineId(ThrownTrident.class, EntityDataSerializers.BYTE);
   private ItemStack tridentItem = new ItemStack(Items.TRIDENT);
   private boolean dealtDamage;
   public int clientSideReturnTridentTickCount;

   public ThrownTrident(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public ThrownTrident(Level level, LivingEntity livingEntity, ItemStack itemStack) {
      super(EntityType.TRIDENT, livingEntity, level);
      this.tridentItem = itemStack.copy();
      this.entityData.set(ID_LOYALTY, Byte.valueOf((byte)EnchantmentHelper.getLoyalty(itemStack)));
   }

   public ThrownTrident(Level level, double var2, double var4, double var6) {
      super(EntityType.TRIDENT, var2, var4, var6, level);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_LOYALTY, Byte.valueOf((byte)0));
   }

   public void tick() {
      if(this.inGroundTime > 4) {
         this.dealtDamage = true;
      }

      Entity var1 = this.getOwner();
      if((this.dealtDamage || this.isNoPhysics()) && var1 != null) {
         int var2 = ((Byte)this.entityData.get(ID_LOYALTY)).byteValue();
         if(var2 > 0 && !this.isAcceptibleReturnOwner()) {
            if(!this.level.isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.remove();
         } else if(var2 > 0) {
            this.setNoPhysics(true);
            Vec3 var3 = new Vec3(var1.x - this.x, var1.y + (double)var1.getEyeHeight() - this.y, var1.z - this.z);
            this.y += var3.y * 0.015D * (double)var2;
            if(this.level.isClientSide) {
               this.yOld = this.y;
            }

            double var4 = 0.05D * (double)var2;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(var3.normalize().scale(var4)));
            if(this.clientSideReturnTridentTickCount == 0) {
               this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
            }

            ++this.clientSideReturnTridentTickCount;
         }
      }

      super.tick();
   }

   private boolean isAcceptibleReturnOwner() {
      Entity var1 = this.getOwner();
      return var1 != null && var1.isAlive()?!(var1 instanceof ServerPlayer) || !var1.isSpectator():false;
   }

   protected ItemStack getPickupItem() {
      return this.tridentItem.copy();
   }

   @Nullable
   protected EntityHitResult findHitEntity(Vec3 var1, Vec3 var2) {
      return this.dealtDamage?null:super.findHitEntity(var1, var2);
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      Entity var2 = entityHitResult.getEntity();
      float var3 = 8.0F;
      if(var2 instanceof LivingEntity) {
         LivingEntity var4 = (LivingEntity)var2;
         var3 += EnchantmentHelper.getDamageBonus(this.tridentItem, var4.getMobType());
      }

      Entity var4 = this.getOwner();
      DamageSource var5 = DamageSource.trident(this, (Entity)(var4 == null?this:var4));
      this.dealtDamage = true;
      SoundEvent var6 = SoundEvents.TRIDENT_HIT;
      if(var2.hurt(var5, var3) && var2 instanceof LivingEntity) {
         LivingEntity var7 = (LivingEntity)var2;
         if(var4 instanceof LivingEntity) {
            EnchantmentHelper.doPostHurtEffects(var7, var4);
            EnchantmentHelper.doPostDamageEffects((LivingEntity)var4, var7);
         }

         this.doPostHurtEffects(var7);
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
      float var7 = 1.0F;
      if(this.level instanceof ServerLevel && this.level.isThundering() && EnchantmentHelper.hasChanneling(this.tridentItem)) {
         BlockPos var8 = var2.getCommandSenderBlockPosition();
         if(this.level.canSeeSky(var8)) {
            LightningBolt var9 = new LightningBolt(this.level, (double)var8.getX() + 0.5D, (double)var8.getY(), (double)var8.getZ() + 0.5D, false);
            var9.setCause(var4 instanceof ServerPlayer?(ServerPlayer)var4:null);
            ((ServerLevel)this.level).addGlobalEntity(var9);
            var6 = SoundEvents.TRIDENT_THUNDER;
            var7 = 5.0F;
         }
      }

      this.playSound(var6, var7, 1.0F);
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.TRIDENT_HIT_GROUND;
   }

   public void playerTouch(Player player) {
      Entity var2 = this.getOwner();
      if(var2 == null || var2.getUUID() == player.getUUID()) {
         super.playerTouch(player);
      }
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("Trident", 10)) {
         this.tridentItem = ItemStack.of(compoundTag.getCompound("Trident"));
      }

      this.dealtDamage = compoundTag.getBoolean("DealtDamage");
      this.entityData.set(ID_LOYALTY, Byte.valueOf((byte)EnchantmentHelper.getLoyalty(this.tridentItem)));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.put("Trident", this.tridentItem.save(new CompoundTag()));
      compoundTag.putBoolean("DealtDamage", this.dealtDamage);
   }

   protected void checkDespawn() {
      int var1 = ((Byte)this.entityData.get(ID_LOYALTY)).byteValue();
      if(this.pickup != AbstractArrow.Pickup.ALLOWED || var1 <= 0) {
         super.checkDespawn();
      }

   }

   protected float getWaterInertia() {
      return 0.99F;
   }

   public boolean shouldRender(double var1, double var3, double var5) {
      return true;
   }
}
