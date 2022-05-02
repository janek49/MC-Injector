package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class Snowball extends ThrowableItemProjectile {
   public Snowball(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public Snowball(Level level, LivingEntity livingEntity) {
      super(EntityType.SNOWBALL, livingEntity, level);
   }

   public Snowball(Level level, double var2, double var4, double var6) {
      super(EntityType.SNOWBALL, var2, var4, var6, level);
   }

   protected Item getDefaultItem() {
      return Items.SNOWBALL;
   }

   private ParticleOptions getParticle() {
      ItemStack var1 = this.getItemRaw();
      return (ParticleOptions)(var1.isEmpty()?ParticleTypes.ITEM_SNOWBALL:new ItemParticleOption(ParticleTypes.ITEM, var1));
   }

   public void handleEntityEvent(byte b) {
      if(b == 3) {
         ParticleOptions var2 = this.getParticle();

         for(int var3 = 0; var3 < 8; ++var3) {
            this.level.addParticle(var2, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void onHit(HitResult hitResult) {
      if(hitResult.getType() == HitResult.Type.ENTITY) {
         Entity var2 = ((EntityHitResult)hitResult).getEntity();
         int var3 = var2 instanceof Blaze?3:0;
         var2.hurt(DamageSource.thrown(this, this.getOwner()), (float)var3);
      }

      if(!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)3);
         this.remove();
      }

   }
}
