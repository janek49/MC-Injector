package net.minecraft.world.entity.animal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.monster.PigZombie;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Pig extends Animal {
   private static final EntityDataAccessor DATA_SADDLE_ID = SynchedEntityData.defineId(Pig.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor DATA_BOOST_TIME = SynchedEntityData.defineId(Pig.class, EntityDataSerializers.INT);
   private static final Ingredient FOOD_ITEMS = Ingredient.of(new ItemLike[]{Items.CARROT, Items.POTATO, Items.BEETROOT});
   private boolean boosting;
   private int boostTime;
   private int boostTimeTotal;

   public Pig(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.of(new ItemLike[]{Items.CARROT_ON_A_STICK}), false));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, false, FOOD_ITEMS));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty()?null:(Entity)this.getPassengers().get(0);
   }

   public boolean canBeControlledByRider() {
      Entity var1 = this.getControllingPassenger();
      if(!(var1 instanceof Player)) {
         return false;
      } else {
         Player var2 = (Player)var1;
         return var2.getMainHandItem().getItem() == Items.CARROT_ON_A_STICK || var2.getOffhandItem().getItem() == Items.CARROT_ON_A_STICK;
      }
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      if(DATA_BOOST_TIME.equals(entityDataAccessor) && this.level.isClientSide) {
         this.boosting = true;
         this.boostTime = 0;
         this.boostTimeTotal = ((Integer)this.entityData.get(DATA_BOOST_TIME)).intValue();
      }

      super.onSyncedDataUpdated(entityDataAccessor);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SADDLE_ID, Boolean.valueOf(false));
      this.entityData.define(DATA_BOOST_TIME, Integer.valueOf(0));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putBoolean("Saddle", this.hasSaddle());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setSaddle(compoundTag.getBoolean("Saddle"));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PIG_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.PIG_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PIG_DEATH;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(SoundEvents.PIG_STEP, 0.15F, 1.0F);
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      if(!super.mobInteract(player, interactionHand)) {
         ItemStack var3 = player.getItemInHand(interactionHand);
         if(var3.getItem() == Items.NAME_TAG) {
            var3.interactEnemy(player, this, interactionHand);
            return true;
         } else if(this.hasSaddle() && !this.isVehicle()) {
            if(!this.level.isClientSide) {
               player.startRiding(this);
            }

            return true;
         } else if(var3.getItem() == Items.SADDLE) {
            var3.interactEnemy(player, this, interactionHand);
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if(this.hasSaddle()) {
         this.spawnAtLocation(Items.SADDLE);
      }

   }

   public boolean hasSaddle() {
      return ((Boolean)this.entityData.get(DATA_SADDLE_ID)).booleanValue();
   }

   public void setSaddle(boolean saddle) {
      if(saddle) {
         this.entityData.set(DATA_SADDLE_ID, Boolean.valueOf(true));
      } else {
         this.entityData.set(DATA_SADDLE_ID, Boolean.valueOf(false));
      }

   }

   public void thunderHit(LightningBolt lightningBolt) {
      PigZombie var2 = (PigZombie)EntityType.ZOMBIE_PIGMAN.create(this.level);
      var2.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
      var2.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
      var2.setNoAi(this.isNoAi());
      if(this.hasCustomName()) {
         var2.setCustomName(this.getCustomName());
         var2.setCustomNameVisible(this.isCustomNameVisible());
      }

      this.level.addFreshEntity(var2);
      this.remove();
   }

   public void travel(Vec3 vec3) {
      if(this.isAlive()) {
         Entity var2 = this.getPassengers().isEmpty()?null:(Entity)this.getPassengers().get(0);
         if(this.isVehicle() && this.canBeControlledByRider()) {
            this.yRot = var2.yRot;
            this.yRotO = this.yRot;
            this.xRot = var2.xRot * 0.5F;
            this.setRot(this.yRot, this.xRot);
            this.yBodyRot = this.yRot;
            this.yHeadRot = this.yRot;
            this.maxUpStep = 1.0F;
            this.flyingSpeed = this.getSpeed() * 0.1F;
            if(this.boosting && this.boostTime++ > this.boostTimeTotal) {
               this.boosting = false;
            }

            if(this.isControlledByLocalInstance()) {
               float var3 = (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue() * 0.225F;
               if(this.boosting) {
                  var3 += var3 * 1.15F * Mth.sin((float)this.boostTime / (float)this.boostTimeTotal * 3.1415927F);
               }

               this.setSpeed(var3);
               super.travel(new Vec3(0.0D, 0.0D, 1.0D));
            } else {
               this.setDeltaMovement(Vec3.ZERO);
            }

            this.animationSpeedOld = this.animationSpeed;
            double var3 = this.x - this.xo;
            double var5 = this.z - this.zo;
            float var7 = Mth.sqrt(var3 * var3 + var5 * var5) * 4.0F;
            if(var7 > 1.0F) {
               var7 = 1.0F;
            }

            this.animationSpeed += (var7 - this.animationSpeed) * 0.4F;
            this.animationPosition += this.animationSpeed;
         } else {
            this.maxUpStep = 0.5F;
            this.flyingSpeed = 0.02F;
            super.travel(vec3);
         }
      }
   }

   public boolean boost() {
      if(this.boosting) {
         return false;
      } else {
         this.boosting = true;
         this.boostTime = 0;
         this.boostTimeTotal = this.getRandom().nextInt(841) + 140;
         this.getEntityData().set(DATA_BOOST_TIME, Integer.valueOf(this.boostTimeTotal));
         return true;
      }
   }

   public Pig getBreedOffspring(AgableMob agableMob) {
      return (Pig)EntityType.PIG.create(this.level);
   }

   public boolean isFood(ItemStack itemStack) {
      return FOOD_ITEMS.test(itemStack);
   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }
}
