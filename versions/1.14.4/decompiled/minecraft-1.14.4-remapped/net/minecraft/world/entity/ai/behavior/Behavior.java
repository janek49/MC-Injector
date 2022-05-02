package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public abstract class Behavior {
   private final Map entryCondition;
   private Behavior.Status status;
   private long endTimestamp;
   private final int minDuration;
   private final int maxDuration;

   public Behavior(Map map) {
      this(map, 60);
   }

   public Behavior(Map map, int var2) {
      this(map, var2, var2);
   }

   public Behavior(Map entryCondition, int minDuration, int maxDuration) {
      this.status = Behavior.Status.STOPPED;
      this.minDuration = minDuration;
      this.maxDuration = maxDuration;
      this.entryCondition = entryCondition;
   }

   public Behavior.Status getStatus() {
      return this.status;
   }

   public final boolean tryStart(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      if(this.hasRequiredMemories(livingEntity) && this.checkExtraStartConditions(serverLevel, livingEntity)) {
         this.status = Behavior.Status.RUNNING;
         int var5 = this.minDuration + serverLevel.getRandom().nextInt(this.maxDuration + 1 - this.minDuration);
         this.endTimestamp = var3 + (long)var5;
         this.start(serverLevel, livingEntity, var3);
         return true;
      } else {
         return false;
      }
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
   }

   public final void tickOrStop(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      if(!this.timedOut(var3) && this.canStillUse(serverLevel, livingEntity, var3)) {
         this.tick(serverLevel, livingEntity, var3);
      } else {
         this.doStop(serverLevel, livingEntity, var3);
      }

   }

   protected void tick(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
   }

   public final void doStop(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      this.status = Behavior.Status.STOPPED;
      this.stop(serverLevel, livingEntity, var3);
   }

   protected void stop(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
   }

   protected boolean canStillUse(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      return false;
   }

   protected boolean timedOut(long l) {
      return l > this.endTimestamp;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      return true;
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }

   private boolean hasRequiredMemories(LivingEntity livingEntity) {
      return this.entryCondition.entrySet().stream().allMatch((map$Entry) -> {
         MemoryModuleType<?> var2 = (MemoryModuleType)map$Entry.getKey();
         MemoryStatus var3 = (MemoryStatus)map$Entry.getValue();
         return livingEntity.getBrain().checkMemory(var2, var3);
      });
   }

   public static enum Status {
      STOPPED,
      RUNNING;
   }
}
