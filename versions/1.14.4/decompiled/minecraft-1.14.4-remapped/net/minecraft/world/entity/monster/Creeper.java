package net.minecraft.world.entity.monster;

import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class Creeper extends Monster {
   private static final EntityDataAccessor DATA_SWELL_DIR = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor DATA_IS_POWERED = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor DATA_IS_IGNITED = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
   private int oldSwell;
   private int swell;
   private int maxSwell = 30;
   private int explosionRadius = 3;
   private int droppedSkulls;

   public Creeper(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SwellGoal(this));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Cat.class, 6.0F, 1.0D, 1.2D));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public int getMaxFallDistance() {
      return this.getTarget() == null?3:3 + (int)(this.getHealth() - 1.0F);
   }

   public void causeFallDamage(float var1, float var2) {
      super.causeFallDamage(var1, var2);
      this.swell = (int)((float)this.swell + var1 * 1.5F);
      if(this.swell > this.maxSwell - 5) {
         this.swell = this.maxSwell - 5;
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SWELL_DIR, Integer.valueOf(-1));
      this.entityData.define(DATA_IS_POWERED, Boolean.valueOf(false));
      this.entityData.define(DATA_IS_IGNITED, Boolean.valueOf(false));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      if(((Boolean)this.entityData.get(DATA_IS_POWERED)).booleanValue()) {
         compoundTag.putBoolean("powered", true);
      }

      compoundTag.putShort("Fuse", (short)this.maxSwell);
      compoundTag.putByte("ExplosionRadius", (byte)this.explosionRadius);
      compoundTag.putBoolean("ignited", this.isIgnited());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.entityData.set(DATA_IS_POWERED, Boolean.valueOf(compoundTag.getBoolean("powered")));
      if(compoundTag.contains("Fuse", 99)) {
         this.maxSwell = compoundTag.getShort("Fuse");
      }

      if(compoundTag.contains("ExplosionRadius", 99)) {
         this.explosionRadius = compoundTag.getByte("ExplosionRadius");
      }

      if(compoundTag.getBoolean("ignited")) {
         this.ignite();
      }

   }

   public void tick() {
      if(this.isAlive()) {
         this.oldSwell = this.swell;
         if(this.isIgnited()) {
            this.setSwellDir(1);
         }

         int var1 = this.getSwellDir();
         if(var1 > 0 && this.swell == 0) {
            this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
         }

         this.swell += var1;
         if(this.swell < 0) {
            this.swell = 0;
         }

         if(this.swell >= this.maxSwell) {
            this.swell = this.maxSwell;
            this.explodeCreeper();
         }
      }

      super.tick();
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.CREEPER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.CREEPER_DEATH;
   }

   protected void dropCustomDeathLoot(DamageSource damageSource, int var2, boolean var3) {
      super.dropCustomDeathLoot(damageSource, var2, var3);
      Entity var4 = damageSource.getEntity();
      if(var4 != this && var4 instanceof Creeper) {
         Creeper var5 = (Creeper)var4;
         if(var5.canDropMobsSkull()) {
            var5.increaseDroppedSkulls();
            this.spawnAtLocation(Items.CREEPER_HEAD);
         }
      }

   }

   public boolean doHurtTarget(Entity entity) {
      return true;
   }

   public boolean isPowered() {
      return ((Boolean)this.entityData.get(DATA_IS_POWERED)).booleanValue();
   }

   public float getSwelling(float f) {
      return Mth.lerp(f, (float)this.oldSwell, (float)this.swell) / (float)(this.maxSwell - 2);
   }

   public int getSwellDir() {
      return ((Integer)this.entityData.get(DATA_SWELL_DIR)).intValue();
   }

   public void setSwellDir(int swellDir) {
      this.entityData.set(DATA_SWELL_DIR, Integer.valueOf(swellDir));
   }

   public void thunderHit(LightningBolt lightningBolt) {
      super.thunderHit(lightningBolt);
      this.entityData.set(DATA_IS_POWERED, Boolean.valueOf(true));
   }

   protected boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      if(var3.getItem() == Items.FLINT_AND_STEEL) {
         this.level.playSound(player, this.x, this.y, this.z, SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
         player.swing(interactionHand);
         if(!this.level.isClientSide) {
            this.ignite();
            var3.hurtAndBreak(1, player, (player) -> {
               player.broadcastBreakEvent(interactionHand);
            });
            return true;
         }
      }

      return super.mobInteract(player, interactionHand);
   }

   private void explodeCreeper() {
      if(!this.level.isClientSide) {
         Explosion.BlockInteraction var1 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)?Explosion.BlockInteraction.DESTROY:Explosion.BlockInteraction.NONE;
         float var2 = this.isPowered()?2.0F:1.0F;
         this.dead = true;
         this.level.explode(this, this.x, this.y, this.z, (float)this.explosionRadius * var2, var1);
         this.remove();
         this.spawnLingeringCloud();
      }

   }

   private void spawnLingeringCloud() {
      Collection<MobEffectInstance> var1 = this.getActiveEffects();
      if(!var1.isEmpty()) {
         AreaEffectCloud var2 = new AreaEffectCloud(this.level, this.x, this.y, this.z);
         var2.setRadius(2.5F);
         var2.setRadiusOnUse(-0.5F);
         var2.setWaitTime(10);
         var2.setDuration(var2.getDuration() / 2);
         var2.setRadiusPerTick(-var2.getRadius() / (float)var2.getDuration());

         for(MobEffectInstance var4 : var1) {
            var2.addEffect(new MobEffectInstance(var4));
         }

         this.level.addFreshEntity(var2);
      }

   }

   public boolean isIgnited() {
      return ((Boolean)this.entityData.get(DATA_IS_IGNITED)).booleanValue();
   }

   public void ignite() {
      this.entityData.set(DATA_IS_IGNITED, Boolean.valueOf(true));
   }

   public boolean canDropMobsSkull() {
      return this.isPowered() && this.droppedSkulls < 1;
   }

   public void increaseDroppedSkulls() {
      ++this.droppedSkulls;
   }
}
