package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class LongTag extends NumericTag {
   private long data;

   LongTag() {
   }

   public LongTag(long data) {
      this.data = data;
   }

   public void write(DataOutput dataOutput) throws IOException {
      dataOutput.writeLong(this.data);
   }

   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(128L);
      this.data = dataInput.readLong();
   }

   public byte getId() {
      return (byte)4;
   }

   public String toString() {
      return this.data + "L";
   }

   public LongTag copy() {
      return new LongTag(this.data);
   }

   public boolean equals(Object object) {
      return this == object?true:object instanceof LongTag && this.data == ((LongTag)object).data;
   }

   public int hashCode() {
      return (int)(this.data ^ this.data >>> 32);
   }

   public Component getPrettyDisplay(String string, int var2) {
      Component component = (new TextComponent("L")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponent(String.valueOf(this.data))).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return this.data;
   }

   public int getAsInt() {
      return (int)(this.data & -1L);
   }

   public short getAsShort() {
      return (short)((int)(this.data & 65535L));
   }

   public byte getAsByte() {
      return (byte)((int)(this.data & 255L));
   }

   public double getAsDouble() {
      return (double)this.data;
   }

   public float getAsFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return Long.valueOf(this.data);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
