package net.minecraft.world.entity.monster;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class PigZombie extends Zombie {
   private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier SPEED_MODIFIER_ATTACKING = (new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.05D, AttributeModifier.Operation.ADDITION)).setSerialize(false);
   private int angerTime;
   private int playAngrySoundIn;
   private UUID lastHurtByUUID;

   public PigZombie(EntityType entityType, Level level) {
      super(entityType, level);
      this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
   }

   public void setLastHurtByMob(@Nullable LivingEntity lastHurtByMob) {
      super.setLastHurtByMob(lastHurtByMob);
      if(lastHurtByMob != null) {
         this.lastHurtByUUID = lastHurtByMob.getUUID();
      }

   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.targetSelector.addGoal(1, new PigZombie.PigZombieHurtByOtherGoal(this));
      this.targetSelector.addGoal(2, new PigZombie.PigZombieAngerTargetGoal(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
   }

   protected boolean convertsInWater() {
      return false;
   }

   protected void customServerAiStep() {
      AttributeInstance var1 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      LivingEntity var2 = this.getLastHurtByMob();
      if(this.isAngry()) {
         if(!this.isBaby() && !var1.hasModifier(SPEED_MODIFIER_ATTACKING)) {
            var1.addModifier(SPEED_MODIFIER_ATTACKING);
         }

         --this.angerTime;
         LivingEntity var3 = var2 != null?var2:this.getTarget();
         if(!this.isAngry() && var3 != null) {
            if(!this.canSee(var3)) {
               this.setLastHurtByMob((LivingEntity)null);
               this.setTarget((LivingEntity)null);
            } else {
               this.angerTime = this.getAngerTime();
            }
         }
      } else if(var1.hasModifier(SPEED_MODIFIER_ATTACKING)) {
         var1.removeModifier(SPEED_MODIFIER_ATTACKING);
      }

      if(this.playAngrySoundIn > 0 && --this.playAngrySoundIn == 0) {
         this.playSound(SoundEvents.ZOMBIE_PIGMAN_ANGRY, this.getSoundVolume() * 2.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.8F);
      }

      if(this.isAngry() && this.lastHurtByUUID != null && var2 == null) {
         Player var3 = this.level.getPlayerByUUID(this.lastHurtByUUID);
         this.setLastHurtByMob(var3);
         this.lastHurtByPlayer = var3;
         this.lastHurtByPlayerTime = this.getLastHurtByMobTimestamp();
      }

      super.customServerAiStep();
   }

   public static boolean checkPigZombieSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return levelAccessor.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean checkSpawnObstruction(LevelReader levelReader) {
      return levelReader.isUnobstructed(this) && !levelReader.containsAnyLiquid(this.getBoundingBox());
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putShort("Anger", (short)this.angerTime);
      if(this.lastHurtByUUID != null) {
         compoundTag.putString("HurtBy", this.lastHurtByUUID.toString());
      } else {
         compoundTag.putString("HurtBy", "");
      }

   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.angerTime = compoundTag.getShort("Anger");
      String var2 = compoundTag.getString("HurtBy");
      if(!var2.isEmpty()) {
         this.lastHurtByUUID = UUID.fromString(var2);
         Player var3 = this.level.getPlayerByUUID(this.lastHurtByUUID);
         this.setLastHurtByMob(var3);
         if(var3 != null) {
            this.lastHurtByPlayer = var3;
            this.lastHurtByPlayerTime = this.getLastHurtByMobTimestamp();
         }
      }

   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else {
         Entity var3 = damageSource.getEntity();
         if(var3 instanceof Player && !((Player)var3).isCreative() && this.canSee(var3)) {
            this.makeAngry(var3);
         }

         return super.hurt(damageSource, var2);
      }
   }

   private boolean makeAngry(Entity lastHurtByMob) {
      this.angerTime = this.getAngerTime();
      this.playAngrySoundIn = this.random.nextInt(40);
      if(lastHurtByMob instanceof LivingEntity) {
         this.setLastHurtByMob((LivingEntity)lastHurtByMob);
      }

      return true;
   }

   private int getAngerTime() {
      return 400 + this.random.nextInt(400);
   }

   private boolean isAngry() {
      return this.angerTime > 0;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_PIGMAN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.ZOMBIE_PIGMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_PIGMAN_DEATH;
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      return false;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   public boolean isPreventingPlayerRest(Player player) {
      return this.isAngry();
   }

   static class PigZombieAngerTargetGoal extends NearestAttackableTargetGoal {
      public PigZombieAngerTargetGoal(PigZombie pigZombie) {
         super(pigZombie, Player.class, true);
      }

      public boolean canUse() {
         return ((PigZombie)this.mob).isAngry() && super.canUse();
      }
   }

   static class PigZombieHurtByOtherGoal extends HurtByTargetGoal {
      public PigZombieHurtByOtherGoal(PigZombie pigZombie) {
         super(pigZombie, new Class[0]);
         this.setAlertOthers(new Class[]{Zombie.class});
      }

      protected void alertOther(Mob mob, LivingEntity livingEntity) {
         if(mob instanceof PigZombie && this.mob.canSee(livingEntity) && ((PigZombie)mob).makeAngry(livingEntity)) {
            mob.setTarget(livingEntity);
         }

      }
   }
}
