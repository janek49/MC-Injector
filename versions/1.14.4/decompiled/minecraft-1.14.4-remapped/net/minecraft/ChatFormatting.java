package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public enum ChatFormatting {
   BLACK("BLACK", '0', 0, Integer.valueOf(0)),
   DARK_BLUE("DARK_BLUE", '1', 1, Integer.valueOf(170)),
   DARK_GREEN("DARK_GREEN", '2', 2, Integer.valueOf('ꨀ')),
   DARK_AQUA("DARK_AQUA", '3', 3, Integer.valueOf('ꪪ')),
   DARK_RED("DARK_RED", '4', 4, Integer.valueOf(11141120)),
   DARK_PURPLE("DARK_PURPLE", '5', 5, Integer.valueOf(11141290)),
   GOLD("GOLD", '6', 6, Integer.valueOf(16755200)),
   GRAY("GRAY", '7', 7, Integer.valueOf(11184810)),
   DARK_GRAY("DARK_GRAY", '8', 8, Integer.valueOf(5592405)),
   BLUE("BLUE", '9', 9, Integer.valueOf(5592575)),
   GREEN("GREEN", 'a', 10, Integer.valueOf(5635925)),
   AQUA("AQUA", 'b', 11, Integer.valueOf(5636095)),
   RED("RED", 'c', 12, Integer.valueOf(16733525)),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, Integer.valueOf(16733695)),
   YELLOW("YELLOW", 'e', 14, Integer.valueOf(16777045)),
   WHITE("WHITE", 'f', 15, Integer.valueOf(16777215)),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1, (Integer)null);

   private static final Map FORMATTING_BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((chatFormatting) -> {
      return cleanName(chatFormatting.name);
   }, (chatFormatting) -> {
      return chatFormatting;
   }));
   private static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
   private final String name;
   private final char code;
   private final boolean isFormat;
   private final String toString;
   private final int id;
   @Nullable
   private final Integer color;

   private static String cleanName(String string) {
      return string.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
   }

   private ChatFormatting(String var3, char var4, int var5, Integer integer) {
      this(var3, var4, false, var5, integer);
   }

   private ChatFormatting(String var3, char var4, boolean var5) {
      this(var3, var4, var5, -1, (Integer)null);
   }

   private ChatFormatting(String name, char code, boolean isFormat, int id, Integer color) {
      this.name = name;
      this.code = code;
      this.isFormat = isFormat;
      this.id = id;
      this.color = color;
      this.toString = "§" + code;
   }

   public static String getLastColors(String string) {
      StringBuilder var1 = new StringBuilder();
      int var2 = -1;
      int var3 = string.length();

      while((var2 = string.indexOf(167, var2 + 1)) != -1) {
         if(var2 < var3 - 1) {
            ChatFormatting var4 = getByCode(string.charAt(var2 + 1));
            if(var4 != null) {
               if(var4.shouldReset()) {
                  var1.setLength(0);
               }

               if(var4 != RESET) {
                  var1.append(var4);
               }
            }
         }
      }

      return var1.toString();
   }

   public int getId() {
      return this.id;
   }

   public boolean isFormat() {
      return this.isFormat;
   }

   public boolean isColor() {
      return !this.isFormat && this != RESET;
   }

   @Nullable
   public Integer getColor() {
      return this.color;
   }

   public boolean shouldReset() {
      return !this.isFormat;
   }

   public String getName() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public String toString() {
      return this.toString;
   }

   @Nullable
   public static String stripFormatting(@Nullable String string) {
      return string == null?null:STRIP_FORMATTING_PATTERN.matcher(string).replaceAll("");
   }

   @Nullable
   public static ChatFormatting getByName(@Nullable String name) {
      return name == null?null:(ChatFormatting)FORMATTING_BY_NAME.get(cleanName(name));
   }

   @Nullable
   public static ChatFormatting getById(int id) {
      if(id < 0) {
         return RESET;
      } else {
         for(ChatFormatting var4 : values()) {
            if(var4.getId() == id) {
               return var4;
            }
         }

         return null;
      }
   }

   @Nullable
   public static ChatFormatting getByCode(char code) {
      char var1 = Character.toString(code).toLowerCase(Locale.ROOT).charAt(0);

      for(ChatFormatting var5 : values()) {
         if(var5.code == var1) {
            return var5;
         }
      }

      return null;
   }

   public static Collection getNames(boolean var0, boolean var1) {
      List<String> var2 = Lists.newArrayList();

      for(ChatFormatting var6 : values()) {
         if((!var6.isColor() || var0) && (!var6.isFormat() || var1)) {
            var2.add(var6.getName());
         }
      }

      return var2;
   }
}
