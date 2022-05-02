package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.PositionWrapper;
import net.minecraft.world.phys.Vec3;

public class BlockPosWrapper implements PositionWrapper {
   private final BlockPos pos;
   private final Vec3 lookAt;

   public BlockPosWrapper(BlockPos pos) {
      this.pos = pos;
      this.lookAt = new Vec3((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Vec3 getLookAtPos() {
      return this.lookAt;
   }

   public boolean isVisible(LivingEntity livingEntity) {
      return true;
   }

   public String toString() {
      return "BlockPosWrapper{pos=" + this.pos + ", lookAt=" + this.lookAt + '}';
   }
}
