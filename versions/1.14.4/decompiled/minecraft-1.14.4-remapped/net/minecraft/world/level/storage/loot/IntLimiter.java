package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class IntLimiter implements IntUnaryOperator {
   private final Integer min;
   private final Integer max;
   private final IntUnaryOperator op;

   private IntLimiter(@Nullable Integer min, @Nullable Integer max) {
      this.min = min;
      this.max = max;
      if(min == null) {
         if(max == null) {
            this.op = (i) -> {
               return i;
            };
         } else {
            int var3 = max.intValue();
            this.op = (var1) -> {
               return Math.min(var3, var1);
            };
         }
      } else {
         int var3 = min.intValue();
         if(max == null) {
            this.op = (var1) -> {
               return Math.max(var3, var1);
            };
         } else {
            int var4 = max.intValue();
            this.op = (var2) -> {
               return Mth.clamp(var2, var3, var4);
            };
         }
      }

   }

   public static IntLimiter clamp(int var0, int var1) {
      return new IntLimiter(Integer.valueOf(var0), Integer.valueOf(var1));
   }

   public static IntLimiter lowerBound(int i) {
      return new IntLimiter(Integer.valueOf(i), (Integer)null);
   }

   public static IntLimiter upperBound(int i) {
      return new IntLimiter((Integer)null, Integer.valueOf(i));
   }

   public int applyAsInt(int i) {
      return this.op.applyAsInt(i);
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public IntLimiter deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "value");
         Integer var5 = var4.has("min")?Integer.valueOf(GsonHelper.getAsInt(var4, "min")):null;
         Integer var6 = var4.has("max")?Integer.valueOf(GsonHelper.getAsInt(var4, "max")):null;
         return new IntLimiter(var5, var6);
      }

      public JsonElement serialize(IntLimiter intLimiter, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         if(intLimiter.max != null) {
            var4.addProperty("max", intLimiter.max);
         }

         if(intLimiter.min != null) {
            var4.addProperty("min", intLimiter.min);
         }

         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((IntLimiter)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
