package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType.Function;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;

public class TagParser {
   public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(new TranslatableComponent("argument.nbt.trailing", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType(new TranslatableComponent("argument.nbt.expected.key", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType(new TranslatableComponent("argument.nbt.expected.value", new Object[0]));
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.nbt.list.mixed", new Object[]{var0, var1});
   });
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.nbt.array.mixed", new Object[]{var0, var1});
   });
   public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.nbt.array.invalid", new Object[]{object});
   });
   private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
   private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
   private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
   private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
   private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
   private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
   private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
   private final StringReader reader;

   public static CompoundTag parseTag(String string) throws CommandSyntaxException {
      return (new TagParser(new StringReader(string))).readSingleStruct();
   }

   @VisibleForTesting
   CompoundTag readSingleStruct() throws CommandSyntaxException {
      CompoundTag compoundTag = this.readStruct();
      this.reader.skipWhitespace();
      if(this.reader.canRead()) {
         throw ERROR_TRAILING_DATA.createWithContext(this.reader);
      } else {
         return compoundTag;
      }
   }

   public TagParser(StringReader reader) {
      this.reader = reader;
   }

   protected String readKey() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      if(!this.reader.canRead()) {
         throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
      } else {
         return this.reader.readString();
      }
   }

   protected Tag readTypedValue() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      int var1 = this.reader.getCursor();
      if(StringReader.isQuotedStringStart(this.reader.peek())) {
         return new StringTag(this.reader.readQuotedString());
      } else {
         String var2 = this.reader.readUnquotedString();
         if(var2.isEmpty()) {
            this.reader.setCursor(var1);
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
         } else {
            return this.type(var2);
         }
      }
   }

   private Tag type(String string) {
      try {
         if(FLOAT_PATTERN.matcher(string).matches()) {
            return new FloatTag(Float.parseFloat(string.substring(0, string.length() - 1)));
         }

         if(BYTE_PATTERN.matcher(string).matches()) {
            return new ByteTag(Byte.parseByte(string.substring(0, string.length() - 1)));
         }

         if(LONG_PATTERN.matcher(string).matches()) {
            return new LongTag(Long.parseLong(string.substring(0, string.length() - 1)));
         }

         if(SHORT_PATTERN.matcher(string).matches()) {
            return new ShortTag(Short.parseShort(string.substring(0, string.length() - 1)));
         }

         if(INT_PATTERN.matcher(string).matches()) {
            return new IntTag(Integer.parseInt(string));
         }

         if(DOUBLE_PATTERN.matcher(string).matches()) {
            return new DoubleTag(Double.parseDouble(string.substring(0, string.length() - 1)));
         }

         if(DOUBLE_PATTERN_NOSUFFIX.matcher(string).matches()) {
            return new DoubleTag(Double.parseDouble(string));
         }

         if("true".equalsIgnoreCase(string)) {
            return new ByteTag((byte)1);
         }

         if("false".equalsIgnoreCase(string)) {
            return new ByteTag((byte)0);
         }
      } catch (NumberFormatException var3) {
         ;
      }

      return new StringTag(string);
   }

   public Tag readValue() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      if(!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         char var1 = this.reader.peek();
         return (Tag)(var1 == 123?this.readStruct():(var1 == 91?this.readList():this.readTypedValue()));
      }
   }

   protected Tag readList() throws CommandSyntaxException {
      return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == 59?this.readArrayTag():this.readListTag();
   }

   public CompoundTag readStruct() throws CommandSyntaxException {
      this.expect('{');
      CompoundTag compoundTag = new CompoundTag();
      this.reader.skipWhitespace();

      while(this.reader.canRead() && this.reader.peek() != 125) {
         int var2 = this.reader.getCursor();
         String var3 = this.readKey();
         if(var3.isEmpty()) {
            this.reader.setCursor(var2);
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
         }

         this.expect(':');
         compoundTag.put(var3, this.readValue());
         if(!this.hasElementSeparator()) {
            break;
         }

         if(!this.reader.canRead()) {
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
         }
      }

      this.expect('}');
      return compoundTag;
   }

   private Tag readListTag() throws CommandSyntaxException {
      this.expect('[');
      this.reader.skipWhitespace();
      if(!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         ListTag var1 = new ListTag();
         int var2 = -1;

         while(this.reader.peek() != 93) {
            int var3 = this.reader.getCursor();
            Tag var4 = this.readValue();
            int var5 = var4.getId();
            if(var2 < 0) {
               var2 = var5;
            } else if(var5 != var2) {
               this.reader.setCursor(var3);
               throw ERROR_INSERT_MIXED_LIST.createWithContext(this.reader, Tag.getTagTypeName(var5), Tag.getTagTypeName(var2));
            }

            var1.add(var4);
            if(!this.hasElementSeparator()) {
               break;
            }

            if(!this.reader.canRead()) {
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
            }
         }

         this.expect(']');
         return var1;
      }
   }

   private Tag readArrayTag() throws CommandSyntaxException {
      this.expect('[');
      int var1 = this.reader.getCursor();
      char var2 = this.reader.read();
      this.reader.read();
      this.reader.skipWhitespace();
      if(!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else if(var2 == 66) {
         return new ByteArrayTag(this.readArray((byte)7, (byte)1));
      } else if(var2 == 76) {
         return new LongArrayTag(this.readArray((byte)12, (byte)4));
      } else if(var2 == 73) {
         return new IntArrayTag(this.readArray((byte)11, (byte)3));
      } else {
         this.reader.setCursor(var1);
         throw ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(var2));
      }
   }

   private List readArray(byte var1, byte var2) throws CommandSyntaxException {
      List<T> list = Lists.newArrayList();

      while(true) {
         if(this.reader.peek() != 93) {
            int var4 = this.reader.getCursor();
            Tag var5 = this.readValue();
            int var6 = var5.getId();
            if(var6 != var2) {
               this.reader.setCursor(var4);
               throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, Tag.getTagTypeName(var6), Tag.getTagTypeName(var1));
            }

            if(var2 == 1) {
               list.add(Byte.valueOf(((NumericTag)var5).getAsByte()));
            } else if(var2 == 4) {
               list.add(Long.valueOf(((NumericTag)var5).getAsLong()));
            } else {
               list.add(Integer.valueOf(((NumericTag)var5).getAsInt()));
            }

            if(this.hasElementSeparator()) {
               if(!this.reader.canRead()) {
                  throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
               }
               continue;
            }
         }

         this.expect(']');
         return list;
      }
   }

   private boolean hasElementSeparator() {
      this.reader.skipWhitespace();
      if(this.reader.canRead() && this.reader.peek() == 44) {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   private void expect(char c) throws CommandSyntaxException {
      this.reader.skipWhitespace();
      this.reader.expect(c);
   }
}
