package net.minecraft.world.entity.animal;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.CatLieOnBedGoal;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.SitGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;

public class Cat extends TamableAnimal {
   private static final Ingredient TEMPT_INGREDIENT = Ingredient.of(new ItemLike[]{Items.COD, Items.SALMON});
   private static final EntityDataAccessor DATA_TYPE_ID = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor IS_LYING = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor RELAX_STATE_ONE = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor DATA_COLLAR_COLOR = SynchedEntityData.defineId(Cat.class, EntityDataSerializers.INT);
   public static final Map TEXTURE_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put(Integer.valueOf(0), new ResourceLocation("textures/entity/cat/tabby.png"));
      hashMap.put(Integer.valueOf(1), new ResourceLocation("textures/entity/cat/black.png"));
      hashMap.put(Integer.valueOf(2), new ResourceLocation("textures/entity/cat/red.png"));
      hashMap.put(Integer.valueOf(3), new ResourceLocation("textures/entity/cat/siamese.png"));
      hashMap.put(Integer.valueOf(4), new ResourceLocation("textures/entity/cat/british_shorthair.png"));
      hashMap.put(Integer.valueOf(5), new ResourceLocation("textures/entity/cat/calico.png"));
      hashMap.put(Integer.valueOf(6), new ResourceLocation("textures/entity/cat/persian.png"));
      hashMap.put(Integer.valueOf(7), new ResourceLocation("textures/entity/cat/ragdoll.png"));
      hashMap.put(Integer.valueOf(8), new ResourceLocation("textures/entity/cat/white.png"));
      hashMap.put(Integer.valueOf(9), new ResourceLocation("textures/entity/cat/jellie.png"));
      hashMap.put(Integer.valueOf(10), new ResourceLocation("textures/entity/cat/all_black.png"));
   });
   private Cat.CatAvoidEntityGoal avoidPlayersGoal;
   private TemptGoal temptGoal;
   private float lieDownAmount;
   private float lieDownAmountO;
   private float lieDownAmountTail;
   private float lieDownAmountOTail;
   private float relaxStateOneAmount;
   private float relaxStateOneAmountO;

   public Cat(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public ResourceLocation getResourceLocation() {
      return (ResourceLocation)TEXTURE_BY_TYPE.get(Integer.valueOf(this.getCatType()));
   }

   protected void registerGoals() {
      this.sitGoal = new SitGoal(this);
      this.temptGoal = new Cat.CatTemptGoal(this, 0.6D, TEMPT_INGREDIENT, true);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new Cat.CatRelaxOnOwnerGoal(this));
      this.goalSelector.addGoal(2, this.sitGoal);
      this.goalSelector.addGoal(3, this.temptGoal);
      this.goalSelector.addGoal(5, new CatLieOnBedGoal(this, 1.1D, 8));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F));
      this.goalSelector.addGoal(7, new CatSitOnBlockGoal(this, 0.8D));
      this.goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.3F));
      this.goalSelector.addGoal(9, new OcelotAttackGoal(this));
      this.goalSelector.addGoal(10, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 0.8D, 1.0000001E-5F));
      this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.targetSelector.addGoal(1, new NonTameRandomTargetGoal(this, Rabbit.class, false, (Predicate)null));
      this.targetSelector.addGoal(1, new NonTameRandomTargetGoal(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public int getCatType() {
      return ((Integer)this.entityData.get(DATA_TYPE_ID)).intValue();
   }

   public void setCatType(int catType) {
      if(catType < 0 || catType >= 11) {
         catType = this.random.nextInt(10);
      }

      this.entityData.set(DATA_TYPE_ID, Integer.valueOf(catType));
   }

   public void setLying(boolean lying) {
      this.entityData.set(IS_LYING, Boolean.valueOf(lying));
   }

   public boolean isLying() {
      return ((Boolean)this.entityData.get(IS_LYING)).booleanValue();
   }

   public void setRelaxStateOne(boolean relaxStateOne) {
      this.entityData.set(RELAX_STATE_ONE, Boolean.valueOf(relaxStateOne));
   }

   public boolean isRelaxStateOne() {
      return ((Boolean)this.entityData.get(RELAX_STATE_ONE)).booleanValue();
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId(((Integer)this.entityData.get(DATA_COLLAR_COLOR)).intValue());
   }

   public void setCollarColor(DyeColor collarColor) {
      this.entityData.set(DATA_COLLAR_COLOR, Integer.valueOf(collarColor.getId()));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TYPE_ID, Integer.valueOf(1));
      this.entityData.define(IS_LYING, Boolean.valueOf(false));
      this.entityData.define(RELAX_STATE_ONE, Boolean.valueOf(false));
      this.entityData.define(DATA_COLLAR_COLOR, Integer.valueOf(DyeColor.RED.getId()));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("CatType", this.getCatType());
      compoundTag.putByte("CollarColor", (byte)this.getCollarColor().getId());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setCatType(compoundTag.getInt("CatType"));
      if(compoundTag.contains("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byId(compoundTag.getInt("CollarColor")));
      }

   }

   public void customServerAiStep() {
      if(this.getMoveControl().hasWanted()) {
         double var1 = this.getMoveControl().getSpeedModifier();
         if(var1 == 0.6D) {
            this.setSneaking(true);
            this.setSprinting(false);
         } else if(var1 == 1.33D) {
            this.setSneaking(false);
            this.setSprinting(true);
         } else {
            this.setSneaking(false);
            this.setSprinting(false);
         }
      } else {
         this.setSneaking(false);
         this.setSprinting(false);
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return this.isTame()?(this.isInLove()?SoundEvents.CAT_PURR:(this.random.nextInt(4) == 0?SoundEvents.CAT_PURREOW:SoundEvents.CAT_AMBIENT)):SoundEvents.CAT_STRAY_AMBIENT;
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public void hiss() {
      this.playSound(SoundEvents.CAT_HISS, this.getSoundVolume(), this.getVoicePitch());
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.CAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.CAT_DEATH;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
   }

   public void causeFallDamage(float var1, float var2) {
   }

   protected void usePlayerItem(Player player, ItemStack itemStack) {
      if(this.isFood(itemStack)) {
         this.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
      }

      super.usePlayerItem(player, itemStack);
   }

   public boolean doHurtTarget(Entity entity) {
      return entity.hurt(DamageSource.mobAttack(this), 3.0F);
   }

   public void tick() {
      super.tick();
      if(this.temptGoal != null && this.temptGoal.isRunning() && !this.isTame() && this.tickCount % 100 == 0) {
         this.playSound(SoundEvents.CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
      }

      this.handleLieDown();
   }

   private void handleLieDown() {
      if((this.isLying() || this.isRelaxStateOne()) && this.tickCount % 5 == 0) {
         this.playSound(SoundEvents.CAT_PURR, 0.6F + 0.4F * (this.random.nextFloat() - this.random.nextFloat()), 1.0F);
      }

      this.updateLieDownAmount();
      this.updateRelaxStateOneAmount();
   }

   private void updateLieDownAmount() {
      this.lieDownAmountO = this.lieDownAmount;
      this.lieDownAmountOTail = this.lieDownAmountTail;
      if(this.isLying()) {
         this.lieDownAmount = Math.min(1.0F, this.lieDownAmount + 0.15F);
         this.lieDownAmountTail = Math.min(1.0F, this.lieDownAmountTail + 0.08F);
      } else {
         this.lieDownAmount = Math.max(0.0F, this.lieDownAmount - 0.22F);
         this.lieDownAmountTail = Math.max(0.0F, this.lieDownAmountTail - 0.13F);
      }

   }

   private void updateRelaxStateOneAmount() {
      this.relaxStateOneAmountO = this.relaxStateOneAmount;
      if(this.isRelaxStateOne()) {
         this.relaxStateOneAmount = Math.min(1.0F, this.relaxStateOneAmount + 0.1F);
      } else {
         this.relaxStateOneAmount = Math.max(0.0F, this.relaxStateOneAmount - 0.13F);
      }

   }

   public float getLieDownAmount(float f) {
      return Mth.lerp(f, this.lieDownAmountO, this.lieDownAmount);
   }

   public float getLieDownAmountTail(float f) {
      return Mth.lerp(f, this.lieDownAmountOTail, this.lieDownAmountTail);
   }

   public float getRelaxStateOneAmount(float f) {
      return Mth.lerp(f, this.relaxStateOneAmountO, this.relaxStateOneAmount);
   }

   public Cat getBreedOffspring(AgableMob agableMob) {
      Cat cat = (Cat)EntityType.CAT.create(this.level);
      if(agableMob instanceof Cat) {
         if(this.random.nextBoolean()) {
            cat.setCatType(this.getCatType());
         } else {
            cat.setCatType(((Cat)agableMob).getCatType());
         }

         if(this.isTame()) {
            cat.setOwnerUUID(this.getOwnerUUID());
            cat.setTame(true);
            if(this.random.nextBoolean()) {
               cat.setCollarColor(this.getCollarColor());
            } else {
               cat.setCollarColor(((Cat)agableMob).getCollarColor());
            }
         }
      }

      return cat;
   }

   public boolean canMate(Animal animal) {
      if(!this.isTame()) {
         return false;
      } else if(!(animal instanceof Cat)) {
         return false;
      } else {
         Cat var2 = (Cat)animal;
         return var2.isTame() && super.canMate(animal);
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      var4 = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
      if(levelAccessor.getMoonBrightness() > 0.9F) {
         this.setCatType(this.random.nextInt(11));
      } else {
         this.setCatType(this.random.nextInt(10));
      }

      if(Feature.SWAMP_HUT.isInsideFeature(levelAccessor, new BlockPos(this))) {
         this.setCatType(10);
         this.setPersistenceRequired();
      }

      return var4;
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      Item var4 = var3.getItem();
      if(this.isTame()) {
         if(this.isOwnedBy(player)) {
            if(var4 instanceof DyeItem) {
               DyeColor var5 = ((DyeItem)var4).getDyeColor();
               if(var5 != this.getCollarColor()) {
                  this.setCollarColor(var5);
                  if(!player.abilities.instabuild) {
                     var3.shrink(1);
                  }

                  this.setPersistenceRequired();
                  return true;
               }
            } else if(this.isFood(var3)) {
               if(this.getHealth() < this.getMaxHealth() && var4.isEdible()) {
                  this.usePlayerItem(player, var3);
                  this.heal((float)var4.getFoodProperties().getNutrition());
                  return true;
               }
            } else if(!this.level.isClientSide) {
               this.sitGoal.wantToSit(!this.isSitting());
            }
         }
      } else if(this.isFood(var3)) {
         this.usePlayerItem(player, var3);
         if(!this.level.isClientSide) {
            if(this.random.nextInt(3) == 0) {
               this.tame(player);
               this.spawnTamingParticles(true);
               this.sitGoal.wantToSit(true);
               this.level.broadcastEntityEvent(this, (byte)7);
            } else {
               this.spawnTamingParticles(false);
               this.level.broadcastEntityEvent(this, (byte)6);
            }
         }

         this.setPersistenceRequired();
         return true;
      }

      boolean var5 = super.mobInteract(player, interactionHand);
      if(var5) {
         this.setPersistenceRequired();
      }

      return var5;
   }

   public boolean isFood(ItemStack itemStack) {
      return TEMPT_INGREDIENT.test(itemStack);
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return entityDimensions.height * 0.5F;
   }

   public boolean removeWhenFarAway(double d) {
      return !this.isTame() && this.tickCount > 2400;
   }

   protected void reassessTameGoals() {
      if(this.avoidPlayersGoal == null) {
         this.avoidPlayersGoal = new Cat.CatAvoidEntityGoal(this, Player.class, 16.0F, 0.8D, 1.33D);
      }

      this.goalSelector.removeGoal(this.avoidPlayersGoal);
      if(!this.isTame()) {
         this.goalSelector.addGoal(4, this.avoidPlayersGoal);
      }

   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }

   static class CatAvoidEntityGoal extends AvoidEntityGoal {
      private final Cat cat;

      public CatAvoidEntityGoal(Cat cat, Class class, float var3, double var4, double var6) {
         Predicate var10006 = EntitySelector.NO_CREATIVE_OR_SPECTATOR;
         EntitySelector.NO_CREATIVE_OR_SPECTATOR.getClass();
         super(cat, class, var3, var4, var6, var10006::test);
         this.cat = cat;
      }

      public boolean canUse() {
         return !this.cat.isTame() && super.canUse();
      }

      public boolean canContinueToUse() {
         return !this.cat.isTame() && super.canContinueToUse();
      }
   }

   static class CatRelaxOnOwnerGoal extends Goal {
      private final Cat cat;
      private Player ownerPlayer;
      private BlockPos goalPos;
      private int onBedTicks;

      public CatRelaxOnOwnerGoal(Cat cat) {
         this.cat = cat;
      }

      public boolean canUse() {
         if(!this.cat.isTame()) {
            return false;
         } else if(this.cat.isSitting()) {
            return false;
         } else {
            LivingEntity var1 = this.cat.getOwner();
            if(var1 instanceof Player) {
               this.ownerPlayer = (Player)var1;
               if(!var1.isSleeping()) {
                  return false;
               }

               if(this.cat.distanceToSqr(this.ownerPlayer) > 100.0D) {
                  return false;
               }

               BlockPos var2 = new BlockPos(this.ownerPlayer);
               BlockState var3 = this.cat.level.getBlockState(var2);
               if(var3.getBlock().is(BlockTags.BEDS)) {
                  Direction var4 = (Direction)var3.getValue(BedBlock.FACING);
                  this.goalPos = new BlockPos(var2.getX() - var4.getStepX(), var2.getY(), var2.getZ() - var4.getStepZ());
                  return !this.spaceIsOccupied();
               }
            }

            return false;
         }
      }

      private boolean spaceIsOccupied() {
         for(Cat var3 : this.cat.level.getEntitiesOfClass(Cat.class, (new AABB(this.goalPos)).inflate(2.0D))) {
            if(var3 != this.cat && (var3.isLying() || var3.isRelaxStateOne())) {
               return true;
            }
         }

         return false;
      }

      public boolean canContinueToUse() {
         return this.cat.isTame() && !this.cat.isSitting() && this.ownerPlayer != null && this.ownerPlayer.isSleeping() && this.goalPos != null && !this.spaceIsOccupied();
      }

      public void start() {
         if(this.goalPos != null) {
            this.cat.getSitGoal().wantToSit(false);
            this.cat.getNavigation().moveTo((double)this.goalPos.getX(), (double)this.goalPos.getY(), (double)this.goalPos.getZ(), 1.100000023841858D);
         }

      }

      public void stop() {
         this.cat.setLying(false);
         float var1 = this.cat.level.getTimeOfDay(1.0F);
         if(this.ownerPlayer.getSleepTimer() >= 100 && (double)var1 > 0.77D && (double)var1 < 0.8D && (double)this.cat.level.getRandom().nextFloat() < 0.7D) {
            this.giveMorningGift();
         }

         this.onBedTicks = 0;
         this.cat.setRelaxStateOne(false);
         this.cat.getNavigation().stop();
      }

      private void giveMorningGift() {
         Random var1 = this.cat.getRandom();
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
         var2.set((Entity)this.cat);
         this.cat.randomTeleport((double)(var2.getX() + var1.nextInt(11) - 5), (double)(var2.getY() + var1.nextInt(5) - 2), (double)(var2.getZ() + var1.nextInt(11) - 5), false);
         var2.set((Entity)this.cat);
         LootTable var3 = this.cat.level.getServer().getLootTables().get(BuiltInLootTables.CAT_MORNING_GIFT);
         LootContext.Builder var4 = (new LootContext.Builder((ServerLevel)this.cat.level)).withParameter(LootContextParams.BLOCK_POS, var2).withParameter(LootContextParams.THIS_ENTITY, this.cat).withRandom(var1);

         for(ItemStack var7 : var3.getRandomItems(var4.create(LootContextParamSets.GIFT))) {
            this.cat.level.addFreshEntity(new ItemEntity(this.cat.level, (double)((float)var2.getX() - Mth.sin(this.cat.yBodyRot * 0.017453292F)), (double)var2.getY(), (double)((float)var2.getZ() + Mth.cos(this.cat.yBodyRot * 0.017453292F)), var7));
         }

      }

      public void tick() {
         if(this.ownerPlayer != null && this.goalPos != null) {
            this.cat.getSitGoal().wantToSit(false);
            this.cat.getNavigation().moveTo((double)this.goalPos.getX(), (double)this.goalPos.getY(), (double)this.goalPos.getZ(), 1.100000023841858D);
            if(this.cat.distanceToSqr(this.ownerPlayer) < 2.5D) {
               ++this.onBedTicks;
               if(this.onBedTicks > 16) {
                  this.cat.setLying(true);
                  this.cat.setRelaxStateOne(false);
               } else {
                  this.cat.lookAt(this.ownerPlayer, 45.0F, 45.0F);
                  this.cat.setRelaxStateOne(true);
               }
            } else {
               this.cat.setLying(false);
            }
         }

      }
   }

   static class CatTemptGoal extends TemptGoal {
      @Nullable
      private Player selectedPlayer;
      private final Cat cat;

      public CatTemptGoal(Cat cat, double var2, Ingredient ingredient, boolean var5) {
         super(cat, var2, ingredient, var5);
         this.cat = cat;
      }

      public void tick() {
         super.tick();
         if(this.selectedPlayer == null && this.mob.getRandom().nextInt(600) == 0) {
            this.selectedPlayer = this.player;
         } else if(this.mob.getRandom().nextInt(500) == 0) {
            this.selectedPlayer = null;
         }

      }

      protected boolean canScare() {
         return this.selectedPlayer != null && this.selectedPlayer.equals(this.player)?false:super.canScare();
      }

      public boolean canUse() {
         return super.canUse() && !this.cat.isTame();
      }
   }
}
