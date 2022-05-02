package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class StringTag implements Tag {
   private String data;

   public StringTag() {
      this("");
   }

   public StringTag(String data) {
      Objects.requireNonNull(data, "Null string not allowed");
      this.data = data;
   }

   public void write(DataOutput dataOutput) throws IOException {
      dataOutput.writeUTF(this.data);
   }

   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(288L);
      this.data = dataInput.readUTF();
      nbtAccounter.accountBits((long)(16 * this.data.length()));
   }

   public byte getId() {
      return (byte)8;
   }

   public String toString() {
      return quoteAndEscape(this.data);
   }

   public StringTag copy() {
      return new StringTag(this.data);
   }

   public boolean equals(Object object) {
      return this == object?true:object instanceof StringTag && Objects.equals(this.data, ((StringTag)object).data);
   }

   public int hashCode() {
      return this.data.hashCode();
   }

   public String getAsString() {
      return this.data;
   }

   public Component getPrettyDisplay(String string, int var2) {
      String string = quoteAndEscape(this.data);
      String var4 = string.substring(0, 1);
      Component var5 = (new TextComponent(string.substring(1, string.length() - 1))).withStyle(SYNTAX_HIGHLIGHTING_STRING);
      return (new TextComponent(var4)).append(var5).append(var4);
   }

   public static String quoteAndEscape(String string) {
      StringBuilder var1 = new StringBuilder(" ");
      char var2 = 0;

      for(int var3 = 0; var3 < string.length(); ++var3) {
         char var4 = string.charAt(var3);
         if(var4 == 92) {
            var1.append('\\');
         } else if(var4 == 34 || var4 == 39) {
            if(var2 == 0) {
               var2 = (char)(var4 == 34?39:34);
            }

            if(var2 == var4) {
               var1.append('\\');
            }
         }

         var1.append(var4);
      }

      if(var2 == 0) {
         var2 = 34;
      }

      var1.setCharAt(0, var2);
      var1.append(var2);
      return var1.toString();
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
