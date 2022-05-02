package net.minecraft.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class LargeFireball extends Fireball {
   public int explosionPower = 1;

   public LargeFireball(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public LargeFireball(Level level, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(EntityType.FIREBALL, var2, var4, var6, var8, var10, var12, level);
   }

   public LargeFireball(Level level, LivingEntity livingEntity, double var3, double var5, double var7) {
      super(EntityType.FIREBALL, livingEntity, var3, var5, var7, level);
   }

   protected void onHit(HitResult hitResult) {
      if(!this.level.isClientSide) {
         if(hitResult.getType() == HitResult.Type.ENTITY) {
            Entity var2 = ((EntityHitResult)hitResult).getEntity();
            var2.hurt(DamageSource.fireball(this, this.owner), 6.0F);
            this.doEnchantDamageEffects(this.owner, var2);
         }

         boolean var2 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
         this.level.explode((Entity)null, this.x, this.y, this.z, (float)this.explosionPower, var2, var2?Explosion.BlockInteraction.DESTROY:Explosion.BlockInteraction.NONE);
         this.remove();
      }

   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("ExplosionPower", this.explosionPower);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("ExplosionPower", 99)) {
         this.explosionPower = compoundTag.getInt("ExplosionPower");
      }

   }
}
