package net.minecraft.core;

import net.minecraft.core.Direction;

public enum AxisCycle {
   NONE {
      public int cycle(int var1, int var2, int var3, Direction.Axis direction$Axis) {
         return direction$Axis.choose(var1, var2, var3);
      }

      public Direction.Axis cycle(Direction.Axis direction$Axis) {
         return direction$Axis;
      }

      public AxisCycle inverse() {
         return this;
      }
   },
   FORWARD {
      public int cycle(int var1, int var2, int var3, Direction.Axis direction$Axis) {
         return direction$Axis.choose(var3, var1, var2);
      }

      public Direction.Axis cycle(Direction.Axis direction$Axis) {
         return AXIS_VALUES[Math.floorMod(direction$Axis.ordinal() + 1, 3)];
      }

      public AxisCycle inverse() {
         return BACKWARD;
      }
   },
   BACKWARD {
      public int cycle(int var1, int var2, int var3, Direction.Axis direction$Axis) {
         return direction$Axis.choose(var2, var3, var1);
      }

      public Direction.Axis cycle(Direction.Axis direction$Axis) {
         return AXIS_VALUES[Math.floorMod(direction$Axis.ordinal() - 1, 3)];
      }

      public AxisCycle inverse() {
         return FORWARD;
      }
   };

   public static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
   public static final AxisCycle[] VALUES = values();

   private AxisCycle() {
   }

   public abstract int cycle(int var1, int var2, int var3, Direction.Axis var4);

   public abstract Direction.Axis cycle(Direction.Axis var1);

   public abstract AxisCycle inverse();

   public static AxisCycle between(Direction.Axis var0, Direction.Axis var1) {
      return VALUES[Math.floorMod(var1.ordinal() - var0.ordinal(), 3)];
   }
}
