package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class MinecartTNT extends AbstractMinecart {
   private int fuse = -1;

   public MinecartTNT(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public MinecartTNT(Level level, double var2, double var4, double var6) {
      super(EntityType.TNT_MINECART, level, var2, var4, var6);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.TNT;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.TNT.defaultBlockState();
   }

   public void tick() {
      super.tick();
      if(this.fuse > 0) {
         --this.fuse;
         this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y + 0.5D, this.z, 0.0D, 0.0D, 0.0D);
      } else if(this.fuse == 0) {
         this.explode(getHorizontalDistanceSqr(this.getDeltaMovement()));
      }

      if(this.horizontalCollision) {
         double var1 = getHorizontalDistanceSqr(this.getDeltaMovement());
         if(var1 >= 0.009999999776482582D) {
            this.explode(var1);
         }
      }

   }

   public boolean hurt(DamageSource damageSource, float var2) {
      Entity var3 = damageSource.getDirectEntity();
      if(var3 instanceof AbstractArrow) {
         AbstractArrow var4 = (AbstractArrow)var3;
         if(var4.isOnFire()) {
            this.explode(var4.getDeltaMovement().lengthSqr());
         }
      }

      return super.hurt(damageSource, var2);
   }

   public void destroy(DamageSource damageSource) {
      double var2 = getHorizontalDistanceSqr(this.getDeltaMovement());
      if(!damageSource.isFire() && !damageSource.isExplosion() && var2 < 0.009999999776482582D) {
         super.destroy(damageSource);
         if(!damageSource.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(Blocks.TNT);
         }

      } else {
         if(this.fuse < 0) {
            this.primeFuse();
            this.fuse = this.random.nextInt(20) + this.random.nextInt(20);
         }

      }
   }

   protected void explode(double d) {
      if(!this.level.isClientSide) {
         double var3 = Math.sqrt(d);
         if(var3 > 5.0D) {
            var3 = 5.0D;
         }

         this.level.explode(this, this.x, this.y, this.z, (float)(4.0D + this.random.nextDouble() * 1.5D * var3), Explosion.BlockInteraction.BREAK);
         this.remove();
      }

   }

   public void causeFallDamage(float var1, float var2) {
      if(var1 >= 3.0F) {
         float var3 = var1 / 10.0F;
         this.explode((double)(var3 * var3));
      }

      super.causeFallDamage(var1, var2);
   }

   public void activateMinecart(int var1, int var2, int var3, boolean var4) {
      if(var4 && this.fuse < 0) {
         this.primeFuse();
      }

   }

   public void handleEntityEvent(byte b) {
      if(b == 10) {
         this.primeFuse();
      } else {
         super.handleEntityEvent(b);
      }

   }

   public void primeFuse() {
      this.fuse = 80;
      if(!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)10);
         if(!this.isSilent()) {
            this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
         }
      }

   }

   public int getFuse() {
      return this.fuse;
   }

   public boolean isPrimed() {
      return this.fuse > -1;
   }

   public float getBlockExplosionResistance(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, FluidState fluidState, float var6) {
      return !this.isPrimed() || !blockState.is(BlockTags.RAILS) && !blockGetter.getBlockState(blockPos.above()).is(BlockTags.RAILS)?super.getBlockExplosionResistance(explosion, blockGetter, blockPos, blockState, fluidState, var6):0.0F;
   }

   public boolean shouldBlockExplode(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, float var5) {
      return !this.isPrimed() || !blockState.is(BlockTags.RAILS) && !blockGetter.getBlockState(blockPos.above()).is(BlockTags.RAILS)?super.shouldBlockExplode(explosion, blockGetter, blockPos, blockState, var5):false;
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("TNTFuse", 99)) {
         this.fuse = compoundTag.getInt("TNTFuse");
      }

   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("TNTFuse", this.fuse);
   }
}
