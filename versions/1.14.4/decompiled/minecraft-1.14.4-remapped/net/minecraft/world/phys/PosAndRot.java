package net.minecraft.world.phys;

import java.util.Objects;
import net.minecraft.world.phys.Vec3;

public class PosAndRot {
   private final Vec3 pos;
   private final float xRot;
   private final float yRot;

   public PosAndRot(Vec3 pos, float xRot, float yRot) {
      this.pos = pos;
      this.xRot = xRot;
      this.yRot = yRot;
   }

   public Vec3 pos() {
      return this.pos;
   }

   public float xRot() {
      return this.xRot;
   }

   public float yRot() {
      return this.yRot;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         PosAndRot var2 = (PosAndRot)object;
         return Float.compare(var2.xRot, this.xRot) == 0 && Float.compare(var2.yRot, this.yRot) == 0 && Objects.equals(this.pos, var2.pos);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.pos, Float.valueOf(this.xRot), Float.valueOf(this.yRot)});
   }

   public String toString() {
      return "PosAndRot[" + this.pos + " (" + this.xRot + ", " + this.yRot + ")]";
   }
}
