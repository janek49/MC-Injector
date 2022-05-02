package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class TryFindWaterGoal extends Goal {
   private final PathfinderMob mob;

   public TryFindWaterGoal(PathfinderMob mob) {
      this.mob = mob;
   }

   public boolean canUse() {
      return this.mob.onGround && !this.mob.level.getFluidState(new BlockPos(this.mob)).is(FluidTags.WATER);
   }

   public void start() {
      BlockPos var1 = null;

      for(BlockPos var4 : BlockPos.betweenClosed(Mth.floor(this.mob.x - 2.0D), Mth.floor(this.mob.y - 2.0D), Mth.floor(this.mob.z - 2.0D), Mth.floor(this.mob.x + 2.0D), Mth.floor(this.mob.y), Mth.floor(this.mob.z + 2.0D))) {
         if(this.mob.level.getFluidState(var4).is(FluidTags.WATER)) {
            var1 = var4;
            break;
         }
      }

      if(var1 != null) {
         this.mob.getMoveControl().setWantedPosition((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), 1.0D);
      }

   }
}
