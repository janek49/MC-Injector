package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityPosWrapper;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InteractWith extends Behavior {
   private final int maxDist;
   private final float speed;
   private final EntityType type;
   private final int interactionRangeSqr;
   private final Predicate targetFilter;
   private final Predicate selfFilter;
   private final MemoryModuleType memory;

   public InteractWith(EntityType type, int var2, Predicate selfFilter, Predicate targetFilter, MemoryModuleType memory, float speed, int maxDist) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, memory, MemoryStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.type = type;
      this.speed = speed;
      this.interactionRangeSqr = var2 * var2;
      this.maxDist = maxDist;
      this.targetFilter = targetFilter;
      this.selfFilter = selfFilter;
      this.memory = memory;
   }

   public static InteractWith of(EntityType entityType, int var1, MemoryModuleType memoryModuleType, float var3, int var4) {
      return new InteractWith(entityType, var1, (livingEntity) -> {
         return true;
      }, (livingEntity) -> {
         return true;
      }, memoryModuleType, var3, var4);
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      return this.selfFilter.test(livingEntity) && ((List)livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get()).stream().anyMatch((livingEntity) -> {
         return this.type.equals(livingEntity.getType()) && this.targetFilter.test(livingEntity);
      });
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      Brain<?> var5 = livingEntity.getBrain();
      var5.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((list) -> {
         list.stream().filter((livingEntity) -> {
            return this.type.equals(livingEntity.getType());
         }).map((livingEntity) -> {
            return livingEntity;
         }).filter((var2) -> {
            return var2.distanceToSqr(livingEntity) <= (double)this.interactionRangeSqr;
         }).filter(this.targetFilter).findFirst().ifPresent((livingEntity) -> {
            var5.setMemory(this.memory, (Object)livingEntity);
            var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(livingEntity)));
            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new EntityPosWrapper(livingEntity), this.speed, this.maxDist)));
         });
      });
   }
}
