package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityPosWrapper;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class SocializeAtBell extends Behavior {
   public SocializeAtBell() {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      Brain<?> var3 = livingEntity.getBrain();
      Optional<GlobalPos> var4 = var3.getMemory(MemoryModuleType.MEETING_POINT);
      return serverLevel.getRandom().nextInt(100) == 0 && var4.isPresent() && Objects.equals(serverLevel.getDimension().getType(), ((GlobalPos)var4.get()).dimension()) && ((GlobalPos)var4.get()).pos().closerThan(livingEntity.position(), 4.0D) && ((List)var3.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get()).stream().anyMatch((livingEntity) -> {
         return EntityType.VILLAGER.equals(livingEntity.getType());
      });
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      Brain<?> var5 = livingEntity.getBrain();
      var5.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((list) -> {
         list.stream().filter((livingEntity) -> {
            return EntityType.VILLAGER.equals(livingEntity.getType());
         }).filter((var1) -> {
            return var1.distanceToSqr(livingEntity) <= 32.0D;
         }).findFirst().ifPresent((livingEntity) -> {
            var5.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object)livingEntity);
            var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(livingEntity)));
            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new EntityPosWrapper(livingEntity), 0.3F, 1)));
         });
      });
   }
}
