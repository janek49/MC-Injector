package net.minecraft.nbt;

import java.util.AbstractList;
import net.minecraft.nbt.Tag;

public abstract class CollectionTag extends AbstractList implements Tag {
   public abstract Tag set(int var1, Tag var2);

   public abstract void add(int var1, Tag var2);

   public abstract Tag remove(int var1);

   public abstract boolean setTag(int var1, Tag var2);

   public abstract boolean addTag(int var1, Tag var2);

   // $FF: synthetic method
   public Object remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(int var1, Object var2) {
      this.add(var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object set(int var1, Object var2) {
      return this.set(var1, (Tag)var2);
   }
}
