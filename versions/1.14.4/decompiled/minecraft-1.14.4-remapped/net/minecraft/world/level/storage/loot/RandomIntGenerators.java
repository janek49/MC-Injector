package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomValueBounds;

public class RandomIntGenerators {
   private static final Map GENERATORS = Maps.newHashMap();

   public static RandomIntGenerator deserialize(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      if(jsonElement.isJsonPrimitive()) {
         return (RandomIntGenerator)jsonDeserializationContext.deserialize(jsonElement, ConstantIntValue.class);
      } else {
         JsonObject var2 = jsonElement.getAsJsonObject();
         String var3 = GsonHelper.getAsString(var2, "type", RandomIntGenerator.UNIFORM.toString());
         Class<? extends RandomIntGenerator> var4 = (Class)GENERATORS.get(new ResourceLocation(var3));
         if(var4 == null) {
            throw new JsonParseException("Unknown generator: " + var3);
         } else {
            return (RandomIntGenerator)jsonDeserializationContext.deserialize(var2, var4);
         }
      }
   }

   public static JsonElement serialize(RandomIntGenerator randomIntGenerator, JsonSerializationContext jsonSerializationContext) {
      JsonElement jsonElement = jsonSerializationContext.serialize(randomIntGenerator);
      if(jsonElement.isJsonObject()) {
         jsonElement.getAsJsonObject().addProperty("type", randomIntGenerator.getType().toString());
      }

      return jsonElement;
   }

   static {
      GENERATORS.put(RandomIntGenerator.UNIFORM, RandomValueBounds.class);
      GENERATORS.put(RandomIntGenerator.BINOMIAL, BinomialDistributionGenerator.class);
      GENERATORS.put(RandomIntGenerator.CONSTANT, ConstantIntValue.class);
   }
}
