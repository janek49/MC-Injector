package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;

public class Swim extends Behavior {
   private final float height;
   private final float chance;

   public Swim(float height, float chance) {
      super(ImmutableMap.of());
      this.height = height;
      this.chance = chance;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Mob mob) {
      return mob.isInWater() && mob.getWaterHeight() > (double)this.height || mob.isInLava();
   }

   protected boolean canStillUse(ServerLevel serverLevel, Mob mob, long var3) {
      return this.checkExtraStartConditions(serverLevel, mob);
   }

   protected void tick(ServerLevel serverLevel, Mob mob, long var3) {
      if(mob.getRandom().nextFloat() < this.chance) {
         mob.getJumpControl().jump();
      }

   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Mob)var2, var3);
   }
}
