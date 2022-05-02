package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class WallClimberNavigation extends GroundPathNavigation {
   private BlockPos pathToPosition;

   public WallClimberNavigation(Mob mob, Level level) {
      super(mob, level);
   }

   public Path createPath(BlockPos pathToPosition, int var2) {
      this.pathToPosition = pathToPosition;
      return super.createPath(pathToPosition, var2);
   }

   public Path createPath(Entity entity, int var2) {
      this.pathToPosition = new BlockPos(entity);
      return super.createPath(entity, var2);
   }

   public boolean moveTo(Entity entity, double speedModifier) {
      Path var4 = this.createPath((Entity)entity, 0);
      if(var4 != null) {
         return this.moveTo(var4, speedModifier);
      } else {
         this.pathToPosition = new BlockPos(entity);
         this.speedModifier = speedModifier;
         return true;
      }
   }

   public void tick() {
      if(!this.isDone()) {
         super.tick();
      } else {
         if(this.pathToPosition != null) {
            if(!this.pathToPosition.closerThan(this.mob.position(), (double)this.mob.getBbWidth()) && (this.mob.y <= (double)this.pathToPosition.getY() || !(new BlockPos((double)this.pathToPosition.getX(), this.mob.y, (double)this.pathToPosition.getZ())).closerThan(this.mob.position(), (double)this.mob.getBbWidth()))) {
               this.mob.getMoveControl().setWantedPosition((double)this.pathToPosition.getX(), (double)this.pathToPosition.getY(), (double)this.pathToPosition.getZ(), this.speedModifier);
            } else {
               this.pathToPosition = null;
            }
         }

      }
   }
}
