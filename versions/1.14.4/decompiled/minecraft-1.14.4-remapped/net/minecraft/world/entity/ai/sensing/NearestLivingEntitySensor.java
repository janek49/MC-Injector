package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class NearestLivingEntitySensor extends Sensor {
   private static final TargetingConditions TARGETING = (new TargetingConditions()).range(16.0D).allowSameTeam().allowNonAttackable().allowUnseeable();

   protected void doTick(ServerLevel serverLevel, LivingEntity livingEntity) {
      List<LivingEntity> var3 = serverLevel.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(16.0D, 16.0D, 16.0D), (var1) -> {
         return var1 != livingEntity && var1.isAlive();
      });
      livingEntity.getClass();
      var3.sort(Comparator.comparingDouble(livingEntity::distanceToSqr));
      Brain<?> var4 = livingEntity.getBrain();
      var4.setMemory(MemoryModuleType.LIVING_ENTITIES, (Object)var3);
      MemoryModuleType var10001 = MemoryModuleType.VISIBLE_LIVING_ENTITIES;
      Stream var10002 = var3.stream().filter((var1) -> {
         return TARGETING.test(livingEntity, var1);
      });
      livingEntity.getClass();
      var4.setMemory(var10001, var10002.filter(livingEntity::canSee).collect(Collectors.toList()));
   }

   public Set requires() {
      return ImmutableSet.of(MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES);
   }
}
