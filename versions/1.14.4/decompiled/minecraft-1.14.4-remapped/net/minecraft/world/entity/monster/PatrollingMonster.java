package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public abstract class PatrollingMonster extends Monster {
   private BlockPos patrolTarget;
   private boolean patrolLeader;
   private boolean patrolling;

   protected PatrollingMonster(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(4, new PatrollingMonster.LongDistancePatrolGoal(this, 0.7D, 0.595D));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      if(this.patrolTarget != null) {
         compoundTag.put("PatrolTarget", NbtUtils.writeBlockPos(this.patrolTarget));
      }

      compoundTag.putBoolean("PatrolLeader", this.patrolLeader);
      compoundTag.putBoolean("Patrolling", this.patrolling);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("PatrolTarget")) {
         this.patrolTarget = NbtUtils.readBlockPos(compoundTag.getCompound("PatrolTarget"));
      }

      this.patrolLeader = compoundTag.getBoolean("PatrolLeader");
      this.patrolling = compoundTag.getBoolean("Patrolling");
   }

   public double getRidingHeight() {
      return -0.45D;
   }

   public boolean canBeLeader() {
      return true;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      if(mobSpawnType != MobSpawnType.PATROL && mobSpawnType != MobSpawnType.EVENT && mobSpawnType != MobSpawnType.STRUCTURE && this.random.nextFloat() < 0.06F && this.canBeLeader()) {
         this.patrolLeader = true;
      }

      if(this.isPatrolLeader()) {
         this.setItemSlot(EquipmentSlot.HEAD, Raid.getLeaderBannerInstance());
         this.setDropChance(EquipmentSlot.HEAD, 2.0F);
      }

      if(mobSpawnType == MobSpawnType.PATROL) {
         this.patrolling = true;
      }

      return super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
   }

   public static boolean checkPatrollingMonsterSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return levelAccessor.getBrightness(LightLayer.BLOCK, blockPos) > 8?false:checkAnyLightMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random);
   }

   public boolean removeWhenFarAway(double d) {
      return !this.patrolling || d > 16384.0D;
   }

   public void setPatrolTarget(BlockPos patrolTarget) {
      this.patrolTarget = patrolTarget;
      this.patrolling = true;
   }

   public BlockPos getPatrolTarget() {
      return this.patrolTarget;
   }

   public boolean hasPatrolTarget() {
      return this.patrolTarget != null;
   }

   public void setPatrolLeader(boolean patrolLeader) {
      this.patrolLeader = patrolLeader;
      this.patrolling = true;
   }

   public boolean isPatrolLeader() {
      return this.patrolLeader;
   }

   public boolean canJoinPatrol() {
      return true;
   }

   public void findPatrolTarget() {
      this.patrolTarget = (new BlockPos(this)).offset(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
      this.patrolling = true;
   }

   protected boolean isPatrolling() {
      return this.patrolling;
   }

   public static class LongDistancePatrolGoal extends Goal {
      private final PatrollingMonster mob;
      private final double speedModifier;
      private final double leaderSpeedModifier;

      public LongDistancePatrolGoal(PatrollingMonster mob, double speedModifier, double leaderSpeedModifier) {
         this.mob = mob;
         this.speedModifier = speedModifier;
         this.leaderSpeedModifier = leaderSpeedModifier;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return this.mob.isPatrolling() && this.mob.getTarget() == null && !this.mob.isVehicle() && this.mob.hasPatrolTarget();
      }

      public void start() {
      }

      public void stop() {
      }

      public void tick() {
         boolean var1 = this.mob.isPatrolLeader();
         PathNavigation var2 = this.mob.getNavigation();
         if(var2.isDone()) {
            if(var1 && this.mob.getPatrolTarget().closerThan(this.mob.position(), 10.0D)) {
               this.mob.findPatrolTarget();
            } else {
               Vec3 var3 = new Vec3(this.mob.getPatrolTarget());
               Vec3 var4 = new Vec3(this.mob.x, this.mob.y, this.mob.z);
               Vec3 var5 = var4.subtract(var3);
               var3 = var5.yRot(90.0F).scale(0.4D).add(var3);
               Vec3 var6 = var3.subtract(var4).normalize().scale(10.0D).add(var4);
               BlockPos var7 = new BlockPos(var6);
               var7 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var7);
               if(!var2.moveTo((double)var7.getX(), (double)var7.getY(), (double)var7.getZ(), var1?this.leaderSpeedModifier:this.speedModifier)) {
                  this.moveRandomly();
               } else if(var1) {
                  for(PatrollingMonster var10 : this.mob.level.getEntitiesOfClass(PatrollingMonster.class, this.mob.getBoundingBox().inflate(16.0D), (patrollingMonster) -> {
                     return !patrollingMonster.isPatrolLeader() && patrollingMonster.canJoinPatrol();
                  })) {
                     var10.setPatrolTarget(var7);
                  }
               }
            }
         }

      }

      private void moveRandomly() {
         Random var1 = this.mob.getRandom();
         BlockPos var2 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (new BlockPos(this.mob)).offset(-8 + var1.nextInt(16), 0, -8 + var1.nextInt(16)));
         this.mob.getNavigation().moveTo((double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), this.speedModifier);
      }
   }
}
