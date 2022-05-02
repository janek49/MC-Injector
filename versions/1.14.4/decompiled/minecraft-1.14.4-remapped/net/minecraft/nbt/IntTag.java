package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class IntTag extends NumericTag {
   private int data;

   IntTag() {
   }

   public IntTag(int data) {
      this.data = data;
   }

   public void write(DataOutput dataOutput) throws IOException {
      dataOutput.writeInt(this.data);
   }

   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(96L);
      this.data = dataInput.readInt();
   }

   public byte getId() {
      return (byte)3;
   }

   public String toString() {
      return String.valueOf(this.data);
   }

   public IntTag copy() {
      return new IntTag(this.data);
   }

   public boolean equals(Object object) {
      return this == object?true:object instanceof IntTag && this.data == ((IntTag)object).data;
   }

   public int hashCode() {
      return this.data;
   }

   public Component getPrettyDisplay(String string, int var2) {
      return (new TextComponent(String.valueOf(this.data))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return (long)this.data;
   }

   public int getAsInt() {
      return this.data;
   }

   public short getAsShort() {
      return (short)(this.data & '\uffff');
   }

   public byte getAsByte() {
      return (byte)(this.data & 255);
   }

   public double getAsDouble() {
      return (double)this.data;
   }

   public float getAsFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return Integer.valueOf(this.data);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
