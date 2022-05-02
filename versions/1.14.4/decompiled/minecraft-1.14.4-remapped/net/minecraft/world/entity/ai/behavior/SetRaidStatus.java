package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class SetRaidStatus extends Behavior {
   public SetRaidStatus() {
      super(ImmutableMap.of());
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      return serverLevel.random.nextInt(20) == 0;
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      Brain<?> var5 = livingEntity.getBrain();
      Raid var6 = serverLevel.getRaidAt(new BlockPos(livingEntity));
      if(var6 != null) {
         if(var6.hasFirstWaveSpawned() && !var6.isBetweenWaves()) {
            var5.setDefaultActivity(Activity.RAID);
            var5.setActivity(Activity.RAID);
         } else {
            var5.setDefaultActivity(Activity.PRE_RAID);
            var5.setActivity(Activity.PRE_RAID);
         }
      }

   }
}
