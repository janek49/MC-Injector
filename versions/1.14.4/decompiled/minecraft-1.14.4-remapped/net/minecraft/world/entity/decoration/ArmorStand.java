package net.minecraft.world.entity.decoration;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

public class ArmorStand extends LivingEntity {
   private static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0F, 0.0F, -10.0F);
   private static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0F, 0.0F, 10.0F);
   private static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0F, 0.0F, -1.0F);
   private static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0F, 0.0F, 1.0F);
   public static final EntityDataAccessor DATA_CLIENT_FLAGS = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.BYTE);
   public static final EntityDataAccessor DATA_HEAD_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor DATA_BODY_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor DATA_LEFT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor DATA_RIGHT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor DATA_LEFT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor DATA_RIGHT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   private static final Predicate RIDABLE_MINECARTS = (entity) -> {
      return entity instanceof AbstractMinecart && ((AbstractMinecart)entity).getMinecartType() == AbstractMinecart.Type.RIDEABLE;
   };
   private final NonNullList handItems;
   private final NonNullList armorItems;
   private boolean invisible;
   public long lastHit;
   private int disabledSlots;
   private Rotations headPose;
   private Rotations bodyPose;
   private Rotations leftArmPose;
   private Rotations rightArmPose;
   private Rotations leftLegPose;
   private Rotations rightLegPose;

   public ArmorStand(EntityType entityType, Level level) {
      super(entityType, level);
      this.handItems = NonNullList.withSize(2, ItemStack.EMPTY);
      this.armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
      this.headPose = DEFAULT_HEAD_POSE;
      this.bodyPose = DEFAULT_BODY_POSE;
      this.leftArmPose = DEFAULT_LEFT_ARM_POSE;
      this.rightArmPose = DEFAULT_RIGHT_ARM_POSE;
      this.leftLegPose = DEFAULT_LEFT_LEG_POSE;
      this.rightLegPose = DEFAULT_RIGHT_LEG_POSE;
      this.maxUpStep = 0.0F;
   }

   public ArmorStand(Level level, double var2, double var4, double var6) {
      this(EntityType.ARMOR_STAND, level);
      this.setPos(var2, var4, var6);
   }

   public void refreshDimensions() {
      double var1 = this.x;
      double var3 = this.y;
      double var5 = this.z;
      super.refreshDimensions();
      this.setPos(var1, var3, var5);
   }

   private boolean hasPhysics() {
      return !this.isMarker() && !this.isNoGravity();
   }

   public boolean isEffectiveAi() {
      return super.isEffectiveAi() && this.hasPhysics();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_CLIENT_FLAGS, Byte.valueOf((byte)0));
      this.entityData.define(DATA_HEAD_POSE, DEFAULT_HEAD_POSE);
      this.entityData.define(DATA_BODY_POSE, DEFAULT_BODY_POSE);
      this.entityData.define(DATA_LEFT_ARM_POSE, DEFAULT_LEFT_ARM_POSE);
      this.entityData.define(DATA_RIGHT_ARM_POSE, DEFAULT_RIGHT_ARM_POSE);
      this.entityData.define(DATA_LEFT_LEG_POSE, DEFAULT_LEFT_LEG_POSE);
      this.entityData.define(DATA_RIGHT_LEG_POSE, DEFAULT_RIGHT_LEG_POSE);
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
         this.playEquipSound(itemStack);
         this.handItems.set(equipmentSlot.getIndex(), itemStack);
         break;
      case ARMOR:
         this.playEquipSound(itemStack);
         this.armorItems.set(equipmentSlot.getIndex(), itemStack);
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

      if(!itemStack.isEmpty() && !Mob.isValidSlotForItem(var3, itemStack) && var3 != EquipmentSlot.HEAD) {
         return false;
      } else {
         this.setItemSlot(var3, itemStack);
         return true;
      }
   }

   public boolean canTakeItem(ItemStack itemStack) {
      EquipmentSlot var2 = Mob.getEquipmentSlotForItem(itemStack);
      return this.getItemBySlot(var2).isEmpty() && !this.isDisabled(var2);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
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
      compoundTag.putBoolean("Invisible", this.isInvisible());
      compoundTag.putBoolean("Small", this.isSmall());
      compoundTag.putBoolean("ShowArms", this.isShowArms());
      compoundTag.putInt("DisabledSlots", this.disabledSlots);
      compoundTag.putBoolean("NoBasePlate", this.isNoBasePlate());
      if(this.isMarker()) {
         compoundTag.putBoolean("Marker", this.isMarker());
      }

      compoundTag.put("Pose", this.writePose());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
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

      this.setInvisible(compoundTag.getBoolean("Invisible"));
      this.setSmall(compoundTag.getBoolean("Small"));
      this.setShowArms(compoundTag.getBoolean("ShowArms"));
      this.disabledSlots = compoundTag.getInt("DisabledSlots");
      this.setNoBasePlate(compoundTag.getBoolean("NoBasePlate"));
      this.setMarker(compoundTag.getBoolean("Marker"));
      this.noPhysics = !this.hasPhysics();
      CompoundTag compoundTag = compoundTag.getCompound("Pose");
      this.readPose(compoundTag);
   }

   private void readPose(CompoundTag compoundTag) {
      ListTag var2 = compoundTag.getList("Head", 5);
      this.setHeadPose(var2.isEmpty()?DEFAULT_HEAD_POSE:new Rotations(var2));
      ListTag var3 = compoundTag.getList("Body", 5);
      this.setBodyPose(var3.isEmpty()?DEFAULT_BODY_POSE:new Rotations(var3));
      ListTag var4 = compoundTag.getList("LeftArm", 5);
      this.setLeftArmPose(var4.isEmpty()?DEFAULT_LEFT_ARM_POSE:new Rotations(var4));
      ListTag var5 = compoundTag.getList("RightArm", 5);
      this.setRightArmPose(var5.isEmpty()?DEFAULT_RIGHT_ARM_POSE:new Rotations(var5));
      ListTag var6 = compoundTag.getList("LeftLeg", 5);
      this.setLeftLegPose(var6.isEmpty()?DEFAULT_LEFT_LEG_POSE:new Rotations(var6));
      ListTag var7 = compoundTag.getList("RightLeg", 5);
      this.setRightLegPose(var7.isEmpty()?DEFAULT_RIGHT_LEG_POSE:new Rotations(var7));
   }

   private CompoundTag writePose() {
      CompoundTag compoundTag = new CompoundTag();
      if(!DEFAULT_HEAD_POSE.equals(this.headPose)) {
         compoundTag.put("Head", this.headPose.save());
      }

      if(!DEFAULT_BODY_POSE.equals(this.bodyPose)) {
         compoundTag.put("Body", this.bodyPose.save());
      }

      if(!DEFAULT_LEFT_ARM_POSE.equals(this.leftArmPose)) {
         compoundTag.put("LeftArm", this.leftArmPose.save());
      }

      if(!DEFAULT_RIGHT_ARM_POSE.equals(this.rightArmPose)) {
         compoundTag.put("RightArm", this.rightArmPose.save());
      }

      if(!DEFAULT_LEFT_LEG_POSE.equals(this.leftLegPose)) {
         compoundTag.put("LeftLeg", this.leftLegPose.save());
      }

      if(!DEFAULT_RIGHT_LEG_POSE.equals(this.rightLegPose)) {
         compoundTag.put("RightLeg", this.rightLegPose.save());
      }

      return compoundTag;
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity entity) {
   }

   protected void pushEntities() {
      List<Entity> var1 = this.level.getEntities((Entity)this, this.getBoundingBox(), RIDABLE_MINECARTS);

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         Entity var3 = (Entity)var1.get(var2);
         if(this.distanceToSqr(var3) <= 0.2D) {
            var3.push(this);
         }
      }

   }

   public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      if(!this.isMarker() && var4.getItem() != Items.NAME_TAG) {
         if(!this.level.isClientSide && !player.isSpectator()) {
            EquipmentSlot var5 = Mob.getEquipmentSlotForItem(var4);
            if(var4.isEmpty()) {
               EquipmentSlot var6 = this.getClickedSlot(vec3);
               EquipmentSlot var7 = this.isDisabled(var6)?var5:var6;
               if(this.hasItemInSlot(var7)) {
                  this.swapItem(player, var7, var4, interactionHand);
               }
            } else {
               if(this.isDisabled(var5)) {
                  return InteractionResult.FAIL;
               }

               if(var5.getType() == EquipmentSlot.Type.HAND && !this.isShowArms()) {
                  return InteractionResult.FAIL;
               }

               this.swapItem(player, var5, var4, interactionHand);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   protected EquipmentSlot getClickedSlot(Vec3 vec3) {
      EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;
      boolean var3 = this.isSmall();
      double var4 = var3?vec3.y * 2.0D:vec3.y;
      EquipmentSlot var6 = EquipmentSlot.FEET;
      if(var4 >= 0.1D && var4 < 0.1D + (var3?0.8D:0.45D) && this.hasItemInSlot(var6)) {
         equipmentSlot = EquipmentSlot.FEET;
      } else if(var4 >= 0.9D + (var3?0.3D:0.0D) && var4 < 0.9D + (var3?1.0D:0.7D) && this.hasItemInSlot(EquipmentSlot.CHEST)) {
         equipmentSlot = EquipmentSlot.CHEST;
      } else if(var4 >= 0.4D && var4 < 0.4D + (var3?1.0D:0.8D) && this.hasItemInSlot(EquipmentSlot.LEGS)) {
         equipmentSlot = EquipmentSlot.LEGS;
      } else if(var4 >= 1.6D && this.hasItemInSlot(EquipmentSlot.HEAD)) {
         equipmentSlot = EquipmentSlot.HEAD;
      } else if(!this.hasItemInSlot(EquipmentSlot.MAINHAND) && this.hasItemInSlot(EquipmentSlot.OFFHAND)) {
         equipmentSlot = EquipmentSlot.OFFHAND;
      }

      return equipmentSlot;
   }

   public boolean isDisabled(EquipmentSlot equipmentSlot) {
      return (this.disabledSlots & 1 << equipmentSlot.getFilterFlag()) != 0 || equipmentSlot.getType() == EquipmentSlot.Type.HAND && !this.isShowArms();
   }

   private void swapItem(Player player, EquipmentSlot equipmentSlot, ItemStack itemStack, InteractionHand interactionHand) {
      ItemStack itemStack = this.getItemBySlot(equipmentSlot);
      if(itemStack.isEmpty() || (this.disabledSlots & 1 << equipmentSlot.getFilterFlag() + 8) == 0) {
         if(!itemStack.isEmpty() || (this.disabledSlots & 1 << equipmentSlot.getFilterFlag() + 16) == 0) {
            if(player.abilities.instabuild && itemStack.isEmpty() && !itemStack.isEmpty()) {
               ItemStack var6 = itemStack.copy();
               var6.setCount(1);
               this.setItemSlot(equipmentSlot, var6);
            } else if(!itemStack.isEmpty() && itemStack.getCount() > 1) {
               if(itemStack.isEmpty()) {
                  ItemStack var6 = itemStack.copy();
                  var6.setCount(1);
                  this.setItemSlot(equipmentSlot, var6);
                  itemStack.shrink(1);
               }
            } else {
               this.setItemSlot(equipmentSlot, itemStack);
               player.setItemInHand(interactionHand, itemStack);
            }
         }
      }
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(!this.level.isClientSide && !this.removed) {
         if(DamageSource.OUT_OF_WORLD.equals(damageSource)) {
            this.remove();
            return false;
         } else if(!this.isInvulnerableTo(damageSource) && !this.invisible && !this.isMarker()) {
            if(damageSource.isExplosion()) {
               this.brokenByAnything(damageSource);
               this.remove();
               return false;
            } else if(DamageSource.IN_FIRE.equals(damageSource)) {
               if(this.isOnFire()) {
                  this.causeDamage(damageSource, 0.15F);
               } else {
                  this.setSecondsOnFire(5);
               }

               return false;
            } else if(DamageSource.ON_FIRE.equals(damageSource) && this.getHealth() > 0.5F) {
               this.causeDamage(damageSource, 4.0F);
               return false;
            } else {
               boolean var3 = damageSource.getDirectEntity() instanceof AbstractArrow;
               boolean var4 = var3 && ((AbstractArrow)damageSource.getDirectEntity()).getPierceLevel() > 0;
               boolean var5 = "player".equals(damageSource.getMsgId());
               if(!var5 && !var3) {
                  return false;
               } else if(damageSource.getEntity() instanceof Player && !((Player)damageSource.getEntity()).abilities.mayBuild) {
                  return false;
               } else if(damageSource.isCreativePlayer()) {
                  this.playBrokenSound();
                  this.showBreakingParticles();
                  this.remove();
                  return var4;
               } else {
                  long var6 = this.level.getGameTime();
                  if(var6 - this.lastHit > 5L && !var3) {
                     this.level.broadcastEntityEvent(this, (byte)32);
                     this.lastHit = var6;
                  } else {
                     this.brokenByPlayer(damageSource);
                     this.showBreakingParticles();
                     this.remove();
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void handleEntityEvent(byte b) {
      if(b == 32) {
         if(this.level.isClientSide) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
            this.lastHit = this.level.getGameTime();
         }
      } else {
         super.handleEntityEvent(b);
      }

   }

   public boolean shouldRenderAtSqrDistance(double d) {
      double var3 = this.getBoundingBox().getSize() * 4.0D;
      if(Double.isNaN(var3) || var3 == 0.0D) {
         var3 = 4.0D;
      }

      var3 = var3 * 64.0D;
      return d < var3 * var3;
   }

   private void showBreakingParticles() {
      if(this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), this.x, this.y + (double)this.getBbHeight() / 1.5D, this.z, 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05D);
      }

   }

   private void causeDamage(DamageSource damageSource, float var2) {
      float var3 = this.getHealth();
      var3 = var3 - var2;
      if(var3 <= 0.5F) {
         this.brokenByAnything(damageSource);
         this.remove();
      } else {
         this.setHealth(var3);
      }

   }

   private void brokenByPlayer(DamageSource damageSource) {
      Block.popResource(this.level, new BlockPos(this), new ItemStack(Items.ARMOR_STAND));
      this.brokenByAnything(damageSource);
   }

   private void brokenByAnything(DamageSource damageSource) {
      this.playBrokenSound();
      this.dropAllDeathLoot(damageSource);

      for(int var2 = 0; var2 < this.handItems.size(); ++var2) {
         ItemStack var3 = (ItemStack)this.handItems.get(var2);
         if(!var3.isEmpty()) {
            Block.popResource(this.level, (new BlockPos(this)).above(), var3);
            this.handItems.set(var2, ItemStack.EMPTY);
         }
      }

      for(int var2 = 0; var2 < this.armorItems.size(); ++var2) {
         ItemStack var3 = (ItemStack)this.armorItems.get(var2);
         if(!var3.isEmpty()) {
            Block.popResource(this.level, (new BlockPos(this)).above(), var3);
            this.armorItems.set(var2, ItemStack.EMPTY);
         }
      }

   }

   private void playBrokenSound() {
      this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
   }

   protected float tickHeadTurn(float var1, float var2) {
      this.yBodyRotO = this.yRotO;
      this.yBodyRot = this.yRot;
      return 0.0F;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return entityDimensions.height * (this.isBaby()?0.5F:0.9F);
   }

   public double getRidingHeight() {
      return this.isMarker()?0.0D:0.10000000149011612D;
   }

   public void travel(Vec3 vec3) {
      if(this.hasPhysics()) {
         super.travel(vec3);
      }
   }

   public void setYBodyRot(float yBodyRot) {
      this.yBodyRotO = this.yRotO = yBodyRot;
      this.yHeadRotO = this.yHeadRot = yBodyRot;
   }

   public void setYHeadRot(float yHeadRot) {
      this.yBodyRotO = this.yRotO = yHeadRot;
      this.yHeadRotO = this.yHeadRot = yHeadRot;
   }

   public void tick() {
      super.tick();
      Rotations var1 = (Rotations)this.entityData.get(DATA_HEAD_POSE);
      if(!this.headPose.equals(var1)) {
         this.setHeadPose(var1);
      }

      Rotations var2 = (Rotations)this.entityData.get(DATA_BODY_POSE);
      if(!this.bodyPose.equals(var2)) {
         this.setBodyPose(var2);
      }

      Rotations var3 = (Rotations)this.entityData.get(DATA_LEFT_ARM_POSE);
      if(!this.leftArmPose.equals(var3)) {
         this.setLeftArmPose(var3);
      }

      Rotations var4 = (Rotations)this.entityData.get(DATA_RIGHT_ARM_POSE);
      if(!this.rightArmPose.equals(var4)) {
         this.setRightArmPose(var4);
      }

      Rotations var5 = (Rotations)this.entityData.get(DATA_LEFT_LEG_POSE);
      if(!this.leftLegPose.equals(var5)) {
         this.setLeftLegPose(var5);
      }

      Rotations var6 = (Rotations)this.entityData.get(DATA_RIGHT_LEG_POSE);
      if(!this.rightLegPose.equals(var6)) {
         this.setRightLegPose(var6);
      }

   }

   protected void updateInvisibilityStatus() {
      this.setInvisible(this.invisible);
   }

   public void setInvisible(boolean invisible) {
      this.invisible = invisible;
      super.setInvisible(invisible);
   }

   public boolean isBaby() {
      return this.isSmall();
   }

   public void kill() {
      this.remove();
   }

   public boolean ignoreExplosion() {
      return this.isInvisible();
   }

   public PushReaction getPistonPushReaction() {
      return this.isMarker()?PushReaction.IGNORE:super.getPistonPushReaction();
   }

   private void setSmall(boolean small) {
      this.entityData.set(DATA_CLIENT_FLAGS, Byte.valueOf(this.setBit(((Byte)this.entityData.get(DATA_CLIENT_FLAGS)).byteValue(), 1, small)));
   }

   public boolean isSmall() {
      return (((Byte)this.entityData.get(DATA_CLIENT_FLAGS)).byteValue() & 1) != 0;
   }

   private void setShowArms(boolean showArms) {
      this.entityData.set(DATA_CLIENT_FLAGS, Byte.valueOf(this.setBit(((Byte)this.entityData.get(DATA_CLIENT_FLAGS)).byteValue(), 4, showArms)));
   }

   public boolean isShowArms() {
      return (((Byte)this.entityData.get(DATA_CLIENT_FLAGS)).byteValue() & 4) != 0;
   }

   private void setNoBasePlate(boolean noBasePlate) {
      this.entityData.set(DATA_CLIENT_FLAGS, Byte.valueOf(this.setBit(((Byte)this.entityData.get(DATA_CLIENT_FLAGS)).byteValue(), 8, noBasePlate)));
   }

   public boolean isNoBasePlate() {
      return (((Byte)this.entityData.get(DATA_CLIENT_FLAGS)).byteValue() & 8) != 0;
   }

   private void setMarker(boolean marker) {
      this.entityData.set(DATA_CLIENT_FLAGS, Byte.valueOf(this.setBit(((Byte)this.entityData.get(DATA_CLIENT_FLAGS)).byteValue(), 16, marker)));
   }

   public boolean isMarker() {
      return (((Byte)this.entityData.get(DATA_CLIENT_FLAGS)).byteValue() & 16) != 0;
   }

   private byte setBit(byte var1, int var2, boolean var3) {
      if(var3) {
         var1 = (byte)(var1 | var2);
      } else {
         var1 = (byte)(var1 & ~var2);
      }

      return var1;
   }

   public void setHeadPose(Rotations headPose) {
      this.headPose = headPose;
      this.entityData.set(DATA_HEAD_POSE, headPose);
   }

   public void setBodyPose(Rotations bodyPose) {
      this.bodyPose = bodyPose;
      this.entityData.set(DATA_BODY_POSE, bodyPose);
   }

   public void setLeftArmPose(Rotations leftArmPose) {
      this.leftArmPose = leftArmPose;
      this.entityData.set(DATA_LEFT_ARM_POSE, leftArmPose);
   }

   public void setRightArmPose(Rotations rightArmPose) {
      this.rightArmPose = rightArmPose;
      this.entityData.set(DATA_RIGHT_ARM_POSE, rightArmPose);
   }

   public void setLeftLegPose(Rotations leftLegPose) {
      this.leftLegPose = leftLegPose;
      this.entityData.set(DATA_LEFT_LEG_POSE, leftLegPose);
   }

   public void setRightLegPose(Rotations rightLegPose) {
      this.rightLegPose = rightLegPose;
      this.entityData.set(DATA_RIGHT_LEG_POSE, rightLegPose);
   }

   public Rotations getHeadPose() {
      return this.headPose;
   }

   public Rotations getBodyPose() {
      return this.bodyPose;
   }

   public Rotations getLeftArmPose() {
      return this.leftArmPose;
   }

   public Rotations getRightArmPose() {
      return this.rightArmPose;
   }

   public Rotations getLeftLegPose() {
      return this.leftLegPose;
   }

   public Rotations getRightLegPose() {
      return this.rightLegPose;
   }

   public boolean isPickable() {
      return super.isPickable() && !this.isMarker();
   }

   public HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }

   protected SoundEvent getFallDamageSound(int i) {
      return SoundEvents.ARMOR_STAND_FALL;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.ARMOR_STAND_HIT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ARMOR_STAND_BREAK;
   }

   public void thunderHit(LightningBolt lightningBolt) {
   }

   public boolean isAffectedByPotions() {
      return false;
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      if(DATA_CLIENT_FLAGS.equals(entityDataAccessor)) {
         this.refreshDimensions();
         this.blocksBuilding = !this.isMarker();
      }

      super.onSyncedDataUpdated(entityDataAccessor);
   }

   public boolean attackable() {
      return false;
   }

   public EntityDimensions getDimensions(Pose pose) {
      float var2 = this.isMarker()?0.0F:(this.isBaby()?0.5F:1.0F);
      return this.getType().getDimensions().scale(var2);
   }
}
