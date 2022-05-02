package net.minecraft.world.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractSkeleton extends Monster implements RangedAttackMob {
   private final RangedBowAttackGoal bowGoal = new RangedBowAttackGoal(this, 1.0D, 20, 15.0F);
   private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, var5) {
      public void stop() {
         super.stop();
         AbstractSkeleton.this.setAggressive(false);
      }

      public void start() {
         super.start();
         AbstractSkeleton.this.setAggressive(true);
      }
   };

   protected AbstractSkeleton(EntityType entityType, Level level) {
      super(entityType, level);
      this.reassessWeaponGoal();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(2, new RestrictSunGoal(this));
      this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Wolf.class, 6.0F, 1.0D, 1.2D));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   abstract SoundEvent getStepSound();

   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   public void aiStep() {
      boolean var1 = this.isSunBurnTick();
      if(var1) {
         ItemStack var2 = this.getItemBySlot(EquipmentSlot.HEAD);
         if(!var2.isEmpty()) {
            if(var2.isDamageableItem()) {
               var2.setDamageValue(var2.getDamageValue() + this.random.nextInt(2));
               if(var2.getDamageValue() >= var2.getMaxDamage()) {
                  this.broadcastBreakEvent(EquipmentSlot.HEAD);
                  this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
               }
            }

            var1 = false;
         }

         if(var1) {
            this.setSecondsOnFire(8);
         }
      }

      super.aiStep();
   }

   public void rideTick() {
      super.rideTick();
      if(this.getVehicle() instanceof PathfinderMob) {
         PathfinderMob var1 = (PathfinderMob)this.getVehicle();
         this.yBodyRot = var1.yBodyRot;
      }

   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
      super.populateDefaultEquipmentSlots(difficultyInstance);
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      var4 = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
      this.populateDefaultEquipmentSlots(difficultyInstance);
      this.populateDefaultEquipmentEnchantments(difficultyInstance);
      this.reassessWeaponGoal();
      this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * difficultyInstance.getSpecialMultiplier());
      if(this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
         LocalDate var6 = LocalDate.now();
         int var7 = var6.get(ChronoField.DAY_OF_MONTH);
         int var8 = var6.get(ChronoField.MONTH_OF_YEAR);
         if(var8 == 10 && var7 == 31 && this.random.nextFloat() < 0.25F) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F?Blocks.JACK_O_LANTERN:Blocks.CARVED_PUMPKIN));
            this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
         }
      }

      return var4;
   }

   public void reassessWeaponGoal() {
      if(this.level != null && !this.level.isClientSide) {
         this.goalSelector.removeGoal(this.meleeGoal);
         this.goalSelector.removeGoal(this.bowGoal);
         ItemStack var1 = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
         if(var1.getItem() == Items.BOW) {
            int var2 = 20;
            if(this.level.getDifficulty() != Difficulty.HARD) {
               var2 = 40;
            }

            this.bowGoal.setMinAttackInterval(var2);
            this.goalSelector.addGoal(4, this.bowGoal);
         } else {
            this.goalSelector.addGoal(4, this.meleeGoal);
         }

      }
   }

   public void performRangedAttack(LivingEntity livingEntity, float var2) {
      ItemStack var3 = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW)));
      AbstractArrow var4 = this.getArrow(var3, var2);
      double var5 = livingEntity.x - this.x;
      double var7 = livingEntity.getBoundingBox().minY + (double)(livingEntity.getBbHeight() / 3.0F) - var4.y;
      double var9 = livingEntity.z - this.z;
      double var11 = (double)Mth.sqrt(var5 * var5 + var9 * var9);
      var4.shoot(var5, var7 + var11 * 0.20000000298023224D, var9, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level.addFreshEntity(var4);
   }

   protected AbstractArrow getArrow(ItemStack itemStack, float var2) {
      return ProjectileUtil.getMobArrow(this, itemStack, var2);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.reassessWeaponGoal();
   }

   public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
      super.setItemSlot(equipmentSlot, itemStack);
      if(!this.level.isClientSide) {
         this.reassessWeaponGoal();
      }

   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 1.74F;
   }

   public double getRidingHeight() {
      return -0.6D;
   }
}
