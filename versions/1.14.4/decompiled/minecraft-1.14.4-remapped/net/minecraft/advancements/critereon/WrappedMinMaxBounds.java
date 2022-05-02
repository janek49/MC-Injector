package net.minecraft.advancements.critereon;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.TranslatableComponent;

public class WrappedMinMaxBounds {
   public static final WrappedMinMaxBounds ANY = new WrappedMinMaxBounds((Float)null, (Float)null);
   public static final SimpleCommandExceptionType ERROR_INTS_ONLY = new SimpleCommandExceptionType(new TranslatableComponent("argument.range.ints", new Object[0]));
   private final Float min;
   private final Float max;

   public WrappedMinMaxBounds(@Nullable Float min, @Nullable Float max) {
      this.min = min;
      this.max = max;
   }

   @Nullable
   public Float getMin() {
      return this.min;
   }

   @Nullable
   public Float getMax() {
      return this.max;
   }

   public static WrappedMinMaxBounds fromReader(StringReader stringReader, boolean var1, Function function) throws CommandSyntaxException {
      if(!stringReader.canRead()) {
         throw MinMaxBounds.ERROR_EMPTY.createWithContext(stringReader);
      } else {
         int var3 = stringReader.getCursor();
         Float var4 = optionallyFormat(readNumber(stringReader, var1), function);
         Float var5;
         if(stringReader.canRead(2) && stringReader.peek() == 46 && stringReader.peek(1) == 46) {
            stringReader.skip();
            stringReader.skip();
            var5 = optionallyFormat(readNumber(stringReader, var1), function);
            if(var4 == null && var5 == null) {
               stringReader.setCursor(var3);
               throw MinMaxBounds.ERROR_EMPTY.createWithContext(stringReader);
            }
         } else {
            if(!var1 && stringReader.canRead() && stringReader.peek() == 46) {
               stringReader.setCursor(var3);
               throw ERROR_INTS_ONLY.createWithContext(stringReader);
            }

            var5 = var4;
         }

         if(var4 == null && var5 == null) {
            stringReader.setCursor(var3);
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(stringReader);
         } else {
            return new WrappedMinMaxBounds(var4, var5);
         }
      }
   }

   @Nullable
   private static Float readNumber(StringReader stringReader, boolean var1) throws CommandSyntaxException {
      int var2 = stringReader.getCursor();

      while(stringReader.canRead() && isAllowedNumber(stringReader, var1)) {
         stringReader.skip();
      }

      String var3 = stringReader.getString().substring(var2, stringReader.getCursor());
      if(var3.isEmpty()) {
         return null;
      } else {
         try {
            return Float.valueOf(Float.parseFloat(var3));
         } catch (NumberFormatException var5) {
            if(var1) {
               throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(stringReader, var3);
            } else {
               throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(stringReader, var3);
            }
         }
      }
   }

   private static boolean isAllowedNumber(StringReader stringReader, boolean var1) {
      char var2 = stringReader.peek();
      return (var2 < 48 || var2 > 57) && var2 != 45?(var1 && var2 == 46?!stringReader.canRead(2) || stringReader.peek(1) != 46:false):true;
   }

   @Nullable
   private static Float optionallyFormat(@Nullable Float var0, Function function) {
      return var0 == null?null:(Float)function.apply(var0);
   }
}
