package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;

public final class ConstantIntValue implements RandomIntGenerator {
   private final int value;

   public ConstantIntValue(int value) {
      this.value = value;
   }

   public int getInt(Random random) {
      return this.value;
   }

   public ResourceLocation getType() {
      return CONSTANT;
   }

   public static ConstantIntValue exactly(int i) {
      return new ConstantIntValue(i);
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public ConstantIntValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         return new ConstantIntValue(GsonHelper.convertToInt(jsonElement, "value"));
      }

      public JsonElement serialize(ConstantIntValue constantIntValue, Type type, JsonSerializationContext jsonSerializationContext) {
         return new JsonPrimitive(Integer.valueOf(constantIntValue.value));
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((ConstantIntValue)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
