package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

public enum Mirror {
   NONE,
   LEFT_RIGHT,
   FRONT_BACK;

   public int mirror(int var1, int var2) {
      int var3 = var2 / 2;
      int var4 = var1 > var3?var1 - var2:var1;
      switch(this) {
      case FRONT_BACK:
         return (var2 - var4) % var2;
      case LEFT_RIGHT:
         return (var3 - var4 + var2) % var2;
      default:
         return var1;
      }
   }

   public Rotation getRotation(Direction direction) {
      Direction.Axis var2 = direction.getAxis();
      return (this != LEFT_RIGHT || var2 != Direction.Axis.Z) && (this != FRONT_BACK || var2 != Direction.Axis.X)?Rotation.NONE:Rotation.CLOCKWISE_180;
   }

   public Direction mirror(Direction direction) {
      return this == FRONT_BACK && direction.getAxis() == Direction.Axis.X?direction.getOpposite():(this == LEFT_RIGHT && direction.getAxis() == Direction.Axis.Z?direction.getOpposite():direction);
   }
}
