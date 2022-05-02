package net.minecraft.server.level;

import net.minecraft.core.BlockPos;

public class ColumnPos {
   public final int x;
   public final int z;

   public ColumnPos(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public ColumnPos(BlockPos blockPos) {
      this.x = blockPos.getX();
      this.z = blockPos.getZ();
   }

   public long toLong() {
      return asLong(this.x, this.z);
   }

   public static long asLong(int var0, int var1) {
      return (long)var0 & 4294967295L | ((long)var1 & 4294967295L) << 32;
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public int hashCode() {
      int var1 = 1664525 * this.x + 1013904223;
      int var2 = 1664525 * (this.z ^ -559038737) + 1013904223;
      return var1 ^ var2;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof ColumnPos)) {
         return false;
      } else {
         ColumnPos var2 = (ColumnPos)object;
         return this.x == var2.x && this.z == var2.z;
      }
   }
}
