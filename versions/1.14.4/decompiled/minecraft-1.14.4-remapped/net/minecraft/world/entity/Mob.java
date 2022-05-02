package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensing;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootContext;

public abstract class Mob extends LivingEntity {
   private static final EntityDataAccessor DATA_MOB_FLAGS_ID = SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);
   public int ambientSoundTime;
   protected int xpReward;
   protected LookControl lookControl;
   protected MoveControl moveControl;
   protected JumpControl jumpControl;
   private final BodyRotationControl bodyRotationControl;
   protected PathNavigation navigation;
   protected final GoalSelector goalSelector;
   protected final GoalSelector targetSelector;
   private LivingEntity target;
   private final Sensing sensing;
   private final NonNullList handItems = NonNullList.withSize(2, ItemStack.EMPTY);
   protected final float[] handDropChances = new float[2];
   private final NonNullList armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
   protected final float[] armorDropChances = new float[4];
   private boolean canPickUpLoot;
   private boolean persistenceRequired;
   private final Map pathfindingMalus = Maps.newEnumMap(BlockPathTypes.class);
   private ResourceLocation lootTable;
   private long lootTableSeed;
   @Nullable
   private Entity leashHolder;
   private int delayedLeashHolderId;
   @Nullable
   private CompoundTag leashInfoTag;
   private BlockPos restrictCenter = BlockPos.ZERO;
   private float restrictRadius = -1.0F;

   protected Mob(EntityType entityType, Level level) {
      super(entityType, level);
      this.goalSelector = new GoalSelector(level != null && level.getProfiler() != null?level.getProfiler():null);
      this.targetSelector = new GoalSelector(level != null && level.getProfiler() != null?level.getProfiler():null);
      this.lookControl = new LookControl(this);
      this.moveControl = new MoveControl(this);
      this.jumpControl = new JumpControl(this);
      this.bodyRotationControl = this.createBodyControl();
      this.navigation = this.createNavigation(level);
      this.sensing = new Sensing(this);
      Arrays.fill(this.armorDropChances, 0.085F);
      Arrays.fill(this.handDropChances, 0.085F);
      if(level != null && !level.isClientSide) {
         this.registerGoals();
      }

   }

   protected void registerGoals() {
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK);
   }

   protected PathNavigation createNavigation(Level level) {
      return new GroundPathNavigation(this, level);
   }

   public float getPathfindingMalus(BlockPathTypes blockPathTypes) {
      Float var2 = (Float)this.pathfindingMalus.get(blockPathTypes);
      return var2 == null?blockPathTypes.getMalus():var2.floatValue();
   }

   public void setPathfindingMalus(BlockPathTypes blockPathTypes, float var2) {
      this.pathfindingMalus.put(blockPathTypes, Float.valueOf(var2));
   }

   protected BodyRotationControl createBodyControl() {
      return new BodyRotationControl(this);
   }

   public LookControl getLookControl() {
      return this.lookControl;
   }

   public MoveControl getMoveControl() {
      if(this.isPassenger() && this.getVehicle() instanceof Mob) {
         Mob var1 = (Mob)this.getVehicle();
         return var1.getMoveControl();
      } else {
         return this.moveControl;
      }
   }

   public JumpControl getJumpControl() {
      return this.jumpControl;
   }

   public PathNavigation getNavigation() {
      if(this.isPassenger() && this.getVehicle() instanceof Mob) {
         Mob var1 = (Mob)this.getVehicle();
         return var1.getNavigation();
      } else {
         return this.navigation;
      }
   }

   public Sensing getSensing() {
      return this.sensing;
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.target;
   }

   public void setTarget(@Nullable LivingEntity target) {
      this.target = target;
   }

   public boolean canAttackType(EntityType entityType) {
      return entityType != EntityType.GHAST;
   }

   public void ate() {
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_MOB_FLAGS_ID, Byte.valueOf((byte)0));
   }

   public int getAmbientSoundInterval() {
      return 80;
   }

   public void playAmbientSound() {
      SoundEvent var1 = this.getAmbientSound();
      if(var1 != null) {
         this.playSound(var1, this.getSoundVolume(), this.getVoicePitch());
      }

   }

   public void baseTick() {
      super.baseTick();
      this.level.getProfiler().push("mobBaseTick");
      if(this.isAlive() && this.random.nextInt(1000) < this.ambientSoundTime++) {
         this.resetAmbientSoundTime();
         this.playAmbientSound();
      }

      this.level.getProfiler().pop();
   }

   protected void playHurtSound(DamageSource damageSource) {
      this.resetAmbientSoundTime();
      super.playHurtSound(damageSource);
   }

   private void resetAmbientSoundTime() {
      this.ambientSoundTime = -this.getAmbientSoundInterval();
   }

   protected int getExperienceReward(Player player) {
      if(this.xpReward > 0) {
         int var2 = this.xpReward;

         for(int var3 = 0; var3 < this.armorItems.size(); ++var3) {
            if(!((ItemStack)this.armorItems.get(var3)).isEmpty() && this.armorDropChances[var3] <= 1.0F) {
               var2 += 1 + this.random.nextInt(3);
            }
         }

         for(int var3 = 0; var3 < this.handItems.size(); ++var3) {
            if(!((ItemStack)this.handItems.get(var3)).isEmpty() && this.handDropChances[var3] <= 1.0F) {
               var2 += 1 + this.random.nextInt(3);
            }
         }

         return var2;
      } else {
         return this.xpReward;
      }
   }

   public void spawnAnim() {
      if(this.level.isClientSide) {
         for(int var1 = 0; var1 < 20; ++var1) {
            double var2 = this.random.nextGaussian() * 0.02D;
            double var4 = this.random.nextGaussian() * 0.02D;
            double var6 = this.random.nextGaussian() * 0.02D;
            double var8 = 10.0D;
            this.level.addParticle(ParticleTypes.POOF, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth() - var2 * 10.0D, this.y + (double)(this.random.nextFloat() * this.getBbHeight()) - var4 * 10.0D, this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth() - var6 * 10.0D, var2, var4, var6);
         }
      } else {
         this.level.broadcastEntityEvent(this, (byte)20);
      }

   }

   public void handleEntityEvent(byte b) {
      if(b == 20) {
         this.spawnAnim();
      } else {
         super.handleEntityEvent(b);
      }

   }

   public void tick() {
      super.tick();
      if(!this.level.isClientSide) {
         this.tickLeash();
         if(this.tickCount % 5 == 0) {
            this.updateControlFlags();
         }
      }

   }

   protected void updateControlFlags() {
      boolean var1 = !(this.getControllingPassenger() instanceof Mob);
      boolean var2 = !(this.getVehicle() instanceof Boat);
      this.goalSelector.setControlFlag(Goal.Flag.MOVE, var1);
      this.goalSelector.setControlFlag(Goal.Flag.JUMP, var1 && var2);
      this.goalSelector.setControlFlag(Goal.Flag.LOOK, var1);
   }

   protected float tickHeadTurn(float var1, float var2) {
      this.bodyRotationControl.clientTick();
      return var2;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putBoolean("CanPickUpLoot", this.canPickUpLoot());
      compoundTag.putBoolean("PersistenceRequired", this.persistenceRequired);
      ListTag var2 = new ListTag();

      for(ItemStack var4 : this.armorItems) {
         CompoundTag var5 = new CompoundTag();
         if(!var4.isEmpty()) {
            var4.save(var5);
         }

         var2.add(var5);
      }

      compoundTag.put("ArmorItems", var2);
      ListTag var3 = new ListTag();

      for(ItemStack var5 : this.handItems) {
         CompoundTag var6 = new CompoundTag();
         if(!var5.isEmpty()) {
            var5.save(var6);
         }

         var3.add(var6);
      }

      compoundTag.put("HandItems", var3);
      ListTag var4 = new ListTag();

      for(float var8 : this.armorDropChances) {
         var4.add(new FloatTag(var8));
      }

      compoundTag.put("ArmorDropChances", var4);
      ListTag var5 = new ListTag();

      for(float var9 : this.handDropChances) {
         var5.add(new FloatTag(var9));
      }

      compoundTag.put("HandDropChances", var5);
      if(this.leashHolder != null) {
         CompoundTag var6 = new CompoundTag();
         if(this.leashHolder instanceof LivingEntity) {
            UUID var7 = this.leashHolder.getUUID();
            var6.putUUID("UUID", var7);
         } else if(this.leashHolder instanceof HangingEntity) {
            BlockPos var7 = ((HangingEntity)this.leashHolder).getPos();
            var6.putInt("X", var7.getX());
            var6.putInt("Y", var7.getY());
            var6.putInt("Z", var7.getZ());
         }

         compoundTag.put("Leash", var6);
      }

      compoundTag.putBoolean("LeftHanded", this.isLeftHanded());
      if(this.lootTable != null) {
         compoundTag.putString("DeathLootTable", this.lootTable.toString());
         if(this.lootTableSeed != 0L) {
            compoundTag.putLong("DeathLootTableSeed", this.lootTableSeed);
         }
      }

      if(this.isNoAi()) {
         compoundTag.putBoolean("NoAI", this.isNoAi());
      }

   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("CanPickUpLoot", 1)) {
         this.setCanPickUpLoot(compoundTag.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = compoundTag.getBoolean("PersistenceRequired");
      if(compoundTag.contains("ArmorItems", 9)) {
         ListTag var2 = compoundTag.getList("ArmorItems", 10);

         for(int var3 = 0; var3 < this.armorItems.size(); ++var3) {
            this.armorItems.set(var3, ItemStack.of(var2.getCompound(var3)));
         }
      }

      if(compoundTag.contains("HandItems", 9)) {
         ListTag var2 = compoundTag.getList("HandItems", 10);

         for(int var3 = 0; var3 < this.handItems.size(); ++var3) {
            this.handItems.set(var3, ItemStack.of(var2.getCompound(var3)));
         }
      }

      if(compoundTag.contains("ArmorDropChances", 9)) {
         ListTag var2 = compoundTag.getList("ArmorDropChances", 5);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            this.armorDropChances[var3] = var2.getFloat(var3);
         }
      }

      if(compoundTag.contains("HandDropChances", 9)) {
         ListTag var2 = compoundTag.getList("HandDropChances", 5);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            this.handDropChances[var3] = var2.getFloat(var3);
         }
      }

      if(compoundTag.contains("Leash", 10)) {
         this.leashInfoTag = compoundTag.getCompound("Leash");
      }

      this.setLeftHanded(compoundTag.getBoolean("LeftHanded"));
      if(compoundTag.contains("DeathLootTable", 8)) {
         this.lootTable = new ResourceLocation(compoundTag.getString("DeathLootTable"));
         this.lootTableSeed = compoundTag.getLong("DeathLootTableSeed");
      }

      this.setNoAi(compoundTag.getBoolean("NoAI"));
   }

   protected void dropFromLootTable(DamageSource damageSource, boolean var2) {
      super.dropFromLootTable(damageSource, var2);
      this.lootTable = null;
   }

   protected LootContext.Builder createLootContext(boolean var1, DamageSource damageSource) {
      return super.createLootContext(var1, damageSource).withOptionalRandomSeed(this.lootTableSeed, this.random);
   }

   public final ResourceLocation getLootTable() {
      return this.lootTable == null?this.getDefaultLootTable():this.lootTable;
   }

   protected ResourceLocation getDefaultLootTable() {
      return super.getLootTable();
   }

   public void setZza(float zza) {
      this.zza = zza;
   }

   public void setYya(float yya) {
      this.yya = yya;
   }

   public void setXxa(float xxa) {
      this.xxa = xxa;
   }

   public void setSpeed(float speed) {
      super.setSpeed(speed);
      this.setZza(speed);
   }

   public void aiStep() {
      super.aiStep();
      this.level.getProfiler().push("looting");
      if(!this.level.isClientSide && this.canPickUpLoot() && this.isAlive() && !this.dead && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         for(ItemEntity var3 : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1.0D, 0.0D, 1.0D))) {
            if(!var3.removed && !var3.getItem().isEmpty() && !var3.hasPickUpDelay()) {
               this.pickUpItem(var3);
            }
         }
      }

      this.level.getProfiler().pop();
   }

   protected void pickUpItem(ItemEntity itemEntity) {
      ItemStack var2 = itemEntity.getItem();
      EquipmentSlot var3 = getEquipmentSlotForItem(var2);
      ItemStack var4 = this.getItemBySlot(var3);
      boolean var5 = this.canReplaceCurrentItem(var2, var4, var3);
      if(var5 && this.canHoldItem(var2)) {
         double var6 = (double)this.getEquipmentDropChance(var3);
         if(!var4.isEmpty() && (double)(this.random.nextFloat() - 0.1F) < var6) {
            this.spawnAtLocation(var4);
         }

         this.setItemSlot(var3, var2);
         switch(var3.getType()) {
         case HAND:
            this.handDropChances[var3.getIndex()] = 2.0F;
            break;
         case ARMOR:
            this.armorDropChances[var3.getIndex()] = 2.0F;
         }

         this.persistenceRequired = true;
         this.take(itemEntity, var2.getCount());
         itemEntity.remove();
      }

   }

   protected boolean canReplaceCurrentItem(ItemStack var1, ItemStack var2, EquipmentSlot equipmentSlot) {
      boolean var4 = true;
      if(!var2.isEmpty()) {
         if(equipmentSlot.getType() == EquipmentSlot.Type.HAND) {
            if(var1.getItem() instanceof SwordItem && !(var2.getItem() instanceof SwordItem)) {
               var4 = true;
            } else if(var1.getItem() instanceof SwordItem && var2.getItem() instanceof SwordItem) {
               SwordItem var5 = (SwordItem)var1.getItem();
               SwordItem var6 = (SwordItem)var2.getItem();
               if(var5.getDamage() == var6.getDamage()) {
                  var4 = var1.getDamageValue() < var2.getDamageValue() || var1.hasTag() && !var2.hasTag();
               } else {
                  var4 = var5.getDamage() > var6.getDamage();
               }
            } else if(var1.getItem() instanceof BowItem && var2.getItem() instanceof BowItem) {
               var4 = var1.hasTag() && !var2.hasTag();
            } else {
               var4 = false;
            }
         } else if(var1.getItem() instanceof ArmorItem && !(var2.getItem() instanceof ArmorItem)) {
            var4 = true;
         } else if(var1.getItem() instanceof ArmorItem && var2.getItem() instanceof ArmorItem && !EnchantmentHelper.hasBindingCurse(var2)) {
            ArmorItem var5 = (ArmorItem)var1.getItem();
            ArmorItem var6 = (ArmorItem)var2.getItem();
            if(var5.getDefense() == var6.getDefense()) {
               var4 = var1.getDamageValue() < var2.getDamageValue() || var1.hasTag() && !var2.hasTag();
            } else {
               var4 = var5.getDefense() > var6.getDefense();
            }
         } else {
            var4 = false;
         }
      }

      return var4;
   }

   protected boolean canHoldItem(ItemStack itemStack) {
      return true;
   }

   public boolean removeWhenFarAway(double d) {
      return true;
   }

   public boolean requiresCustomPersistence() {
      return false;
   }

   protected void checkDespawn() {
      if(!this.isPersistenceRequired() && !this.requiresCustomPersistence()) {
         Entity var1 = this.level.getNearestPlayer(this, -1.0D);
         if(var1 != null) {
            double var2 = var1.distanceToSqr((Entity)this);
            if(var2 > 16384.0D && this.removeWhenFarAway(var2)) {
               this.remove();
            }

            if(this.noActionTime > 600 && this.random.nextInt(800) == 0 && var2 > 1024.0D && this.removeWhenFarAway(var2)) {
               this.remove();
            } else if(var2 < 1024.0D) {
               this.noActionTime = 0;
            }
         }

      } else {
         this.noActionTime = 0;
      }
   }

   protected final void serverAiStep() {
      ++this.noActionTime;
      this.level.getProfiler().push("checkDespawn");
      this.checkDespawn();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("sensing");
      this.sensing.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("targetSelector");
      this.targetSelector.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("goalSelector");
      this.goalSelector.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("navigation");
      this.navigation.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("mob tick");
      this.customServerAiStep();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("controls");
      this.level.getProfiler().push("move");
      this.moveControl.tick();
      this.level.getProfiler().popPush("look");
      this.lookControl.tick();
      this.level.getProfiler().popPush("jump");
      this.jumpControl.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().pop();
      this.sendDebugPackets();
   }

   protected void sendDebugPackets() {
      DebugPackets.sendGoalSelector(this.level, this, this.goalSelector);
   }

   protected void customServerAiStep() {
   }

   public int getMaxHeadXRot() {
      return 40;
   }

   public int getMaxHeadYRot() {
      return 75;
   }

   public int getHeadRotSpeed() {
      return 10;
   }

   public void lookAt(Entity entity, float var2, float var3) {
      double var4 = entity.x - this.x;
      double var8 = entity.z - this.z;
      double var6;
      if(entity instanceof LivingEntity) {
         LivingEntity var10 = (LivingEntity)entity;
         var6 = var10.y + (double)var10.getEyeHeight() - (this.y + (double)this.getEyeHeight());
      } else {
         var6 = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D - (this.y + (double)this.getEyeHeight());
      }

      double var10 = (double)Mth.sqrt(var4 * var4 + var8 * var8);
      float var12 = (float)(Mth.atan2(var8, var4) * 57.2957763671875D) - 90.0F;
      float var13 = (float)(-(Mth.atan2(var6, var10) * 57.2957763671875D));
      this.xRot = this.rotlerp(this.xRot, var13, var3);
      this.yRot = this.rotlerp(this.yRot, var12, var2);
   }

   private float rotlerp(float var1, float var2, float var3) {
      float var4 = Mth.wrapDegrees(var2 - var1);
      if(var4 > var3) {
         var4 = var3;
      }

      if(var4 < -var3) {
         var4 = -var3;
      }

      return var1 + var4;
   }

   public static boolean checkMobSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      BlockPos blockPos = blockPos.below();
      return mobSpawnType == MobSpawnType.SPAWNER || levelAccessor.getBlockState(blockPos).isValidSpawn(levelAccessor, blockPos, entityType);
   }

   public boolean checkSpawnRules(LevelAccessor levelAccessor, MobSpawnType mobSpawnType) {
      return true;
   }

   public boolean checkSpawnObstruction(LevelReader levelReader) {
      return !levelReader.containsAnyLiquid(this.getBoundingBox()) && levelReader.isUnobstructed(this);
   }

   public int getMaxSpawnClusterSize() {
      return 4;
   }

   public boolean isMaxGroupSizeReached(int i) {
      return false;
   }

   public int getMaxFallDistance() {
      if(this.getTarget() == null) {
         return 3;
      } else {
         int var1 = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
         var1 = var1 - (3 - this.level.getDifficulty().getId()) * 4;
         if(var1 < 0) {
            var1 = 0;
         }

         return var1 + 3;
      }
   }

   public Iterable getHandSlots() {
      return this.handItems;
   }

   public Iterable getArmorSlots() {
      return this.armorItems;
   }

   public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
      switch(equipmentSlot.getType()) {
      case HAND:
         return (ItemStack)this.handItems.get(equipmentSlot.getIndex());
      case ARMOR:
         return (ItemStack)this.armorItems.get(equipmentSlot.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
      switch(equipmentSlot.getType()) {
      case HAND:
         this.handItems.set(equipmentSlot.getIndex(), itemStack);
         break;
      case ARMOR:
         this.armorItems.set(equipmentSlot.getIndex(), itemStack);
      }

   }

   protected void dropCustomDeathLoot(DamageSource damageSource, int var2, boolean var3) {
      super.dropCustomDeathLoot(damageSource, var2, var3);

      for(EquipmentSlot var7 : EquipmentSlot.values()) {
         ItemStack var8 = this.getItemBySlot(var7);
         float var9 = this.getEquipmentDropChance(var7);
         boolean var10 = var9 > 1.0F;
         if(!var8.isEmpty() && !EnchantmentHelper.hasVanishingCurse(var8) && (var3 || var10) && this.random.nextFloat() - (float)var2 * 0.01F < var9) {
            if(!var10 && var8.isDamageableItem()) {
               var8.setDamageValue(var8.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(var8.getMaxDamage() - 3, 1))));
            }

            this.spawnAtLocation(var8);
         }
      }

   }

   protected float getEquipmentDropChance(EquipmentSlot equipmentSlot) {
      float var2;
      switch(equipmentSlot.getType()) {
      case HAND:
         var2 = this.handDropChances[equipmentSlot.getIndex()];
         break;
      case ARMOR:
         var2 = this.armorDropChances[equipmentSlot.getIndex()];
         break;
      default:
         var2 = 0.0F;
      }

      return var2;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
      if(this.random.nextFloat() < 0.15F * difficultyInstance.getSpecialMultiplier()) {
         int var2 = this.random.nextInt(2);
         float var3 = this.level.getDifficulty() == Difficulty.HARD?0.1F:0.25F;
         if(this.random.nextFloat() < 0.095F) {
            ++var2;
         }

         if(this.random.nextFloat() < 0.095F) {
            ++var2;
         }

         if(this.random.nextFloat() < 0.095F) {
            ++var2;
         }

         boolean var4 = true;

         for(EquipmentSlot var8 : EquipmentSlot.values()) {
            if(var8.getType() == EquipmentSlot.Type.ARMOR) {
               ItemStack var9 = this.getItemBySlot(var8);
               if(!var4 && this.random.nextFloat() < var3) {
                  break;
               }

               var4 = false;
               if(var9.isEmpty()) {
                  Item var10 = getEquipmentForSlot(var8, var2);
                  if(var10 != null) {
                     this.setItemSlot(var8, new ItemStack(var10));
                  }
               }
            }
         }
      }

   }

   public static EquipmentSlot getEquipmentSlotForItem(ItemStack itemStack) {
      Item var1 = itemStack.getItem();
      return var1 != Blocks.CARVED_PUMPKIN.asItem() && (!(var1 instanceof BlockItem) || !(((BlockItem)var1).getBlock() instanceof AbstractSkullBlock))?(var1 instanceof ArmorItem?((ArmorItem)var1).getSlot():(var1 == Items.ELYTRA?EquipmentSlot.CHEST:(var1 == Items.SHIELD?EquipmentSlot.OFFHAND:EquipmentSlot.MAINHAND))):EquipmentSlot.HEAD;
   }

   @Nullable
   public static Item getEquipmentForSlot(EquipmentSlot equipmentSlot, int var1) {
      switch(equipmentSlot) {
      case HEAD:
         if(var1 == 0) {
            return Items.LEATHER_HELMET;
         } else if(var1 == 1) {
            return Items.GOLDEN_HELMET;
         } else if(var1 == 2) {
            return Items.CHAINMAIL_HELMET;
         } else if(var1 == 3) {
            return Items.IRON_HELMET;
         } else if(var1 == 4) {
            return Items.DIAMOND_HELMET;
         }
      case CHEST:
         if(var1 == 0) {
            return Items.LEATHER_CHESTPLATE;
         } else if(var1 == 1) {
            return Items.GOLDEN_CHESTPLATE;
         } else if(var1 == 2) {
            return Items.CHAINMAIL_CHESTPLATE;
         } else if(var1 == 3) {
            return Items.IRON_CHESTPLATE;
         } else if(var1 == 4) {
            return Items.DIAMOND_CHESTPLATE;
         }
      case LEGS:
         if(var1 == 0) {
            return Items.LEATHER_LEGGINGS;
         } else if(var1 == 1) {
            return Items.GOLDEN_LEGGINGS;
         } else if(var1 == 2) {
            return Items.CHAINMAIL_LEGGINGS;
         } else if(var1 == 3) {
            return Items.IRON_LEGGINGS;
         } else if(var1 == 4) {
            return Items.DIAMOND_LEGGINGS;
         }
      case FEET:
         if(var1 == 0) {
            return Items.LEATHER_BOOTS;
         } else if(var1 == 1) {
            return Items.GOLDEN_BOOTS;
         } else if(var1 == 2) {
            return Items.CHAINMAIL_BOOTS;
         } else if(var1 == 3) {
            return Items.IRON_BOOTS;
         } else if(var1 == 4) {
            return Items.DIAMOND_BOOTS;
         }
      default:
         return null;
      }
   }

   protected void populateDefaultEquipmentEnchantments(DifficultyInstance difficultyInstance) {
      float var2 = difficultyInstance.getSpecialMultiplier();
      if(!this.getMainHandItem().isEmpty() && this.random.nextFloat() < 0.25F * var2) {
         this.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(this.random, this.getMainHandItem(), (int)(5.0F + var2 * (float)this.random.nextInt(18)), false));
      }

      for(EquipmentSlot var6 : EquipmentSlot.values()) {
         if(var6.getType() == EquipmentSlot.Type.ARMOR) {
            ItemStack var7 = this.getItemBySlot(var6);
            if(!var7.isEmpty() && this.random.nextFloat() < 0.5F * var2) {
               this.setItemSlot(var6, EnchantmentHelper.enchantItem(this.random, var7, (int)(5.0F + var2 * (float)this.random.nextInt(18)), false));
            }
         }
      }

   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).addModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));
      if(this.random.nextFloat() < 0.05F) {
         this.setLeftHanded(true);
      } else {
         this.setLeftHanded(false);
      }

      return var4;
   }

   public boolean canBeControlledByRider() {
      return false;
   }

   public void setPersistenceRequired() {
      this.persistenceRequired = true;
   }

   public void setDropChance(EquipmentSlot equipmentSlot, float var2) {
      switch(equipmentSlot.getType()) {
      case HAND:
         this.handDropChances[equipmentSlot.getIndex()] = var2;
         break;
      case ARMOR:
         this.armorDropChances[equipmentSlot.getIndex()] = var2;
      }

   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean canPickUpLoot) {
      this.canPickUpLoot = canPickUpLoot;
   }

   public boolean canTakeItem(ItemStack itemStack) {
      EquipmentSlot var2 = getEquipmentSlotForItem(itemStack);
      return this.getItemBySlot(var2).isEmpty() && this.canPickUpLoot();
   }

   public boolean isPersistenceRequired() {
      return this.persistenceRequired;
   }

   public final boolean interact(Player player, InteractionHand interactionHand) {
      if(!this.isAlive()) {
         return false;
      } else if(this.getLeashHolder() == player) {
         this.dropLeash(true, !player.abilities.instabuild);
         return true;
      } else {
         ItemStack var3 = player.getItemInHand(interactionHand);
         if(var3.getItem() == Items.LEAD && this.canBeLeashed(player)) {
            this.setLeashedTo(player, true);
            var3.shrink(1);
            return true;
         } else {
            return this.mobInteract(player, interactionHand)?true:super.interact(player, interactionHand);
         }
      }
   }

   protected boolean mobInteract(Player player, InteractionHand interactionHand) {
      return false;
   }

   public boolean isWithinRestriction() {
      return this.isWithinRestriction(new BlockPos(this));
   }

   public boolean isWithinRestriction(BlockPos blockPos) {
      return this.restrictRadius == -1.0F?true:this.restrictCenter.distSqr(blockPos) < (double)(this.restrictRadius * this.restrictRadius);
   }

   public void restrictTo(BlockPos restrictCenter, int var2) {
      this.restrictCenter = restrictCenter;
      this.restrictRadius = (float)var2;
   }

   public BlockPos getRestrictCenter() {
      return this.restrictCenter;
   }

   public float getRestrictRadius() {
      return this.restrictRadius;
   }

   public boolean hasRestriction() {
      return this.restrictRadius != -1.0F;
   }

   protected void tickLeash() {
      if(this.leashInfoTag != null) {
         this.restoreLeashFromSave();
      }

      if(this.leashHolder != null) {
         if(!this.isAlive() || !this.leashHolder.isAlive()) {
            this.dropLeash(true, true);
         }

      }
   }

   public void dropLeash(boolean var1, boolean var2) {
      if(this.leashHolder != null) {
         this.forcedLoading = false;
         if(!(this.leashHolder instanceof Player)) {
            this.leashHolder.forcedLoading = false;
         }

         this.leashHolder = null;
         if(!this.level.isClientSide && var2) {
            this.spawnAtLocation(Items.LEAD);
         }

         if(!this.level.isClientSide && var1 && this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, (Entity)null));
         }
      }

   }

   public boolean canBeLeashed(Player player) {
      return !this.isLeashed() && !(this instanceof Enemy);
   }

   public boolean isLeashed() {
      return this.leashHolder != null;
   }

   @Nullable
   public Entity getLeashHolder() {
      if(this.leashHolder == null && this.delayedLeashHolderId != 0 && this.level.isClientSide) {
         this.leashHolder = this.level.getEntity(this.delayedLeashHolderId);
      }

      return this.leashHolder;
   }

   public void setLeashedTo(Entity leashHolder, boolean var2) {
      this.leashHolder = leashHolder;
      this.forcedLoading = true;
      if(!(this.leashHolder instanceof Player)) {
         this.leashHolder.forcedLoading = true;
      }

      if(!this.level.isClientSide && var2 && this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, this.leashHolder));
      }

      if(this.isPassenger()) {
         this.stopRiding();
      }

   }

   public void setDelayedLeashHolderId(int delayedLeashHolderId) {
      this.delayedLeashHolderId = delayedLeashHolderId;
      this.dropLeash(false, false);
   }

   public boolean startRiding(Entity entity, boolean var2) {
      boolean var3 = super.startRiding(entity, var2);
      if(var3 && this.isLeashed()) {
         this.dropLeash(true, true);
      }

      return var3;
   }

   private void restoreLeashFromSave() {
      if(this.leashInfoTag != null && this.level instanceof ServerLevel) {
         if(this.leashInfoTag.hasUUID("UUID")) {
            UUID var1 = this.leashInfoTag.getUUID("UUID");
            Entity var2 = ((ServerLevel)this.level).getEntity(var1);
            if(var2 != null) {
               this.setLeashedTo(var2, true);
            }
         } else if(this.leashInfoTag.contains("X", 99) && this.leashInfoTag.contains("Y", 99) && this.leashInfoTag.contains("Z", 99)) {
            BlockPos var1 = new BlockPos(this.leashInfoTag.getInt("X"), this.leashInfoTag.getInt("Y"), this.leashInfoTag.getInt("Z"));
            this.setLeashedTo(LeashFenceKnotEntity.getOrCreateKnot(this.level, var1), true);
         } else {
            this.dropLeash(false, true);
         }

         this.leashInfoTag = null;
      }

   }

   public boolean setSlot(int var1, ItemStack itemStack) {
      EquipmentSlot var3;
      if(var1 == 98) {
         var3 = EquipmentSlot.MAINHAND;
      } else if(var1 == 99) {
         var3 = EquipmentSlot.OFFHAND;
      } else if(var1 == 100 + EquipmentSlot.HEAD.getIndex()) {
         var3 = EquipmentSlot.HEAD;
      } else if(var1 == 100 + EquipmentSlot.CHEST.getIndex()) {
         var3 = EquipmentSlot.CHEST;
      } else if(var1 == 100 + EquipmentSlot.LEGS.getIndex()) {
         var3 = EquipmentSlot.LEGS;
      } else {
         if(var1 != 100 + EquipmentSlot.FEET.getIndex()) {
            return false;
         }

         var3 = EquipmentSlot.FEET;
      }

      if(!itemStack.isEmpty() && !isValidSlotForItem(var3, itemStack) && var3 != EquipmentSlot.HEAD) {
         return false;
      } else {
         this.setItemSlot(var3, itemStack);
         return true;
      }
   }

   public boolean isControlledByLocalInstance() {
      return this.canBeControlledByRider() && super.isControlledByLocalInstance();
   }

   public static boolean isValidSlotForItem(EquipmentSlot equipmentSlot, ItemStack itemStack) {
      EquipmentSlot equipmentSlot = getEquipmentSlotForItem(itemStack);
      return equipmentSlot == equipmentSlot || equipmentSlot == EquipmentSlot.MAINHAND && equipmentSlot == EquipmentSlot.OFFHAND || equipmentSlot == EquipmentSlot.OFFHAND && equipmentSlot == EquipmentSlot.MAINHAND;
   }

   public boolean isEffectiveAi() {
      return super.isEffectiveAi() && !this.isNoAi();
   }

   public void setNoAi(boolean noAi) {
      byte var2 = ((Byte)this.entityData.get(DATA_MOB_FLAGS_ID)).byteValue();
      this.entityData.set(DATA_MOB_FLAGS_ID, Byte.valueOf(noAi?(byte)(var2 | 1):(byte)(var2 & -2)));
   }

   public void setLeftHanded(boolean leftHanded) {
      byte var2 = ((Byte)this.entityData.get(DATA_MOB_FLAGS_ID)).byteValue();
      this.entityData.set(DATA_MOB_FLAGS_ID, Byte.valueOf(leftHanded?(byte)(var2 | 2):(byte)(var2 & -3)));
   }

   public void setAggressive(boolean aggressive) {
      byte var2 = ((Byte)this.entityData.get(DATA_MOB_FLAGS_ID)).byteValue();
      this.entityData.set(DATA_MOB_FLAGS_ID, Byte.valueOf(aggressive?(byte)(var2 | 4):(byte)(var2 & -5)));
   }

   public boolean isNoAi() {
      return (((Byte)this.entityData.get(DATA_MOB_FLAGS_ID)).byteValue() & 1) != 0;
   }

   public boolean isLeftHanded() {
      return (((Byte)this.entityData.get(DATA_MOB_FLAGS_ID)).byteValue() & 2) != 0;
   }

   public boolean isAggressive() {
      return (((Byte)this.entityData.get(DATA_MOB_FLAGS_ID)).byteValue() & 4) != 0;
   }

   public HumanoidArm getMainArm() {
      return this.isLeftHanded()?HumanoidArm.LEFT:HumanoidArm.RIGHT;
   }

   public boolean canAttack(LivingEntity livingEntity) {
      return livingEntity.getType() == EntityType.PLAYER && ((Player)livingEntity).abilities.invulnerable?false:super.canAttack(livingEntity);
   }

   public boolean doHurtTarget(Entity entity) {
      float var2 = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
      float var3 = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).getValue();
      if(entity instanceof LivingEntity) {
         var2 += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)entity).getMobType());
         var3 += (float)EnchantmentHelper.getKnockbackBonus(this);
      }

      int var4 = EnchantmentHelper.getFireAspect(this);
      if(var4 > 0) {
         entity.setSecondsOnFire(var4 * 4);
      }

      boolean var5 = entity.hurt(DamageSource.mobAttack(this), var2);
      if(var5) {
         if(var3 > 0.0F && entity instanceof LivingEntity) {
            ((LivingEntity)entity).knockback(this, var3 * 0.5F, (double)Mth.sin(this.yRot * 0.017453292F), (double)(-Mth.cos(this.yRot * 0.017453292F)));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
         }

         if(entity instanceof Player) {
            Player var6 = (Player)entity;
            ItemStack var7 = this.getMainHandItem();
            ItemStack var8 = var6.isUsingItem()?var6.getUseItem():ItemStack.EMPTY;
            if(!var7.isEmpty() && !var8.isEmpty() && var7.getItem() instanceof AxeItem && var8.getItem() == Items.SHIELD) {
               float var9 = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
               if(this.random.nextFloat() < var9) {
                  var6.getCooldowns().addCooldown(Items.SHIELD, 100);
                  this.level.broadcastEntityEvent(var6, (byte)30);
               }
            }
         }

         this.doEnchantDamageEffects(this, entity);
      }

      return var5;
   }

   protected boolean isSunBurnTick() {
      if(this.level.isDay() && !this.level.isClientSide) {
         float var1 = this.getBrightness();
         BlockPos var2 = this.getVehicle() instanceof Boat?(new BlockPos(this.x, (double)Math.round(this.y), this.z)).above():new BlockPos(this.x, (double)Math.round(this.y), this.z);
         if(var1 > 0.5F && this.random.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F && this.level.canSeeSky(var2)) {
            return true;
         }
      }

      return false;
   }

   protected void jumpInLiquid(Tag tag) {
      if(this.getNavigation().canFloat()) {
         super.jumpInLiquid(tag);
      } else {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.3D, 0.0D));
      }

   }

   public boolean isHolding(Item item) {
      return this.getMainHandItem().getItem() == item || this.getOffhandItem().getItem() == item;
   }
}
