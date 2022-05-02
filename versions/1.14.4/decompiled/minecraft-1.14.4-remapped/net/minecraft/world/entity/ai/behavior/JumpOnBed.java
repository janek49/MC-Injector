package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class JumpOnBed extends Behavior {
   private final float speed;
   @Nullable
   private BlockPos targetBed;
   private int remainingTimeToReachBed;
   private int remainingJumps;
   private int remainingCooldownUntilNextJump;

   public JumpOnBed(float speed) {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_BED, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speed = speed;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Mob mob) {
      return mob.isBaby() && this.nearBed(serverLevel, mob);
   }

   protected void start(ServerLevel serverLevel, Mob mob, long var3) {
      super.start(serverLevel, mob, var3);
      this.getNearestBed(mob).ifPresent((targetBed) -> {
         this.targetBed = targetBed;
         this.remainingTimeToReachBed = 100;
         this.remainingJumps = 3 + serverLevel.random.nextInt(4);
         this.remainingCooldownUntilNextJump = 0;
         this.startWalkingTowardsBed(mob, targetBed);
      });
   }

   protected void stop(ServerLevel serverLevel, Mob mob, long var3) {
      super.stop(serverLevel, mob, var3);
      this.targetBed = null;
      this.remainingTimeToReachBed = 0;
      this.remainingJumps = 0;
      this.remainingCooldownUntilNextJump = 0;
   }

   protected boolean canStillUse(ServerLevel serverLevel, Mob mob, long var3) {
      return mob.isBaby() && this.targetBed != null && this.isBed(serverLevel, this.targetBed) && !this.tiredOfWalking(serverLevel, mob) && !this.tiredOfJumping(serverLevel, mob);
   }

   protected boolean timedOut(long l) {
      return false;
   }

   protected void tick(ServerLevel serverLevel, Mob mob, long var3) {
      if(!this.onOrOverBed(serverLevel, mob)) {
         --this.remainingTimeToReachBed;
      } else if(this.remainingCooldownUntilNextJump > 0) {
         --this.remainingCooldownUntilNextJump;
      } else {
         if(this.onBedSurface(serverLevel, mob)) {
            mob.getJumpControl().jump();
            --this.remainingJumps;
            this.remainingCooldownUntilNextJump = 5;
         }

      }
   }

   private void startWalkingTowardsBed(Mob mob, BlockPos blockPos) {
      mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(blockPos, this.speed, 0)));
   }

   private boolean nearBed(ServerLevel serverLevel, Mob mob) {
      return this.onOrOverBed(serverLevel, mob) || this.getNearestBed(mob).isPresent();
   }

   private boolean onOrOverBed(ServerLevel serverLevel, Mob mob) {
      BlockPos var3 = new BlockPos(mob);
      BlockPos var4 = var3.below();
      return this.isBed(serverLevel, var3) || this.isBed(serverLevel, var4);
   }

   private boolean onBedSurface(ServerLevel serverLevel, Mob mob) {
      return this.isBed(serverLevel, new BlockPos(mob));
   }

   private boolean isBed(ServerLevel serverLevel, BlockPos blockPos) {
      return serverLevel.getBlockState(blockPos).is(BlockTags.BEDS);
   }

   private Optional getNearestBed(Mob mob) {
      return mob.getBrain().getMemory(MemoryModuleType.NEAREST_BED);
   }

   private boolean tiredOfWalking(ServerLevel serverLevel, Mob mob) {
      return !this.onOrOverBed(serverLevel, mob) && this.remainingTimeToReachBed <= 0;
   }

   private boolean tiredOfJumping(ServerLevel serverLevel, Mob mob) {
      return this.onOrOverBed(serverLevel, mob) && this.remainingJumps <= 0;
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Mob)var2, var3);
   }
}
