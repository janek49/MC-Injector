package net.minecraft.world.entity.monster;

import com.google.common.collect.Maps;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class Pillager extends AbstractIllager implements CrossbowAttackMob, RangedAttackMob {
   private static final EntityDataAccessor IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(Pillager.class, EntityDataSerializers.BOOLEAN);
   private final SimpleContainer inventory = new SimpleContainer(5);

   public Pillager(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));
      this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal(this, 1.0D, 8.0F));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Raider.class})).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(IS_CHARGING_CROSSBOW, Boolean.valueOf(false));
   }

   public boolean isChargingCrossbow() {
      return ((Boolean)this.entityData.get(IS_CHARGING_CROSSBOW)).booleanValue();
   }

   public void setChargingCrossbow(boolean chargingCrossbow) {
      this.entityData.set(IS_CHARGING_CROSSBOW, Boolean.valueOf(chargingCrossbow));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      ListTag var2 = new ListTag();

      for(int var3 = 0; var3 < this.inventory.getContainerSize(); ++var3) {
         ItemStack var4 = this.inventory.getItem(var3);
         if(!var4.isEmpty()) {
            var2.add(var4.save(new CompoundTag()));
         }
      }

      compoundTag.put("Inventory", var2);
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      return this.isChargingCrossbow()?AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE:(this.isHolding(Items.CROSSBOW)?AbstractIllager.IllagerArmPose.CROSSBOW_HOLD:(this.isAggressive()?AbstractIllager.IllagerArmPose.ATTACKING:AbstractIllager.IllagerArmPose.CROSSED));
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      ListTag var2 = compoundTag.getList("Inventory", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ItemStack var4 = ItemStack.of(var2.getCompound(var3));
         if(!var4.isEmpty()) {
            this.inventory.addItem(var4);
         }
      }

      this.setCanPickUpLoot(true);
   }

   public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
      Block var3 = levelReader.getBlockState(blockPos.below()).getBlock();
      return var3 != Blocks.GRASS_BLOCK && var3 != Blocks.SAND?0.5F - levelReader.getBrightness(blockPos):10.0F;
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      this.populateDefaultEquipmentSlots(difficultyInstance);
      this.populateDefaultEquipmentEnchantments(difficultyInstance);
      return super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
      ItemStack var2 = new ItemStack(Items.CROSSBOW);
      if(this.random.nextInt(300) == 0) {
         Map<Enchantment, Integer> var3 = Maps.newHashMap();
         var3.put(Enchantments.PIERCING, Integer.valueOf(1));
         EnchantmentHelper.setEnchantments(var3, var2);
      }

      this.setItemSlot(EquipmentSlot.MAINHAND, var2);
   }

   public boolean isAlliedTo(Entity entity) {
      return super.isAlliedTo(entity)?true:(entity instanceof LivingEntity && ((LivingEntity)entity).getMobType() == MobType.ILLAGER?this.getTeam() == null && entity.getTeam() == null:false);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PILLAGER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PILLAGER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.PILLAGER_HURT;
   }

   public void performRangedAttack(LivingEntity livingEntity, float var2) {
      InteractionHand var3 = ProjectileUtil.getWeaponHoldingHand(this, Items.CROSSBOW);
      ItemStack var4 = this.getItemInHand(var3);
      if(this.isHolding(Items.CROSSBOW)) {
         CrossbowItem.performShooting(this.level, this, var3, var4, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      }

      this.noActionTime = 0;
   }

   public void shootProjectile(LivingEntity livingEntity, ItemStack itemStack, Projectile projectile, float var4) {
      Entity var5 = (Entity)projectile;
      double var6 = livingEntity.x - this.x;
      double var8 = livingEntity.z - this.z;
      double var10 = (double)Mth.sqrt(var6 * var6 + var8 * var8);
      double var12 = livingEntity.getBoundingBox().minY + (double)(livingEntity.getBbHeight() / 3.0F) - var5.y + var10 * 0.20000000298023224D;
      Vector3f var14 = this.getProjectileShotVector(new Vec3(var6, var12, var8), var4);
      projectile.shoot((double)var14.x(), (double)var14.y(), (double)var14.z(), 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   private Vector3f getProjectileShotVector(Vec3 vec3, float var2) {
      Vec3 vec3 = vec3.normalize();
      Vec3 var4 = vec3.cross(new Vec3(0.0D, 1.0D, 0.0D));
      if(var4.lengthSqr() <= 1.0E-7D) {
         var4 = vec3.cross(this.getUpVector(1.0F));
      }

      Quaternion var5 = new Quaternion(new Vector3f(var4), 90.0F, true);
      Vector3f var6 = new Vector3f(vec3);
      var6.transform(var5);
      Quaternion var7 = new Quaternion(var6, var2, true);
      Vector3f var8 = new Vector3f(vec3);
      var8.transform(var7);
      return var8;
   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   protected void pickUpItem(ItemEntity itemEntity) {
      ItemStack var2 = itemEntity.getItem();
      if(var2.getItem() instanceof BannerItem) {
         super.pickUpItem(itemEntity);
      } else {
         Item var3 = var2.getItem();
         if(this.wantsItem(var3)) {
            ItemStack var4 = this.inventory.addItem(var2);
            if(var4.isEmpty()) {
               itemEntity.remove();
            } else {
               var2.setCount(var4.getCount());
            }
         }
      }

   }

   private boolean wantsItem(Item item) {
      return this.hasActiveRaid() && item == Items.WHITE_BANNER;
   }

   public boolean setSlot(int var1, ItemStack itemStack) {
      if(super.setSlot(var1, itemStack)) {
         return true;
      } else {
         int var3 = var1 - 300;
         if(var3 >= 0 && var3 < this.inventory.getContainerSize()) {
            this.inventory.setItem(var3, itemStack);
            return true;
         } else {
            return false;
         }
      }
   }

   public void applyRaidBuffs(int var1, boolean var2) {
      Raid var3 = this.getCurrentRaid();
      boolean var4 = this.random.nextFloat() <= var3.getEnchantOdds();
      if(var4) {
         ItemStack var5 = new ItemStack(Items.CROSSBOW);
         Map<Enchantment, Integer> var6 = Maps.newHashMap();
         if(var1 > var3.getNumGroups(Difficulty.NORMAL)) {
            var6.put(Enchantments.QUICK_CHARGE, Integer.valueOf(2));
         } else if(var1 > var3.getNumGroups(Difficulty.EASY)) {
            var6.put(Enchantments.QUICK_CHARGE, Integer.valueOf(1));
         }

         var6.put(Enchantments.MULTISHOT, Integer.valueOf(1));
         EnchantmentHelper.setEnchantments(var6, var5);
         this.setItemSlot(EquipmentSlot.MAINHAND, var5);
      }

   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() && this.getInventory().isEmpty();
   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.PILLAGER_CELEBRATE;
   }

   public boolean removeWhenFarAway(double d) {
      return super.removeWhenFarAway(d) && this.getInventory().isEmpty();
   }
}
