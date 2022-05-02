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

public class FloatTag extends NumericTag {
   private float data;

   FloatTag() {
   }

   public FloatTag(float data) {
      this.data = data;
   }

   public void write(DataOutput dataOutput) throws IOException {
      dataOutput.writeFloat(this.data);
   }

   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(96L);
      this.data = dataInput.readFloat();
   }

   public byte getId() {
      return (byte)5;
   }

   public String toString() {
      return this.data + "f";
   }

   public FloatTag copy() {
      return new FloatTag(this.data);
   }

   public boolean equals(Object object) {
      return this == object?true:object instanceof FloatTag && this.data == ((FloatTag)object).data;
   }

   public int hashCode() {
      return Float.floatToIntBits(this.data);
   }

   public Component getPrettyDisplay(String string, int var2) {
      Component component = (new TextComponent("f")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponent(String.valueOf(this.data))).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return (long)this.data;
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
      return (double)this.data;
   }

   public float getAsFloat() {
      return this.data;
   }

   public Number getAsNumber() {
      return Float.valueOf(this.data);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
