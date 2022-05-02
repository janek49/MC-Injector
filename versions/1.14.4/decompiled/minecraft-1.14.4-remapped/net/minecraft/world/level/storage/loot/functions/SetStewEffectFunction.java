package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetStewEffectFunction extends LootItemConditionalFunction {
   private final Map effectDurationMap;

   private SetStewEffectFunction(LootItemCondition[] lootItemConditions, Map map) {
      super(lootItemConditions);
      this.effectDurationMap = ImmutableMap.copyOf(map);
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      if(var1.getItem() == Items.SUSPICIOUS_STEW && !this.effectDurationMap.isEmpty()) {
         Random var3 = lootContext.getRandom();
         int var4 = var3.nextInt(this.effectDurationMap.size());
         Entry<MobEffect, RandomValueBounds> var5 = (Entry)Iterables.get(this.effectDurationMap.entrySet(), var4);
         MobEffect var6 = (MobEffect)var5.getKey();
         int var7 = ((RandomValueBounds)var5.getValue()).getInt(var3);
         if(!var6.isInstantenous()) {
            var7 *= 20;
         }

         SuspiciousStewItem.saveMobEffect(var1, var6, var7);
         return var1;
      } else {
         return var1;
      }
   }

   public static SetStewEffectFunction.Builder stewEffect() {
      return new SetStewEffectFunction.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final Map effectDurationMap = Maps.newHashMap();

      protected SetStewEffectFunction.Builder getThis() {
         return this;
      }

      public SetStewEffectFunction.Builder withEffect(MobEffect mobEffect, RandomValueBounds randomValueBounds) {
         this.effectDurationMap.put(mobEffect, randomValueBounds);
         return this;
      }

      public LootItemFunction build() {
         return new SetStewEffectFunction(this.getConditions(), this.effectDurationMap);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("set_stew_effect"), SetStewEffectFunction.class);
      }

      public void serialize(JsonObject jsonObject, SetStewEffectFunction setStewEffectFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)setStewEffectFunction, jsonSerializationContext);
         if(!setStewEffectFunction.effectDurationMap.isEmpty()) {
            JsonArray var4 = new JsonArray();

            for(MobEffect var6 : setStewEffectFunction.effectDurationMap.keySet()) {
               JsonObject var7 = new JsonObject();
               ResourceLocation var8 = Registry.MOB_EFFECT.getKey(var6);
               if(var8 == null) {
                  throw new IllegalArgumentException("Don\'t know how to serialize mob effect " + var6);
               }

               var7.add("type", new JsonPrimitive(var8.toString()));
               var7.add("duration", jsonSerializationContext.serialize(setStewEffectFunction.effectDurationMap.get(var6)));
               var4.add(var7);
            }

            jsonObject.add("effects", var4);
         }

      }

      public SetStewEffectFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         Map<MobEffect, RandomValueBounds> var4 = Maps.newHashMap();
         if(jsonObject.has("effects")) {
            for(JsonElement var7 : GsonHelper.getAsJsonArray(jsonObject, "effects")) {
               String var8 = GsonHelper.getAsString(var7.getAsJsonObject(), "type");
               MobEffect var9 = (MobEffect)Registry.MOB_EFFECT.getOptional(new ResourceLocation(var8)).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown mob effect \'" + var8 + "\'");
               });
               RandomValueBounds var10 = (RandomValueBounds)GsonHelper.getAsObject(var7.getAsJsonObject(), "duration", jsonDeserializationContext, RandomValueBounds.class);
               var4.put(var9, var10);
            }
         }

         return new SetStewEffectFunction(lootItemConditions, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
