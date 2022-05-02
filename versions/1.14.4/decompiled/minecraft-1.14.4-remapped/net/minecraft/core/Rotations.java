package net.minecraft.core;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;

public class Rotations {
   protected final float x;
   protected final float y;
   protected final float z;

   public Rotations(float var1, float var2, float var3) {
      this.x = !Float.isInfinite(var1) && !Float.isNaN(var1)?var1 % 360.0F:0.0F;
      this.y = !Float.isInfinite(var2) && !Float.isNaN(var2)?var2 % 360.0F:0.0F;
      this.z = !Float.isInfinite(var3) && !Float.isNaN(var3)?var3 % 360.0F:0.0F;
   }

   public Rotations(ListTag listTag) {
      this(listTag.getFloat(0), listTag.getFloat(1), listTag.getFloat(2));
   }

   public ListTag save() {
      ListTag listTag = new ListTag();
      listTag.add(new FloatTag(this.x));
      listTag.add(new FloatTag(this.y));
      listTag.add(new FloatTag(this.z));
      return listTag;
   }

   public boolean equals(Object object) {
      if(!(object instanceof Rotations)) {
         return false;
      } else {
         Rotations var2 = (Rotations)object;
         return this.x == var2.x && this.y == var2.y && this.z == var2.z;
      }
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }
}
