package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.LocateHidingPlace;
import net.minecraft.world.entity.raid.Raid;

public class LocateHidingPlaceDuringRaid extends LocateHidingPlace {
   public LocateHidingPlaceDuringRaid(int var1, float var2) {
      super(var1, var2, 1);
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      Raid var3 = serverLevel.getRaidAt(new BlockPos(livingEntity));
      return super.checkExtraStartConditions(serverLevel, livingEntity) && var3 != null && var3.isActive() && !var3.isVictory() && !var3.isLoss();
   }
}
