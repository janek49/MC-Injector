package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class EndTag implements Tag {
   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(64L);
   }

   public void write(DataOutput dataOutput) throws IOException {
   }

   public byte getId() {
      return (byte)0;
   }

   public String toString() {
      return "END";
   }

   public EndTag copy() {
      return new EndTag();
   }

   public Component getPrettyDisplay(String string, int var2) {
      return new TextComponent("");
   }

   public boolean equals(Object object) {
      return object instanceof EndTag;
   }

   public int hashCode() {
      return this.getId();
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
