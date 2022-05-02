package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class LookAtTargetSink extends Behavior {
   public LookAtTargetSink(int var1, int var2) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_PRESENT), var1, var2);
   }

   protected boolean canStillUse(ServerLevel serverLevel, Mob mob, long var3) {
      return mob.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter((positionWrapper) -> {
         return positionWrapper.isVisible(mob);
      }).isPresent();
   }

   protected void stop(ServerLevel serverLevel, Mob mob, long var3) {
      mob.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void tick(ServerLevel serverLevel, Mob mob, long var3) {
      mob.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).ifPresent((positionWrapper) -> {
         mob.getLookControl().setLookAt(positionWrapper.getLookAtPos());
      });
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Mob)var2, var3);
   }
}
