package net.minecraft.world.entity.animal.horse;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class TraderLlama extends Llama {
   private int despawnDelay = '뭿';

   public TraderLlama(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public boolean isTraderLlama() {
      return true;
   }

   protected Llama makeBabyLlama() {
      return (Llama)EntityType.TRADER_LLAMA.create(this.level);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("DespawnDelay", this.despawnDelay);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("DespawnDelay", 99)) {
         this.despawnDelay = compoundTag.getInt("DespawnDelay");
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
      this.targetSelector.addGoal(1, new TraderLlama.TraderLlamaDefendWanderingTraderGoal(this));
   }

   protected void doPlayerRide(Player player) {
      Entity var2 = this.getLeashHolder();
      if(!(var2 instanceof WanderingTrader)) {
         super.doPlayerRide(player);
      }
   }

   public void aiStep() {
      super.aiStep();
      if(!this.level.isClientSide) {
         this.maybeDespawn();
      }

   }

   private void maybeDespawn() {
      if(this.canDespawn()) {
         this.despawnDelay = this.isLeashedToWanderingTrader()?((WanderingTrader)this.getLeashHolder()).getDespawnDelay() - 1:this.despawnDelay - 1;
         if(this.despawnDelay <= 0) {
            this.dropLeash(true, false);
            this.remove();
         }

      }
   }

   private boolean canDespawn() {
      return !this.isTamed() && !this.isLeashedToSomethingOtherThanTheWanderingTrader() && !this.hasOnePlayerPassenger();
   }

   private boolean isLeashedToWanderingTrader() {
      return this.getLeashHolder() instanceof WanderingTrader;
   }

   private boolean isLeashedToSomethingOtherThanTheWanderingTrader() {
      return this.isLeashed() && !this.isLeashedToWanderingTrader();
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      SpawnGroupData var6 = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
      if(mobSpawnType == MobSpawnType.EVENT) {
         this.setAge(0);
      }

      return var6;
   }

   public class TraderLlamaDefendWanderingTraderGoal extends TargetGoal {
      private final Llama llama;
      private LivingEntity ownerLastHurtBy;
      private int timestamp;

      public TraderLlamaDefendWanderingTraderGoal(Llama llama) {
         super(llama, false);
         this.llama = llama;
         this.setFlags(EnumSet.of(Goal.Flag.TARGET));
      }

      public boolean canUse() {
         if(!this.llama.isLeashed()) {
            return false;
         } else {
            Entity var1 = this.llama.getLeashHolder();
            if(!(var1 instanceof WanderingTrader)) {
               return false;
            } else {
               WanderingTrader var2 = (WanderingTrader)var1;
               this.ownerLastHurtBy = var2.getLastHurtByMob();
               int var3 = var2.getLastHurtByMobTimestamp();
               return var3 != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
            }
         }
      }

      public void start() {
         this.mob.setTarget(this.ownerLastHurtBy);
         Entity var1 = this.llama.getLeashHolder();
         if(var1 instanceof WanderingTrader) {
            this.timestamp = ((WanderingTrader)var1).getLastHurtByMobTimestamp();
         }

         super.start();
      }
   }
}
