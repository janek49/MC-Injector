package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;

public abstract class MinMaxBounds {
   public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(new TranslatableComponent("argument.range.empty", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(new TranslatableComponent("argument.range.swapped", new Object[0]));
   protected final Number min;
   protected final Number max;

   protected MinMaxBounds(@Nullable Number min, @Nullable Number max) {
      this.min = min;
      this.max = max;
   }

   @Nullable
   public Number getMin() {
      return this.min;
   }

   @Nullable
   public Number getMax() {
      return this.max;
   }

   public boolean isAny() {
      return this.min == null && this.max == null;
   }

   public JsonElement serializeToJson() {
      if(this.isAny()) {
         return JsonNull.INSTANCE;
      } else if(this.min != null && this.min.equals(this.max)) {
         return new JsonPrimitive(this.min);
      } else {
         JsonObject var1 = new JsonObject();
         if(this.min != null) {
            var1.addProperty("min", this.min);
         }

         if(this.max != null) {
            var1.addProperty("max", this.max);
         }

         return var1;
      }
   }

   protected static MinMaxBounds fromJson(@Nullable JsonElement jsonElement, MinMaxBounds var1, BiFunction biFunction, MinMaxBounds.BoundsFactory minMaxBounds$BoundsFactory) {
      if(jsonElement != null && !jsonElement.isJsonNull()) {
         if(GsonHelper.isNumberValue(jsonElement)) {
            T var4 = (Number)biFunction.apply(jsonElement, "value");
            return minMaxBounds$BoundsFactory.create(var4, var4);
         } else {
            JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "value");
            T var5 = var4.has("min")?(Number)biFunction.apply(var4.get("min"), "min"):null;
            T var6 = var4.has("max")?(Number)biFunction.apply(var4.get("max"), "max"):null;
            return minMaxBounds$BoundsFactory.create(var5, var6);
         }
      } else {
         return var1;
      }
   }

   protected static MinMaxBounds fromReader(StringReader stringReader, MinMaxBounds.BoundsFromReaderFactory minMaxBounds$BoundsFromReaderFactory, Function var2, Supplier supplier, Function var4) throws CommandSyntaxException {
      if(!stringReader.canRead()) {
         throw ERROR_EMPTY.createWithContext(stringReader);
      } else {
         int var5 = stringReader.getCursor();

         try {
            T var6 = (Number)optionallyFormat(readNumber(stringReader, var2, supplier), var4);
            T var7;
            if(stringReader.canRead(2) && stringReader.peek() == 46 && stringReader.peek(1) == 46) {
               stringReader.skip();
               stringReader.skip();
               var7 = (Number)optionallyFormat(readNumber(stringReader, var2, supplier), var4);
               if(var6 == null && var7 == null) {
                  throw ERROR_EMPTY.createWithContext(stringReader);
               }
            } else {
               var7 = var6;
            }

            if(var6 == null && var7 == null) {
               throw ERROR_EMPTY.createWithContext(stringReader);
            } else {
               return minMaxBounds$BoundsFromReaderFactory.create(stringReader, var6, var7);
            }
         } catch (CommandSyntaxException var8) {
            stringReader.setCursor(var5);
            throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), var5);
         }
      }
   }

   @Nullable
   private static Number readNumber(StringReader stringReader, Function function, Supplier supplier) throws CommandSyntaxException {
      int var3 = stringReader.getCursor();

      while(stringReader.canRead() && isAllowedInputChat(stringReader)) {
         stringReader.skip();
      }

      String var4 = stringReader.getString().substring(var3, stringReader.getCursor());
      if(var4.isEmpty()) {
         return null;
      } else {
         try {
            return (Number)function.apply(var4);
         } catch (NumberFormatException var6) {
            throw ((DynamicCommandExceptionType)supplier.get()).createWithContext(stringReader, var4);
         }
      }
   }

   private static boolean isAllowedInputChat(StringReader stringReader) {
      char var1 = stringReader.peek();
      return (var1 < 48 || var1 > 57) && var1 != 45?(var1 != 46?false:!stringReader.canRead(2) || stringReader.peek(1) != 46):true;
   }

   @Nullable
   private static Object optionallyFormat(@Nullable Object var0, Function function) {
      return var0 == null?null:function.apply(var0);
   }

   @FunctionalInterface
   public interface BoundsFactory {
      MinMaxBounds create(@Nullable Number var1, @Nullable Number var2);
   }

   @FunctionalInterface
   public interface BoundsFromReaderFactory {
      MinMaxBounds create(StringReader var1, @Nullable Number var2, @Nullable Number var3) throws CommandSyntaxException;
   }

   public static class Floats extends MinMaxBounds {
      public static final MinMaxBounds.Floats ANY = new MinMaxBounds.Floats((Float)null, (Float)null);
      private final Double minSq;
      private final Double maxSq;

      private static MinMaxBounds.Floats create(StringReader stringReader, @Nullable Float var1, @Nullable Float var2) throws CommandSyntaxException {
         if(var1 != null && var2 != null && var1.floatValue() > var2.floatValue()) {
            throw ERROR_SWAPPED.createWithContext(stringReader);
         } else {
            return new MinMaxBounds.Floats(var1, var2);
         }
      }

      @Nullable
      private static Double squareOpt(@Nullable Float float) {
         return float == null?null:Double.valueOf(float.doubleValue() * float.doubleValue());
      }

      private Floats(@Nullable Float var1, @Nullable Float var2) {
         super(var1, var2);
         this.minSq = squareOpt(var1);
         this.maxSq = squareOpt(var2);
      }

      public static MinMaxBounds.Floats atLeast(float f) {
         return new MinMaxBounds.Floats(Float.valueOf(f), (Float)null);
      }

      public boolean matches(float f) {
         return this.min != null && ((Float)this.min).floatValue() > f?false:this.max == null || ((Float)this.max).floatValue() >= f;
      }

      public boolean matchesSqr(double d) {
         return this.minSq != null && this.minSq.doubleValue() > d?false:this.maxSq == null || this.maxSq.doubleValue() >= d;
      }

      public static MinMaxBounds.Floats fromJson(@Nullable JsonElement json) {
         return (MinMaxBounds.Floats)fromJson(json, ANY, GsonHelper::convertToFloat, MinMaxBounds.Floats::<init>);
      }

      public static MinMaxBounds.Floats fromReader(StringReader reader) throws CommandSyntaxException {
         return fromReader(reader, (float) -> {
            return float;
         });
      }

      public static MinMaxBounds.Floats fromReader(StringReader stringReader, Function function) throws CommandSyntaxException {
         MinMaxBounds.BoundsFromReaderFactory var10001 = MinMaxBounds.Floats::create;
         Function var10002 = Float::parseFloat;
         BuiltInExceptionProvider var10003 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.getClass();
         return (MinMaxBounds.Floats)fromReader(stringReader, var10001, var10002, var10003::readerInvalidFloat, function);
      }
   }

   public static class Ints extends MinMaxBounds {
      public static final MinMaxBounds.Ints ANY = new MinMaxBounds.Ints((Integer)null, (Integer)null);
      private final Long minSq;
      private final Long maxSq;

      private static MinMaxBounds.Ints create(StringReader stringReader, @Nullable Integer var1, @Nullable Integer var2) throws CommandSyntaxException {
         if(var1 != null && var2 != null && var1.intValue() > var2.intValue()) {
            throw ERROR_SWAPPED.createWithContext(stringReader);
         } else {
            return new MinMaxBounds.Ints(var1, var2);
         }
      }

      @Nullable
      private static Long squareOpt(@Nullable Integer integer) {
         return integer == null?null:Long.valueOf(integer.longValue() * integer.longValue());
      }

      private Ints(@Nullable Integer var1, @Nullable Integer var2) {
         super(var1, var2);
         this.minSq = squareOpt(var1);
         this.maxSq = squareOpt(var2);
      }

      public static MinMaxBounds.Ints exactly(int i) {
         return new MinMaxBounds.Ints(Integer.valueOf(i), Integer.valueOf(i));
      }

      public static MinMaxBounds.Ints atLeast(int i) {
         return new MinMaxBounds.Ints(Integer.valueOf(i), (Integer)null);
      }

      public boolean matches(int i) {
         return this.min != null && ((Integer)this.min).intValue() > i?false:this.max == null || ((Integer)this.max).intValue() >= i;
      }

      public static MinMaxBounds.Ints fromJson(@Nullable JsonElement json) {
         return (MinMaxBounds.Ints)fromJson(json, ANY, GsonHelper::convertToInt, MinMaxBounds.Ints::<init>);
      }

      public static MinMaxBounds.Ints fromReader(StringReader reader) throws CommandSyntaxException {
         return fromReader(reader, (integer) -> {
            return integer;
         });
      }

      public static MinMaxBounds.Ints fromReader(StringReader stringReader, Function function) throws CommandSyntaxException {
         MinMaxBounds.BoundsFromReaderFactory var10001 = MinMaxBounds.Ints::create;
         Function var10002 = Integer::parseInt;
         BuiltInExceptionProvider var10003 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.getClass();
         return (MinMaxBounds.Ints)fromReader(stringReader, var10001, var10002, var10003::readerInvalidInt, function);
      }
   }
}
