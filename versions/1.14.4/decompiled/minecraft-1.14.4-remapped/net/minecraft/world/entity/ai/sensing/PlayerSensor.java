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
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;

public class PlayerSensor extends Sensor {
   protected void doTick(ServerLevel serverLevel, LivingEntity livingEntity) {
      Stream var10000 = serverLevel.players().stream().filter(EntitySelector.NO_SPECTATORS).filter((serverPlayer) -> {
         return livingEntity.distanceToSqr(serverPlayer) < 256.0D;
      });
      livingEntity.getClass();
      List<Player> var3 = (List)var10000.sorted(Comparator.comparingDouble(livingEntity::distanceToSqr)).collect(Collectors.toList());
      Brain<?> var4 = livingEntity.getBrain();
      var4.setMemory(MemoryModuleType.NEAREST_PLAYERS, (Object)var3);
      MemoryModuleType var10001 = MemoryModuleType.NEAREST_VISIBLE_PLAYER;
      Stream var10002 = var3.stream();
      livingEntity.getClass();
      var4.setMemory(var10001, var10002.filter(livingEntity::canSee).findFirst());
   }

   public Set requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER);
   }
}
