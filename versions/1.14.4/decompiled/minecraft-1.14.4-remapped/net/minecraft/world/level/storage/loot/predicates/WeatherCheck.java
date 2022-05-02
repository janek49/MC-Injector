package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class WeatherCheck implements LootItemCondition {
   @Nullable
   private final Boolean isRaining;
   @Nullable
   private final Boolean isThundering;

   private WeatherCheck(@Nullable Boolean isRaining, @Nullable Boolean isThundering) {
      this.isRaining = isRaining;
      this.isThundering = isThundering;
   }

   public boolean test(LootContext lootContext) {
      ServerLevel var2 = lootContext.getLevel();
      return this.isRaining != null && this.isRaining.booleanValue() != var2.isRaining()?false:this.isThundering == null || this.isThundering.booleanValue() == var2.isThundering();
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      public Serializer() {
         super(new ResourceLocation("weather_check"), WeatherCheck.class);
      }

      public void serialize(JsonObject jsonObject, WeatherCheck weatherCheck, JsonSerializationContext jsonSerializationContext) {
         jsonObject.addProperty("raining", weatherCheck.isRaining);
         jsonObject.addProperty("thundering", weatherCheck.isThundering);
      }

      public WeatherCheck deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         Boolean var3 = jsonObject.has("raining")?Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, "raining")):null;
         Boolean var4 = jsonObject.has("thundering")?Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, "thundering")):null;
         return new WeatherCheck(var3, var4);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
