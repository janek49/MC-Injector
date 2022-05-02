package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;

public final class BinomialDistributionGenerator implements RandomIntGenerator {
   private final int n;
   private final float p;

   public BinomialDistributionGenerator(int n, float p) {
      this.n = n;
      this.p = p;
   }

   public int getInt(Random random) {
      int var2 = 0;

      for(int var3 = 0; var3 < this.n; ++var3) {
         if(random.nextFloat() < this.p) {
            ++var2;
         }
      }

      return var2;
   }

   public static BinomialDistributionGenerator binomial(int var0, float var1) {
      return new BinomialDistributionGenerator(var0, var1);
   }

   public ResourceLocation getType() {
      return BINOMIAL;
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public BinomialDistributionGenerator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "value");
         int var5 = GsonHelper.getAsInt(var4, "n");
         float var6 = GsonHelper.getAsFloat(var4, "p");
         return new BinomialDistributionGenerator(var5, var6);
      }

      public JsonElement serialize(BinomialDistributionGenerator binomialDistributionGenerator, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("n", Integer.valueOf(binomialDistributionGenerator.n));
         var4.addProperty("p", Float.valueOf(binomialDistributionGenerator.p));
         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((BinomialDistributionGenerator)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
