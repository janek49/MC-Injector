package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;

public class UpdateActivityFromSchedule extends Behavior {
   public UpdateActivityFromSchedule() {
      super(ImmutableMap.of());
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      livingEntity.getBrain().updateActivity(serverLevel.getDayTime(), serverLevel.getGameTime());
   }
}
