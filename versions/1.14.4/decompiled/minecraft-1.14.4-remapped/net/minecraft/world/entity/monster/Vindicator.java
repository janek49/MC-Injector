package net.minecraft.world.entity.monster;

import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class Vindicator extends AbstractIllager {
   private static final Predicate DOOR_BREAKING_PREDICATE = (difficulty) -> {
      return difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD;
   };
   private boolean isJohnny;

   public Vindicator(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new Vindicator.VindicatorBreakDoorGoal(this));
      this.goalSelector.addGoal(2, new AbstractIllager.RaiderOpenDoorGoal(this));
      this.goalSelector.addGoal(3, new Raider.HoldGroundAttackGoal(this, 10.0F));
      this.goalSelector.addGoal(4, new Vindicator.VindicatorMeleeAttackGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Raider.class})).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(4, new Vindicator.VindicatorJohnnyAttackGoal(this));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
   }

   protected void customServerAiStep() {
      if(!this.isNoAi()) {
         if(((ServerLevel)this.level).isRaided(new BlockPos(this))) {
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
         } else {
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(false);
         }
      }

      super.customServerAiStep();
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      if(this.isJohnny) {
         compoundTag.putBoolean("Johnny", true);
      }

   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      return this.isAggressive()?AbstractIllager.IllagerArmPose.ATTACKING:(this.isCelebrating()?AbstractIllager.IllagerArmPose.CELEBRATING:AbstractIllager.IllagerArmPose.CROSSED);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("Johnny", 99)) {
         this.isJohnny = compoundTag.getBoolean("Johnny");
      }

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.VINDICATOR_CELEBRATE;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      SpawnGroupData var6 = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      this.populateDefaultEquipmentSlots(difficultyInstance);
      this.populateDefaultEquipmentEnchantments(difficultyInstance);
      return var6;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
      if(this.getCurrentRaid() == null) {
         this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
      }

   }

   public boolean isAlliedTo(Entity entity) {
      return super.isAlliedTo(entity)?true:(entity instanceof LivingEntity && ((LivingEntity)entity).getMobType() == MobType.ILLAGER?this.getTeam() == null && entity.getTeam() == null:false);
   }

   public void setCustomName(@Nullable Component customName) {
      super.setCustomName(customName);
      if(!this.isJohnny && customName != null && customName.getString().equals("Johnny")) {
         this.isJohnny = true;
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.VINDICATOR_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VINDICATOR_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.VINDICATOR_HURT;
   }

   public void applyRaidBuffs(int var1, boolean var2) {
      ItemStack var3 = new ItemStack(Items.IRON_AXE);
      Raid var4 = this.getCurrentRaid();
      int var5 = 1;
      if(var1 > var4.getNumGroups(Difficulty.NORMAL)) {
         var5 = 2;
      }

      boolean var6 = this.random.nextFloat() <= var4.getEnchantOdds();
      if(var6) {
         Map<Enchantment, Integer> var7 = Maps.newHashMap();
         var7.put(Enchantments.SHARPNESS, Integer.valueOf(var5));
         EnchantmentHelper.setEnchantments(var7, var3);
      }

      this.setItemSlot(EquipmentSlot.MAINHAND, var3);
   }

   static class VindicatorBreakDoorGoal extends BreakDoorGoal {
      public VindicatorBreakDoorGoal(Mob mob) {
         super(mob, 6, Vindicator.DOOR_BREAKING_PREDICATE);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canContinueToUse() {
         Vindicator var1 = (Vindicator)this.mob;
         return var1.hasActiveRaid() && super.canContinueToUse();
      }

      public boolean canUse() {
         Vindicator var1 = (Vindicator)this.mob;
         return var1.hasActiveRaid() && var1.random.nextInt(10) == 0 && super.canUse();
      }

      public void start() {
         super.start();
         this.mob.setNoActionTime(0);
      }
   }

   static class VindicatorJohnnyAttackGoal extends NearestAttackableTargetGoal {
      public VindicatorJohnnyAttackGoal(Vindicator vindicator) {
         super(vindicator, LivingEntity.class, 0, true, true, LivingEntity::attackable);
      }

      public boolean canUse() {
         return ((Vindicator)this.mob).isJohnny && super.canUse();
      }

      public void start() {
         super.start();
         this.mob.setNoActionTime(0);
      }
   }

   class VindicatorMeleeAttackGoal extends MeleeAttackGoal {
      public VindicatorMeleeAttackGoal(Vindicator var2) {
         super(var2, 1.0D, false);
      }

      protected double getAttackReachSqr(LivingEntity livingEntity) {
         if(this.mob.getVehicle() instanceof Ravager) {
            float var2 = this.mob.getVehicle().getBbWidth() - 0.1F;
            return (double)(var2 * 2.0F * var2 * 2.0F + livingEntity.getBbWidth());
         } else {
            return super.getAttackReachSqr(livingEntity);
         }
      }
   }
}
