package net.minecraft.world.entity.projectile;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThrownPotion extends ThrowableProjectile implements ItemSupplier {
   private static final EntityDataAccessor DATA_ITEM_STACK = SynchedEntityData.defineId(ThrownPotion.class, EntityDataSerializers.ITEM_STACK);
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Predicate WATER_SENSITIVE = ThrownPotion::isWaterSensitiveEntity;

   public ThrownPotion(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public ThrownPotion(Level level, LivingEntity livingEntity) {
      super(EntityType.POTION, livingEntity, level);
   }

   public ThrownPotion(Level level, double var2, double var4, double var6) {
      super(EntityType.POTION, var2, var4, var6, level);
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
   }

   public ItemStack getItem() {
      ItemStack itemStack = (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
      if(itemStack.getItem() != Items.SPLASH_POTION && itemStack.getItem() != Items.LINGERING_POTION) {
         if(this.level != null) {
            LOGGER.error("ThrownPotion entity {} has no item?!", Integer.valueOf(this.getId()));
         }

         return new ItemStack(Items.SPLASH_POTION);
      } else {
         return itemStack;
      }
   }

   public void setItem(ItemStack item) {
      this.getEntityData().set(DATA_ITEM_STACK, item.copy());
   }

   protected float getGravity() {
      return 0.05F;
   }

   protected void onHit(HitResult hitResult) {
      if(!this.level.isClientSide) {
         ItemStack var2 = this.getItem();
         Potion var3 = PotionUtils.getPotion(var2);
         List<MobEffectInstance> var4 = PotionUtils.getMobEffects(var2);
         boolean var5 = var3 == Potions.WATER && var4.isEmpty();
         if(hitResult.getType() == HitResult.Type.BLOCK && var5) {
            BlockHitResult var6 = (BlockHitResult)hitResult;
            Direction var7 = var6.getDirection();
            BlockPos var8 = var6.getBlockPos().relative(var7);
            this.dowseFire(var8, var7);
            this.dowseFire(var8.relative(var7.getOpposite()), var7);

            for(Direction var10 : Direction.Plane.HORIZONTAL) {
               this.dowseFire(var8.relative(var10), var10);
            }
         }

         if(var5) {
            this.applyWater();
         } else if(!var4.isEmpty()) {
            if(this.isLingering()) {
               this.makeAreaOfEffectCloud(var2, var3);
            } else {
               this.applySplash(var4, hitResult.getType() == HitResult.Type.ENTITY?((EntityHitResult)hitResult).getEntity():null);
            }
         }

         int var6 = var3.hasInstantEffects()?2007:2002;
         this.level.levelEvent(var6, new BlockPos(this), PotionUtils.getColor(var2));
         this.remove();
      }
   }

   private void applyWater() {
      AABB var1 = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
      List<LivingEntity> var2 = this.level.getEntitiesOfClass(LivingEntity.class, var1, WATER_SENSITIVE);
      if(!var2.isEmpty()) {
         for(LivingEntity var4 : var2) {
            double var5 = this.distanceToSqr(var4);
            if(var5 < 16.0D && isWaterSensitiveEntity(var4)) {
               var4.hurt(DamageSource.indirectMagic(var4, this.getOwner()), 1.0F);
            }
         }
      }

   }

   private void applySplash(List list, @Nullable Entity entity) {
      AABB var3 = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
      List<LivingEntity> var4 = this.level.getEntitiesOfClass(LivingEntity.class, var3);
      if(!var4.isEmpty()) {
         for(LivingEntity var6 : var4) {
            if(var6.isAffectedByPotions()) {
               double var7 = this.distanceToSqr(var6);
               if(var7 < 16.0D) {
                  double var9 = 1.0D - Math.sqrt(var7) / 4.0D;
                  if(var6 == entity) {
                     var9 = 1.0D;
                  }

                  for(MobEffectInstance var12 : list) {
                     MobEffect var13 = var12.getEffect();
                     if(var13.isInstantenous()) {
                        var13.applyInstantenousEffect(this, this.getOwner(), var6, var12.getAmplifier(), var9);
                     } else {
                        int var14 = (int)(var9 * (double)var12.getDuration() + 0.5D);
                        if(var14 > 20) {
                           var6.addEffect(new MobEffectInstance(var13, var14, var12.getAmplifier(), var12.isAmbient(), var12.isVisible()));
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private void makeAreaOfEffectCloud(ItemStack itemStack, Potion potion) {
      AreaEffectCloud var3 = new AreaEffectCloud(this.level, this.x, this.y, this.z);
      var3.setOwner(this.getOwner());
      var3.setRadius(3.0F);
      var3.setRadiusOnUse(-0.5F);
      var3.setWaitTime(10);
      var3.setRadiusPerTick(-var3.getRadius() / (float)var3.getDuration());
      var3.setPotion(potion);

      for(MobEffectInstance var5 : PotionUtils.getCustomEffects(itemStack)) {
         var3.addEffect(new MobEffectInstance(var5));
      }

      CompoundTag var4 = itemStack.getTag();
      if(var4 != null && var4.contains("CustomPotionColor", 99)) {
         var3.setFixedColor(var4.getInt("CustomPotionColor"));
      }

      this.level.addFreshEntity(var3);
   }

   private boolean isLingering() {
      return this.getItem().getItem() == Items.LINGERING_POTION;
   }

   private void dowseFire(BlockPos blockPos, Direction direction) {
      BlockState var3 = this.level.getBlockState(blockPos);
      Block var4 = var3.getBlock();
      if(var4 == Blocks.FIRE) {
         this.level.extinguishFire((Player)null, blockPos.relative(direction), direction.getOpposite());
      } else if(var4 == Blocks.CAMPFIRE && ((Boolean)var3.getValue(CampfireBlock.LIT)).booleanValue()) {
         this.level.levelEvent((Player)null, 1009, blockPos, 0);
         this.level.setBlockAndUpdate(blockPos, (BlockState)var3.setValue(CampfireBlock.LIT, Boolean.valueOf(false)));
      }

   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      ItemStack var2 = ItemStack.of(compoundTag.getCompound("Potion"));
      if(var2.isEmpty()) {
         this.remove();
      } else {
         this.setItem(var2);
      }

   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      ItemStack var2 = this.getItem();
      if(!var2.isEmpty()) {
         compoundTag.put("Potion", var2.save(new CompoundTag()));
      }

   }

   private static boolean isWaterSensitiveEntity(LivingEntity livingEntity) {
      return livingEntity instanceof EnderMan || livingEntity instanceof Blaze;
   }
}
