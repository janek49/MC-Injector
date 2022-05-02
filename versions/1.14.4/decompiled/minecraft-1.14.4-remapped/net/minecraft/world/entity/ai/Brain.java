package net.minecraft.world.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Serializable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;

public class Brain implements Serializable {
   private final Map memories = Maps.newHashMap();
   private final Map sensors = Maps.newLinkedHashMap();
   private final Map availableGoalsByPriority = Maps.newTreeMap();
   private Schedule schedule = Schedule.EMPTY;
   private final Map activityRequirements = Maps.newHashMap();
   private Set coreActivities = Sets.newHashSet();
   private final Set activeActivities = Sets.newHashSet();
   private Activity defaultActivity = Activity.IDLE;
   private long lastScheduleUpdate = -9999L;

   public Brain(Collection var1, Collection var2, Dynamic dynamic) {
      var1.forEach((memoryModuleType) -> {
         Optional var10000 = (Optional)this.memories.put(memoryModuleType, Optional.empty());
      });
      var2.forEach((sensorType) -> {
         Sensor var10000 = (Sensor)this.sensors.put(sensorType, sensorType.create());
      });
      this.sensors.values().forEach((sensor) -> {
         for(MemoryModuleType<?> var3 : sensor.requires()) {
            this.memories.put(var3, Optional.empty());
         }

      });

      for(Entry<Dynamic<T>, Dynamic<T>> var5 : dynamic.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
         this.readMemory((MemoryModuleType)Registry.MEMORY_MODULE_TYPE.get(new ResourceLocation(((Dynamic)var5.getKey()).asString(""))), (Dynamic)var5.getValue());
      }

   }

   public boolean hasMemoryValue(MemoryModuleType memoryModuleType) {
      return this.checkMemory(memoryModuleType, MemoryStatus.VALUE_PRESENT);
   }

   private void readMemory(MemoryModuleType memoryModuleType, Dynamic dynamic) {
      this.setMemory(memoryModuleType, ((Function)memoryModuleType.getDeserializer().orElseThrow(RuntimeException::<init>)).apply(dynamic));
   }

   public void eraseMemory(MemoryModuleType memoryModuleType) {
      this.setMemory(memoryModuleType, Optional.empty());
   }

   public void setMemory(MemoryModuleType memoryModuleType, @Nullable Object object) {
      this.setMemory(memoryModuleType, Optional.ofNullable(object));
   }

   public void setMemory(MemoryModuleType memoryModuleType, Optional optional) {
      if(this.memories.containsKey(memoryModuleType)) {
         if(optional.isPresent() && this.isEmptyCollection(optional.get())) {
            this.eraseMemory(memoryModuleType);
         } else {
            this.memories.put(memoryModuleType, optional);
         }
      }

   }

   public Optional getMemory(MemoryModuleType memoryModuleType) {
      return (Optional)this.memories.get(memoryModuleType);
   }

   public boolean checkMemory(MemoryModuleType memoryModuleType, MemoryStatus memoryStatus) {
      Optional<?> var3 = (Optional)this.memories.get(memoryModuleType);
      return var3 == null?false:memoryStatus == MemoryStatus.REGISTERED || memoryStatus == MemoryStatus.VALUE_PRESENT && var3.isPresent() || memoryStatus == MemoryStatus.VALUE_ABSENT && !var3.isPresent();
   }

   public Schedule getSchedule() {
      return this.schedule;
   }

   public void setSchedule(Schedule schedule) {
      this.schedule = schedule;
   }

   public void setCoreActivities(Set coreActivities) {
      this.coreActivities = coreActivities;
   }

   @Deprecated
   public Stream getRunningBehaviorsStream() {
      return this.availableGoalsByPriority.values().stream().flatMap((map) -> {
         return map.values().stream();
      }).flatMap(Collection::stream).filter((behavior) -> {
         return behavior.getStatus() == Behavior.Status.RUNNING;
      });
   }

   public void setActivity(Activity activity) {
      this.activeActivities.clear();
      this.activeActivities.addAll(this.coreActivities);
      boolean var2 = this.activityRequirements.keySet().contains(activity) && this.activityRequirementsAreMet(activity);
      this.activeActivities.add(var2?activity:this.defaultActivity);
   }

