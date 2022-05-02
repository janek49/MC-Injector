package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;

public class DoNothing extends Behavior {
   public DoNothing(int var1, int var2) {
      super(ImmutableMap.of(), var1, var2);
   }

   protected boolean canStillUse(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      return true;
   }
}
