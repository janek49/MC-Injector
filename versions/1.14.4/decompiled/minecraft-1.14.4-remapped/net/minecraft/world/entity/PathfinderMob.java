package net.minecraft.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public abstract class PathfinderMob extends Mob {
   protected PathfinderMob(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public float getWalkTargetValue(BlockPos blockPos) {
      return this.getWalkTargetValue(blockPos, this.level);
   }

   public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
      return 0.0F;
   }

   public boolean checkSpawnRules(LevelAccessor levelAccessor, MobSpawnType mobSpawnType) {
      return this.getWalkTargetValue(new BlockPos(this.x, this.getBoundingBox().minY, this.z), levelAccessor) >= 0.0F;
   }

   public boolean isPathFinding() {
      return !this.getNavigation().isDone();
   }

   protected void tickLeash() {
      super.tickLeash();
      Entity var1 = this.getLeashHolder();
      if(var1 != null && var1.level == this.level) {
         this.restrictTo(new BlockPos(var1), 5);
         float var2 = this.distanceTo(var1);
         if(this instanceof TamableAnimal && ((TamableAnimal)this).isSitting()) {
            if(var2 > 10.0F) {
               this.dropLeash(true, true);
            }

            return;
         }

         this.onLeashDistance(var2);
         if(var2 > 10.0F) {
            this.dropLeash(true, true);
            this.goalSelector.disableControlFlag(Goal.Flag.MOVE);
         } else if(var2 > 6.0F) {
            double var3 = (var1.x - this.x) / (double)var2;
            double var5 = (var1.y - this.y) / (double)var2;
            double var7 = (var1.z - this.z) / (double)var2;
            this.setDeltaMovement(this.getDeltaMovement().add(Math.copySign(var3 * var3 * 0.4D, var3), Math.copySign(var5 * var5 * 0.4D, var5), Math.copySign(var7 * var7 * 0.4D, var7)));
         } else {
            this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
            float var3 = 2.0F;
            Vec3 var4 = (new Vec3(var1.x - this.x, var1.y - this.y, var1.z - this.z)).normalize().scale((double)Math.max(var2 - 2.0F, 0.0F));
            this.getNavigation().moveTo(this.x + var4.x, this.y + var4.y, this.z + var4.z, this.followLeashSpeed());
         }
      }

   }

   protected double followLeashSpeed() {
      return 1.0D;
   }

   protected void onLeashDistance(float f) {
   }
}
