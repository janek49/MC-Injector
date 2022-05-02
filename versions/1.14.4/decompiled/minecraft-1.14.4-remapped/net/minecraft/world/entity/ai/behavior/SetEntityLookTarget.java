package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityPosWrapper;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class SetEntityLookTarget extends Behavior {
   private final Predicate predicate;
   private final float maxDistSqr;

   public SetEntityLookTarget(MobCategory mobCategory, float var2) {
      this((livingEntity) -> {
         return mobCategory.equals(livingEntity.getType().getCategory());
      }, var2);
   }

   public SetEntityLookTarget(EntityType entityType, float var2) {
      this((livingEntity) -> {
         return entityType.equals(livingEntity.getType());
      }, var2);
   }

   public SetEntityLookTarget(Predicate predicate, float var2) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.predicate = predicate;
      this.maxDistSqr = var2 * var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      return ((List)livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get()).stream().anyMatch(this.predicate);
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      Brain<?> var5 = livingEntity.getBrain();
      var5.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((list) -> {
         list.stream().filter(this.predicate).filter((var2) -> {
            return var2.distanceToSqr(livingEntity) <= (double)this.maxDistSqr;
         }).findFirst().ifPresent((livingEntity) -> {
            var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(livingEntity)));
         });
      });
   }
}
