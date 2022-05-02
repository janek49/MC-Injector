package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayTag extends CollectionTag {
   private int[] data;

   IntArrayTag() {
   }

   public IntArrayTag(int[] data) {
      this.data = data;
   }

   public IntArrayTag(List list) {
      this(toArray(list));
   }

   private static int[] toArray(List list) {
      int[] ints = new int[list.size()];

      for(int var2 = 0; var2 < list.size(); ++var2) {
         Integer var3 = (Integer)list.get(var2);
         ints[var2] = var3 == null?0:var3.intValue();
      }

      return ints;
   }

   public void write(DataOutput dataOutput) throws IOException {
      dataOutput.writeInt(this.data.length);

      for(int var5 : this.data) {
         dataOutput.writeInt(var5);
      }

   }

   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(192L);
      int var4 = dataInput.readInt();
      nbtAccounter.accountBits((long)(32 * var4));
      this.data = new int[var4];

      for(int var5 = 0; var5 < var4; ++var5) {
         this.data[var5] = dataInput.readInt();
      }

   }

   public byte getId() {
      return (byte)11;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("[I;");

      for(int var2 = 0; var2 < this.data.length; ++var2) {
         if(var2 != 0) {
            var1.append(',');
         }

         var1.append(this.data[var2]);
      }

      return var1.append(']').toString();
   }

   public IntArrayTag copy() {
      int[] vars1 = new int[this.data.length];
      System.arraycopy(this.data, 0, vars1, 0, this.data.length);
      return new IntArrayTag(vars1);
   }

   public boolean equals(Object object) {
      return this == object?true:object instanceof IntArrayTag && Arrays.equals(this.data, ((IntArrayTag)object).data);
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public int[] getAsIntArray() {
      return this.data;
   }

   public Component getPrettyDisplay(String string, int var2) {
      Component component = (new TextComponent("I")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      Component var4 = (new TextComponent("[")).append(component).append(";");

      for(int var5 = 0; var5 < this.data.length; ++var5) {
         var4.append(" ").append((new TextComponent(String.valueOf(this.data[var5]))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
         if(var5 != this.data.length - 1) {
            var4.append(",");
         }
      }

      var4.append("]");
      return var4;
   }

   public int size() {
      return this.data.length;
   }

   public IntTag get(int i) {
      return new IntTag(this.data[i]);
   }

   public IntTag set(int var1, IntTag var2) {
      int var3 = this.data[var1];
      this.data[var1] = var2.getAsInt();
      return new IntTag(var3);
   }

   public void add(int var1, IntTag intTag) {
      this.data = ArrayUtils.add(this.data, var1, intTag.getAsInt());
   }

   public boolean setTag(int var1, Tag tag) {
      if(tag instanceof NumericTag) {
         this.data[var1] = ((NumericTag)tag).getAsInt();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int var1, Tag tag) {
      if(tag instanceof NumericTag) {
         this.data = ArrayUtils.add(this.data, var1, ((NumericTag)tag).getAsInt());
         return true;
      } else {
         return false;
      }
   }

   public IntTag remove(int i) {
      int var2 = this.data[i];
      this.data = ArrayUtils.remove(this.data, i);
      return new IntTag(var2);
   }

   public void clear() {
      this.data = new int[0];
   }

   // $FF: synthetic method
   public Tag remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(int var1, Tag var2) {
      this.add(var1, (IntTag)var2);
   }

   // $FF: synthetic method
   public Tag set(int var1, Tag var2) {
      return this.set(var1, (IntTag)var2);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }

   // $FF: synthetic method
   public Object remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(int var1, Object var2) {
      this.add(var1, (IntTag)var2);
   }

   // $FF: synthetic method
   public Object set(int var1, Object var2) {
      return this.set(var1, (IntTag)var2);
   }

   // $FF: synthetic method
   public Object get(int var1) {
      return this.get(var1);
   }
}
