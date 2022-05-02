package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ShortTag extends NumericTag {
   private short data;

   public ShortTag() {
   }

   public ShortTag(short data) {
      this.data = data;
   }

   public void write(DataOutput dataOutput) throws IOException {
      dataOutput.writeShort(this.data);
   }

   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(80L);
      this.data = dataInput.readShort();
   }

   public byte getId() {
      return (byte)2;
   }

   public String toString() {
      return this.data + "s";
   }

   public ShortTag copy() {
      return new ShortTag(this.data);
   }

   public boolean equals(Object object) {
      return this == object?true:object instanceof ShortTag && this.data == ((ShortTag)object).data;
   }

   public int hashCode() {
      return this.data;
   }

   public Component getPrettyDisplay(String string, int var2) {
      Component component = (new TextComponent("s")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponent(String.valueOf(this.data))).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return (long)this.data;
   }

   public int getAsInt() {
      return this.data;
   }

   public short getAsShort() {
      return this.data;
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
      return Short.valueOf(this.data);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
