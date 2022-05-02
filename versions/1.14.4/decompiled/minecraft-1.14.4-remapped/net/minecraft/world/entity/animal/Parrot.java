package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerFlyingGoal;
import net.minecraft.world.entity.ai.goal.LandOnOwnersShoulderGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.SitGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LogBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Parrot extends ShoulderRidingEntity implements FlyingAnimal {
   private static final EntityDataAccessor DATA_VARIANT_ID = SynchedEntityData.defineId(Parrot.class, EntityDataSerializers.INT);
   private static final Predicate NOT_PARROT_PREDICATE = new Predicate() {
      public boolean test(@Nullable Mob mob) {
         return mob != null && Parrot.MOB_SOUND_MAP.containsKey(mob.getType());
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((Mob)var1);
      }
   };
   private static final Item POISONOUS_FOOD = Items.COOKIE;
   private static final Set TAME_FOOD = Sets.newHashSet(new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS});
   private static final Map MOB_SOUND_MAP = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put(EntityType.BLAZE, SoundEvents.PARROT_IMITATE_BLAZE);
      hashMap.put(EntityType.CAVE_SPIDER, SoundEvents.PARROT_IMITATE_SPIDER);
      hashMap.put(EntityType.CREEPER, SoundEvents.PARROT_IMITATE_CREEPER);
      hashMap.put(EntityType.DROWNED, SoundEvents.PARROT_IMITATE_DROWNED);
      hashMap.put(EntityType.ELDER_GUARDIAN, SoundEvents.PARROT_IMITATE_ELDER_GUARDIAN);
      hashMap.put(EntityType.ENDER_DRAGON, SoundEvents.PARROT_IMITATE_ENDER_DRAGON);
      hashMap.put(EntityType.ENDERMAN, SoundEvents.PARROT_IMITATE_ENDERMAN);
      hashMap.put(EntityType.ENDERMITE, SoundEvents.PARROT_IMITATE_ENDERMITE);
      hashMap.put(EntityType.EVOKER, SoundEvents.PARROT_IMITATE_EVOKER);
      hashMap.put(EntityType.GHAST, SoundEvents.PARROT_IMITATE_GHAST);
      hashMap.put(EntityType.GUARDIAN, SoundEvents.PARROT_IMITATE_GUARDIAN);
      hashMap.put(EntityType.HUSK, SoundEvents.PARROT_IMITATE_HUSK);
      hashMap.put(EntityType.ILLUSIONER, SoundEvents.PARROT_IMITATE_ILLUSIONER);
      hashMap.put(EntityType.MAGMA_CUBE, SoundEvents.PARROT_IMITATE_MAGMA_CUBE);
      hashMap.put(EntityType.ZOMBIE_PIGMAN, SoundEvents.PARROT_IMITATE_ZOMBIE_PIGMAN);
      hashMap.put(EntityType.PANDA, SoundEvents.PARROT_IMITATE_PANDA);
      hashMap.put(EntityType.PHANTOM, SoundEvents.PARROT_IMITATE_PHANTOM);
      hashMap.put(EntityType.PILLAGER, SoundEvents.PARROT_IMITATE_PILLAGER);
      hashMap.put(EntityType.POLAR_BEAR, SoundEvents.PARROT_IMITATE_POLAR_BEAR);
      hashMap.put(EntityType.RAVAGER, SoundEvents.PARROT_IMITATE_RAVAGER);
      hashMap.put(EntityType.SHULKER, SoundEvents.PARROT_IMITATE_SHULKER);
      hashMap.put(EntityType.SILVERFISH, SoundEvents.PARROT_IMITATE_SILVERFISH);
      hashMap.put(EntityType.SKELETON, SoundEvents.PARROT_IMITATE_SKELETON);
      hashMap.put(EntityType.SLIME, SoundEvents.PARROT_IMITATE_SLIME);
      hashMap.put(EntityType.SPIDER, SoundEvents.PARROT_IMITATE_SPIDER);
      hashMap.put(EntityType.STRAY, SoundEvents.PARROT_IMITATE_STRAY);
      hashMap.put(EntityType.VEX, SoundEvents.PARROT_IMITATE_VEX);
      hashMap.put(EntityType.VINDICATOR, SoundEvents.PARROT_IMITATE_VINDICATOR);
      hashMap.put(EntityType.WITCH, SoundEvents.PARROT_IMITATE_WITCH);
      hashMap.put(EntityType.WITHER, SoundEvents.PARROT_IMITATE_WITHER);
      hashMap.put(EntityType.WITHER_SKELETON, SoundEvents.PARROT_IMITATE_WITHER_SKELETON);
      hashMap.put(EntityType.WOLF, SoundEvents.PARROT_IMITATE_WOLF);
      hashMap.put(EntityType.ZOMBIE, SoundEvents.PARROT_IMITATE_ZOMBIE);
      hashMap.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.PARROT_IMITATE_ZOMBIE_VILLAGER);
   });
   public float flap;
   public float flapSpeed;
   public float oFlapSpeed;
   public float oFlap;
   public float flapping = 1.0F;
   private boolean partyParrot;
   private BlockPos jukebox;

   public Parrot(EntityType entityType, Level level) {
      super(entityType, level);
      this.moveControl = new FlyingMoveControl(this);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      this.setVariant(this.random.nextInt(5));
      return super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
   }

   protected void registerGoals() {
      this.sitGoal = new SitGoal(this);
      this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(2, this.sitGoal);
      this.goalSelector.addGoal(2, new FollowOwnerFlyingGoal(this, 1.0D, 5.0F, 1.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new LandOnOwnersShoulderGoal(this));
      this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
      this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.4000000059604645D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
   }

   protected PathNavigation createNavigation(Level level) {
      FlyingPathNavigation var2 = new FlyingPathNavigation(this, level);
      var2.setCanOpenDoors(false);
      var2.setCanFloat(true);
      var2.setCanPassDoors(true);
      return var2;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return entityDimensions.height * 0.6F;
   }

   public void aiStep() {
      imitateNearbyMobs(this.level, this);
      if(this.jukebox == null || !this.jukebox.closerThan(this.position(), 3.46D) || this.level.getBlockState(this.jukebox).getBlock() != Blocks.JUKEBOX) {
         this.partyParrot = false;
         this.jukebox = null;
      }

      super.aiStep();
      this.calculateFlapping();
   }

   public void setRecordPlayingNearby(BlockPos jukebox, boolean partyParrot) {
      this.jukebox = jukebox;
      this.partyParrot = partyParrot;
   }

   public boolean isPartyParrot() {
      return this.partyParrot;
   }

   private void calculateFlapping() {
      this.oFlap = this.flap;
      this.oFlapSpeed = this.flapSpeed;
      this.flapSpeed = (float)((double)this.flapSpeed + (double)(!this.onGround && !this.isPassenger()?4:-1) * 0.3D);
      this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
      if(!this.onGround && this.flapping < 1.0F) {
         this.flapping = 1.0F;
      }

      this.flapping = (float)((double)this.flapping * 0.9D);
      Vec3 var1 = this.getDeltaMovement();
      if(!this.onGround && var1.y < 0.0D) {
         this.setDeltaMovement(var1.multiply(1.0D, 0.6D, 1.0D));
      }

      this.flap += this.flapping * 2.0F;
   }

   private static boolean imitateNearbyMobs(Level level, Entity entity) {
      if(entity.isAlive() && !entity.isSilent() && level.random.nextInt(50) == 0) {
         List<Mob> var2 = level.getEntitiesOfClass(Mob.class, entity.getBoundingBox().inflate(20.0D), NOT_PARROT_PREDICATE);
         if(!var2.isEmpty()) {
            Mob var3 = (Mob)var2.get(level.random.nextInt(var2.size()));
            if(!var3.isSilent()) {
               SoundEvent var4 = getImitatedSound(var3.getType());
               level.playSound((Player)null, entity.x, entity.y, entity.z, var4, entity.getSoundSource(), 0.7F, getPitch(level.random));
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      if(!this.isTame() && TAME_FOOD.contains(var3.getItem())) {
         if(!player.abilities.instabuild) {
            var3.shrink(1);
         }

         if(!this.isSilent()) {
            this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
         }

         if(!this.level.isClientSide) {
            if(this.random.nextInt(10) == 0) {
               this.tame(player);
               this.spawnTamingParticles(true);
               this.level.broadcastEntityEvent(this, (byte)7);
            } else {
               this.spawnTamingParticles(false);
               this.level.broadcastEntityEvent(this, (byte)6);
            }
         }

         return true;
      } else if(var3.getItem() == POISONOUS_FOOD) {
         if(!player.abilities.instabuild) {
            var3.shrink(1);
         }

         this.addEffect(new MobEffectInstance(MobEffects.POISON, 900));
         if(player.isCreative() || !this.isInvulnerable()) {
            this.hurt(DamageSource.playerAttack(player), Float.MAX_VALUE);
         }

         return true;
      } else {
         if(!this.level.isClientSide && !this.isFlying() && this.isTame() && this.isOwnedBy(player)) {
            this.sitGoal.wantToSit(!this.isSitting());
         }

         return super.mobInteract(player, interactionHand);
      }
   }

   public boolean isFood(ItemStack itemStack) {
      return false;
   }

   public static boolean checkParrotSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      Block var5 = levelAccessor.getBlockState(blockPos.below()).getBlock();
      return (var5.is(BlockTags.LEAVES) || var5 == Blocks.GRASS_BLOCK || var5 instanceof LogBlock || var5 == Blocks.AIR) && levelAccessor.getRawBrightness(blockPos, 0) > 8;
   }

   public void causeFallDamage(float var1, float var2) {
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState blockState, BlockPos blockPos) {
   }

   public boolean canMate(Animal animal) {
      return false;
   }

   @Nullable
   public AgableMob getBreedOffspring(AgableMob agableMob) {
      return null;
   }

   public static void playAmbientSound(Level level, Entity entity) {
      if(!entity.isSilent() && !imitateNearbyMobs(level, entity) && level.random.nextInt(200) == 0) {
         level.playSound((Player)null, entity.x, entity.y, entity.z, getAmbient(level.random), entity.getSoundSource(), 1.0F, getPitch(level.random));
      }

   }

   public boolean doHurtTarget(Entity entity) {
      return entity.hurt(DamageSource.mobAttack(this), 3.0F);
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return getAmbient(this.random);
   }

   private static SoundEvent getAmbient(Random random) {
      if(random.nextInt(1000) == 0) {
         List<EntityType<?>> var1 = Lists.newArrayList(MOB_SOUND_MAP.keySet());
         return getImitatedSound((EntityType)var1.get(random.nextInt(var1.size())));
      } else {
         return SoundEvents.PARROT_AMBIENT;
      }
   }

   public static SoundEvent getImitatedSound(EntityType entityType) {
      return (SoundEvent)MOB_SOUND_MAP.getOrDefault(entityType, SoundEvents.PARROT_AMBIENT);
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.PARROT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PARROT_DEATH;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(SoundEvents.PARROT_STEP, 0.15F, 1.0F);
   }

   protected float playFlySound(float f) {
      this.playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
      return f + this.flapSpeed / 2.0F;
   }

   protected boolean makeFlySound() {
      return true;
   }

   protected float getVoicePitch() {
      return getPitch(this.random);
   }

   private static float getPitch(Random random) {
      return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   public boolean isPushable() {
      return true;
   }

   protected void doPush(Entity entity) {
      if(!(entity instanceof Player)) {
         super.doPush(entity);
      }
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else {
         if(this.sitGoal != null) {
            this.sitGoal.wantToSit(false);
         }

         return super.hurt(damageSource, var2);
      }
   }

   public int getVariant() {
      return Mth.clamp(((Integer)this.entityData.get(DATA_VARIANT_ID)).intValue(), 0, 4);
   }

   public void setVariant(int variant) {
      this.entityData.set(DATA_VARIANT_ID, Integer.valueOf(variant));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_VARIANT_ID, Integer.valueOf(0));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("Variant", this.getVariant());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setVariant(compoundTag.getInt("Variant"));
   }

   public boolean isFlying() {
      return !this.onGround;
   }
}
