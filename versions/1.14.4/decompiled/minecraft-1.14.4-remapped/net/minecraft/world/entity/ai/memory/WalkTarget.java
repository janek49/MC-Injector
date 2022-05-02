package net.minecraft.world.entity.ai.memory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.BlockPosWrapper;
import net.minecraft.world.entity.ai.behavior.PositionWrapper;
import net.minecraft.world.phys.Vec3;

public class WalkTarget {
   private final PositionWrapper target;
   private final float speed;
   private final int closeEnoughDist;

   public WalkTarget(BlockPos blockPos, float var2, int var3) {
      this((PositionWrapper)(new BlockPosWrapper(blockPos)), var2, var3);
   }

   public WalkTarget(Vec3 vec3, float var2, int var3) {
      this((PositionWrapper)(new BlockPosWrapper(new BlockPos(vec3))), var2, var3);
   }

   public WalkTarget(PositionWrapper target, float speed, int closeEnoughDist) {
      this.target = target;
      this.speed = speed;
      this.closeEnoughDist = closeEnoughDist;
   }

   public PositionWrapper getTarget() {
      return this.target;
   }

   public float getSpeed() {
      return this.speed;
   }

   public int getCloseEnoughDist() {
      return this.closeEnoughDist;
   }
}
