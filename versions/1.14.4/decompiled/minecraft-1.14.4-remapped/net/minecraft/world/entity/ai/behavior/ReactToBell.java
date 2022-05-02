package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class ReactToBell extends Behavior {
   public ReactToBell() {
      super(ImmutableMap.of(MemoryModuleType.HEARD_BELL_TIME, MemoryStatus.VALUE_PRESENT));
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      Brain<?> var5 = livingEntity.getBrain();
      Raid var6 = serverLevel.getRaidAt(new BlockPos(livingEntity));
      if(var6 == null) {
         var5.setActivity(Activity.HIDE);
      }

   }
}
