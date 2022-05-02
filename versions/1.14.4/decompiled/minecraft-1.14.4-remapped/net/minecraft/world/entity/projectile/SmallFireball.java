package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SmallFireball extends Fireball {
   public SmallFireball(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public SmallFireball(Level level, LivingEntity livingEntity, double var3, double var5, double var7) {
      super(EntityType.SMALL_FIREBALL, livingEntity, var3, var5, var7, level);
   }

   public SmallFireball(Level level, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(EntityType.SMALL_FIREBALL, var2, var4, var6, var8, var10, var12, level);
   }

   protected void onHit(HitResult hitResult) {
      if(!this.level.isClientSide) {
         if(hitResult.getType() == HitResult.Type.ENTITY) {
            Entity var2 = ((EntityHitResult)hitResult).getEntity();
            if(!var2.fireImmune()) {
               int var3 = var2.getRemainingFireTicks();
               var2.setSecondsOnFire(5);
               boolean var4 = var2.hurt(DamageSource.fireball(this, this.owner), 5.0F);
               if(var4) {
                  this.doEnchantDamageEffects(this.owner, var2);
               } else {
                  var2.setRemainingFireTicks(var3);
               }
            }
         } else if(this.owner == null || !(this.owner instanceof Mob) || this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockHitResult var2 = (BlockHitResult)hitResult;
            BlockPos var3 = var2.getBlockPos().relative(var2.getDirection());
            if(this.level.isEmptyBlock(var3)) {
               this.level.setBlockAndUpdate(var3, Blocks.FIRE.defaultBlockState());
            }
         }

         this.remove();
      }

   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      return false;
   }
}
