package net.minecraft.world.entity.animal;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BegGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Wolf extends TamableAnimal {
   private static final EntityDataAccessor DATA_HEALTH_ID = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor DATA_INTERESTED_ID = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor DATA_COLLAR_COLOR = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.INT);
   public static final Predicate PREY_SELECTOR = (livingEntity) -> {
      EntityType<?> var1 = livingEntity.getType();
      return var1 == EntityType.SHEEP || var1 == EntityType.RABBIT || var1 == EntityType.FOX;
   };
   private float interestedAngle;
   private float interestedAngleO;
   private boolean isWet;
   private boolean isShaking;
   private float shakeAnim;
   private float shakeAnimO;

   public Wolf(EntityType entityType, Level level) {
      super(entityType, level);
      this.setTame(false);
   }

   protected void registerGoals() {
      this.sitGoal = new SitGoal(this);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, this.sitGoal);
      this.goalSelector.addGoal(3, new Wolf.WolfAvoidEntityGoal(this, Llama.class, 24.0F, 1.5D, 1.5D));
      this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
      this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(9, new BegGoal(this, 8.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(4, new NonTameRandomTargetGoal(this, Animal.class, false, PREY_SELECTOR));
      this.targetSelector.addGoal(4, new NonTameRandomTargetGoal(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, AbstractSkeleton.class, false));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
      if(this.isTame()) {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
      } else {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      }

      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
   }

   public void setTarget(@Nullable LivingEntity target) {
      super.setTarget(target);
      if(target == null) {
         this.setAngry(false);
      } else if(!this.isTame()) {
         this.setAngry(true);
      }

   }

   protected void customServerAiStep() {
      this.entityData.set(DATA_HEALTH_ID, Float.valueOf(this.getHealth()));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_HEALTH_ID, Float.valueOf(this.getHealth()));
      this.entityData.define(DATA_INTERESTED_ID, Boolean.valueOf(false));
      this.entityData.define(DATA_COLLAR_COLOR, Integer.valueOf(DyeColor.RED.getId()));
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putBoolean("Angry", this.isAngry());
      compoundTag.putByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setAngry(compoundTag.getBoolean("Angry"));
      if(compoundTag.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(compoundTag.getInt("CollarColor")));
      }

   }

   protected SoundEvent getAmbientSound() {
      return this.isAngry()?SoundEvents.WOLF_GROWL:(this.random.nextInt(3) == 0?(this.isTame() && ((Float)this.entityData.get(DATA_HEALTH_ID)).floatValue() < 10.0F?SoundEvents.WOLF_WHINE:SoundEvents.WOLF_PANT):SoundEvents.WOLF_AMBIENT);
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.WOLF_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WOLF_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   public void aiStep() {
      super.aiStep();
      if(!this.level.isClientSide && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround) {
         this.isShaking = true;
         this.shakeAnim = 0.0F;
         this.shakeAnimO = 0.0F;
         this.level.broadcastEntityEvent(this, (byte)8);
      }

      if(!this.level.isClientSide && this.getTarget() == null && this.isAngry()) {
         this.setAngry(false);
      }

   }

   public void tick() {
      super.tick();
      if(this.isAlive()) {
         this.interestedAngleO = this.interestedAngle;
         if(this.isInterested()) {
            this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
         } else {
            this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
         }

         if(this.isInWaterRainOrBubble()) {
            this.isWet = true;
            this.isShaking = false;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
         } else if((this.isWet || this.isShaking) && this.isShaking) {
            if(this.shakeAnim == 0.0F) {
               this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            this.shakeAnimO = this.shakeAnim;
            this.shakeAnim += 0.05F;
            if(this.shakeAnimO >= 2.0F) {
               this.isWet = false;
               this.isShaking = false;
               this.shakeAnimO = 0.0F;
               this.shakeAnim = 0.0F;
            }

            if(this.shakeAnim > 0.4F) {
               float var1 = (float)this.getBoundingBox().minY;
               int var2 = (int)(Mth.sin((this.shakeAnim - 0.4F) * 3.1415927F) * 7.0F);
               Vec3 var3 = this.getDeltaMovement();

               for(int var4 = 0; var4 < var2; ++var4) {
                  float var5 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                  float var6 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                  this.level.addParticle(ParticleTypes.SPLASH, this.x + (double)var5, (double)(var1 + 0.8F), this.z + (double)var6, var3.x, var3.y, var3.z);
               }
            }
         }

      }
   }

   public void die(DamageSource damageSource) {
      this.isWet = false;
      this.isShaking = false;
      this.shakeAnimO = 0.0F;
      this.shakeAnim = 0.0F;
      super.die(damageSource);
   }

   public boolean isWet() {
      return this.isWet;
   }

   public float getWetShade(float f) {
      return 0.75F + Mth.lerp(f, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.25F;
   }

   public float getBodyRollAngle(float var1, float var2) {
      float var3 = (Mth.lerp(var1, this.shakeAnimO, this.shakeAnim) + var2) / 1.8F;
      if(var3 < 0.0F) {
         var3 = 0.0F;
      } else if(var3 > 1.0F) {
         var3 = 1.0F;
      }

      return Mth.sin(var3 * 3.1415927F) * Mth.sin(var3 * 3.1415927F * 11.0F) * 0.15F * 3.1415927F;
   }

   public float getHeadRollAngle(float f) {
      return Mth.lerp(f, this.interestedAngleO, this.interestedAngle) * 0.15F * 3.1415927F;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return entityDimensions.height * 0.8F;
   }

   public int getMaxHeadXRot() {
      return this.isSitting()?20:super.getMaxHeadXRot();
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else {
         Entity var3 = damageSource.getEntity();
         if(this.sitGoal != null) {
            this.sitGoal.wantToSit(false);
         }

         if(var3 != null && !(var3 instanceof Player) && !(var3 instanceof AbstractArrow)) {
            var2 = (var2 + 1.0F) / 2.0F;
         }

         return super.hurt(damageSource, var2);
      }
   }

   public boolean doHurtTarget(Entity entity) {
      boolean var2 = entity.hurt(DamageSource.mobAttack(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
      if(var2) {
         this.doEnchantDamageEffects(this, entity);
      }

      return var2;
   }

   public void setTame(boolean tame) {
      super.setTame(tame);
      if(tame) {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
      } else {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      }

      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      Item var4 = var3.getItem();
      if(this.isTame()) {
         if(!var3.isEmpty()) {
            if(var4.isEdible()) {
               if(var4.getFoodProperties().isMeat() && ((Float)this.entityData.get(DATA_HEALTH_ID)).floatValue() < 20.0F) {
                  if(!player.abilities.instabuild) {
                     var3.shrink(1);
                  }

                  this.heal((float)var4.getFoodProperties().getNutrition());
                  return true;
               }
            } else if(var4 instanceof DyeItem) {
               DyeColor var5 = ((DyeItem)var4).getDyeColor();
               if(var5 != this.getCollarColor()) {
                  this.setCollarColor(var5);
                  if(!player.abilities.instabuild) {
                     var3.shrink(1);
                  }

                  return true;
               }
            }
         }

         if(this.isOwnedBy(player) && !this.level.isClientSide && !this.isFood(var3)) {
            this.sitGoal.wantToSit(!this.isSitting());
            this.jumping = false;
            this.navigation.stop();
            this.setTarget((LivingEntity)null);
         }
      } else if(var4 == Items.BONE && !this.isAngry()) {
         if(!player.abilities.instabuild) {
            var3.shrink(1);
         }

         if(!this.level.isClientSide) {
            if(this.random.nextInt(3) == 0) {
               this.tame(player);
               this.navigation.stop();
               this.setTarget((LivingEntity)null);
               this.sitGoal.wantToSit(true);
               this.setHealth(20.0F);
               this.spawnTamingParticles(true);
               this.level.broadcastEntityEvent(this, (byte)7);
            } else {
               this.spawnTamingParticles(false);
               this.level.broadcastEntityEvent(this, (byte)6);
            }
         }

         return true;
      }

      return super.mobInteract(player, interactionHand);
   }

   public void handleEntityEvent(byte b) {
      if(b == 8) {
         this.isShaking = true;
         this.shakeAnim = 0.0F;
         this.shakeAnimO = 0.0F;
      } else {
         super.handleEntityEvent(b);
      }

   }

   public float getTailAngle() {
      return this.isAngry()?1.5393804F:(this.isTame()?(0.55F - (this.getMaxHealth() - ((Float)this.entityData.get(DATA_HEALTH_ID)).floatValue()) * 0.02F) * 3.1415927F:0.62831855F);
   }

   public boolean isFood(ItemStack itemStack) {
      Item var2 = itemStack.getItem();
      return var2.isEdible() && var2.getFoodProperties().isMeat();
   }

   public int getMaxSpawnClusterSize() {
      return 8;
   }

   public boolean isAngry() {
      return (((Byte)this.entityData.get(DATA_FLAGS_ID)).byteValue() & 2) != 0;
   }

   public void setAngry(boolean angry) {
      byte var2 = ((Byte)this.entityData.get(DATA_FLAGS_ID)).byteValue();
      if(angry) {
         this.entityData.set(DATA_FLAGS_ID, Byte.valueOf((byte)(var2 | 2)));
      } else {
         this.entityData.set(DATA_FLAGS_ID, Byte.valueOf((byte)(var2 & -3)));
      }

   }

   public DyeColor getCollarColor() {
      return DyeColor.byId(((Integer)this.entityData.get(DATA_COLLAR_COLOR)).intValue());
   }

   public void setCollarColor(DyeColor collarColor) {
      this.entityData.set(DATA_COLLAR_COLOR, Integer.valueOf(collarColor.getId()));
   }

   public Wolf getBreedOffspring(AgableMob agableMob) {
      Wolf wolf = (Wolf)EntityType.WOLF.create(this.level);
      UUID var3 = this.getOwnerUUID();
      if(var3 != null) {
         wolf.setOwnerUUID(var3);
         wolf.setTame(true);
      }

      return wolf;
   }

   public void setIsInterested(boolean isInterested) {
      this.entityData.set(DATA_INTERESTED_ID, Boolean.valueOf(isInterested));
   }

   public boolean canMate(Animal animal) {
      if(animal == this) {
         return false;
      } else if(!this.isTame()) {
         return false;
      } else if(!(animal instanceof Wolf)) {
         return false;
      } else {
         Wolf var2 = (Wolf)animal;
         return !var2.isTame()?false:(var2.isSitting()?false:this.isInLove() && var2.isInLove());
      }
   }

   public boolean isInterested() {
      return ((Boolean)this.entityData.get(DATA_INTERESTED_ID)).booleanValue();
   }

   public boolean wantsToAttack(LivingEntity var1, LivingEntity var2) {
      if(!(var1 instanceof Creeper) && !(var1 instanceof Ghast)) {
         if(var1 instanceof Wolf) {
            Wolf var3 = (Wolf)var1;
            if(var3.isTame() && var3.getOwner() == var2) {
               return false;
            }
         }

         return var1 instanceof Player && var2 instanceof Player && !((Player)var2).canHarmPlayer((Player)var1)?false:(var1 instanceof AbstractHorse && ((AbstractHorse)var1).isTamed()?false:!(var1 instanceof Cat) || !((Cat)var1).isTame());
      } else {
         return false;
      }
   }

   public boolean canBeLeashed(Player player) {
      return !this.isAngry() && super.canBeLeashed(player);
   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }

   class WolfAvoidEntityGoal extends AvoidEntityGoal {
      private final Wolf wolf;

      public WolfAvoidEntityGoal(Wolf wolf, Class class, float var4, double var5, double var7) {
         super(wolf, class, var4, var5, var7);
         this.wolf = wolf;
      }

      public boolean canUse() {
         return super.canUse() && this.toAvoid instanceof Llama?!this.wolf.isTame() && this.avoidLlama((Llama)this.toAvoid):false;
      }

      private boolean avoidLlama(Llama llama) {
         return llama.getStrength() >= Wolf.this.random.nextInt(5);
      }

      public void start() {
         Wolf.this.setTarget((LivingEntity)null);
         super.start();
      }

      public void tick() {
         Wolf.this.setTarget((LivingEntity)null);
         super.tick();
      }
   }
}
