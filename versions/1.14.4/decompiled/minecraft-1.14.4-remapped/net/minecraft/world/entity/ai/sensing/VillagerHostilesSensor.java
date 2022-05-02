package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class VillagerHostilesSensor extends Sensor {
   private static final ImmutableMap ACCEPTABLE_DISTANCE_FROM_HOSTILES = ImmutableMap.builder().put(EntityType.DROWNED, Float.valueOf(8.0F)).put(EntityType.EVOKER, Float.valueOf(12.0F)).put(EntityType.HUSK, Float.valueOf(8.0F)).put(EntityType.ILLUSIONER, Float.valueOf(12.0F)).put(EntityType.PILLAGER, Float.valueOf(15.0F)).put(EntityType.RAVAGER, Float.valueOf(12.0F)).put(EntityType.VEX, Float.valueOf(8.0F)).put(EntityType.VINDICATOR, Float.valueOf(10.0F)).put(EntityType.ZOMBIE, Float.valueOf(8.0F)).put(EntityType.ZOMBIE_VILLAGER, Float.valueOf(8.0F)).build();

   public Set requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
   }

   protected void doTick(ServerLevel serverLevel, LivingEntity livingEntity) {
      livingEntity.getBrain().setMemory(MemoryModuleType.NEAREST_HOSTILE, this.getNearestHostile(livingEntity));
   }

   private Optional getNearestHostile(LivingEntity livingEntity) {
      return this.getVisibleEntities(livingEntity).flatMap((list) -> {
         return list.stream().filter(this::isHostile).filter((var2) -> {
            return this.isClose(livingEntity, var2);
         }).min((var2, var3) -> {
            return this.compareMobDistance(livingEntity, var2, var3);
         });
      });
   }

   private Optional getVisibleEntities(LivingEntity livingEntity) {
      return livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
   }

   private int compareMobDistance(LivingEntity var1, LivingEntity var2, LivingEntity var3) {
      return Mth.floor(var2.distanceToSqr(var1) - var3.distanceToSqr(var1));
   }

   private boolean isClose(LivingEntity var1, LivingEntity var2) {
      float var3 = ((Float)ACCEPTABLE_DISTANCE_FROM_HOSTILES.get(var2.getType())).floatValue();
      return var2.distanceToSqr(var1) <= (double)(var3 * var3);
   }

   private boolean isHostile(LivingEntity livingEntity) {
      return ACCEPTABLE_DISTANCE_FROM_HOSTILES.containsKey(livingEntity.getType());
   }
}
