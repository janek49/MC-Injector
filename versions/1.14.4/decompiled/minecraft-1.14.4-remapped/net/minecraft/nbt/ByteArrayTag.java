package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayTag extends CollectionTag {
   private byte[] data;

   ByteArrayTag() {
   }

   public ByteArrayTag(byte[] data) {
      this.data = data;
   }

   public ByteArrayTag(List list) {
      this(toArray(list));
   }

   private static byte[] toArray(List list) {
      byte[] bytes = new byte[list.size()];

      for(int var2 = 0; var2 < list.size(); ++var2) {
         Byte var3 = (Byte)list.get(var2);
         bytes[var2] = var3 == null?0:var3.byteValue();
      }

      return bytes;
   }

   public void write(DataOutput dataOutput) throws IOException {
      dataOutput.writeInt(this.data.length);
      dataOutput.write(this.data);
   }

   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(192L);
      int var4 = dataInput.readInt();
      nbtAccounter.accountBits((long)(8 * var4));
      this.data = new byte[var4];
      dataInput.readFully(this.data);
   }

   public byte getId() {
      return (byte)7;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("[B;");

      for(int var2 = 0; var2 < this.data.length; ++var2) {
         if(var2 != 0) {
            var1.append(',');
         }

         var1.append(this.data[var2]).append('B');
      }

      return var1.append(']').toString();
   }

   public Tag copy() {
      byte[] vars1 = new byte[this.data.length];
      System.arraycopy(this.data, 0, vars1, 0, this.data.length);
      return new ByteArrayTag(vars1);
   }

   public boolean equals(Object object) {
      return this == object?true:object instanceof ByteArrayTag && Arrays.equals(this.data, ((ByteArrayTag)object).data);
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public Component getPrettyDisplay(String string, int var2) {
      Component component = (new TextComponent("B")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      Component var4 = (new TextComponent("[")).append(component).append(";");

      for(int var5 = 0; var5 < this.data.length; ++var5) {
         Component var6 = (new TextComponent(String.valueOf(this.data[var5]))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         var4.append(" ").append(var6).append(component);
         if(var5 != this.data.length - 1) {
            var4.append(",");
         }
      }

      var4.append("]");
      return var4;
   }

   public byte[] getAsByteArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public ByteTag get(int i) {
      return new ByteTag(this.data[i]);
   }

   public ByteTag set(int var1, ByteTag var2) {
      byte var3 = this.data[var1];
      this.data[var1] = var2.getAsByte();
      return new ByteTag(var3);
   }

   public void add(int var1, ByteTag byteTag) {
      this.data = ArrayUtils.add(this.data, var1, byteTag.getAsByte());
   }

   public boolean setTag(int var1, Tag tag) {
      if(tag instanceof NumericTag) {
         this.data[var1] = ((NumericTag)tag).getAsByte();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int var1, Tag tag) {
      if(tag instanceof NumericTag) {
         this.data = ArrayUtils.add(this.data, var1, ((NumericTag)tag).getAsByte());
         return true;
      } else {
         return false;
      }
   }

   public ByteTag remove(int i) {
      byte var2 = this.data[i];
      this.data = ArrayUtils.remove(this.data, i);
      return new ByteTag(var2);
   }

   public void clear() {
      this.data = new byte[0];
   }

   // $FF: synthetic method
   public Tag remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(int var1, Tag var2) {
      this.add(var1, (ByteTag)var2);
   }

   // $FF: synthetic method
   public Tag set(int var1, Tag var2) {
      return this.set(var1, (ByteTag)var2);
   }

   // $FF: synthetic method
   public Object remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(int var1, Object var2) {
      this.add(var1, (ByteTag)var2);
   }

   // $FF: synthetic method
   public Object set(int var1, Object var2) {
      return this.set(var1, (ByteTag)var2);
   }

   // $FF: synthetic method
   public Object get(int var1) {
      return this.get(var1);
   }
}
