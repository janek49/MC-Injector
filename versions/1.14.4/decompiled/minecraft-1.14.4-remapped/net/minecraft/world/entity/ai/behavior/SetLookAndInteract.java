package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityPosWrapper;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class SetLookAndInteract extends Behavior {
   private final EntityType type;
   private final int interactionRangeSqr;
   private final Predicate targetFilter;
   private final Predicate selfFilter;

   public SetLookAndInteract(EntityType type, int var2, Predicate selfFilter, Predicate targetFilter) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.type = type;
      this.interactionRangeSqr = var2 * var2;
      this.targetFilter = targetFilter;
      this.selfFilter = selfFilter;
   }

   public SetLookAndInteract(EntityType entityType, int var2) {
      this(entityType, var2, (livingEntity) -> {
         return true;
      }, (livingEntity) -> {
         return true;
      });
   }

   public boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      return this.selfFilter.test(livingEntity) && this.getVisibleEntities(livingEntity).stream().anyMatch(this::isMatchingTarget);
   }

   public void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      super.start(serverLevel, livingEntity, var3);
      Brain<?> var5 = livingEntity.getBrain();
      var5.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((list) -> {
         list.stream().filter((var2) -> {
            return var2.distanceToSqr(livingEntity) <= (double)this.interactionRangeSqr;
         }).filter(this::isMatchingTarget).findFirst().ifPresent((livingEntity) -> {
            var5.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object)livingEntity);
            var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(livingEntity)));
         });
      });
   }

   private boolean isMatchingTarget(LivingEntity livingEntity) {
      return this.type.equals(livingEntity.getType()) && this.targetFilter.test(livingEntity);
   }

   private List getVisibleEntities(LivingEntity livingEntity) {
      return (List)livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get();
   }
}
