package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.MoveToSkySeeingSpot;
import net.minecraft.world.entity.raid.Raid;

public class GoOutsideToCelebrate extends MoveToSkySeeingSpot {
   public GoOutsideToCelebrate(float f) {
      super(f);
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      Raid var3 = serverLevel.getRaidAt(new BlockPos(livingEntity));
      return var3 != null && var3.isVictory() && super.checkExtraStartConditions(serverLevel, livingEntity);
   }
}
