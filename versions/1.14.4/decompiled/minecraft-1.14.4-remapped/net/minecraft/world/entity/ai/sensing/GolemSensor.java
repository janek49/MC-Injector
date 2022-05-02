package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class GolemSensor extends Sensor {
   public GolemSensor() {
      this(200);
   }

   public GolemSensor(int i) {
      super(i);
   }

   protected void doTick(ServerLevel serverLevel, LivingEntity livingEntity) {
      checkForNearbyGolem(serverLevel.getGameTime(), livingEntity);
   }

   public Set requires() {
      return ImmutableSet.of(MemoryModuleType.LIVING_ENTITIES);
   }

   public static void checkForNearbyGolem(long var0, LivingEntity livingEntity) {
      Brain<?> var3 = livingEntity.getBrain();
      Optional<List<LivingEntity>> var4 = var3.getMemory(MemoryModuleType.LIVING_ENTITIES);
      if(var4.isPresent()) {
         boolean var5 = ((List)var4.get()).stream().anyMatch((livingEntity) -> {
            return livingEntity.getType().equals(EntityType.IRON_GOLEM);
         });
         if(var5) {
            var3.setMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, (Object)Long.valueOf(var0));
         }

      }
   }
}
