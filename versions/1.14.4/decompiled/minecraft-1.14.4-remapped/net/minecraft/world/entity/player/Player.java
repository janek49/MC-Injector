package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public abstract class Player extends LivingEntity {
   public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.8F);
   private static final Map POSES;
   private static final EntityDataAccessor DATA_PLAYER_ABSORPTION_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor DATA_SCORE_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
   protected static final EntityDataAccessor DATA_PLAYER_MAIN_HAND = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
   protected static final EntityDataAccessor DATA_SHOULDER_LEFT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
   protected static final EntityDataAccessor DATA_SHOULDER_RIGHT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
   private long timeEntitySatOnShoulder;
   public final Inventory inventory = new Inventory(this);
   protected PlayerEnderChestContainer enderChestInventory = new PlayerEnderChestContainer();
   public final InventoryMenu inventoryMenu;
   public AbstractContainerMenu containerMenu;
   protected FoodData foodData = new FoodData();
   protected int jumpTriggerTime;
   public float oBob;
   public float bob;
   public int takeXpDelay;
   public double xCloakO;
   public double yCloakO;
   public double zCloakO;
   public double xCloak;
   public double yCloak;
   public double zCloak;
   private int sleepCounter;
   protected boolean wasUnderwater;
   private BlockPos respawnPosition;
   private boolean respawnForced;
   public final Abilities abilities = new Abilities();
   public int experienceLevel;
   public int totalExperience;
   public float experienceProgress;
   protected int enchantmentSeed;
   protected final float defaultFlySpeed = 0.02F;
   private int lastLevelUpTime;
   private final GameProfile gameProfile;
   private boolean reducedDebugInfo;
   private ItemStack lastItemInMainHand = ItemStack.EMPTY;
   private final ItemCooldowns cooldowns = this.createItemCooldowns();
   @Nullable
   public FishingHook fishing;

   public Player(Level level, GameProfile gameProfile) {
      super(EntityType.PLAYER, level);
      this.setUUID(createPlayerUUID(gameProfile));
      this.gameProfile = gameProfile;
      this.inventoryMenu = new InventoryMenu(this.inventory, !level.isClientSide, this);
      this.containerMenu = this.inventoryMenu;
      BlockPos var3 = level.getSharedSpawnPos();
      this.moveTo((double)var3.getX() + 0.5D, (double)(var3.getY() + 1), (double)var3.getZ() + 0.5D, 0.0F, 0.0F);
      this.rotOffs = 180.0F;
   }

   public boolean blockActionRestricted(Level level, BlockPos blockPos, GameType gameType) {
      if(!gameType.isBlockPlacingRestricted()) {
         return false;
      } else if(gameType == GameType.SPECTATOR) {
         return true;
      } else if(this.mayBuild()) {
         return false;
      } else {
         ItemStack var4 = this.getMainHandItem();
         return var4.isEmpty() || !var4.hasAdventureModeBreakTagForBlock(level.getTagManager(), new BlockInWorld(level, blockPos, false));
      }
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.10000000149011612D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.LUCK);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_PLAYER_ABSORPTION_ID, Float.valueOf(0.0F));
      this.entityData.define(DATA_SCORE_ID, Integer.valueOf(0));
      this.entityData.define(DATA_PLAYER_MODE_CUSTOMISATION, Byte.valueOf((byte)0));
      this.entityData.define(DATA_PLAYER_MAIN_HAND, Byte.valueOf((byte)1));
      this.entityData.define(DATA_SHOULDER_LEFT, new CompoundTag());
      this.entityData.define(DATA_SHOULDER_RIGHT, new CompoundTag());
   }

   public void tick() {
      this.noPhysics = this.isSpectator();
      if(this.isSpectator()) {
         this.onGround = false;
      }

      if(this.takeXpDelay > 0) {
         --this.takeXpDelay;
      }

      if(this.isSleeping()) {
         ++this.sleepCounter;
         if(this.sleepCounter > 100) {
            this.sleepCounter = 100;
         }

         if(!this.level.isClientSide && this.level.isDay()) {
            this.stopSleepInBed(false, true, true);
         }
      } else if(this.sleepCounter > 0) {
         ++this.sleepCounter;
         if(this.sleepCounter >= 110) {
            this.sleepCounter = 0;
         }
      }

      this.updateIsUnderwater();
      super.tick();
      if(!this.level.isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
         this.closeContainer();
         this.containerMenu = this.inventoryMenu;
      }

      if(this.isOnFire() && this.abilities.invulnerable) {
         this.clearFire();
      }

      this.moveCloak();
      if(!this.level.isClientSide) {
         this.foodData.tick(this);
         this.awardStat(Stats.PLAY_ONE_MINUTE);
         if(this.isAlive()) {
            this.awardStat(Stats.TIME_SINCE_DEATH);
         }

         if(this.isSneaking()) {
            this.awardStat(Stats.SNEAK_TIME);
         }

         if(!this.isSleeping()) {
            this.awardStat(Stats.TIME_SINCE_REST);
         }
      }

      int var1 = 29999999;
      double var2 = Mth.clamp(this.x, -2.9999999E7D, 2.9999999E7D);
      double var4 = Mth.clamp(this.z, -2.9999999E7D, 2.9999999E7D);
      if(var2 != this.x || var4 != this.z) {
         this.setPos(var2, this.y, var4);
      }

      ++this.attackStrengthTicker;
      ItemStack var6 = this.getMainHandItem();
      if(!ItemStack.matches(this.lastItemInMainHand, var6)) {
         if(!ItemStack.isSameIgnoreDurability(this.lastItemInMainHand, var6)) {
            this.resetAttackStrengthTicker();
         }

         this.lastItemInMainHand = var6.isEmpty()?ItemStack.EMPTY:var6.copy();
      }

      this.turtleHelmetTick();
      this.cooldowns.tick();
      this.updatePlayerPose();
   }

   protected boolean updateIsUnderwater() {
      this.wasUnderwater = this.isUnderLiquid(FluidTags.WATER, true);
      return this.wasUnderwater;
   }

   private void turtleHelmetTick() {
      ItemStack var1 = this.getItemBySlot(EquipmentSlot.HEAD);
      if(var1.getItem() == Items.TURTLE_HELMET && !this.isUnderLiquid(FluidTags.WATER)) {
         this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
      }

   }

   protected ItemCooldowns createItemCooldowns() {
      return new ItemCooldowns();
   }

   private void moveCloak() {
      this.xCloakO = this.xCloak;
      this.yCloakO = this.yCloak;
      this.zCloakO = this.zCloak;
      double var1 = this.x - this.xCloak;
      double var3 = this.y - this.yCloak;
      double var5 = this.z - this.zCloak;
      double var7 = 10.0D;
      if(var1 > 10.0D) {
         this.xCloak = this.x;
         this.xCloakO = this.xCloak;
      }

      if(var5 > 10.0D) {
         this.zCloak = this.z;
         this.zCloakO = this.zCloak;
      }

      if(var3 > 10.0D) {
         this.yCloak = this.y;
         this.yCloakO = this.yCloak;
      }

      if(var1 < -10.0D) {
         this.xCloak = this.x;
         this.xCloakO = this.xCloak;
      }

      if(var5 < -10.0D) {
         this.zCloak = this.z;
         this.zCloakO = this.zCloak;
      }

      if(var3 < -10.0D) {
         this.yCloak = this.y;
         this.yCloakO = this.yCloak;
      }

      this.xCloak += var1 * 0.25D;
      this.zCloak += var5 * 0.25D;
      this.yCloak += var3 * 0.25D;
   }

   protected void updatePlayerPose() {
      if(this.canEnterPose(Pose.SWIMMING)) {
         Pose var1;
         if(this.isFallFlying()) {
            var1 = Pose.FALL_FLYING;
         } else if(this.isSleeping()) {
            var1 = Pose.SLEEPING;
         } else if(this.isSwimming()) {
            var1 = Pose.SWIMMING;
         } else if(this.isAutoSpinAttack()) {
            var1 = Pose.SPIN_ATTACK;
         } else if(this.isSneaking() && !this.abilities.flying) {
            var1 = Pose.SNEAKING;
         } else {
            var1 = Pose.STANDING;
         }

         Pose var2;
         if(!this.isSpectator() && !this.isPassenger() && !this.canEnterPose(var1)) {
            if(this.canEnterPose(Pose.SNEAKING)) {
               var2 = Pose.SNEAKING;
            } else {
               var2 = Pose.SWIMMING;
            }
         } else {
            var2 = var1;
         }

         this.setPose(var2);
      }
   }

   public int getPortalWaitTime() {
      return this.abilities.invulnerable?1:80;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.PLAYER_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.PLAYER_SPLASH;
   }

   protected SoundEvent getSwimHighSpeedSplashSound() {
      return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
   }

   public int getDimensionChangingDelay() {
      return 10;
   }

   public void playSound(SoundEvent soundEvent, float var2, float var3) {
      this.level.playSound(this, this.x, this.y, this.z, soundEvent, this.getSoundSource(), var2, var3);
   }

   public void playNotifySound(SoundEvent soundEvent, SoundSource soundSource, float var3, float var4) {
   }

   public SoundSource getSoundSource() {
      return SoundSource.PLAYERS;
   }

   protected int getFireImmuneTicks() {
      return 20;
   }

   public void handleEntityEvent(byte b) {
      if(b == 9) {
         this.completeUsingItem();
      } else if(b == 23) {
         this.reducedDebugInfo = false;
      } else if(b == 22) {
         this.reducedDebugInfo = true;
      } else if(b == 43) {
         this.addParticlesAroundSelf(ParticleTypes.CLOUD);
      } else {
         super.handleEntityEvent(b);
      }

   }

   private void addParticlesAroundSelf(ParticleOptions particleOptions) {
      for(int var2 = 0; var2 < 5; ++var2) {
         double var3 = this.random.nextGaussian() * 0.02D;
         double var5 = this.random.nextGaussian() * 0.02D;
         double var7 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(particleOptions, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), this.y + 1.0D + (double)(this.random.nextFloat() * this.getBbHeight()), this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), var3, var5, var7);
      }

   }

   protected void closeContainer() {
      this.containerMenu = this.inventoryMenu;
   }

   public void rideTick() {
      if(!this.level.isClientSide && this.isSneaking() && this.isPassenger()) {
         this.stopRiding();
         this.setSneaking(false);
      } else {
         double var1 = this.x;
         double var3 = this.y;
         double var5 = this.z;
         float var7 = this.yRot;
         float var8 = this.xRot;
         super.rideTick();
         this.oBob = this.bob;
         this.bob = 0.0F;
         this.checkRidingStatistiscs(this.x - var1, this.y - var3, this.z - var5);
         if(this.getVehicle() instanceof Pig) {
            this.xRot = var8;
            this.yRot = var7;
            this.yBodyRot = ((Pig)this.getVehicle()).yBodyRot;
         }

      }
   }

   public void resetPos() {
      this.setPose(Pose.STANDING);
      super.resetPos();
      this.setHealth(this.getMaxHealth());
      this.deathTime = 0;
   }

   protected void serverAiStep() {
      super.serverAiStep();
      this.updateSwingTime();
      this.yHeadRot = this.yRot;
   }

   public void aiStep() {
      if(this.jumpTriggerTime > 0) {
         --this.jumpTriggerTime;
      }

      if(this.level.getDifficulty() == Difficulty.PEACEFUL && this.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
         if(this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
            this.heal(1.0F);
         }

         if(this.foodData.needsFood() && this.tickCount % 10 == 0) {
            this.foodData.setFoodLevel(this.foodData.getFoodLevel() + 1);
         }
      }

      this.inventory.tick();
      this.oBob = this.bob;
      super.aiStep();
      AttributeInstance var1 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      if(!this.level.isClientSide) {
         var1.setBaseValue((double)this.abilities.getWalkingSpeed());
      }

      this.flyingSpeed = 0.02F;
      if(this.isSprinting()) {
         this.flyingSpeed = (float)((double)this.flyingSpeed + 0.005999999865889549D);
      }

      this.setSpeed((float)var1.getValue());
      float var2;
      if(this.onGround && this.getHealth() > 0.0F && !this.isSwimming()) {
         var2 = Math.min(0.1F, Mth.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement())));
      } else {
         var2 = 0.0F;
      }

      this.bob += (var2 - this.bob) * 0.4F;
      if(this.getHealth() > 0.0F && !this.isSpectator()) {
         AABB var3;
         if(this.isPassenger() && !this.getVehicle().removed) {
            var3 = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0D, 0.0D, 1.0D);
         } else {
            var3 = this.getBoundingBox().inflate(1.0D, 0.5D, 1.0D);
         }

         List<Entity> var4 = this.level.getEntities(this, var3);

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            Entity var6 = (Entity)var4.get(var5);
            if(!var6.removed) {
               this.touch(var6);
            }
         }
      }

      this.playShoulderEntityAmbientSound(this.getShoulderEntityLeft());
      this.playShoulderEntityAmbientSound(this.getShoulderEntityRight());
      if(!this.level.isClientSide && (this.fallDistance > 0.5F || this.isInWater() || this.isPassenger()) || this.abilities.flying || this.isSleeping()) {
         this.removeEntitiesOnShoulder();
      }

   }

   private void playShoulderEntityAmbientSound(@Nullable CompoundTag compoundTag) {
      if(compoundTag != null && !compoundTag.contains("Silent") || !compoundTag.getBoolean("Silent")) {
         String var2 = compoundTag.getString("id");
         EntityType.byString(var2).filter((entityType) -> {
            return entityType == EntityType.PARROT;
         }).ifPresent((entityType) -> {
            Parrot.playAmbientSound(this.level, this);
         });
      }

   }

   private void touch(Entity entity) {
      entity.playerTouch(this);
   }

   public int getScore() {
      return ((Integer)this.entityData.get(DATA_SCORE_ID)).intValue();
   }

   public void setScore(int score) {
      this.entityData.set(DATA_SCORE_ID, Integer.valueOf(score));
   }

   public void increaseScore(int i) {
      int var2 = this.getScore();
      this.entityData.set(DATA_SCORE_ID, Integer.valueOf(var2 + i));
   }

   public void die(DamageSource damageSource) {
      super.die(damageSource);
      this.setPos(this.x, this.y, this.z);
      if(!this.isSpectator()) {
         this.dropAllDeathLoot(damageSource);
      }

      if(damageSource != null) {
         this.setDeltaMovement((double)(-Mth.cos((this.hurtDir + this.yRot) * 0.017453292F) * 0.1F), 0.10000000149011612D, (double)(-Mth.sin((this.hurtDir + this.yRot) * 0.017453292F) * 0.1F));
      } else {
         this.setDeltaMovement(0.0D, 0.1D, 0.0D);
      }

      this.awardStat(Stats.DEATHS);
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      this.clearFire();
      this.setSharedFlag(0, false);
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if(!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
         this.destroyVanishingCursedItems();
         this.inventory.dropAll();
      }

   }

   protected void destroyVanishingCursedItems() {
      for(int var1 = 0; var1 < this.inventory.getContainerSize(); ++var1) {
         ItemStack var2 = this.inventory.getItem(var1);
         if(!var2.isEmpty() && EnchantmentHelper.hasVanishingCurse(var2)) {
            this.inventory.removeItemNoUpdate(var1);
         }
      }

   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return damageSource == DamageSource.ON_FIRE?SoundEvents.PLAYER_HURT_ON_FIRE:(damageSource == DamageSource.DROWN?SoundEvents.PLAYER_HURT_DROWN:(damageSource == DamageSource.SWEET_BERRY_BUSH?SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH:SoundEvents.PLAYER_HURT));
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PLAYER_DEATH;
   }

   @Nullable
   public ItemEntity drop(boolean b) {
      return this.drop(this.inventory.removeItem(this.inventory.selected, b && !this.inventory.getSelected().isEmpty()?this.inventory.getSelected().getCount():1), false, true);
   }

   @Nullable
   public ItemEntity drop(ItemStack itemStack, boolean var2) {
      return this.drop(itemStack, false, var2);
   }

   @Nullable
   public ItemEntity drop(ItemStack itemStack, boolean var2, boolean var3) {
      if(itemStack.isEmpty()) {
         return null;
      } else {
         double var4 = this.y - 0.30000001192092896D + (double)this.getEyeHeight();
         ItemEntity var6 = new ItemEntity(this.level, this.x, var4, this.z, itemStack);
         var6.setPickUpDelay(40);
         if(var3) {
            var6.setThrower(this.getUUID());
         }

         if(var2) {
            float var7 = this.random.nextFloat() * 0.5F;
            float var8 = this.random.nextFloat() * 6.2831855F;
            this.setDeltaMovement((double)(-Mth.sin(var8) * var7), 0.20000000298023224D, (double)(Mth.cos(var8) * var7));
         } else {
            float var7 = 0.3F;
            float var8 = Mth.sin(this.xRot * 0.017453292F);
            float var9 = Mth.cos(this.xRot * 0.017453292F);
            float var10 = Mth.sin(this.yRot * 0.017453292F);
            float var11 = Mth.cos(this.yRot * 0.017453292F);
            float var12 = this.random.nextFloat() * 6.2831855F;
            float var13 = 0.02F * this.random.nextFloat();
            var6.setDeltaMovement((double)(-var10 * var9 * 0.3F) + Math.cos((double)var12) * (double)var13, (double)(-var8 * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(var11 * var9 * 0.3F) + Math.sin((double)var12) * (double)var13);
         }

         return var6;
      }
   }

   public float getDestroySpeed(BlockState blockState) {
      float var2 = this.inventory.getDestroySpeed(blockState);
      if(var2 > 1.0F) {
         int var3 = EnchantmentHelper.getBlockEfficiency(this);
         ItemStack var4 = this.getMainHandItem();
         if(var3 > 0 && !var4.isEmpty()) {
            var2 += (float)(var3 * var3 + 1);
         }
      }

      if(MobEffectUtil.hasDigSpeed(this)) {
         var2 *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2F;
      }

      if(this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
         float var3;
         switch(this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
         case 0:
            var3 = 0.3F;
            break;
         case 1:
            var3 = 0.09F;
            break;
         case 2:
            var3 = 0.0027F;
            break;
         case 3:
         default:
            var3 = 8.1E-4F;
         }

         var2 *= var3;
      }

      if(this.isUnderLiquid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
         var2 /= 5.0F;
      }

      if(!this.onGround) {
         var2 /= 5.0F;
      }

      return var2;
   }

   public boolean canDestroy(BlockState blockState) {
      return blockState.getMaterial().isAlwaysDestroyable() || this.inventory.canDestroy(blockState);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setUUID(createPlayerUUID(this.gameProfile));
      ListTag var2 = compoundTag.getList("Inventory", 10);
      this.inventory.load(var2);
      this.inventory.selected = compoundTag.getInt("SelectedItemSlot");
      this.sleepCounter = compoundTag.getShort("SleepTimer");
      this.experienceProgress = compoundTag.getFloat("XpP");
      this.experienceLevel = compoundTag.getInt("XpLevel");
      this.totalExperience = compoundTag.getInt("XpTotal");
      this.enchantmentSeed = compoundTag.getInt("XpSeed");
      if(this.enchantmentSeed == 0) {
         this.enchantmentSeed = this.random.nextInt();
      }

      this.setScore(compoundTag.getInt("Score"));
      if(compoundTag.contains("SpawnX", 99) && compoundTag.contains("SpawnY", 99) && compoundTag.contains("SpawnZ", 99)) {
         this.respawnPosition = new BlockPos(compoundTag.getInt("SpawnX"), compoundTag.getInt("SpawnY"), compoundTag.getInt("SpawnZ"));
         this.respawnForced = compoundTag.getBoolean("SpawnForced");
      }

      this.foodData.readAdditionalSaveData(compoundTag);
      this.abilities.loadSaveData(compoundTag);
      if(compoundTag.contains("EnderItems", 9)) {
         this.enderChestInventory.fromTag(compoundTag.getList("EnderItems", 10));
      }

      if(compoundTag.contains("ShoulderEntityLeft", 10)) {
         this.setShoulderEntityLeft(compoundTag.getCompound("ShoulderEntityLeft"));
      }

      if(compoundTag.contains("ShoulderEntityRight", 10)) {
         this.setShoulderEntityRight(compoundTag.getCompound("ShoulderEntityRight"));
      }

   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      compoundTag.put("Inventory", this.inventory.save(new ListTag()));
      compoundTag.putInt("SelectedItemSlot", this.inventory.selected);
      compoundTag.putShort("SleepTimer", (short)this.sleepCounter);
      compoundTag.putFloat("XpP", this.experienceProgress);
      compoundTag.putInt("XpLevel", this.experienceLevel);
      compoundTag.putInt("XpTotal", this.totalExperience);
      compoundTag.putInt("XpSeed", this.enchantmentSeed);
      compoundTag.putInt("Score", this.getScore());
      if(this.respawnPosition != null) {
         compoundTag.putInt("SpawnX", this.respawnPosition.getX());
         compoundTag.putInt("SpawnY", this.respawnPosition.getY());
         compoundTag.putInt("SpawnZ", this.respawnPosition.getZ());
         compoundTag.putBoolean("SpawnForced", this.respawnForced);
      }

      this.foodData.addAdditionalSaveData(compoundTag);
      this.abilities.addSaveData(compoundTag);
      compoundTag.put("EnderItems", this.enderChestInventory.createTag());
      if(!this.getShoulderEntityLeft().isEmpty()) {
         compoundTag.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
      }

      if(!this.getShoulderEntityRight().isEmpty()) {
         compoundTag.put("ShoulderEntityRight", this.getShoulderEntityRight());
      }

   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else if(this.abilities.invulnerable && !damageSource.isBypassInvul()) {
         return false;
      } else {
         this.noActionTime = 0;
         if(this.getHealth() <= 0.0F) {
            return false;
         } else {
            this.removeEntitiesOnShoulder();
            if(damageSource.scalesWithDifficulty()) {
               if(this.level.getDifficulty() == Difficulty.PEACEFUL) {
                  var2 = 0.0F;
               }

               if(this.level.getDifficulty() == Difficulty.EASY) {
                  var2 = Math.min(var2 / 2.0F + 1.0F, var2);
               }

               if(this.level.getDifficulty() == Difficulty.HARD) {
                  var2 = var2 * 3.0F / 2.0F;
               }
            }

            return var2 == 0.0F?false:super.hurt(damageSource, var2);
         }
      }
   }

   protected void blockUsingShield(LivingEntity livingEntity) {
      super.blockUsingShield(livingEntity);
      if(livingEntity.getMainHandItem().getItem() instanceof AxeItem) {
         this.disableShield(true);
      }

   }

   public boolean canHarmPlayer(Player player) {
      Team var2 = this.getTeam();
      Team var3 = player.getTeam();
      return var2 == null?true:(!var2.isAlliedTo(var3)?true:var2.isAllowFriendlyFire());
   }

   protected void hurtArmor(float f) {
      this.inventory.hurtArmor(f);
   }

   protected void hurtCurrentlyUsedShield(float f) {
      if(f >= 3.0F && this.useItem.getItem() == Items.SHIELD) {
         int var2 = 1 + Mth.floor(f);
         InteractionHand var3 = this.getUsedItemHand();
         this.useItem.hurtAndBreak(var2, this, (player) -> {
            player.broadcastBreakEvent(var3);
         });
         if(this.useItem.isEmpty()) {
            if(var3 == InteractionHand.MAIN_HAND) {
               this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else {
               this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }

            this.useItem = ItemStack.EMPTY;
            this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
         }
      }

   }

   protected void actuallyHurt(DamageSource damageSource, float var2) {
      if(!this.isInvulnerableTo(damageSource)) {
         var2 = this.getDamageAfterArmorAbsorb(damageSource, var2);
         var2 = this.getDamageAfterMagicAbsorb(damageSource, var2);
         float var3 = var2;
         var2 = Math.max(var2 - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (var3 - var2));
         float var4 = var3 - var2;
         if(var4 > 0.0F && var4 < 3.4028235E37F) {
            this.awardStat(Stats.DAMAGE_ABSORBED, Math.round(var4 * 10.0F));
         }

         if(var2 != 0.0F) {
            this.causeFoodExhaustion(damageSource.getFoodExhaustion());
            float var5 = this.getHealth();
            this.setHealth(this.getHealth() - var2);
            this.getCombatTracker().recordDamage(damageSource, var5, var2);
            if(var2 < 3.4028235E37F) {
               this.awardStat(Stats.DAMAGE_TAKEN, Math.round(var2 * 10.0F));
            }

         }
      }
   }

   public void openTextEdit(SignBlockEntity signBlockEntity) {
   }

   public void openMinecartCommandBlock(BaseCommandBlock baseCommandBlock) {
   }

   public void openCommandBlock(CommandBlockEntity commandBlockEntity) {
   }

   public void openStructureBlock(StructureBlockEntity structureBlockEntity) {
   }

   public void openJigsawBlock(JigsawBlockEntity jigsawBlockEntity) {
   }

   public void openHorseInventory(AbstractHorse abstractHorse, Container container) {
   }

   public OptionalInt openMenu(@Nullable MenuProvider menuProvider) {
      return OptionalInt.empty();
   }

   public void sendMerchantOffers(int var1, MerchantOffers merchantOffers, int var3, int var4, boolean var5, boolean var6) {
   }

   public void openItemGui(ItemStack itemStack, InteractionHand interactionHand) {
   }

   public InteractionResult interactOn(Entity entity, InteractionHand interactionHand) {
      if(this.isSpectator()) {
         if(entity instanceof MenuProvider) {
            this.openMenu((MenuProvider)entity);
         }

         return InteractionResult.PASS;
      } else {
         ItemStack var3 = this.getItemInHand(interactionHand);
         ItemStack var4 = var3.isEmpty()?ItemStack.EMPTY:var3.copy();
         if(entity.interact(this, interactionHand)) {
            if(this.abilities.instabuild && var3 == this.getItemInHand(interactionHand) && var3.getCount() < var4.getCount()) {
               var3.setCount(var4.getCount());
            }

            return InteractionResult.SUCCESS;
         } else {
            if(!var3.isEmpty() && entity instanceof LivingEntity) {
               if(this.abilities.instabuild) {
                  var3 = var4;
               }

               if(var3.interactEnemy(this, (LivingEntity)entity, interactionHand)) {
                  if(var3.isEmpty() && !this.abilities.instabuild) {
                     this.setItemInHand(interactionHand, ItemStack.EMPTY);
                  }

                  return InteractionResult.SUCCESS;
               }
            }

            return InteractionResult.PASS;
         }
      }
   }

   public double getRidingHeight() {
      return -0.35D;
   }

   public void stopRiding() {
      super.stopRiding();
      this.boardingCooldown = 0;
   }

   protected boolean isImmobile() {
      return super.isImmobile() || this.isSleeping();
   }

   public void attack(Entity lastHurtMob) {
      if(lastHurtMob.isAttackable()) {
         if(!lastHurtMob.skipAttackInteraction(this)) {
            float var2 = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
            float var3;
            if(lastHurtMob instanceof LivingEntity) {
               var3 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)lastHurtMob).getMobType());
            } else {
               var3 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), MobType.UNDEFINED);
            }

            float var4 = this.getAttackStrengthScale(0.5F);
            var2 = var2 * (0.2F + var4 * var4 * 0.8F);
            var3 = var3 * var4;
            this.resetAttackStrengthTicker();
            if(var2 > 0.0F || var3 > 0.0F) {
               boolean var5 = var4 > 0.9F;
               boolean var6 = false;
               int var7 = 0;
               var7 = var7 + EnchantmentHelper.getKnockbackBonus(this);
               if(this.isSprinting() && var5) {
                  this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
                  ++var7;
                  var6 = true;
               }

               boolean var8 = var5 && this.fallDistance > 0.0F && !this.onGround && !this.onLadder() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && lastHurtMob instanceof LivingEntity;
               var8 = var8 && !this.isSprinting();
               if(var8) {
                  var2 *= 1.5F;
               }

               var2 = var2 + var3;
               boolean var9 = false;
               double var10 = (double)(this.walkDist - this.walkDistO);
               if(var5 && !var8 && !var6 && this.onGround && var10 < (double)this.getSpeed()) {
                  ItemStack var12 = this.getItemInHand(InteractionHand.MAIN_HAND);
                  if(var12.getItem() instanceof SwordItem) {
                     var9 = true;
                  }
               }

               float var12 = 0.0F;
               boolean var13 = false;
               int var14 = EnchantmentHelper.getFireAspect(this);
               if(lastHurtMob instanceof LivingEntity) {
                  var12 = ((LivingEntity)lastHurtMob).getHealth();
                  if(var14 > 0 && !lastHurtMob.isOnFire()) {
                     var13 = true;
                     lastHurtMob.setSecondsOnFire(1);
                  }
               }

               Vec3 var15 = lastHurtMob.getDeltaMovement();
               boolean var16 = lastHurtMob.hurt(DamageSource.playerAttack(this), var2);
               if(var16) {
                  if(var7 > 0) {
                     if(lastHurtMob instanceof LivingEntity) {
                        ((LivingEntity)lastHurtMob).knockback(this, (float)var7 * 0.5F, (double)Mth.sin(this.yRot * 0.017453292F), (double)(-Mth.cos(this.yRot * 0.017453292F)));
                     } else {
                        lastHurtMob.push((double)(-Mth.sin(this.yRot * 0.017453292F) * (float)var7 * 0.5F), 0.1D, (double)(Mth.cos(this.yRot * 0.017453292F) * (float)var7 * 0.5F));
                     }

                     this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                     this.setSprinting(false);
                  }

                  if(var9) {
                     float var17 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * var2;

                     for(LivingEntity var20 : this.level.getEntitiesOfClass(LivingEntity.class, lastHurtMob.getBoundingBox().inflate(1.0D, 0.25D, 1.0D))) {
                        if(var20 != this && var20 != lastHurtMob && !this.isAlliedTo(var20) && (!(var20 instanceof ArmorStand) || !((ArmorStand)var20).isMarker()) && this.distanceToSqr(var20) < 9.0D) {
                           var20.knockback(this, 0.4F, (double)Mth.sin(this.yRot * 0.017453292F), (double)(-Mth.cos(this.yRot * 0.017453292F)));
                           var20.hurt(DamageSource.playerAttack(this), var17);
                        }
                     }

                     this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
                     this.sweepAttack();
                  }

                  if(lastHurtMob instanceof ServerPlayer && lastHurtMob.hurtMarked) {
                     ((ServerPlayer)lastHurtMob).connection.send(new ClientboundSetEntityMotionPacket(lastHurtMob));
                     lastHurtMob.hurtMarked = false;
                     lastHurtMob.setDeltaMovement(var15);
                  }

                  if(var8) {
                     this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
                     this.crit(lastHurtMob);
                  }

                  if(!var8 && !var9) {
                     if(var5) {
                        this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F);
                     } else {
                        this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F);
                     }
                  }

                  if(var3 > 0.0F) {
                     this.magicCrit(lastHurtMob);
                  }

                  this.setLastHurtMob(lastHurtMob);
                  if(lastHurtMob instanceof LivingEntity) {
                     EnchantmentHelper.doPostHurtEffects((LivingEntity)lastHurtMob, this);
                  }

                  EnchantmentHelper.doPostDamageEffects(this, lastHurtMob);
                  ItemStack var17 = this.getMainHandItem();
                  Entity var18 = lastHurtMob;
                  if(lastHurtMob instanceof EnderDragonPart) {
                     var18 = ((EnderDragonPart)lastHurtMob).parentMob;
                  }

                  if(!this.level.isClientSide && !var17.isEmpty() && var18 instanceof LivingEntity) {
                     var17.hurtEnemy((LivingEntity)var18, this);
                     if(var17.isEmpty()) {
                        this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                     }
                  }

                  if(lastHurtMob instanceof LivingEntity) {
                     float var19 = var12 - ((LivingEntity)lastHurtMob).getHealth();
                     this.awardStat(Stats.DAMAGE_DEALT, Math.round(var19 * 10.0F));
                     if(var14 > 0) {
                        lastHurtMob.setSecondsOnFire(var14 * 4);
                     }

                     if(this.level instanceof ServerLevel && var19 > 2.0F) {
                        int var20 = (int)((double)var19 * 0.5D);
                        ((ServerLevel)this.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, lastHurtMob.x, lastHurtMob.y + (double)(lastHurtMob.getBbHeight() * 0.5F), lastHurtMob.z, var20, 0.1D, 0.0D, 0.1D, 0.2D);
                     }
                  }

                  this.causeFoodExhaustion(0.1F);
               } else {
                  this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F);
                  if(var13) {
                     lastHurtMob.clearFire();
                  }
               }
            }

         }
      }
   }

   protected void doAutoAttackOnTouch(LivingEntity livingEntity) {
      this.attack(livingEntity);
   }

   public void disableShield(boolean b) {
      float var2 = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
      if(b) {
         var2 += 0.75F;
      }

      if(this.random.nextFloat() < var2) {
         this.getCooldowns().addCooldown(Items.SHIELD, 100);
         this.stopUsingItem();
         this.level.broadcastEntityEvent(this, (byte)30);
      }

   }

   public void crit(Entity entity) {
   }

   public void magicCrit(Entity entity) {
   }

   public void sweepAttack() {
      double var1 = (double)(-Mth.sin(this.yRot * 0.017453292F));
      double var3 = (double)Mth.cos(this.yRot * 0.017453292F);
      if(this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).sendParticles(ParticleTypes.SWEEP_ATTACK, this.x + var1, this.y + (double)this.getBbHeight() * 0.5D, this.z + var3, 0, var1, 0.0D, var3, 0.0D);
      }

   }

   public void respawn() {
   }

   public void remove() {
      super.remove();
      this.inventoryMenu.removed(this);
      if(this.containerMenu != null) {
         this.containerMenu.removed(this);
      }

   }

   public boolean isLocalPlayer() {
      return false;
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public Either startSleepInBed(BlockPos blockPos) {
      Direction var2 = (Direction)this.level.getBlockState(blockPos).getValue(HorizontalDirectionalBlock.FACING);
      if(!this.level.isClientSide) {
         if(this.isSleeping() || !this.isAlive()) {
            return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
         }

         if(!this.level.dimension.isNaturalDimension()) {
            return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_HERE);
         }

         if(this.level.isDay()) {
            return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
         }

         if(!this.bedInRange(blockPos, var2)) {
            return Either.left(Player.BedSleepingProblem.TOO_FAR_AWAY);
         }

         if(this.bedBlocked(blockPos, var2)) {
            return Either.left(Player.BedSleepingProblem.OBSTRUCTED);
         }

         if(!this.isCreative()) {
            double var3 = 8.0D;
            double var5 = 5.0D;
            List<Monster> var7 = this.level.getEntitiesOfClass(Monster.class, new AABB((double)blockPos.getX() - 8.0D, (double)blockPos.getY() - 5.0D, (double)blockPos.getZ() - 8.0D, (double)blockPos.getX() + 8.0D, (double)blockPos.getY() + 5.0D, (double)blockPos.getZ() + 8.0D), (monster) -> {
               return monster.isPreventingPlayerRest(this);
            });
            if(!var7.isEmpty()) {
               return Either.left(Player.BedSleepingProblem.NOT_SAFE);
            }
         }
      }

      this.startSleeping(blockPos);
      this.sleepCounter = 0;
      if(this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).updateSleepingPlayerList();
      }

      return Either.right(Unit.INSTANCE);
   }

   public void startSleeping(BlockPos blockPos) {
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      super.startSleeping(blockPos);
   }

   private boolean bedInRange(BlockPos blockPos, Direction direction) {
      if(Math.abs(this.x - (double)blockPos.getX()) <= 3.0D && Math.abs(this.y - (double)blockPos.getY()) <= 2.0D && Math.abs(this.z - (double)blockPos.getZ()) <= 3.0D) {
         return true;
      } else {
         BlockPos blockPos = blockPos.relative(direction.getOpposite());
         return Math.abs(this.x - (double)blockPos.getX()) <= 3.0D && Math.abs(this.y - (double)blockPos.getY()) <= 2.0D && Math.abs(this.z - (double)blockPos.getZ()) <= 3.0D;
      }
   }

   private boolean bedBlocked(BlockPos blockPos, Direction direction) {
      BlockPos blockPos = blockPos.above();
      return !this.freeAt(blockPos) || !this.freeAt(blockPos.relative(direction.getOpposite()));
   }

   public void stopSleepInBed(boolean var1, boolean var2, boolean var3) {
      Optional<BlockPos> var4 = this.getSleepingPos();
      super.stopSleeping();
      if(this.level instanceof ServerLevel && var2) {
         ((ServerLevel)this.level).updateSleepingPlayerList();
      }

      this.sleepCounter = var1?0:100;
      if(var3) {
         var4.ifPresent((blockPos) -> {
            this.setRespawnPosition(blockPos, false);
         });
      }

   }

   public void stopSleeping() {
      this.stopSleepInBed(true, true, false);
   }

   public static Optional checkBedValidRespawnPosition(LevelReader levelReader, BlockPos blockPos, boolean var2) {
      Block var3 = levelReader.getBlockState(blockPos).getBlock();
      if(!(var3 instanceof BedBlock)) {
         if(!var2) {
            return Optional.empty();
         } else {
            boolean var4 = var3.isPossibleToRespawnInThis();
            boolean var5 = levelReader.getBlockState(blockPos.above()).getBlock().isPossibleToRespawnInThis();
            return var4 && var5?Optional.of(new Vec3((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.1D, (double)blockPos.getZ() + 0.5D)):Optional.empty();
         }
      } else {
         return BedBlock.findStandUpPosition(EntityType.PLAYER, levelReader, blockPos, 0);
      }
   }

   public boolean isSleepingLongEnough() {
      return this.isSleeping() && this.sleepCounter >= 100;
   }

   public int getSleepTimer() {
      return this.sleepCounter;
   }

   public void displayClientMessage(Component component, boolean var2) {
   }

   public BlockPos getRespawnPosition() {
      return this.respawnPosition;
   }

   public boolean isRespawnForced() {
      return this.respawnForced;
   }

   public void setRespawnPosition(BlockPos respawnPosition, boolean respawnForced) {
      if(respawnPosition != null) {
         this.respawnPosition = respawnPosition;
         this.respawnForced = respawnForced;
      } else {
         this.respawnPosition = null;
         this.respawnForced = false;
      }

   }

   public void awardStat(ResourceLocation resourceLocation) {
      this.awardStat(Stats.CUSTOM.get(resourceLocation));
   }

   public void awardStat(ResourceLocation resourceLocation, int var2) {
      this.awardStat(Stats.CUSTOM.get(resourceLocation), var2);
   }

   public void awardStat(Stat stat) {
      this.awardStat((Stat)stat, 1);
   }

   public void awardStat(Stat stat, int var2) {
   }

   public void resetStat(Stat stat) {
   }

   public int awardRecipes(Collection collection) {
      return 0;
   }

   public void awardRecipesByKey(ResourceLocation[] resourceLocations) {
   }

   public int resetRecipes(Collection collection) {
      return 0;
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      this.awardStat(Stats.JUMP);
      if(this.isSprinting()) {
         this.causeFoodExhaustion(0.2F);
      } else {
         this.causeFoodExhaustion(0.05F);
      }

   }

   public void travel(Vec3 vec3) {
      double var2 = this.x;
      double var4 = this.y;
      double var6 = this.z;
      if(this.isSwimming() && !this.isPassenger()) {
         double var8 = this.getLookAngle().y;
         double var10 = var8 < -0.2D?0.085D:0.06D;
         if(var8 <= 0.0D || this.jumping || !this.level.getBlockState(new BlockPos(this.x, this.y + 1.0D - 0.1D, this.z)).getFluidState().isEmpty()) {
            Vec3 var12 = this.getDeltaMovement();
            this.setDeltaMovement(var12.add(0.0D, (var8 - var12.y) * var10, 0.0D));
         }
      }

      if(this.abilities.flying && !this.isPassenger()) {
         double var8 = this.getDeltaMovement().y;
         float var10 = this.flyingSpeed;
         this.flyingSpeed = this.abilities.getFlyingSpeed() * (float)(this.isSprinting()?2:1);
         super.travel(vec3);
         Vec3 var11 = this.getDeltaMovement();
         this.setDeltaMovement(var11.x, var8 * 0.6D, var11.z);
         this.flyingSpeed = var10;
         this.fallDistance = 0.0F;
         this.setSharedFlag(7, false);
      } else {
         super.travel(vec3);
      }

      this.checkMovementStatistics(this.x - var2, this.y - var4, this.z - var6);
   }

   public void updateSwimming() {
      if(this.abilities.flying) {
         this.setSwimming(false);
      } else {
         super.updateSwimming();
      }

   }

   protected boolean freeAt(BlockPos blockPos) {
      return !this.level.getBlockState(blockPos).isViewBlocking(this.level, blockPos);
   }

   public float getSpeed() {
      return (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
   }

   public void checkMovementStatistics(double var1, double var3, double var5) {
      if(!this.isPassenger()) {
         if(this.isSwimming()) {
            int var7 = Math.round(Mth.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            if(var7 > 0) {
               this.awardStat(Stats.SWIM_ONE_CM, var7);
               this.causeFoodExhaustion(0.01F * (float)var7 * 0.01F);
            }
         } else if(this.isUnderLiquid(FluidTags.WATER, true)) {
            int var7 = Math.round(Mth.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            if(var7 > 0) {
               this.awardStat(Stats.WALK_UNDER_WATER_ONE_CM, var7);
               this.causeFoodExhaustion(0.01F * (float)var7 * 0.01F);
            }
         } else if(this.isInWater()) {
            int var7 = Math.round(Mth.sqrt(var1 * var1 + var5 * var5) * 100.0F);
            if(var7 > 0) {
               this.awardStat(Stats.WALK_ON_WATER_ONE_CM, var7);
               this.causeFoodExhaustion(0.01F * (float)var7 * 0.01F);
            }
         } else if(this.onLadder()) {
            if(var3 > 0.0D) {
               this.awardStat(Stats.CLIMB_ONE_CM, (int)Math.round(var3 * 100.0D));
            }
         } else if(this.onGround) {
            int var7 = Math.round(Mth.sqrt(var1 * var1 + var5 * var5) * 100.0F);
            if(var7 > 0) {
               if(this.isSprinting()) {
                  this.awardStat(Stats.SPRINT_ONE_CM, var7);
                  this.causeFoodExhaustion(0.1F * (float)var7 * 0.01F);
               } else if(this.isSneaking()) {
                  this.awardStat(Stats.CROUCH_ONE_CM, var7);
                  this.causeFoodExhaustion(0.0F * (float)var7 * 0.01F);
               } else {
                  this.awardStat(Stats.WALK_ONE_CM, var7);
                  this.causeFoodExhaustion(0.0F * (float)var7 * 0.01F);
               }
            }
         } else if(this.isFallFlying()) {
            int var7 = Math.round(Mth.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            this.awardStat(Stats.AVIATE_ONE_CM, var7);
         } else {
            int var7 = Math.round(Mth.sqrt(var1 * var1 + var5 * var5) * 100.0F);
            if(var7 > 25) {
               this.awardStat(Stats.FLY_ONE_CM, var7);
            }
         }

      }
   }

   private void checkRidingStatistiscs(double var1, double var3, double var5) {
      if(this.isPassenger()) {
         int var7 = Math.round(Mth.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
         if(var7 > 0) {
            if(this.getVehicle() instanceof AbstractMinecart) {
               this.awardStat(Stats.MINECART_ONE_CM, var7);
            } else if(this.getVehicle() instanceof Boat) {
               this.awardStat(Stats.BOAT_ONE_CM, var7);
            } else if(this.getVehicle() instanceof Pig) {
               this.awardStat(Stats.PIG_ONE_CM, var7);
            } else if(this.getVehicle() instanceof AbstractHorse) {
               this.awardStat(Stats.HORSE_ONE_CM, var7);
            }
         }
      }

   }

   public void causeFallDamage(float var1, float var2) {
      if(!this.abilities.mayfly) {
         if(var1 >= 2.0F) {
            this.awardStat(Stats.FALL_ONE_CM, (int)Math.round((double)var1 * 100.0D));
         }

         super.causeFallDamage(var1, var2);
      }
   }

   protected void doWaterSplashEffect() {
      if(!this.isSpectator()) {
         super.doWaterSplashEffect();
      }

   }

   protected SoundEvent getFallDamageSound(int i) {
      return i > 4?SoundEvents.PLAYER_BIG_FALL:SoundEvents.PLAYER_SMALL_FALL;
   }

   public void killed(LivingEntity livingEntity) {
      this.awardStat(Stats.ENTITY_KILLED.get(livingEntity.getType()));
   }

   public void makeStuckInBlock(BlockState blockState, Vec3 vec3) {
      if(!this.abilities.flying) {
         super.makeStuckInBlock(blockState, vec3);
      }

   }

   public void giveExperiencePoints(int i) {
      this.increaseScore(i);
      this.experienceProgress += (float)i / (float)this.getXpNeededForNextLevel();
      this.totalExperience = Mth.clamp(this.totalExperience + i, 0, Integer.MAX_VALUE);

      while(this.experienceProgress < 0.0F) {
         float var2 = this.experienceProgress * (float)this.getXpNeededForNextLevel();
         if(this.experienceLevel > 0) {
            this.giveExperienceLevels(-1);
            this.experienceProgress = 1.0F + var2 / (float)this.getXpNeededForNextLevel();
         } else {
            this.giveExperienceLevels(-1);
            this.experienceProgress = 0.0F;
         }
      }

      while(this.experienceProgress >= 1.0F) {
         this.experienceProgress = (this.experienceProgress - 1.0F) * (float)this.getXpNeededForNextLevel();
         this.giveExperienceLevels(1);
         this.experienceProgress /= (float)this.getXpNeededForNextLevel();
      }

   }

   public int getEnchantmentSeed() {
      return this.enchantmentSeed;
   }

   public void onEnchantmentPerformed(ItemStack itemStack, int var2) {
      this.experienceLevel -= var2;
      if(this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experienceProgress = 0.0F;
         this.totalExperience = 0;
      }

      this.enchantmentSeed = this.random.nextInt();
   }

   public void giveExperienceLevels(int i) {
      this.experienceLevel += i;
      if(this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experienceProgress = 0.0F;
         this.totalExperience = 0;
      }

      if(i > 0 && this.experienceLevel % 5 == 0 && (float)this.lastLevelUpTime < (float)this.tickCount - 100.0F) {
         float var2 = this.experienceLevel > 30?1.0F:(float)this.experienceLevel / 30.0F;
         this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), var2 * 0.75F, 1.0F);
         this.lastLevelUpTime = this.tickCount;
      }

   }

   public int getXpNeededForNextLevel() {
      return this.experienceLevel >= 30?112 + (this.experienceLevel - 30) * 9:(this.experienceLevel >= 15?37 + (this.experienceLevel - 15) * 5:7 + this.experienceLevel * 2);
   }

   public void causeFoodExhaustion(float f) {
      if(!this.abilities.invulnerable) {
         if(!this.level.isClientSide) {
            this.foodData.addExhaustion(f);
         }

      }
   }

   public FoodData getFoodData() {
      return this.foodData;
   }

   public boolean canEat(boolean b) {
      return !this.abilities.invulnerable && (b || this.foodData.needsFood());
   }

   public boolean isHurt() {
      return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
   }

   public boolean mayBuild() {
      return this.abilities.mayBuild;
   }

   public boolean mayUseItemAt(BlockPos blockPos, Direction direction, ItemStack itemStack) {
      if(this.abilities.mayBuild) {
         return true;
      } else {
         BlockPos blockPos = blockPos.relative(direction.getOpposite());
         BlockInWorld var5 = new BlockInWorld(this.level, blockPos, false);
         return itemStack.hasAdventureModePlaceTagForBlock(this.level.getTagManager(), var5);
      }
   }

   protected int getExperienceReward(Player player) {
      if(!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !this.isSpectator()) {
         int var2 = this.experienceLevel * 7;
         return var2 > 100?100:var2;
      } else {
         return 0;
      }
   }

   protected boolean isAlwaysExperienceDropper() {
      return true;
   }

   public boolean shouldShowName() {
      return true;
   }

   protected boolean makeStepSound() {
      return !this.abilities.flying;
   }

   public void onUpdateAbilities() {
   }

   public void setGameMode(GameType gameMode) {
   }

   public Component getName() {
      return new TextComponent(this.gameProfile.getName());
   }

   public PlayerEnderChestContainer getEnderChestInventory() {
      return this.enderChestInventory;
   }

   public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.MAINHAND?this.inventory.getSelected():(equipmentSlot == EquipmentSlot.OFFHAND?(ItemStack)this.inventory.offhand.get(0):(equipmentSlot.getType() == EquipmentSlot.Type.ARMOR?(ItemStack)this.inventory.armor.get(equipmentSlot.getIndex()):ItemStack.EMPTY));
   }

   public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
      if(equipmentSlot == EquipmentSlot.MAINHAND) {
         this.playEquipSound(itemStack);
         this.inventory.items.set(this.inventory.selected, itemStack);
      } else if(equipmentSlot == EquipmentSlot.OFFHAND) {
         this.playEquipSound(itemStack);
         this.inventory.offhand.set(0, itemStack);
      } else if(equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
         this.playEquipSound(itemStack);
         this.inventory.armor.set(equipmentSlot.getIndex(), itemStack);
      }

   }

   public boolean addItem(ItemStack itemStack) {
      this.playEquipSound(itemStack);
      return this.inventory.add(itemStack);
   }

   public Iterable getHandSlots() {
      return Lists.newArrayList(new ItemStack[]{this.getMainHandItem(), this.getOffhandItem()});
   }

   public Iterable getArmorSlots() {
      return this.inventory.armor;
   }

   public boolean setEntityOnShoulder(CompoundTag entityOnShoulder) {
      if(!this.isPassenger() && this.onGround && !this.isInWater()) {
         if(this.getShoulderEntityLeft().isEmpty()) {
            this.setShoulderEntityLeft(entityOnShoulder);
            this.timeEntitySatOnShoulder = this.level.getGameTime();
            return true;
         } else if(this.getShoulderEntityRight().isEmpty()) {
            this.setShoulderEntityRight(entityOnShoulder);
            this.timeEntitySatOnShoulder = this.level.getGameTime();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void removeEntitiesOnShoulder() {
      if(this.timeEntitySatOnShoulder + 20L < this.level.getGameTime()) {
         this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
         this.setShoulderEntityLeft(new CompoundTag());
         this.respawnEntityOnShoulder(this.getShoulderEntityRight());
         this.setShoulderEntityRight(new CompoundTag());
      }

   }

   private void respawnEntityOnShoulder(CompoundTag compoundTag) {
      if(!this.level.isClientSide && !compoundTag.isEmpty()) {
         EntityType.create(compoundTag, this.level).ifPresent((entity) -> {
            if(entity instanceof TamableAnimal) {
               ((TamableAnimal)entity).setOwnerUUID(this.uuid);
            }

            entity.setPos(this.x, this.y + 0.699999988079071D, this.z);
            ((ServerLevel)this.level).addWithUUID(entity);
         });
      }

   }

   public boolean isInvisibleTo(Player player) {
      if(!this.isInvisible()) {
         return false;
      } else if(player.isSpectator()) {
         return false;
      } else {
         Team var2 = this.getTeam();
         return var2 == null || player == null || player.getTeam() != var2 || !var2.canSeeFriendlyInvisibles();
      }
   }

   public abstract boolean isSpectator();

   public boolean isSwimming() {
      return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
   }

   public abstract boolean isCreative();

   public boolean isPushedByWater() {
      return !this.abilities.flying;
   }

   public Scoreboard getScoreboard() {
      return this.level.getScoreboard();
   }

   public Component getDisplayName() {
      Component component = PlayerTeam.formatNameForTeam(this.getTeam(), this.getName());
      return this.decorateDisplayNameComponent(component);
   }

   public Component getDisplayNameWithUuid() {
      return (new TextComponent("")).append(this.getName()).append(" (").append(this.gameProfile.getId().toString()).append(")");
   }

   private Component decorateDisplayNameComponent(Component component) {
      String var2 = this.getGameProfile().getName();
      return component.withStyle((style) -> {
         style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + var2 + " ")).setHoverEvent(this.createHoverEvent()).setInsertion(var2);
      });
   }

   public String getScoreboardName() {
      return this.getGameProfile().getName();
   }

   public float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      switch(pose) {
      case SWIMMING:
      case FALL_FLYING:
      case SPIN_ATTACK:
         return 0.4F;
      case SNEAKING:
         return 1.27F;
      default:
         return 1.62F;
      }
   }

   public void setAbsorptionAmount(float absorptionAmount) {
      if(absorptionAmount < 0.0F) {
         absorptionAmount = 0.0F;
      }

      this.getEntityData().set(DATA_PLAYER_ABSORPTION_ID, Float.valueOf(absorptionAmount));
   }

   public float getAbsorptionAmount() {
      return ((Float)this.getEntityData().get(DATA_PLAYER_ABSORPTION_ID)).floatValue();
   }

   public static UUID createPlayerUUID(GameProfile gameProfile) {
      UUID uUID = gameProfile.getId();
      if(uUID == null) {
         uUID = createPlayerUUID(gameProfile.getName());
      }

      return uUID;
   }

   public static UUID createPlayerUUID(String string) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(StandardCharsets.UTF_8));
   }

   public boolean isModelPartShown(PlayerModelPart playerModelPart) {
      return (((Byte)this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION)).byteValue() & playerModelPart.getMask()) == playerModelPart.getMask();
   }

   public boolean setSlot(int var1, ItemStack itemStack) {
      if(var1 >= 0 && var1 < this.inventory.items.size()) {
         this.inventory.setItem(var1, itemStack);
         return true;
      } else {
         EquipmentSlot var3;
         if(var1 == 100 + EquipmentSlot.HEAD.getIndex()) {
            var3 = EquipmentSlot.HEAD;
         } else if(var1 == 100 + EquipmentSlot.CHEST.getIndex()) {
            var3 = EquipmentSlot.CHEST;
         } else if(var1 == 100 + EquipmentSlot.LEGS.getIndex()) {
            var3 = EquipmentSlot.LEGS;
         } else if(var1 == 100 + EquipmentSlot.FEET.getIndex()) {
            var3 = EquipmentSlot.FEET;
         } else {
            var3 = null;
         }

         if(var1 == 98) {
            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
            return true;
         } else if(var1 == 99) {
            this.setItemSlot(EquipmentSlot.OFFHAND, itemStack);
            return true;
         } else if(var3 == null) {
            int var4 = var1 - 200;
            if(var4 >= 0 && var4 < this.enderChestInventory.getContainerSize()) {
               this.enderChestInventory.setItem(var4, itemStack);
               return true;
            } else {
               return false;
            }
         } else {
            if(!itemStack.isEmpty()) {
               if(!(itemStack.getItem() instanceof ArmorItem) && !(itemStack.getItem() instanceof ElytraItem)) {
                  if(var3 != EquipmentSlot.HEAD) {
                     return false;
                  }
               } else if(Mob.getEquipmentSlotForItem(itemStack) != var3) {
                  return false;
               }
            }

            this.inventory.setItem(var3.getIndex() + this.inventory.items.size(), itemStack);
            return true;
         }
      }
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   public void setReducedDebugInfo(boolean reducedDebugInfo) {
      this.reducedDebugInfo = reducedDebugInfo;
   }

   public HumanoidArm getMainArm() {
      return ((Byte)this.entityData.get(DATA_PLAYER_MAIN_HAND)).byteValue() == 0?HumanoidArm.LEFT:HumanoidArm.RIGHT;
   }

   public void setMainArm(HumanoidArm mainArm) {
      this.entityData.set(DATA_PLAYER_MAIN_HAND, Byte.valueOf((byte)(mainArm == HumanoidArm.LEFT?0:1)));
   }

   public CompoundTag getShoulderEntityLeft() {
      return (CompoundTag)this.entityData.get(DATA_SHOULDER_LEFT);
   }

   protected void setShoulderEntityLeft(CompoundTag shoulderEntityLeft) {
      this.entityData.set(DATA_SHOULDER_LEFT, shoulderEntityLeft);
   }

   public CompoundTag getShoulderEntityRight() {
      return (CompoundTag)this.entityData.get(DATA_SHOULDER_RIGHT);
   }

   protected void setShoulderEntityRight(CompoundTag shoulderEntityRight) {
      this.entityData.set(DATA_SHOULDER_RIGHT, shoulderEntityRight);
   }

   public float getCurrentItemAttackStrengthDelay() {
      return (float)(1.0D / this.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getValue() * 20.0D);
   }

   public float getAttackStrengthScale(float f) {
      return Mth.clamp(((float)this.attackStrengthTicker + f) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
   }

   public void resetAttackStrengthTicker() {
      this.attackStrengthTicker = 0;
   }

   public ItemCooldowns getCooldowns() {
      return this.cooldowns;
   }

   public float getLuck() {
      return (float)this.getAttribute(SharedMonsterAttributes.LUCK).getValue();
   }

   public boolean canUseGameMasterBlocks() {
      return this.abilities.instabuild && this.getPermissionLevel() >= 2;
   }

   public boolean canTakeItem(ItemStack itemStack) {
      EquipmentSlot var2 = Mob.getEquipmentSlotForItem(itemStack);
      return this.getItemBySlot(var2).isEmpty();
   }

   public EntityDimensions getDimensions(Pose pose) {
      return (EntityDimensions)POSES.getOrDefault(pose, STANDING_DIMENSIONS);
   }

   public ItemStack getProjectile(ItemStack itemStack) {
      if(!(itemStack.getItem() instanceof ProjectileWeaponItem)) {
         return ItemStack.EMPTY;
      } else {
         Predicate<ItemStack> var2 = ((ProjectileWeaponItem)itemStack.getItem()).getSupportedHeldProjectiles();
         ItemStack var3 = ProjectileWeaponItem.getHeldProjectile(this, var2);
         if(!var3.isEmpty()) {
            return var3;
         } else {
            var2 = ((ProjectileWeaponItem)itemStack.getItem()).getAllSupportedProjectiles();

            for(int var4 = 0; var4 < this.inventory.getContainerSize(); ++var4) {
               ItemStack var5 = this.inventory.getItem(var4);
               if(var2.test(var5)) {
                  return var5;
               }
            }

            return this.abilities.instabuild?new ItemStack(Items.ARROW):ItemStack.EMPTY;
         }
      }
   }

   public ItemStack eat(Level level, ItemStack var2) {
      this.getFoodData().eat(var2.getItem(), var2);
      this.awardStat(Stats.ITEM_USED.get(var2.getItem()));
      level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
      if(this instanceof ServerPlayer) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)this, var2);
      }

      return super.eat(level, var2);
   }

   static {
      POSES = ImmutableMap.builder().put(Pose.STANDING, STANDING_DIMENSIONS).put(Pose.SLEEPING, SLEEPING_DIMENSIONS).put(Pose.FALL_FLYING, EntityDimensions.scalable(0.6F, 0.6F)).put(Pose.SWIMMING, EntityDimensions.scalable(0.6F, 0.6F)).put(Pose.SPIN_ATTACK, EntityDimensions.scalable(0.6F, 0.6F)).put(Pose.SNEAKING, EntityDimensions.scalable(0.6F, 1.5F)).put(Pose.DYING, EntityDimensions.fixed(0.2F, 0.2F)).build();
   }

   public static enum BedSleepingProblem {
      NOT_POSSIBLE_HERE,
      NOT_POSSIBLE_NOW(new TranslatableComponent("block.minecraft.bed.no_sleep", new Object[0])),
      TOO_FAR_AWAY(new TranslatableComponent("block.minecraft.bed.too_far_away", new Object[0])),
      OBSTRUCTED(new TranslatableComponent("block.minecraft.bed.obstructed", new Object[0])),
      OTHER_PROBLEM,
      NOT_SAFE(new TranslatableComponent("block.minecraft.bed.not_safe", new Object[0]));

      @Nullable
      private final Component message;

      private BedSleepingProblem() {
         this.message = null;
      }

      private BedSleepingProblem(Component message) {
         this.message = message;
      }

      @Nullable
      public Component getMessage() {
         return this.message;
      }
   }
}
