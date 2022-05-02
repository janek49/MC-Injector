package net.minecraft.world.entity.ai.navigation;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WaterBoundPathNavigation extends PathNavigation {
   private boolean allowBreaching;

   public WaterBoundPathNavigation(Mob mob, Level level) {
      super(mob, level);
   }

   protected PathFinder createPathFinder(int i) {
      this.allowBreaching = this.mob instanceof Dolphin;
      this.nodeEvaluator = new SwimNodeEvaluator(this.allowBreaching);
      return new PathFinder(this.nodeEvaluator, i);
   }

   protected boolean canUpdatePath() {
      return this.allowBreaching || this.isInLiquid();
   }

   protected Vec3 getTempMobPos() {
      return new Vec3(this.mob.x, this.mob.y + (double)this.mob.getBbHeight() * 0.5D, this.mob.z);
   }

   public void tick() {
      ++this.tick;
      if(this.hasDelayedRecomputation) {
         this.recomputePath();
      }

      if(!this.isDone()) {
         if(this.canUpdatePath()) {
            this.updatePath();
         } else if(this.path != null && this.path.getIndex() < this.path.getSize()) {
            Vec3 var1 = this.path.getPos(this.mob, this.path.getIndex());
            if(Mth.floor(this.mob.x) == Mth.floor(var1.x) && Mth.floor(this.mob.y) == Mth.floor(var1.y) && Mth.floor(this.mob.z) == Mth.floor(var1.z)) {
               this.path.setIndex(this.path.getIndex() + 1);
            }
         }

         DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
         if(!this.isDone()) {
            Vec3 var1 = this.path.currentPos(this.mob);
            this.mob.getMoveControl().setWantedPosition(var1.x, var1.y, var1.z, this.speedModifier);
         }
      }
   }

   protected void updatePath() {
      if(this.path != null) {
         Vec3 var1 = this.getTempMobPos();
         float var2 = this.mob.getBbWidth();
         float var3 = var2 > 0.75F?var2 / 2.0F:0.75F - var2 / 2.0F;
         Vec3 var4 = this.mob.getDeltaMovement();
         if(Math.abs(var4.x) > 0.2D || Math.abs(var4.z) > 0.2D) {
            var3 = (float)((double)var3 * var4.length() * 6.0D);
         }

         int var5 = 6;
         Vec3 var6 = this.path.currentPos();
         if(Math.abs(this.mob.x - (var6.x + 0.5D)) < (double)var3 && Math.abs(this.mob.z - (var6.z + 0.5D)) < (double)var3 && Math.abs(this.mob.y - var6.y) < (double)(var3 * 2.0F)) {
            this.path.next();
         }

         for(int var7 = Math.min(this.path.getIndex() + 6, this.path.getSize() - 1); var7 > this.path.getIndex(); --var7) {
            var6 = this.path.getPos(this.mob, var7);
            if(var6.distanceToSqr(var1) <= 36.0D && this.canMoveDirectly(var1, var6, 0, 0, 0)) {
               this.path.setIndex(var7);
               break;
            }
         }

         this.doStuckDetection(var1);
      }
   }

   protected void doStuckDetection(Vec3 lastStuckCheckPos) {
      if(this.tick - this.lastStuckCheck > 100) {
         if(lastStuckCheckPos.distanceToSqr(this.lastStuckCheckPos) < 2.25D) {
            this.stop();
         }

         this.lastStuckCheck = this.tick;
         this.lastStuckCheckPos = lastStuckCheckPos;
      }

      if(this.path != null && !this.path.isDone()) {
         Vec3 vec3 = this.path.currentPos();
         if(vec3.equals(this.timeoutCachedNode)) {
            this.timeoutTimer += Util.getMillis() - this.lastTimeoutCheck;
         } else {
            this.timeoutCachedNode = vec3;
            double var3 = lastStuckCheckPos.distanceTo(this.timeoutCachedNode);
            this.timeoutLimit = this.mob.getSpeed() > 0.0F?var3 / (double)this.mob.getSpeed() * 100.0D:0.0D;
         }

         if(this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 2.0D) {
            this.timeoutCachedNode = Vec3.ZERO;
            this.timeoutTimer = 0L;
            this.timeoutLimit = 0.0D;
            this.stop();
         }

         this.lastTimeoutCheck = Util.getMillis();
      }

   }

   protected boolean canMoveDirectly(Vec3 var1, Vec3 var2, int var3, int var4, int var5) {
      Vec3 var6 = new Vec3(var2.x, var2.y + (double)this.mob.getBbHeight() * 0.5D, var2.z);
      return this.level.clip(new ClipContext(var1, var6, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob)).getType() == HitResult.Type.MISS;
   }

   public boolean isStableDestination(BlockPos blockPos) {
      return !this.level.getBlockState(blockPos).isSolidRender(this.level, blockPos);
   }

   public void setCanFloat(boolean canFloat) {
   }
}
