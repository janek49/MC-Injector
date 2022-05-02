package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ByteTag extends NumericTag {
   private byte data;

   ByteTag() {
   }

   public ByteTag(byte data) {
      this.data = data;
   }

   public void write(DataOutput dataOutput) throws IOException {
      dataOutput.writeByte(this.data);
   }

   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(72L);
      this.data = dataInput.readByte();
   }

   public byte getId() {
      return (byte)1;
   }

   public String toString() {
      return this.data + "b";
   }

   public ByteTag copy() {
      return new ByteTag(this.data);
   }

   public boolean equals(Object object) {
      return this == object?true:object instanceof ByteTag && this.data == ((ByteTag)object).data;
   }

   public int hashCode() {
      return this.data;
   }

   public Component getPrettyDisplay(String string, int var2) {
      Component component = (new TextComponent("b")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponent(String.valueOf(this.data))).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return (long)this.data;
   }

   public int getAsInt() {
      return this.data;
   }

   public short getAsShort() {
      return (short)this.data;
   }

   public byte getAsByte() {
      return this.data;
   }

   public double getAsDouble() {
      return (double)this.data;
   }

   public float getAsFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return Byte.valueOf(this.data);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
