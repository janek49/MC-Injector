package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.predicates.AlternativeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.EntityHasScoreCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.predicates.WeatherCheck;

public class LootItemConditions {
   private static final Map CONDITIONS_BY_NAME = Maps.newHashMap();
   private static final Map CONDITIONS_BY_CLASS = Maps.newHashMap();

   public static void register(LootItemCondition.Serializer lootItemCondition$Serializer) {
      ResourceLocation var1 = lootItemCondition$Serializer.getName();
      Class<T> var2 = lootItemCondition$Serializer.getPredicateClass();
      if(CONDITIONS_BY_NAME.containsKey(var1)) {
         throw new IllegalArgumentException("Can\'t re-register item condition name " + var1);
      } else if(CONDITIONS_BY_CLASS.containsKey(var2)) {
         throw new IllegalArgumentException("Can\'t re-register item condition class " + var2.getName());
      } else {
         CONDITIONS_BY_NAME.put(var1, lootItemCondition$Serializer);
         CONDITIONS_BY_CLASS.put(var2, lootItemCondition$Serializer);
      }
   }

   public static LootItemCondition.Serializer getSerializer(ResourceLocation resourceLocation) {
      LootItemCondition.Serializer<?> lootItemCondition$Serializer = (LootItemCondition.Serializer)CONDITIONS_BY_NAME.get(resourceLocation);
      if(lootItemCondition$Serializer == null) {
         throw new IllegalArgumentException("Unknown loot item condition \'" + resourceLocation + "\'");
      } else {
         return lootItemCondition$Serializer;
      }
   }

   public static LootItemCondition.Serializer getSerializer(LootItemCondition lootItemCondition) {
      LootItemCondition.Serializer<T> lootItemCondition$Serializer = (LootItemCondition.Serializer)CONDITIONS_BY_CLASS.get(lootItemCondition.getClass());
      if(lootItemCondition$Serializer == null) {
         throw new IllegalArgumentException("Unknown loot item condition " + lootItemCondition);
      } else {
         return lootItemCondition$Serializer;
      }
   }

   public static Predicate andConditions(Predicate[] predicates) {
      switch(predicates.length) {
      case 0:
         return (object) -> {
            return true;
         };
      case 1:
         return predicates[0];
      case 2:
         return predicates[0].and(predicates[1]);
      default:
         return (object) -> {
            for(Predicate<T> var5 : predicates) {
               if(!var5.test(object)) {
                  return false;
               }
            }

            return true;
         };
      }
   }

   public static Predicate orConditions(Predicate[] predicates) {
      switch(predicates.length) {
      case 0:
         return (object) -> {
            return false;
         };
      case 1:
         return predicates[0];
      case 2:
         return predicates[0].or(predicates[1]);
      default:
         return (object) -> {
            for(Predicate<T> var5 : predicates) {
               if(var5.test(object)) {
                  return true;
               }
            }

            return false;
         };
      }
   }

   static {
      register(new InvertedLootItemCondition.Serializer());
      register(new AlternativeLootItemCondition.Serializer());
      register(new LootItemRandomChanceCondition.Serializer());
      register(new LootItemRandomChanceWithLootingCondition.Serializer());
      register(new LootItemEntityPropertyCondition.Serializer());
      register(new LootItemKilledByPlayerCondition.Serializer());
      register(new EntityHasScoreCondition.Serializer());
      register(new LootItemBlockStatePropertyCondition.Serializer());
      register(new MatchTool.Serializer());
      register(new BonusLevelTableCondition.Serializer());
      register(new ExplosionCondition.Serializer());
      register(new DamageSourceCondition.Serializer());
      register(new LocationCheck.Serializer());
      register(new WeatherCheck.Serializer());
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public LootItemCondition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "condition");
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var4, "condition"));

         LootItemCondition.Serializer<?> var6;
         try {
            var6 = LootItemConditions.getSerializer(var5);
         } catch (IllegalArgumentException var8) {
            throw new JsonSyntaxException("Unknown condition \'" + var5 + "\'");
         }

         return var6.deserialize(var4, jsonDeserializationContext);
      }

      public JsonElement serialize(LootItemCondition lootItemCondition, Type type, JsonSerializationContext jsonSerializationContext) {
         LootItemCondition.Serializer<LootItemCondition> var4 = LootItemConditions.getSerializer(lootItemCondition);
         JsonObject var5 = new JsonObject();
         var5.addProperty("condition", var4.getName().toString());
         var4.serialize(var5, lootItemCondition, jsonSerializationContext);
         return var5;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootItemCondition)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
