package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class DummySensor extends Sensor {
   protected void doTick(ServerLevel serverLevel, LivingEntity livingEntity) {
   }

   public Set requires() {
      return ImmutableSet.of();
   }
}
