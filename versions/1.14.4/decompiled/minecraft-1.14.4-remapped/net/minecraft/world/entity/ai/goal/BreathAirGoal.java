package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class BreathAirGoal extends Goal {
   private final PathfinderMob mob;

   public BreathAirGoal(PathfinderMob mob) {
      this.mob = mob;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      return this.mob.getAirSupply() < 140;
   }

   public boolean canContinueToUse() {
      return this.canUse();
   }

   public boolean isInterruptable() {
      return false;
   }

   public void start() {
      this.findAirPosition();
   }

   private void findAirPosition() {
      Iterable<BlockPos> var1 = BlockPos.betweenClosed(Mth.floor(this.mob.x - 1.0D), Mth.floor(this.mob.y), Mth.floor(this.mob.z - 1.0D), Mth.floor(this.mob.x + 1.0D), Mth.floor(this.mob.y + 8.0D), Mth.floor(this.mob.z + 1.0D));
      BlockPos var2 = null;

      for(BlockPos var4 : var1) {
         if(this.givesAir(this.mob.level, var4)) {
            var2 = var4;
            break;
         }
      }

      if(var2 == null) {
         var2 = new BlockPos(this.mob.x, this.mob.y + 8.0D, this.mob.z);
      }

      this.mob.getNavigation().moveTo((double)var2.getX(), (double)(var2.getY() + 1), (double)var2.getZ(), 1.0D);
   }

   public void tick() {
      this.findAirPosition();
      this.mob.moveRelative(0.02F, new Vec3((double)this.mob.xxa, (double)this.mob.yya, (double)this.mob.zza));
      this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
   }

   private boolean givesAir(LevelReader levelReader, BlockPos blockPos) {
      BlockState var3 = levelReader.getBlockState(blockPos);
      return (levelReader.getFluidState(blockPos).isEmpty() || var3.getBlock() == Blocks.BUBBLE_COLUMN) && var3.isPathfindable(levelReader, blockPos, PathComputationType.LAND);
   }
}
