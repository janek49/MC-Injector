package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SpectralArrow extends AbstractArrow {
   private int duration = 200;

   public SpectralArrow(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public SpectralArrow(Level level, LivingEntity livingEntity) {
      super(EntityType.SPECTRAL_ARROW, livingEntity, level);
   }

   public SpectralArrow(Level level, double var2, double var4, double var6) {
      super(EntityType.SPECTRAL_ARROW, var2, var4, var6, level);
   }

   public void tick() {
      super.tick();
      if(this.level.isClientSide && !this.inGround) {
         this.level.addParticle(ParticleTypes.INSTANT_EFFECT, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
      }

   }

   protected ItemStack getPickupItem() {
      return new ItemStack(Items.SPECTRAL_ARROW);
   }

   protected void doPostHurtEffects(LivingEntity livingEntity) {
      super.doPostHurtEffects(livingEntity);
      MobEffectInstance var2 = new MobEffectInstance(MobEffects.GLOWING, this.duration, 0);
      livingEntity.addEffect(var2);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("Duration")) {
         this.duration = compoundTag.getInt("Duration");
      }

   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("Duration", this.duration);
   }
}
