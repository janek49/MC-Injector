package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ListTag extends CollectionTag {
   private List list = Lists.newArrayList();
   private byte type = 0;

   public void write(DataOutput dataOutput) throws IOException {
      if(this.list.isEmpty()) {
         this.type = 0;
      } else {
         this.type = ((Tag)this.list.get(0)).getId();
      }

      dataOutput.writeByte(this.type);
      dataOutput.writeInt(this.list.size());

      for(Tag var3 : this.list) {
         var3.write(dataOutput);
      }

   }

   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(296L);
      if(var2 > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.type = dataInput.readByte();
         int var4 = dataInput.readInt();
         if(this.type == 0 && var4 > 0) {
            throw new RuntimeException("Missing type on ListTag");
         } else {
            nbtAccounter.accountBits(32L * (long)var4);
            this.list = Lists.newArrayListWithCapacity(var4);

            for(int var5 = 0; var5 < var4; ++var5) {
               Tag var6 = Tag.newTag(this.type);
               var6.load(dataInput, var2 + 1, nbtAccounter);
               this.list.add(var6);
            }

         }
      }
   }

   public byte getId() {
      return (byte)9;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("[");

      for(int var2 = 0; var2 < this.list.size(); ++var2) {
         if(var2 != 0) {
            var1.append(',');
         }

         var1.append(this.list.get(var2));
      }

      return var1.append(']').toString();
   }

   private void updateTypeAfterRemove() {
      if(this.list.isEmpty()) {
         this.type = 0;
      }

   }

   public Tag remove(int i) {
      Tag tag = (Tag)this.list.remove(i);
      this.updateTypeAfterRemove();
      return tag;
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public CompoundTag getCompound(int i) {
      if(i >= 0 && i < this.list.size()) {
         Tag var2 = (Tag)this.list.get(i);
         if(var2.getId() == 10) {
            return (CompoundTag)var2;
         }
      }

      return new CompoundTag();
   }

   public ListTag getList(int i) {
      if(i >= 0 && i < this.list.size()) {
         Tag var2 = (Tag)this.list.get(i);
         if(var2.getId() == 9) {
            return (ListTag)var2;
         }
      }

      return new ListTag();
   }

   public short getShort(int i) {
      if(i >= 0 && i < this.list.size()) {
         Tag var2 = (Tag)this.list.get(i);
         if(var2.getId() == 2) {
            return ((ShortTag)var2).getAsShort();
         }
      }

      return (short)0;
   }

   public int getInt(int i) {
      if(i >= 0 && i < this.list.size()) {
         Tag var2 = (Tag)this.list.get(i);
         if(var2.getId() == 3) {
            return ((IntTag)var2).getAsInt();
         }
      }

      return 0;
   }

   public int[] getIntArray(int i) {
      if(i >= 0 && i < this.list.size()) {
         Tag var2 = (Tag)this.list.get(i);
         if(var2.getId() == 11) {
            return ((IntArrayTag)var2).getAsIntArray();
         }
      }

      return new int[0];
   }

   public double getDouble(int i) {
      if(i >= 0 && i < this.list.size()) {
         Tag var2 = (Tag)this.list.get(i);
         if(var2.getId() == 6) {
            return ((DoubleTag)var2).getAsDouble();
         }
      }

      return 0.0D;
   }

   public float getFloat(int i) {
      if(i >= 0 && i < this.list.size()) {
         Tag var2 = (Tag)this.list.get(i);
         if(var2.getId() == 5) {
            return ((FloatTag)var2).getAsFloat();
         }
      }

      return 0.0F;
   }

   public String getString(int i) {
      if(i >= 0 && i < this.list.size()) {
         Tag var2 = (Tag)this.list.get(i);
         return var2.getId() == 8?var2.getAsString():var2.toString();
      } else {
         return "";
      }
   }

   public int size() {
      return this.list.size();
   }

   public Tag get(int i) {
      return (Tag)this.list.get(i);
   }

   public Tag set(int var1, Tag var2) {
      Tag var3 = this.get(var1);
      if(!this.setTag(var1, var2)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", new Object[]{Byte.valueOf(var2.getId()), Byte.valueOf(this.type)}));
      } else {
         return var3;
      }
   }

   public void add(int var1, Tag tag) {
      if(!this.addTag(var1, tag)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", new Object[]{Byte.valueOf(tag.getId()), Byte.valueOf(this.type)}));
      }
   }

   public boolean setTag(int var1, Tag tag) {
      if(this.updateType(tag)) {
         this.list.set(var1, tag);
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int var1, Tag tag) {
      if(this.updateType(tag)) {
         this.list.add(var1, tag);
         return true;
      } else {
         return false;
      }
   }

   private boolean updateType(Tag tag) {
      if(tag.getId() == 0) {
         return false;
      } else if(this.type == 0) {
         this.type = tag.getId();
         return true;
      } else {
         return this.type == tag.getId();
      }
   }

   public ListTag copy() {
      ListTag listTag = new ListTag();
      listTag.type = this.type;

      for(Tag var3 : this.list) {
         Tag var4 = var3.copy();
         listTag.list.add(var4);
      }

      return listTag;
   }

   public boolean equals(Object object) {
      return this == object?true:object instanceof ListTag && Objects.equals(this.list, ((ListTag)object).list);
   }

   public int hashCode() {
      return this.list.hashCode();
   }

   public Component getPrettyDisplay(String string, int var2) {
      if(this.isEmpty()) {
         return new TextComponent("[]");
      } else {
         Component component = new TextComponent("[");
         if(!string.isEmpty()) {
            component.append("\n");
         }

         for(int var4 = 0; var4 < this.list.size(); ++var4) {
            Component var5 = new TextComponent(Strings.repeat(string, var2 + 1));
            var5.append(((Tag)this.list.get(var4)).getPrettyDisplay(string, var2 + 1));
            if(var4 != this.list.size() - 1) {
               var5.append(String.valueOf(',')).append(string.isEmpty()?" ":"\n");
            }

            component.append(var5);
         }

         if(!string.isEmpty()) {
            component.append("\n").append(Strings.repeat(string, var2));
         }

         component.append("]");
         return component;
      }
   }

   public int getElementType() {
      return this.type;
   }

   public void clear() {
      this.list.clear();
      this.type = 0;
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
      this.add(var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object set(int var1, Object var2) {
      return this.set(var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object get(int var1) {
      return this.get(var1);
   }
}
