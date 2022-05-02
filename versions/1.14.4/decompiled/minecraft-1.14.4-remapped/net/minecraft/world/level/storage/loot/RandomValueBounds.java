package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;

public class RandomValueBounds implements RandomIntGenerator {
   private final float min;
   private final float max;

   public RandomValueBounds(float min, float max) {
      this.min = min;
      this.max = max;
   }

   public RandomValueBounds(float min) {
      this.min = min;
      this.max = min;
   }

   public static RandomValueBounds between(float var0, float var1) {
      return new RandomValueBounds(var0, var1);
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }

   public int getInt(Random random) {
      return Mth.nextInt(random, Mth.floor(this.min), Mth.floor(this.max));
   }

   public float getFloat(Random random) {
      return Mth.nextFloat(random, this.min, this.max);
   }

   public boolean matchesValue(int i) {
      return (float)i <= this.max && (float)i >= this.min;
   }

   public ResourceLocation getType() {
      return UNIFORM;
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public RandomValueBounds deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         if(GsonHelper.isNumberValue(jsonElement)) {
            return new RandomValueBounds(GsonHelper.convertToFloat(jsonElement, "value"));
         } else {
            JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "value");
            float var5 = GsonHelper.getAsFloat(var4, "min");
            float var6 = GsonHelper.getAsFloat(var4, "max");
            return new RandomValueBounds(var5, var6);
         }
      }

      public JsonElement serialize(RandomValueBounds randomValueBounds, Type type, JsonSerializationContext jsonSerializationContext) {
         if(randomValueBounds.min == randomValueBounds.max) {
            return new JsonPrimitive(Float.valueOf(randomValueBounds.min));
         } else {
            JsonObject var4 = new JsonObject();
            var4.addProperty("min", Float.valueOf(randomValueBounds.min));
            var4.addProperty("max", Float.valueOf(randomValueBounds.max));
            return var4;
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((RandomValueBounds)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
