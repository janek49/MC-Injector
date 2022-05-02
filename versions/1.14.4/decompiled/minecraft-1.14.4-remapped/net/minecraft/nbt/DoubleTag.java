package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;

public class DoubleTag extends NumericTag {
   private double data;

   DoubleTag() {
   }

   public DoubleTag(double data) {
      this.data = data;
   }

   public void write(DataOutput dataOutput) throws IOException {
      dataOutput.writeDouble(this.data);
   }

   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(128L);
      this.data = dataInput.readDouble();
   }

   public byte getId() {
      return (byte)6;
   }

   public String toString() {
      return this.data + "d";
   }

   public DoubleTag copy() {
      return new DoubleTag(this.data);
   }

   public boolean equals(Object object) {
      return this == object?true:object instanceof DoubleTag && this.data == ((DoubleTag)object).data;
   }

   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.data);
      return (int)(var1 ^ var1 >>> 32);
   }

   public Component getPrettyDisplay(String string, int var2) {
      Component component = (new TextComponent("d")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponent(String.valueOf(this.data))).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return (long)Math.floor(this.data);
   }

   public int getAsInt() {
      return Mth.floor(this.data);
   }

   public short getAsShort() {
      return (short)(Mth.floor(this.data) & '\uffff');
   }

   public byte getAsByte() {
      return (byte)(Mth.floor(this.data) & 255);
   }

   public double getAsDouble() {
      return this.data;
   }

   public float getAsFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return Double.valueOf(this.data);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
