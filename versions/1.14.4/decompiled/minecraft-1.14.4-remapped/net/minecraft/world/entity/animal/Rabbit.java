package net.minecraft.world.entity.animal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class Rabbit extends Animal {
   private static final EntityDataAccessor DATA_TYPE_ID = SynchedEntityData.defineId(Rabbit.class, EntityDataSerializers.INT);
   private static final ResourceLocation KILLER_BUNNY = new ResourceLocation("killer_bunny");
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int jumpDelayTicks;
   private int moreCarrotTicks;

   public Rabbit(EntityType entityType, Level level) {
      super(entityType, level);
      this.jumpControl = new Rabbit.RabbitJumpControl(this);
      this.moveControl = new Rabbit.RabbitMoveControl(this);
      this.setSpeedModifier(0.0D);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new Rabbit.RabbitPanicGoal(this, 2.2D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(new ItemLike[]{Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION}), false));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal(this, Player.class, 8.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal(this, Wolf.class, 10.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal(this, Monster.class, 4.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(5, new Rabbit.RaidGardenGoal(this));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
   }

   protected float getJumpPower() {
      if(!this.horizontalCollision && (!this.moveControl.hasWanted() || this.moveControl.getWantedY() <= this.y + 0.5D)) {
         Path var1 = this.navigation.getPath();
         if(var1 != null && var1.getIndex() < var1.getSize()) {
            Vec3 var2 = var1.currentPos(this);
            if(var2.y > this.y + 0.5D) {
               return 0.5F;
            }
         }

         return this.moveControl.getSpeedModifier() <= 0.6D?0.2F:0.3F;
      } else {
         return 0.5F;
      }
   }

   protected void jumpFromGround() {
      super.jumpFromGround();
      double var1 = this.moveControl.getSpeedModifier();
      if(var1 > 0.0D) {
         double var3 = getHorizontalDistanceSqr(this.getDeltaMovement());
         if(var3 < 0.01D) {
            this.moveRelative(0.1F, new Vec3(0.0D, 0.0D, 1.0D));
         }
      }

      if(!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)1);
      }

   }

   public float getJumpCompletion(float f) {
      return this.jumpDuration == 0?0.0F:((float)this.jumpTicks + f) / (float)this.jumpDuration;
   }

   public void setSpeedModifier(double speedModifier) {
      this.getNavigation().setSpeedModifier(speedModifier);
      this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), speedModifier);
   }

   public void setJumping(boolean jumping) {
      super.setJumping(jumping);
      if(jumping) {
         this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

   }

   public void startJumping() {
      this.setJumping(true);
      this.jumpDuration = 10;
      this.jumpTicks = 0;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TYPE_ID, Integer.valueOf(0));
   }

   public void customServerAiStep() {
      if(this.jumpDelayTicks > 0) {
         --this.jumpDelayTicks;
      }

      if(this.moreCarrotTicks > 0) {
         this.moreCarrotTicks -= this.random.nextInt(3);
         if(this.moreCarrotTicks < 0) {
            this.moreCarrotTicks = 0;
         }
      }

      if(this.onGround) {
         if(!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         if(this.getRabbitType() == 99 && this.jumpDelayTicks == 0) {
            LivingEntity var1 = this.getTarget();
            if(var1 != null && this.distanceToSqr(var1) < 16.0D) {
               this.facePoint(var1.x, var1.z);
               this.moveControl.setWantedPosition(var1.x, var1.y, var1.z, this.moveControl.getSpeedModifier());
               this.startJumping();
               this.wasOnGround = true;
            }
         }

         Rabbit.RabbitJumpControl var1 = (Rabbit.RabbitJumpControl)this.jumpControl;
         if(!var1.wantJump()) {
            if(this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
               Path var2 = this.navigation.getPath();
               Vec3 var3 = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
               if(var2 != null && var2.getIndex() < var2.getSize()) {
                  var3 = var2.currentPos(this);
               }

               this.facePoint(var3.x, var3.z);
               this.startJumping();
            }
         } else if(!var1.canJump()) {
            this.enableJumpControl();
         }
      }

      this.wasOnGround = this.onGround;
   }

   public void updateSprintingState() {
   }

   private void facePoint(double var1, double var3) {
      this.yRot = (float)(Mth.atan2(var3 - this.z, var1 - this.x) * 57.2957763671875D) - 90.0F;
   }

   private void enableJumpControl() {
      ((Rabbit.RabbitJumpControl)this.jumpControl).setCanJump(true);
   }

   private void disableJumpControl() {
      ((Rabbit.RabbitJumpControl)this.jumpControl).setCanJump(false);
   }

   private void setLandingDelay() {
      if(this.moveControl.getSpeedModifier() < 2.2D) {
         this.jumpDelayTicks = 10;
      } else {
         this.jumpDelayTicks = 1;
      }

   }

   private void checkLandingDelay() {
      this.setLandingDelay();
      this.disableJumpControl();
   }

   public void aiStep() {
      super.aiStep();
      if(this.jumpTicks != this.jumpDuration) {
         ++this.jumpTicks;
      } else if(this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("RabbitType", this.getRabbitType());
      compoundTag.putInt("MoreCarrotTicks", this.moreCarrotTicks);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setRabbitType(compoundTag.getInt("RabbitType"));
      this.moreCarrotTicks = compoundTag.getInt("MoreCarrotTicks");
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.RABBIT_JUMP;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.RABBIT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.RABBIT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.RABBIT_DEATH;
   }

   public boolean doHurtTarget(Entity entity) {
      if(this.getRabbitType() == 99) {
         this.playSound(SoundEvents.RABBIT_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         return entity.hurt(DamageSource.mobAttack(this), 8.0F);
      } else {
         return entity.hurt(DamageSource.mobAttack(this), 3.0F);
      }
   }

   public SoundSource getSoundSource() {
      return this.getRabbitType() == 99?SoundSource.HOSTILE:SoundSource.NEUTRAL;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      return this.isInvulnerableTo(damageSource)?false:super.hurt(damageSource, var2);
   }

   private boolean isTemptingItem(Item item) {
      return item == Items.CARROT || item == Items.GOLDEN_CARROT || item == Blocks.DANDELION.asItem();
   }

   public Rabbit getBreedOffspring(AgableMob agableMob) {
      Rabbit rabbit = (Rabbit)EntityType.RABBIT.create(this.level);
      int var3 = this.getRandomRabbitType(this.level);
      if(this.random.nextInt(20) != 0) {
         if(agableMob instanceof Rabbit && this.random.nextBoolean()) {
            var3 = ((Rabbit)agableMob).getRabbitType();
         } else {
            var3 = this.getRabbitType();
         }
      }

      rabbit.setRabbitType(var3);
      return rabbit;
   }

   public boolean isFood(ItemStack itemStack) {
      return this.isTemptingItem(itemStack.getItem());
   }

   public int getRabbitType() {
      return ((Integer)this.entityData.get(DATA_TYPE_ID)).intValue();
   }

   public void setRabbitType(int rabbitType) {
      if(rabbitType == 99) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8.0D);
         this.goalSelector.addGoal(4, new Rabbit.EvilRabbitAttackGoal(this));
         this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Wolf.class, true));
         if(!this.hasCustomName()) {
            this.setCustomName(new TranslatableComponent(Util.makeDescriptionId("entity", KILLER_BUNNY), new Object[0]));
         }
      }

      this.entityData.set(DATA_TYPE_ID, Integer.valueOf(rabbitType));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      var4 = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
      int var6 = this.getRandomRabbitType(levelAccessor);
      boolean var7 = false;
      if(var4 instanceof Rabbit.RabbitGroupData) {
         var6 = ((Rabbit.RabbitGroupData)var4).rabbitType;
         var7 = true;
      } else {
         var4 = new Rabbit.RabbitGroupData(var6);
      }

      this.setRabbitType(var6);
      if(var7) {
         this.setAge(-24000);
      }

      return var4;
   }

   private int getRandomRabbitType(LevelAccessor levelAccessor) {
      Biome var2 = levelAccessor.getBiome(new BlockPos(this));
      int var3 = this.random.nextInt(100);
      return var2.getPrecipitation() == Biome.Precipitation.SNOW?(var3 < 80?1:3):(var2.getBiomeCategory() == Biome.BiomeCategory.DESERT?4:(var3 < 50?0:(var3 < 90?5:2)));
   }

   public static boolean checkRabbitSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      Block var5 = levelAccessor.getBlockState(blockPos.below()).getBlock();
      return (var5 == Blocks.GRASS_BLOCK || var5 == Blocks.SNOW || var5 == Blocks.SAND) && levelAccessor.getRawBrightness(blockPos, 0) > 8;
   }

   private boolean wantsMoreFood() {
      return this.moreCarrotTicks == 0;
   }

   public void handleEntityEvent(byte b) {
      if(b == 1) {
         this.doSprintParticleEffect();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleEntityEvent(b);
      }

   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }

   static class EvilRabbitAttackGoal extends MeleeAttackGoal {
      public EvilRabbitAttackGoal(Rabbit rabbit) {
         super(rabbit, 1.4D, true);
      }

      protected double getAttackReachSqr(LivingEntity livingEntity) {
         return (double)(4.0F + livingEntity.getBbWidth());
      }
   }

   static class RabbitAvoidEntityGoal extends AvoidEntityGoal {
      private final Rabbit rabbit;

      public RabbitAvoidEntityGoal(Rabbit rabbit, Class class, float var3, double var4, double var6) {
         super(rabbit, class, var3, var4, var6);
         this.rabbit = rabbit;
      }

      public boolean canUse() {
         return this.rabbit.getRabbitType() != 99 && super.canUse();
      }
   }

   public static class RabbitGroupData implements SpawnGroupData {
      public final int rabbitType;

      public RabbitGroupData(int rabbitType) {
         this.rabbitType = rabbitType;
      }
   }

   public class RabbitJumpControl extends JumpControl {
      private final Rabbit rabbit;
      private boolean canJump;

      public RabbitJumpControl(Rabbit rabbit) {
         super(rabbit);
         this.rabbit = rabbit;
      }

      public boolean wantJump() {
         return this.jump;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean canJump) {
         this.canJump = canJump;
      }

      public void tick() {
         if(this.jump) {
            this.rabbit.startJumping();
            this.jump = false;
         }

      }
   }

   static class RabbitMoveControl extends MoveControl {
      private final Rabbit rabbit;
      private double nextJumpSpeed;

      public RabbitMoveControl(Rabbit rabbit) {
         super(rabbit);
         this.rabbit = rabbit;
      }

      public void tick() {
         if(this.rabbit.onGround && !this.rabbit.jumping && !((Rabbit.RabbitJumpControl)this.rabbit.jumpControl).wantJump()) {
            this.rabbit.setSpeedModifier(0.0D);
         } else if(this.hasWanted()) {
            this.rabbit.setSpeedModifier(this.nextJumpSpeed);
         }

         super.tick();
      }

      public void setWantedPosition(double var1, double var3, double var5, double nextJumpSpeed) {
         if(this.rabbit.isInWater()) {
            nextJumpSpeed = 1.5D;
         }

         super.setWantedPosition(var1, var3, var5, nextJumpSpeed);
         if(nextJumpSpeed > 0.0D) {
            this.nextJumpSpeed = nextJumpSpeed;
         }

      }
   }

   static class RabbitPanicGoal extends PanicGoal {
      private final Rabbit rabbit;

      public RabbitPanicGoal(Rabbit rabbit, double var2) {
         super(rabbit, var2);
         this.rabbit = rabbit;
      }

      public void tick() {
         super.tick();
         this.rabbit.setSpeedModifier(this.speedModifier);
      }
   }

   static class RaidGardenGoal extends MoveToBlockGoal {
      private final Rabbit rabbit;
      private boolean wantsToRaid;
      private boolean canRaid;

      public RaidGardenGoal(Rabbit rabbit) {
         super(rabbit, 0.699999988079071D, 16);
         this.rabbit = rabbit;
      }

      public boolean canUse() {
         if(this.nextStartTick <= 0) {
            if(!this.rabbit.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
               return false;
            }

            this.canRaid = false;
            this.wantsToRaid = this.rabbit.wantsMoreFood();
            this.wantsToRaid = true;
         }

         return super.canUse();
      }

      public boolean canContinueToUse() {
         return this.canRaid && super.canContinueToUse();
      }

      public void tick() {
         super.tick();
         this.rabbit.getLookControl().setLookAt((double)this.blockPos.getX() + 0.5D, (double)(this.blockPos.getY() + 1), (double)this.blockPos.getZ() + 0.5D, 10.0F, (float)this.rabbit.getMaxHeadXRot());
         if(this.isReachedTarget()) {
            Level var1 = this.rabbit.level;
            BlockPos var2 = this.blockPos.above();
            BlockState var3 = var1.getBlockState(var2);
            Block var4 = var3.getBlock();
            if(this.canRaid && var4 instanceof CarrotBlock) {
               Integer var5 = (Integer)var3.getValue(CarrotBlock.AGE);
               if(var5.intValue() == 0) {
                  var1.setBlock(var2, Blocks.AIR.defaultBlockState(), 2);
                  var1.destroyBlock(var2, true);
               } else {
                  var1.setBlock(var2, (BlockState)var3.setValue(CarrotBlock.AGE, Integer.valueOf(var5.intValue() - 1)), 2);
                  var1.levelEvent(2001, var2, Block.getId(var3));
               }

               this.rabbit.moreCarrotTicks = 40;
            }

            this.canRaid = false;
            this.nextStartTick = 10;
         }

      }

      protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
         Block var3 = levelReader.getBlockState(blockPos).getBlock();
         if(var3 == Blocks.FARMLAND && this.wantsToRaid && !this.canRaid) {
            blockPos = blockPos.above();
            BlockState var4 = levelReader.getBlockState(blockPos);
            var3 = var4.getBlock();
            if(var3 instanceof CarrotBlock && ((CarrotBlock)var3).isMaxAge(var4)) {
               this.canRaid = true;
               return true;
            }
         }

         return false;
      }
   }
}
