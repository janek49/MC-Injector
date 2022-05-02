package net.minecraft.world.entity.monster;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Evoker extends SpellcasterIllager {
   private Sheep wololoTarget;

   public Evoker(EntityType entityType, Level level) {
      super(entityType, level);
      this.xpReward = 10;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new Evoker.EvokerCastingSpellGoal());
      this.goalSelector.addGoal(2, new AvoidEntityGoal(this, Player.class, 8.0F, 0.6D, 1.0D));
      this.goalSelector.addGoal(4, new Evoker.EvokerSummonSpellGoal());
      this.goalSelector.addGoal(5, new Evoker.EvokerAttackSpellGoal());
      this.goalSelector.addGoal(6, new Evoker.EvokerWololoSpellGoal());
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Raider.class})).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal(this, Player.class, true)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, false));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.EVOKER_CELEBRATE;
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
   }

   public void tick() {
      super.tick();
   }

   public boolean isAlliedTo(Entity entity) {
      return entity == null?false:(entity == this?true:(super.isAlliedTo(entity)?true:(entity instanceof Vex?this.isAlliedTo(((Vex)entity).getOwner()):(entity instanceof LivingEntity && ((LivingEntity)entity).getMobType() == MobType.ILLAGER?this.getTeam() == null && entity.getTeam() == null:false))));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.EVOKER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.EVOKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.EVOKER_HURT;
   }

   private void setWololoTarget(@Nullable Sheep wololoTarget) {
      this.wololoTarget = wololoTarget;
   }

   @Nullable
   private Sheep getWololoTarget() {
      return this.wololoTarget;
   }

   protected SoundEvent getCastingSoundEvent() {
      return SoundEvents.EVOKER_CAST_SPELL;
   }

   public void applyRaidBuffs(int var1, boolean var2) {
   }

   class EvokerAttackSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      private EvokerAttackSpellGoal() {
         super();
      }

      protected int getCastingTime() {
         return 40;
      }

      protected int getCastingInterval() {
         return 100;
      }

      protected void performSpellCasting() {
         LivingEntity var1 = Evoker.this.getTarget();
         double var2 = Math.min(var1.y, Evoker.this.y);
         double var4 = Math.max(var1.y, Evoker.this.y) + 1.0D;
         float var6 = (float)Mth.atan2(var1.z - Evoker.this.z, var1.x - Evoker.this.x);
         if(Evoker.this.distanceToSqr(var1) < 9.0D) {
            for(int var7 = 0; var7 < 5; ++var7) {
               float var8 = var6 + (float)var7 * 3.1415927F * 0.4F;
               this.createSpellEntity(Evoker.this.x + (double)Mth.cos(var8) * 1.5D, Evoker.this.z + (double)Mth.sin(var8) * 1.5D, var2, var4, var8, 0);
            }

            for(int var7 = 0; var7 < 8; ++var7) {
               float var8 = var6 + (float)var7 * 3.1415927F * 2.0F / 8.0F + 1.2566371F;
               this.createSpellEntity(Evoker.this.x + (double)Mth.cos(var8) * 2.5D, Evoker.this.z + (double)Mth.sin(var8) * 2.5D, var2, var4, var8, 3);
            }
         } else {
            for(int var7 = 0; var7 < 16; ++var7) {
               double var8 = 1.25D * (double)(var7 + 1);
               int var10 = 1 * var7;
               this.createSpellEntity(Evoker.this.x + (double)Mth.cos(var6) * var8, Evoker.this.z + (double)Mth.sin(var6) * var8, var2, var4, var6, var10);
            }
         }

      }

      private void createSpellEntity(double var1, double var3, double var5, double var7, float var9, int var10) {
         BlockPos var11 = new BlockPos(var1, var7, var3);
         boolean var12 = false;
         double var13 = 0.0D;

         while(true) {
            BlockPos var15 = var11.below();
            BlockState var16 = Evoker.this.level.getBlockState(var15);
            if(var16.isFaceSturdy(Evoker.this.level, var15, Direction.UP)) {
               if(!Evoker.this.level.isEmptyBlock(var11)) {
                  BlockState var17 = Evoker.this.level.getBlockState(var11);
                  VoxelShape var18 = var17.getCollisionShape(Evoker.this.level, var11);
                  if(!var18.isEmpty()) {
                     var13 = var18.max(Direction.Axis.Y);
                  }
               }

               var12 = true;
               break;
            }

            var11 = var11.below();
            if(var11.getY() < Mth.floor(var5) - 1) {
               break;
            }
         }

         if(var12) {
            Evoker.this.level.addFreshEntity(new EvokerFangs(Evoker.this.level, var1, (double)var11.getY() + var13, var3, var9, var10, Evoker.this));
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_ATTACK;
      }

      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.FANGS;
      }
   }

   class EvokerCastingSpellGoal extends SpellcasterIllager.SpellcasterCastingSpellGoal {
      private EvokerCastingSpellGoal() {
         super();
      }

      public void tick() {
         if(Evoker.this.getTarget() != null) {
            Evoker.this.getLookControl().setLookAt(Evoker.this.getTarget(), (float)Evoker.this.getMaxHeadYRot(), (float)Evoker.this.getMaxHeadXRot());
         } else if(Evoker.this.getWololoTarget() != null) {
            Evoker.this.getLookControl().setLookAt(Evoker.this.getWololoTarget(), (float)Evoker.this.getMaxHeadYRot(), (float)Evoker.this.getMaxHeadXRot());
         }

      }
   }

   class EvokerSummonSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      private final TargetingConditions vexCountTargeting;

      private EvokerSummonSpellGoal() {
         super();
         this.vexCountTargeting = (new TargetingConditions()).range(16.0D).allowUnseeable().ignoreInvisibilityTesting().allowInvulnerable().allowSameTeam();
      }

      public boolean canUse() {
         if(!super.canUse()) {
            return false;
         } else {
            int var1 = Evoker.this.level.getNearbyEntities(Vex.class, this.vexCountTargeting, Evoker.this, Evoker.this.getBoundingBox().inflate(16.0D)).size();
            return Evoker.this.random.nextInt(8) + 1 > var1;
         }
      }

      protected int getCastingTime() {
         return 100;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void performSpellCasting() {
         for(int var1 = 0; var1 < 3; ++var1) {
            BlockPos var2 = (new BlockPos(Evoker.this)).offset(-2 + Evoker.this.random.nextInt(5), 1, -2 + Evoker.this.random.nextInt(5));
            Vex var3 = (Vex)EntityType.VEX.create(Evoker.this.level);
            var3.moveTo(var2, 0.0F, 0.0F);
            var3.finalizeSpawn(Evoker.this.level, Evoker.this.level.getCurrentDifficultyAt(var2), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
            var3.setOwner(Evoker.this);
            var3.setBoundOrigin(var2);
            var3.setLimitedLife(20 * (30 + Evoker.this.random.nextInt(90)));
            Evoker.this.level.addFreshEntity(var3);
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_SUMMON;
      }

      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.SUMMON_VEX;
      }
   }

   public class EvokerWololoSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      private final TargetingConditions wololoTargeting = (new TargetingConditions()).range(16.0D).allowInvulnerable().selector((livingEntity) -> {
         return ((Sheep)livingEntity).getColor() == DyeColor.BLUE;
      });

      public EvokerWololoSpellGoal() {
         super();
      }

      public boolean canUse() {
         if(Evoker.this.getTarget() != null) {
            return false;
         } else if(Evoker.this.isCastingSpell()) {
            return false;
         } else if(Evoker.this.tickCount < this.nextAttackTickCount) {
            return false;
         } else if(!Evoker.this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
         } else {
            List<Sheep> var1 = Evoker.this.level.getNearbyEntities(Sheep.class, this.wololoTargeting, Evoker.this, Evoker.this.getBoundingBox().inflate(16.0D, 4.0D, 16.0D));
            if(var1.isEmpty()) {
               return false;
            } else {
               Evoker.this.setWololoTarget((Sheep)var1.get(Evoker.this.random.nextInt(var1.size())));
               return true;
            }
         }
      }

      public boolean canContinueToUse() {
         return Evoker.this.getWololoTarget() != null && this.attackWarmupDelay > 0;
      }

      public void stop() {
         super.stop();
         Evoker.this.setWololoTarget((Sheep)null);
      }

      protected void performSpellCasting() {
         Sheep var1 = Evoker.this.getWololoTarget();
         if(var1 != null && var1.isAlive()) {
            var1.setColor(DyeColor.RED);
         }

      }

      protected int getCastWarmupTime() {
         return 40;
      }

      protected int getCastingTime() {
         return 60;
      }

      protected int getCastingInterval() {
         return 140;
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_WOLOLO;
      }

      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.WOLOLO;
      }
   }
}
