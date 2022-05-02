package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Ocelot extends Animal {
   private static final Ingredient TEMPT_INGREDIENT = Ingredient.of(new ItemLike[]{Items.COD, Items.SALMON});
   private static final EntityDataAccessor DATA_TRUSTING = SynchedEntityData.defineId(Ocelot.class, EntityDataSerializers.BOOLEAN);
   private Ocelot.OcelotAvoidEntityGoal ocelotAvoidPlayersGoal;
   private Ocelot.OcelotTemptGoal temptGoal;

   public Ocelot(EntityType entityType, Level level) {
      super(entityType, level);
      this.reassessTrustingGoals();
   }

   private boolean isTrusting() {
      return ((Boolean)this.entityData.get(DATA_TRUSTING)).booleanValue();
   }

   private void setTrusting(boolean trusting) {
      this.entityData.set(DATA_TRUSTING, Boolean.valueOf(trusting));
      this.reassessTrustingGoals();
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putBoolean("Trusting", this.isTrusting());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setTrusting(compoundTag.getBoolean("Trusting"));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TRUSTING, Boolean.valueOf(false));
   }

   protected void registerGoals() {
      this.temptGoal = new Ocelot.OcelotTemptGoal(this, 0.6D, TEMPT_INGREDIENT, true);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(3, this.temptGoal);
      this.goalSelector.addGoal(7, new LeapAtTargetGoal(this, 0.3F));
      this.goalSelector.addGoal(8, new OcelotAttackGoal(this));
      this.goalSelector.addGoal(9, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 0.8D, 1.0000001E-5F));
      this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Chicken.class, false));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Turtle.class, 10, false, false, Turtle.BABY_ON_LAND_SELECTOR));
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

   public boolean removeWhenFarAway(double d) {
      return !this.isTrusting() && this.tickCount > 2400;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
   }

   public void causeFallDamage(float var1, float var2) {
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.OCELOT_AMBIENT;
   }

   public int getAmbientSoundInterval() {
      return 900;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.OCELOT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.OCELOT_DEATH;
   }

   public boolean doHurtTarget(Entity entity) {
      return entity.hurt(DamageSource.mobAttack(this), 3.0F);
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      return this.isInvulnerableTo(damageSource)?false:super.hurt(damageSource, var2);
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      if((this.temptGoal == null || this.temptGoal.isRunning()) && !this.isTrusting() && this.isFood(var3) && player.distanceToSqr(this) < 9.0D) {
         this.usePlayerItem(player, var3);
         if(!this.level.isClientSide) {
            if(this.random.nextInt(3) == 0) {
               this.setTrusting(true);
               this.spawnTrustingParticles(true);
               this.level.broadcastEntityEvent(this, (byte)41);
            } else {
               this.spawnTrustingParticles(false);
               this.level.broadcastEntityEvent(this, (byte)40);
            }
         }

         return true;
      } else {
         return super.mobInteract(player, interactionHand);
      }
   }

   public void handleEntityEvent(byte b) {
      if(b == 41) {
         this.spawnTrustingParticles(true);
      } else if(b == 40) {
         this.spawnTrustingParticles(false);
      } else {
         super.handleEntityEvent(b);
      }

   }

   private void spawnTrustingParticles(boolean b) {
      ParticleOptions var2 = ParticleTypes.HEART;
      if(!b) {
         var2 = ParticleTypes.SMOKE;
      }

      for(int var3 = 0; var3 < 7; ++var3) {
         double var4 = this.random.nextGaussian() * 0.02D;
         double var6 = this.random.nextGaussian() * 0.02D;
         double var8 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(var2, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), this.y + 0.5D + (double)(this.random.nextFloat() * this.getBbHeight()), this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), var4, var6, var8);
      }

   }

   protected void reassessTrustingGoals() {
      if(this.ocelotAvoidPlayersGoal == null) {
         this.ocelotAvoidPlayersGoal = new Ocelot.OcelotAvoidEntityGoal(this, Player.class, 16.0F, 0.8D, 1.33D);
      }

      this.goalSelector.removeGoal(this.ocelotAvoidPlayersGoal);
      if(!this.isTrusting()) {
         this.goalSelector.addGoal(4, this.ocelotAvoidPlayersGoal);
      }

   }

   public Ocelot getBreedOffspring(AgableMob agableMob) {
      return (Ocelot)EntityType.OCELOT.create(this.level);
   }

   public boolean isFood(ItemStack itemStack) {
      return TEMPT_INGREDIENT.test(itemStack);
   }

   public static boolean checkOcelotSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return random.nextInt(3) != 0;
   }

   public boolean checkSpawnObstruction(LevelReader levelReader) {
      if(levelReader.isUnobstructed(this) && !levelReader.containsAnyLiquid(this.getBoundingBox())) {
         BlockPos var2 = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
         if(var2.getY() < levelReader.getSeaLevel()) {
            return false;
         }

         BlockState var3 = levelReader.getBlockState(var2.below());
         Block var4 = var3.getBlock();
         if(var4 == Blocks.GRASS_BLOCK || var3.is(BlockTags.LEAVES)) {
            return true;
         }
      }

      return false;
   }

   protected void addKittensDuringSpawn() {
      for(int var1 = 0; var1 < 2; ++var1) {
         Ocelot var2 = (Ocelot)EntityType.OCELOT.create(this.level);
         var2.moveTo(this.x, this.y, this.z, this.yRot, 0.0F);
         var2.setAge(-24000);
         this.level.addFreshEntity(var2);
      }

   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      var4 = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
      if(levelAccessor.getRandom().nextInt(7) == 0) {
         this.addKittensDuringSpawn();
      }

      return var4;
   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }

   static class OcelotAvoidEntityGoal extends AvoidEntityGoal {
      private final Ocelot ocelot;

      public OcelotAvoidEntityGoal(Ocelot ocelot, Class class, float var3, double var4, double var6) {
         Predicate var10006 = EntitySelector.NO_CREATIVE_OR_SPECTATOR;
         EntitySelector.NO_CREATIVE_OR_SPECTATOR.getClass();
         super(ocelot, class, var3, var4, var6, var10006::test);
         this.ocelot = ocelot;
      }

      public boolean canUse() {
         return !this.ocelot.isTrusting() && super.canUse();
      }

      public boolean canContinueToUse() {
         return !this.ocelot.isTrusting() && super.canContinueToUse();
      }
   }

   static class OcelotTemptGoal extends TemptGoal {
      private final Ocelot ocelot;

      public OcelotTemptGoal(Ocelot ocelot, double var2, Ingredient ingredient, boolean var5) {
         super(ocelot, var2, ingredient, var5);
         this.ocelot = ocelot;
      }

      protected boolean canScare() {
         return super.canScare() && !this.ocelot.isTrusting();
      }
   }
}
