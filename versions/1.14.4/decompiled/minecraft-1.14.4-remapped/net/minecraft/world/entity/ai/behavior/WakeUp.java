package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.schedule.Activity;

public class WakeUp extends Behavior {
   public WakeUp() {
      super(ImmutableMap.of());
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      return !livingEntity.getBrain().isActive(Activity.REST) && livingEntity.isSleeping();
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      livingEntity.stopSleeping();
   }
}
