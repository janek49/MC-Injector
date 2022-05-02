package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompoundTag implements Tag {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
   private final Map tags = Maps.newHashMap();

   public void write(DataOutput dataOutput) throws IOException {
      for(String var3 : this.tags.keySet()) {
         Tag var4 = (Tag)this.tags.get(var3);
         writeNamedTag(var3, var4, dataOutput);
      }

      dataOutput.writeByte(0);
   }

   public void load(DataInput dataInput, int var2, NbtAccounter nbtAccounter) throws IOException {
      nbtAccounter.accountBits(384L);
      if(var2 > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.tags.clear();

         byte var4;
         while((var4 = readNamedTagType(dataInput, nbtAccounter)) != 0) {
            String var5 = readNamedTagName(dataInput, nbtAccounter);
            nbtAccounter.accountBits((long)(224 + 16 * var5.length()));
            Tag var6 = readNamedTagData(var4, var5, dataInput, var2 + 1, nbtAccounter);
            if(this.tags.put(var5, var6) != null) {
               nbtAccounter.accountBits(288L);
            }
         }

      }
   }

   public Set getAllKeys() {
      return this.tags.keySet();
   }

   public byte getId() {
      return (byte)10;
   }

   public int size() {
      return this.tags.size();
   }

   @Nullable
   public Tag put(String string, Tag var2) {
      return (Tag)this.tags.put(string, var2);
   }

   public void putByte(String string, byte var2) {
      this.tags.put(string, new ByteTag(var2));
   }

   public void putShort(String string, short var2) {
      this.tags.put(string, new ShortTag(var2));
   }

   public void putInt(String string, int var2) {
      this.tags.put(string, new IntTag(var2));
   }

   public void putLong(String string, long var2) {
      this.tags.put(string, new LongTag(var2));
   }

   public void putUUID(String string, UUID uUID) {
      this.putLong(string + "Most", uUID.getMostSignificantBits());
      this.putLong(string + "Least", uUID.getLeastSignificantBits());
   }

   public UUID getUUID(String string) {
      return new UUID(this.getLong(string + "Most"), this.getLong(string + "Least"));
   }

   public boolean hasUUID(String string) {
      return this.contains(string + "Most", 99) && this.contains(string + "Least", 99);
   }

   public void putFloat(String string, float var2) {
      this.tags.put(string, new FloatTag(var2));
   }

   public void putDouble(String string, double var2) {
      this.tags.put(string, new DoubleTag(var2));
   }

   public void putString(String var1, String var2) {
      this.tags.put(var1, new StringTag(var2));
   }

   public void putByteArray(String string, byte[] bytes) {
      this.tags.put(string, new ByteArrayTag(bytes));
   }

   public void putIntArray(String string, int[] ints) {
      this.tags.put(string, new IntArrayTag(ints));
   }

   public void putIntArray(String string, List list) {
      this.tags.put(string, new IntArrayTag(list));
   }

   public void putLongArray(String string, long[] longs) {
      this.tags.put(string, new LongArrayTag(longs));
   }

   public void putLongArray(String string, List list) {
      this.tags.put(string, new LongArrayTag(list));
   }

   public void putBoolean(String string, boolean var2) {
      this.putByte(string, (byte)(var2?1:0));
   }

   @Nullable
   public Tag get(String string) {
      return (Tag)this.tags.get(string);
   }

   public byte getTagType(String string) {
      Tag var2 = (Tag)this.tags.get(string);
      return var2 == null?0:var2.getId();
   }

   public boolean contains(String string) {
      return this.tags.containsKey(string);
   }

   public boolean contains(String string, int var2) {
      int var3 = this.getTagType(string);
      return var3 == var2?true:(var2 != 99?false:var3 == 1 || var3 == 2 || var3 == 3 || var3 == 4 || var3 == 5 || var3 == 6);
   }

   public byte getByte(String te) {
      try {
         if(this.contains(te, 99)) {
            return ((NumericTag)this.tags.get(te)).getAsByte();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return (byte)0;
   }

   public short getShort(String string) {
      try {
         if(this.contains(string, 99)) {
            return ((NumericTag)this.tags.get(string)).getAsShort();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return (short)0;
   }

   public int getInt(String string) {
      try {
         if(this.contains(string, 99)) {
            return ((NumericTag)this.tags.get(string)).getAsInt();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0;
   }

   public long getLong(String string) {
      try {
         if(this.contains(string, 99)) {
            return ((NumericTag)this.tags.get(string)).getAsLong();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0L;
   }

   public float getFloat(String string) {
      try {
         if(this.contains(string, 99)) {
            return ((NumericTag)this.tags.get(string)).getAsFloat();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0.0F;
   }

   public double getDouble(String string) {
      try {
         if(this.contains(string, 99)) {
            return ((NumericTag)this.tags.get(string)).getAsDouble();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0.0D;
   }

   public String getString(String string) {
      try {
         if(this.contains(string, 8)) {
            return ((Tag)this.tags.get(string)).getAsString();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return "";
   }

   public byte[] getByteArray(String teArray) {
      try {
         if(this.contains(teArray, 7)) {
            return ((ByteArrayTag)this.tags.get(teArray)).getAsByteArray();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.createReport(teArray, 7, var3));
      }

      return new byte[0];
   }

   public int[] getIntArray(String string) {
      try {
         if(this.contains(string, 11)) {
            return ((IntArrayTag)this.tags.get(string)).getAsIntArray();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.createReport(string, 11, var3));
      }

      return new int[0];
   }

   public long[] getLongArray(String string) {
      try {
         if(this.contains(string, 12)) {
            return ((LongArrayTag)this.tags.get(string)).getAsLongArray();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.createReport(string, 12, var3));
      }

      return new long[0];
   }

   public CompoundTag getCompound(String string) {
      try {
         if(this.contains(string, 10)) {
            return (CompoundTag)this.tags.get(string);
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.createReport(string, 10, var3));
      }

      return new CompoundTag();
   }

   public ListTag getList(String string, int var2) {
      try {
         if(this.getTagType(string) == 9) {
            ListTag listTag = (ListTag)this.tags.get(string);
            if(!listTag.isEmpty() && listTag.getElementType() != var2) {
               return new ListTag();
            }

            return listTag;
         }
      } catch (ClassCastException var4) {
         throw new ReportedException(this.createReport(string, 9, var4));
      }

      return new ListTag();
   }

   public boolean getBoolean(String string) {
      return this.getByte(string) != 0;
   }

   public void remove(String string) {
      this.tags.remove(string);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      Collection<String> var2 = this.tags.keySet();
      if(LOGGER.isDebugEnabled()) {
         List<String> var3 = Lists.newArrayList(this.tags.keySet());
         Collections.sort(var3);
         var2 = var3;
      }

      for(String var4 : var2) {
         if(var1.length() != 1) {
            var1.append(',');
         }

         var1.append(handleEscape(var4)).append(':').append(this.tags.get(var4));
      }

      return var1.append('}').toString();
   }

   public boolean isEmpty() {
      return this.tags.isEmpty();
   }

   private CrashReport createReport(String string, int var2, ClassCastException classCastException) {
      CrashReport crashReport = CrashReport.forThrowable(classCastException, "Reading NBT data");
      CrashReportCategory var5 = crashReport.addCategory("Corrupt NBT tag", 1);
      var5.setDetail("Tag type found", () -> {
         return TAG_NAMES[((Tag)this.tags.get(string)).getId()];
      });
      var5.setDetail("Tag type expected", () -> {
         return TAG_NAMES[var2];
      });
      var5.setDetail("Tag name", (Object)string);
      return crashReport;
   }

   public CompoundTag copy() {
      CompoundTag compoundTag = new CompoundTag();

      for(String var3 : this.tags.keySet()) {
         compoundTag.put(var3, ((Tag)this.tags.get(var3)).copy());
      }

      return compoundTag;
   }

   public boolean equals(Object object) {
      return this == object?true:object instanceof CompoundTag && Objects.equals(this.tags, ((CompoundTag)object).tags);
   }

   public int hashCode() {
      return this.tags.hashCode();
   }

   private static void writeNamedTag(String string, Tag tag, DataOutput dataOutput) throws IOException {
      dataOutput.writeByte(tag.getId());
      if(tag.getId() != 0) {
         dataOutput.writeUTF(string);
         tag.write(dataOutput);
      }
   }

   private static byte readNamedTagType(DataInput dataInput, NbtAccounter nbtAccounter) throws IOException {
      return dataInput.readByte();
   }

   private static String readNamedTagName(DataInput dataInput, NbtAccounter nbtAccounter) throws IOException {
      return dataInput.readUTF();
   }

   static Tag readNamedTagData(byte var0, String string, DataInput dataInput, int var3, NbtAccounter nbtAccounter) throws IOException {
      Tag tag = Tag.newTag(var0);

      try {
         tag.load(dataInput, var3, nbtAccounter);
         return tag;
      } catch (IOException var9) {
         CrashReport var7 = CrashReport.forThrowable(var9, "Loading NBT data");
         CrashReportCategory var8 = var7.addCategory("NBT Tag");
         var8.setDetail("Tag name", (Object)string);
         var8.setDetail("Tag type", (Object)Byte.valueOf(var0));
         throw new ReportedException(var7);
      }
   }

   public CompoundTag merge(CompoundTag compoundTag) {
      for(String var3 : compoundTag.tags.keySet()) {
         Tag var4 = (Tag)compoundTag.tags.get(var3);
         if(var4.getId() == 10) {
            if(this.contains(var3, 10)) {
               CompoundTag var5 = this.getCompound(var3);
               var5.merge((CompoundTag)var4);
            } else {
               this.put(var3, var4.copy());
            }
         } else {
            this.put(var3, var4.copy());
         }
      }

      return this;
   }

   protected static String handleEscape(String string) {
      return SIMPLE_VALUE.matcher(string).matches()?string:StringTag.quoteAndEscape(string);
   }

   protected static Component handleEscapePretty(String string) {
      if(SIMPLE_VALUE.matcher(string).matches()) {
         return (new TextComponent(string)).withStyle(SYNTAX_HIGHLIGHTING_KEY);
      } else {
         String string = StringTag.quoteAndEscape(string);
         String var2 = string.substring(0, 1);
         Component var3 = (new TextComponent(string.substring(1, string.length() - 1))).withStyle(SYNTAX_HIGHLIGHTING_KEY);
         return (new TextComponent(var2)).append(var3).append(var2);
      }
   }

   public Component getPrettyDisplay(String string, int var2) {
      if(this.tags.isEmpty()) {
         return new TextComponent("{}");
      } else {
         Component component = new TextComponent("{");
         Collection<String> var4 = this.tags.keySet();
         if(LOGGER.isDebugEnabled()) {
            List<String> var5 = Lists.newArrayList(this.tags.keySet());
            Collections.sort(var5);
            var4 = var5;
         }

         if(!string.isEmpty()) {
            component.append("\n");
         }

         Component var7;
         for(Iterator<String> var5 = var4.iterator(); var5.hasNext(); component.append(var7)) {
            String var6 = (String)var5.next();
            var7 = (new TextComponent(Strings.repeat(string, var2 + 1))).append(handleEscapePretty(var6)).append(String.valueOf(':')).append(" ").append(((Tag)this.tags.get(var6)).getPrettyDisplay(string, var2 + 1));
            if(var5.hasNext()) {
               var7.append(String.valueOf(',')).append(string.isEmpty()?" ":"\n");
            }
         }

         if(!string.isEmpty()) {
            component.append("\n").append(Strings.repeat(string, var2));
         }

         component.append("}");
         return component;
      }
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