   public void updateActivity(long var1, long lastScheduleUpdate) {
      if(lastScheduleUpdate - this.lastScheduleUpdate > 20L) {
         this.lastScheduleUpdate = lastScheduleUpdate;
         Activity var5 = this.getSchedule().getActivityAt((int)(var1 % 24000L));
         if(!this.activeActivities.contains(var5)) {
            this.setActivity(var5);
         }
      }

   }

   public void setDefaultActivity(Activity defaultActivity) {
      this.defaultActivity = defaultActivity;
   }

   public void addActivity(Activity activity, ImmutableList immutableList) {
      this.addActivity(activity, immutableList, ImmutableSet.of());
   }

   public void addActivity(Activity activity, ImmutableList immutableList, Set set) {
      this.activityRequirements.put(activity, set);
      immutableList.forEach((pair) -> {
         ((Set)((Map)this.availableGoalsByPriority.computeIfAbsent(pair.getFirst(), (integer) -> {
            return Maps.newHashMap();
         })).computeIfAbsent(activity, (activity) -> {
            return Sets.newLinkedHashSet();
         })).add(pair.getSecond());
      });
   }

   public boolean isActive(Activity activity) {
      return this.activeActivities.contains(activity);
   }

   public Brain copyWithoutGoals() {
      Brain<E> brain = new Brain(this.memories.keySet(), this.sensors.keySet(), new Dynamic(NbtOps.INSTANCE, new CompoundTag()));
      this.memories.forEach((memoryModuleType, optional) -> {
         optional.ifPresent((object) -> {
            Optional var10000 = (Optional)brain.memories.put(memoryModuleType, Optional.of(object));
         });
      });
      return brain;
   }

   public void tick(ServerLevel serverLevel, LivingEntity livingEntity) {
      this.tickEachSensor(serverLevel, livingEntity);
      this.startEachNonRunningBehavior(serverLevel, livingEntity);
      this.tickEachRunningBehavior(serverLevel, livingEntity);
   }

   public void stopAll(ServerLevel serverLevel, LivingEntity livingEntity) {
      long var3 = livingEntity.level.getGameTime();
      this.getRunningBehaviorsStream().forEach((behavior) -> {
         behavior.doStop(serverLevel, livingEntity, var3);
      });
   }

   public Object serialize(DynamicOps dynamicOps) {
      T object = dynamicOps.createMap((Map)this.memories.entrySet().stream().filter((map$Entry) -> {
         return ((MemoryModuleType)map$Entry.getKey()).getDeserializer().isPresent() && ((Optional)map$Entry.getValue()).isPresent();
      }).map((map$Entry) -> {
         return Pair.of(dynamicOps.createString(Registry.MEMORY_MODULE_TYPE.getKey(map$Entry.getKey()).toString()), ((Serializable)((Optional)map$Entry.getValue()).get()).serialize(dynamicOps));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("memories"), object));
   }

   private void tickEachSensor(ServerLevel serverLevel, LivingEntity livingEntity) {
      this.sensors.values().forEach((sensor) -> {
         sensor.tick(serverLevel, livingEntity);
      });
   }

   private void startEachNonRunningBehavior(ServerLevel serverLevel, LivingEntity livingEntity) {
      long var3 = serverLevel.getGameTime();
      this.availableGoalsByPriority.values().stream().flatMap((map) -> {
         return map.entrySet().stream();
      }).filter((map$Entry) -> {
         return this.activeActivities.contains(map$Entry.getKey());
      }).map(Entry::getValue).flatMap(Collection::stream).filter((behavior) -> {
         return behavior.getStatus() == Behavior.Status.STOPPED;
      }).forEach((behavior) -> {
         behavior.tryStart(serverLevel, livingEntity, var3);
      });
   }

   private void tickEachRunningBehavior(ServerLevel serverLevel, LivingEntity livingEntity) {
      long var3 = serverLevel.getGameTime();
      this.getRunningBehaviorsStream().forEach((behavior) -> {
         behavior.tickOrStop(serverLevel, livingEntity, var3);
      });
   }

   private boolean activityRequirementsAreMet(Activity activity) {
      return ((Set)this.activityRequirements.get(activity)).stream().allMatch((pair) -> {
         MemoryModuleType<?> var2 = (MemoryModuleType)pair.getFirst();
         MemoryStatus var3 = (MemoryStatus)pair.getSecond();
         return this.checkMemory(var2, var3);
      });
   }

   private boolean isEmptyCollection(Object object) {
      return object instanceof Collection && ((Collection)object).isEmpty();
   }
}
