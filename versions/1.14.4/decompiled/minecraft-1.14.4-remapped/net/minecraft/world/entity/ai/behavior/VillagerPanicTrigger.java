package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;

public class VillagerPanicTrigger extends Behavior {
   public VillagerPanicTrigger() {
      super(ImmutableMap.of());
   }

   protected boolean canStillUse(ServerLevel serverLevel, Villager villager, long var3) {
      return isHurt(villager) || hasHostile(villager);
   }

   protected void start(ServerLevel serverLevel, Villager villager, long var3) {
      if(isHurt(villager) || hasHostile(villager)) {
         Brain<?> var5 = villager.getBrain();
         if(!var5.isActive(Activity.PANIC)) {
            var5.eraseMemory(MemoryModuleType.PATH);
            var5.eraseMemory(MemoryModuleType.WALK_TARGET);
            var5.eraseMemory(MemoryModuleType.LOOK_TARGET);
            var5.eraseMemory(MemoryModuleType.BREED_TARGET);
            var5.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
         }

         var5.setActivity(Activity.PANIC);
      }

   }

   protected void tick(ServerLevel serverLevel, Villager villager, long var3) {
      if(var3 % 100L == 0L) {
         villager.spawnGolemIfNeeded(var3, 3);
      }

   }

   public static boolean hasHostile(LivingEntity livingEntity) {
      return livingEntity.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_HOSTILE);
   }

   public static boolean isHurt(LivingEntity livingEntity) {
      return livingEntity.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Villager)var2, var3);
   }
}
