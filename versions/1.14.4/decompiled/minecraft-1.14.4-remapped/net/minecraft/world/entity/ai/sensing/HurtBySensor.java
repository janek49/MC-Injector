package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class HurtBySensor extends Sensor {
   protected void doTick(ServerLevel serverLevel, LivingEntity livingEntity) {
      Brain<?> var3 = livingEntity.getBrain();
      if(livingEntity.getLastDamageSource() != null) {
         var3.setMemory(MemoryModuleType.HURT_BY, (Object)livingEntity.getLastDamageSource());
         Entity var4 = ((DamageSource)var3.getMemory(MemoryModuleType.HURT_BY).get()).getEntity();
         if(var4 instanceof LivingEntity) {
            var3.setMemory(MemoryModuleType.HURT_BY_ENTITY, (Object)((LivingEntity)var4));
         }
      } else {
         var3.eraseMemory(MemoryModuleType.HURT_BY);
      }

   }

   public Set requires() {
      return ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
   }
}
