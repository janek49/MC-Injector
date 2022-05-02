package net.minecraft.world.entity;

import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExperienceOrb extends Entity {
   public int tickCount;
   public int age;
   public int throwTime;
   private int health;
   private int value;
   private Player followingPlayer;
   private int followingTime;

   public ExperienceOrb(Level level, double var2, double var4, double var6, int value) {
      this(EntityType.EXPERIENCE_ORB, level);
      this.setPos(var2, var4, var6);
      this.yRot = (float)(this.random.nextDouble() * 360.0D);
      this.setDeltaMovement((this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D, (this.random.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D);
      this.value = value;
   }

   public ExperienceOrb(EntityType entityType, Level level) {
      super(entityType, level);
      this.health = 5;
   }

   protected boolean makeStepSound() {
      return false;
   }

   protected void defineSynchedData() {
   }

   public int getLightColor() {
      float var1 = 0.5F;
      var1 = Mth.clamp(var1, 0.0F, 1.0F);
      int var2 = super.getLightColor();
      int var3 = var2 & 255;
      int var4 = var2 >> 16 & 255;
      var3 = var3 + (int)(var1 * 15.0F * 16.0F);
      if(var3 > 240) {
         var3 = 240;
      }

      return var3 | var4 << 16;
   }

   public void tick() {
      super.tick();
      if(this.throwTime > 0) {
         --this.throwTime;
      }

      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.isUnderLiquid(FluidTags.WATER)) {
         this.setUnderwaterMovement();
      } else if(!this.isNoGravity()) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
      }

      if(this.level.getFluidState(new BlockPos(this)).is(FluidTags.LAVA)) {
         this.setDeltaMovement((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), 0.20000000298023224D, (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
         this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
      }

      if(!this.level.noCollision(this.getBoundingBox())) {
         this.checkInBlock(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.z);
      }

      double var1 = 8.0D;
      if(this.followingTime < this.tickCount - 20 + this.getId() % 100) {
         if(this.followingPlayer == null || this.followingPlayer.distanceToSqr(this) > 64.0D) {
            this.followingPlayer = this.level.getNearestPlayer(this, 8.0D);
         }

         this.followingTime = this.tickCount;
      }

      if(this.followingPlayer != null && this.followingPlayer.isSpectator()) {
         this.followingPlayer = null;
      }

      if(this.followingPlayer != null) {
         Vec3 var3 = new Vec3(this.followingPlayer.x - this.x, this.followingPlayer.y + (double)this.followingPlayer.getEyeHeight() / 2.0D - this.y, this.followingPlayer.z - this.z);
         double var4 = var3.lengthSqr();
         if(var4 < 64.0D) {
            double var6 = 1.0D - Math.sqrt(var4) / 8.0D;
            this.setDeltaMovement(this.getDeltaMovement().add(var3.normalize().scale(var6 * var6 * 0.1D)));
         }
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      float var3 = 0.98F;
      if(this.onGround) {
         var3 = this.level.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0D, this.z)).getBlock().getFriction() * 0.98F;
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply((double)var3, 0.98D, (double)var3));
      if(this.onGround) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.9D, 1.0D));
      }

      ++this.tickCount;
      ++this.age;
      if(this.age >= 6000) {
         this.remove();
      }

   }

   private void setUnderwaterMovement() {
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(var1.x * 0.9900000095367432D, Math.min(var1.y + 5.000000237487257E-4D, 0.05999999865889549D), var1.z * 0.9900000095367432D);
   }

   protected void doWaterSplashEffect() {
   }

   protected void burn(int i) {
      this.hurt(DamageSource.IN_FIRE, (float)i);
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else {
         this.markHurt();
         this.health = (int)((float)this.health - var2);
         if(this.health <= 0) {
            this.remove();
         }

         return false;
      }
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putShort("Health", (short)this.health);
      compoundTag.putShort("Age", (short)this.age);
      compoundTag.putShort("Value", (short)this.value);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      this.health = compoundTag.getShort("Health");
      this.age = compoundTag.getShort("Age");
      this.value = compoundTag.getShort("Value");
   }

   public void playerTouch(Player player) {
      if(!this.level.isClientSide) {
         if(this.throwTime == 0 && player.takeXpDelay == 0) {
            player.takeXpDelay = 2;
            player.take(this, 1);
            Entry<EquipmentSlot, ItemStack> var2 = EnchantmentHelper.getRandomItemWith(Enchantments.MENDING, player);
            if(var2 != null) {
               ItemStack var3 = (ItemStack)var2.getValue();
               if(!var3.isEmpty() && var3.isDamaged()) {
                  int var4 = Math.min(this.xpToDurability(this.value), var3.getDamageValue());
                  this.value -= this.durabilityToXp(var4);
                  var3.setDamageValue(var3.getDamageValue() - var4);
               }
            }

            if(this.value > 0) {
               player.giveExperiencePoints(this.value);
            }

            this.remove();
         }

      }
   }

   private int durabilityToXp(int i) {
      return i / 2;
   }

   private int xpToDurability(int i) {
      return i * 2;
   }

   public int getValue() {
      return this.value;
   }

   public int getIcon() {
      return this.value >= 2477?10:(this.value >= 1237?9:(this.value >= 617?8:(this.value >= 307?7:(this.value >= 149?6:(this.value >= 73?5:(this.value >= 37?4:(this.value >= 17?3:(this.value >= 7?2:(this.value >= 3?1:0)))))))));
   }

   public static int getExperienceValue(int i) {
      return i >= 2477?2477:(i >= 1237?1237:(i >= 617?617:(i >= 307?307:(i >= 149?149:(i >= 73?73:(i >= 37?37:(i >= 17?17:(i >= 7?7:(i >= 3?3:1)))))))));
   }

   public boolean isAttackable() {
      return false;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddExperienceOrbPacket(this);
   }
}
