package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class VillagerBabiesSensor extends Sensor {
   public Set requires() {
      return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
   }

   protected void doTick(ServerLevel serverLevel, LivingEntity livingEntity) {
      livingEntity.getBrain().setMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, (Object)this.getNearestVillagerBabies(livingEntity));
   }

   private List getNearestVillagerBabies(LivingEntity livingEntity) {
      return (List)this.getVisibleEntities(livingEntity).stream().filter(this::isVillagerBaby).collect(Collectors.toList());
   }

   private boolean isVillagerBaby(LivingEntity livingEntity) {
      return livingEntity.getType() == EntityType.VILLAGER && livingEntity.isBaby();
   }

   private List getVisibleEntities(LivingEntity livingEntity) {
      return (List)livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse(Lists.newArrayList());
   }
}
