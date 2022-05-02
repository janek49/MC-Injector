package net.minecraft.world.entity.boss.wither;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WitherBoss extends Monster implements RangedAttackMob {
   private static final EntityDataAccessor DATA_TARGET_A = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor DATA_TARGET_B = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor DATA_TARGET_C = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
   private static final List DATA_TARGETS = ImmutableList.of(DATA_TARGET_A, DATA_TARGET_B, DATA_TARGET_C);
   private static final EntityDataAccessor DATA_ID_INV = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.INT);
   private final float[] xRotHeads = new float[2];
   private final float[] yRotHeads = new float[2];
   private final float[] xRotOHeads = new float[2];
   private final float[] yRotOHeads = new float[2];
   private final int[] nextHeadUpdate = new int[2];
   private final int[] idleHeadUpdates = new int[2];
   private int destroyBlocksTick;
   private final ServerBossEvent bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);
   private static final Predicate LIVING_ENTITY_SELECTOR = (livingEntity) -> {
      return livingEntity.getMobType() != MobType.UNDEAD && livingEntity.attackable();
   };
   private static final TargetingConditions TARGETING_CONDITIONS = (new TargetingConditions()).range(20.0D).selector(LIVING_ENTITY_SELECTOR);

   public WitherBoss(EntityType entityType, Level level) {
      super(entityType, level);
      this.setHealth(this.getMaxHealth());
      this.getNavigation().setCanFloat(true);
      this.xpReward = 50;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new WitherBoss.WitherDoNothingGoal());
      this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Mob.class, 0, false, false, LIVING_ENTITY_SELECTOR));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TARGET_A, Integer.valueOf(0));
      this.entityData.define(DATA_TARGET_B, Integer.valueOf(0));
      this.entityData.define(DATA_TARGET_C, Integer.valueOf(0));
      this.entityData.define(DATA_ID_INV, Integer.valueOf(0));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("Invul", this.getInvulnerableTicks());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setInvulnerableTicks(compoundTag.getInt("Invul"));
      if(this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }

   }

   public void setCustomName(@Nullable Component customName) {
      super.setCustomName(customName);
      this.bossEvent.setName(this.getDisplayName());
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITHER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.WITHER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WITHER_DEATH;
   }

   public void aiStep() {
      Vec3 var1 = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
      if(!this.level.isClientSide && this.getAlternativeTarget(0) > 0) {
         Entity var2 = this.level.getEntity(this.getAlternativeTarget(0));
         if(var2 != null) {
            double var3 = var1.y;
            if(this.y < var2.y || !this.isPowered() && this.y < var2.y + 5.0D) {
               var3 = Math.max(0.0D, var3);
               var3 = var3 + (0.3D - var3 * 0.6000000238418579D);
            }

            var1 = new Vec3(var1.x, var3, var1.z);
            Vec3 var5 = new Vec3(var2.x - this.x, 0.0D, var2.z - this.z);
            if(getHorizontalDistanceSqr(var5) > 9.0D) {
               Vec3 var6 = var5.normalize();
               var1 = var1.add(var6.x * 0.3D - var1.x * 0.6D, 0.0D, var6.z * 0.3D - var1.z * 0.6D);
            }
         }
      }

      this.setDeltaMovement(var1);
      if(getHorizontalDistanceSqr(var1) > 0.05D) {
         this.yRot = (float)Mth.atan2(var1.z, var1.x) * 57.295776F - 90.0F;
      }

      super.aiStep();

      for(int var2 = 0; var2 < 2; ++var2) {
         this.yRotOHeads[var2] = this.yRotHeads[var2];
         this.xRotOHeads[var2] = this.xRotHeads[var2];
      }

      for(int var2 = 0; var2 < 2; ++var2) {
         int var3 = this.getAlternativeTarget(var2 + 1);
         Entity var4 = null;
         if(var3 > 0) {
            var4 = this.level.getEntity(var3);
         }

         if(var4 != null) {
            double var5 = this.getHeadX(var2 + 1);
            double var7 = this.getHeadY(var2 + 1);
            double var9 = this.getHeadZ(var2 + 1);
            double var11 = var4.x - var5;
            double var13 = var4.y + (double)var4.getEyeHeight() - var7;
            double var15 = var4.z - var9;
            double var17 = (double)Mth.sqrt(var11 * var11 + var15 * var15);
            float var19 = (float)(Mth.atan2(var15, var11) * 57.2957763671875D) - 90.0F;
            float var20 = (float)(-(Mth.atan2(var13, var17) * 57.2957763671875D));
            this.xRotHeads[var2] = this.rotlerp(this.xRotHeads[var2], var20, 40.0F);
            this.yRotHeads[var2] = this.rotlerp(this.yRotHeads[var2], var19, 10.0F);
         } else {
            this.yRotHeads[var2] = this.rotlerp(this.yRotHeads[var2], this.yBodyRot, 10.0F);
         }
      }

      boolean var2 = this.isPowered();

      for(int var3 = 0; var3 < 3; ++var3) {
         double var4 = this.getHeadX(var3);
         double var6 = this.getHeadY(var3);
         double var8 = this.getHeadZ(var3);
         this.level.addParticle(ParticleTypes.SMOKE, var4 + this.random.nextGaussian() * 0.30000001192092896D, var6 + this.random.nextGaussian() * 0.30000001192092896D, var8 + this.random.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);
         if(var2 && this.level.random.nextInt(4) == 0) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, var4 + this.random.nextGaussian() * 0.30000001192092896D, var6 + this.random.nextGaussian() * 0.30000001192092896D, var8 + this.random.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
         }
      }

      if(this.getInvulnerableTicks() > 0) {
         for(int var3 = 0; var3 < 3; ++var3) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.x + this.random.nextGaussian(), this.y + (double)(this.random.nextFloat() * 3.3F), this.z + this.random.nextGaussian(), 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
         }
      }

   }

   protected void customServerAiStep() {
      if(this.getInvulnerableTicks() > 0) {
         int var1 = this.getInvulnerableTicks() - 1;
         if(var1 <= 0) {
            Explosion.BlockInteraction var2 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)?Explosion.BlockInteraction.DESTROY:Explosion.BlockInteraction.NONE;
            this.level.explode(this, this.x, this.y + (double)this.getEyeHeight(), this.z, 7.0F, false, var2);
            this.level.globalLevelEvent(1023, new BlockPos(this), 0);
         }

         this.setInvulnerableTicks(var1);
         if(this.tickCount % 10 == 0) {
            this.heal(10.0F);
         }

      } else {
         super.customServerAiStep();

         for(int var1 = 1; var1 < 3; ++var1) {
            if(this.tickCount >= this.nextHeadUpdate[var1 - 1]) {
               this.nextHeadUpdate[var1 - 1] = this.tickCount + 10 + this.random.nextInt(10);
               if(this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD) {
                  int var10001 = var1 - 1;
                  int var10003 = this.idleHeadUpdates[var1 - 1];
                  this.idleHeadUpdates[var10001] = this.idleHeadUpdates[var1 - 1] + 1;
                  if(var10003 > 15) {
                     float var2 = 10.0F;
                     float var3 = 5.0F;
                     double var4 = Mth.nextDouble(this.random, this.x - 10.0D, this.x + 10.0D);
                     double var6 = Mth.nextDouble(this.random, this.y - 5.0D, this.y + 5.0D);
                     double var8 = Mth.nextDouble(this.random, this.z - 10.0D, this.z + 10.0D);
                     this.performRangedAttack(var1 + 1, var4, var6, var8, true);
                     this.idleHeadUpdates[var1 - 1] = 0;
                  }
               }

               int var2 = this.getAlternativeTarget(var1);
               if(var2 > 0) {
                  Entity var3 = this.level.getEntity(var2);
                  if(var3 != null && var3.isAlive() && this.distanceToSqr(var3) <= 900.0D && this.canSee(var3)) {
                     if(var3 instanceof Player && ((Player)var3).abilities.invulnerable) {
                        this.setAlternativeTarget(var1, 0);
                     } else {
                        this.performRangedAttack(var1 + 1, (LivingEntity)var3);
                        this.nextHeadUpdate[var1 - 1] = this.tickCount + 40 + this.random.nextInt(20);
                        this.idleHeadUpdates[var1 - 1] = 0;
                     }
                  } else {
                     this.setAlternativeTarget(var1, 0);
                  }
               } else {
                  List<LivingEntity> var3 = this.level.getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));

                  for(int var4 = 0; var4 < 10 && !var3.isEmpty(); ++var4) {
                     LivingEntity var5 = (LivingEntity)var3.get(this.random.nextInt(var3.size()));
                     if(var5 != this && var5.isAlive() && this.canSee(var5)) {
                        if(var5 instanceof Player) {
                           if(!((Player)var5).abilities.invulnerable) {
                              this.setAlternativeTarget(var1, var5.getId());
                           }
                        } else {
                           this.setAlternativeTarget(var1, var5.getId());
                        }
                        break;
                     }

                     var3.remove(var5);
                  }
               }
            }
         }

         if(this.getTarget() != null) {
            this.setAlternativeTarget(0, this.getTarget().getId());
         } else {
            this.setAlternativeTarget(0, 0);
         }

         if(this.destroyBlocksTick > 0) {
            --this.destroyBlocksTick;
            if(this.destroyBlocksTick == 0 && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
               int var1 = Mth.floor(this.y);
               int var2 = Mth.floor(this.x);
               int var3 = Mth.floor(this.z);
               boolean var4 = false;

               for(int var5 = -1; var5 <= 1; ++var5) {
                  for(int var6 = -1; var6 <= 1; ++var6) {
                     for(int var7 = 0; var7 <= 3; ++var7) {
                        int var8 = var2 + var5;
                        int var9 = var1 + var7;
                        int var10 = var3 + var6;
                        BlockPos var11 = new BlockPos(var8, var9, var10);
                        BlockState var12 = this.level.getBlockState(var11);
                        if(canDestroy(var12)) {
                           var4 = this.level.destroyBlock(var11, true) || var4;
                        }
                     }
                  }
               }

               if(var4) {
                  this.level.levelEvent((Player)null, 1022, new BlockPos(this), 0);
               }
            }
         }

         if(this.tickCount % 20 == 0) {
            this.heal(1.0F);
         }

         this.bossEvent.setPercent(this.getHealth() / this.getMaxHealth());
      }
   }

   public static boolean canDestroy(BlockState blockState) {
      return !blockState.isAir() && !BlockTags.WITHER_IMMUNE.contains(blockState.getBlock());
   }

   public void makeInvulnerable() {
      this.setInvulnerableTicks(220);
      this.setHealth(this.getMaxHealth() / 3.0F);
   }

   public void makeStuckInBlock(BlockState blockState, Vec3 vec3) {
   }

   public void startSeenByPlayer(ServerPlayer serverPlayer) {
      super.startSeenByPlayer(serverPlayer);
      this.bossEvent.addPlayer(serverPlayer);
   }

   public void stopSeenByPlayer(ServerPlayer serverPlayer) {
      super.stopSeenByPlayer(serverPlayer);
      this.bossEvent.removePlayer(serverPlayer);
   }

   private double getHeadX(int i) {
      if(i <= 0) {
         return this.x;
      } else {
         float var2 = (this.yBodyRot + (float)(180 * (i - 1))) * 0.017453292F;
         float var3 = Mth.cos(var2);
         return this.x + (double)var3 * 1.3D;
      }
   }

   private double getHeadY(int i) {
      return i <= 0?this.y + 3.0D:this.y + 2.2D;
   }

   private double getHeadZ(int i) {
      if(i <= 0) {
         return this.z;
      } else {
         float var2 = (this.yBodyRot + (float)(180 * (i - 1))) * 0.017453292F;
         float var3 = Mth.sin(var2);
         return this.z + (double)var3 * 1.3D;
      }
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

   private void performRangedAttack(int var1, LivingEntity livingEntity) {
      this.performRangedAttack(var1, livingEntity.x, livingEntity.y + (double)livingEntity.getEyeHeight() * 0.5D, livingEntity.z, var1 == 0 && this.random.nextFloat() < 0.001F);
   }

   private void performRangedAttack(int var1, double var2, double var4, double var6, boolean var8) {
      this.level.levelEvent((Player)null, 1024, new BlockPos(this), 0);
      double var9 = this.getHeadX(var1);
      double var11 = this.getHeadY(var1);
      double var13 = this.getHeadZ(var1);
      double var15 = var2 - var9;
      double var17 = var4 - var11;
      double var19 = var6 - var13;
      WitherSkull var21 = new WitherSkull(this.level, this, var15, var17, var19);
      if(var8) {
         var21.setDangerous(true);
      }

      var21.y = var11;
      var21.x = var9;
      var21.z = var13;
      this.level.addFreshEntity(var21);
   }

   public void performRangedAttack(LivingEntity livingEntity, float var2) {
      this.performRangedAttack(0, livingEntity);
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else if(damageSource != DamageSource.DROWN && !(damageSource.getEntity() instanceof WitherBoss)) {
         if(this.getInvulnerableTicks() > 0 && damageSource != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if(this.isPowered()) {
               Entity var3 = damageSource.getDirectEntity();
               if(var3 instanceof AbstractArrow) {
                  return false;
               }
            }

            Entity var3 = damageSource.getEntity();
            if(var3 != null && !(var3 instanceof Player) && var3 instanceof LivingEntity && ((LivingEntity)var3).getMobType() == this.getMobType()) {
               return false;
            } else {
               if(this.destroyBlocksTick <= 0) {
                  this.destroyBlocksTick = 20;
               }

               for(int var4 = 0; var4 < this.idleHeadUpdates.length; ++var4) {
                  this.idleHeadUpdates[var4] += 3;
               }

               return super.hurt(damageSource, var2);
            }
         }
      } else {
         return false;
      }
   }

   protected void dropCustomDeathLoot(DamageSource damageSource, int var2, boolean var3) {
      super.dropCustomDeathLoot(damageSource, var2, var3);
      ItemEntity var4 = this.spawnAtLocation(Items.NETHER_STAR);
      if(var4 != null) {
         var4.setExtendedLifetime();
      }

   }

   protected void checkDespawn() {
      this.noActionTime = 0;
   }

   public int getLightColor() {
      return 15728880;
   }

   public void causeFallDamage(float var1, float var2) {
   }

   public boolean addEffect(MobEffectInstance mobEffectInstance) {
      return false;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6000000238418579D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
      this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
   }

   public float getHeadYRot(int i) {
      return this.yRotHeads[i];
   }

   public float getHeadXRot(int i) {
      return this.xRotHeads[i];
   }

   public int getInvulnerableTicks() {
      return ((Integer)this.entityData.get(DATA_ID_INV)).intValue();
   }

   public void setInvulnerableTicks(int invulnerableTicks) {
      this.entityData.set(DATA_ID_INV, Integer.valueOf(invulnerableTicks));
   }

   public int getAlternativeTarget(int i) {
      return ((Integer)this.entityData.get((EntityDataAccessor)DATA_TARGETS.get(i))).intValue();
   }

   public void setAlternativeTarget(int var1, int var2) {
      this.entityData.set((EntityDataAccessor)DATA_TARGETS.get(var1), Integer.valueOf(var2));
   }

   public boolean isPowered() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   protected boolean canRide(Entity entity) {
      return false;
   }

   public boolean canChangeDimensions() {
      return false;
   }

   public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
      return mobEffectInstance.getEffect() == MobEffects.WITHER?false:super.canBeAffected(mobEffectInstance);
   }

   class WitherDoNothingGoal extends Goal {
      public WitherDoNothingGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return WitherBoss.this.getInvulnerableTicks() > 0;
      }
   }
}
