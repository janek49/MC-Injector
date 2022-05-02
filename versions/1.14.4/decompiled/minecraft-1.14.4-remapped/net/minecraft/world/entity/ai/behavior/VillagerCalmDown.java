package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.VillagerPanicTrigger;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class VillagerCalmDown extends Behavior {
   public VillagerCalmDown() {
      super(ImmutableMap.of());
   }

   protected void start(ServerLevel serverLevel, Villager villager, long var3) {
      boolean var5 = VillagerPanicTrigger.isHurt(villager) || VillagerPanicTrigger.hasHostile(villager) || isCloseToEntityThatHurtMe(villager);
      if(!var5) {
         villager.getBrain().eraseMemory(MemoryModuleType.HURT_BY);
         villager.getBrain().eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
         villager.getBrain().updateActivity(serverLevel.getDayTime(), serverLevel.getGameTime());
      }

   }

   private static boolean isCloseToEntityThatHurtMe(Villager villager) {
      return villager.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).filter((livingEntity) -> {
         return livingEntity.distanceToSqr(villager) <= 36.0D;
      }).isPresent();
   }
}
