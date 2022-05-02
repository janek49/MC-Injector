package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class WitherSkull extends AbstractHurtingProjectile {
   private static final EntityDataAccessor DATA_DANGEROUS = SynchedEntityData.defineId(WitherSkull.class, EntityDataSerializers.BOOLEAN);

   public WitherSkull(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public WitherSkull(Level level, LivingEntity livingEntity, double var3, double var5, double var7) {
      super(EntityType.WITHER_SKULL, livingEntity, var3, var5, var7, level);
   }

   public WitherSkull(Level level, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(EntityType.WITHER_SKULL, var2, var4, var6, var8, var10, var12, level);
   }

   protected float getInertia() {
      return this.isDangerous()?0.73F:super.getInertia();
   }

   public boolean isOnFire() {
      return false;
   }

   public float getBlockExplosionResistance(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, FluidState fluidState, float var6) {
      return this.isDangerous() && WitherBoss.canDestroy(blockState)?Math.min(0.8F, var6):var6;
   }

   protected void onHit(HitResult hitResult) {
      if(!this.level.isClientSide) {
         if(hitResult.getType() == HitResult.Type.ENTITY) {
            Entity var2 = ((EntityHitResult)hitResult).getEntity();
            if(this.owner != null) {
               if(var2.hurt(DamageSource.mobAttack(this.owner), 8.0F)) {
                  if(var2.isAlive()) {
                     this.doEnchantDamageEffects(this.owner, var2);
                  } else {
                     this.owner.heal(5.0F);
                  }
               }
            } else {
               var2.hurt(DamageSource.MAGIC, 5.0F);
            }

            if(var2 instanceof LivingEntity) {
               int var3 = 0;
               if(this.level.getDifficulty() == Difficulty.NORMAL) {
                  var3 = 10;
               } else if(this.level.getDifficulty() == Difficulty.HARD) {
                  var3 = 40;
               }

               if(var3 > 0) {
                  ((LivingEntity)var2).addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * var3, 1));
               }
            }
         }

         Explosion.BlockInteraction var2 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)?Explosion.BlockInteraction.DESTROY:Explosion.BlockInteraction.NONE;
         this.level.explode(this, this.x, this.y, this.z, 1.0F, false, var2);
         this.remove();
      }

   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      return false;
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_DANGEROUS, Boolean.valueOf(false));
   }

   public boolean isDangerous() {
      return ((Boolean)this.entityData.get(DATA_DANGEROUS)).booleanValue();
   }

   public void setDangerous(boolean dangerous) {
      this.entityData.set(DATA_DANGEROUS, Boolean.valueOf(dangerous));
   }

   protected boolean shouldBurn() {
      return false;
   }
}
