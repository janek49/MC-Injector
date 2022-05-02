package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemRandomChanceCondition implements LootItemCondition {
   private final float probability;

   private LootItemRandomChanceCondition(float probability) {
      this.probability = probability;
   }

   public boolean test(LootContext lootContext) {
      return lootContext.getRandom().nextFloat() < this.probability;
   }

   public static LootItemCondition.Builder randomChance(float f) {
      return () -> {
         return new LootItemRandomChanceCondition(f);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      protected Serializer() {
         super(new ResourceLocation("random_chance"), LootItemRandomChanceCondition.class);
      }

      public void serialize(JsonObject jsonObject, LootItemRandomChanceCondition lootItemRandomChanceCondition, JsonSerializationContext jsonSerializationContext) {
         jsonObject.addProperty("chance", Float.valueOf(lootItemRandomChanceCondition.probability));
      }

      public LootItemRandomChanceCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         return new LootItemRandomChanceCondition(GsonHelper.getAsFloat(jsonObject, "chance"));
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
