package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntLimiter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LimitCount extends LootItemConditionalFunction {
   private final IntLimiter limiter;

   private LimitCount(LootItemCondition[] lootItemConditions, IntLimiter limiter) {
      super(lootItemConditions);
      this.limiter = limiter;
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      int var3 = this.limiter.applyAsInt(var1.getCount());
      var1.setCount(var3);
      return var1;
   }

   public static LootItemConditionalFunction.Builder limitCount(IntLimiter intLimiter) {
      return simpleBuilder((lootItemConditions) -> {
         return new LimitCount(lootItemConditions, intLimiter);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("limit_count"), LimitCount.class);
      }

      public void serialize(JsonObject jsonObject, LimitCount limitCount, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)limitCount, jsonSerializationContext);
         jsonObject.add("limit", jsonSerializationContext.serialize(limitCount.limiter));
      }

      public LimitCount deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         IntLimiter var4 = (IntLimiter)GsonHelper.getAsObject(jsonObject, "limit", jsonDeserializationContext, IntLimiter.class);
         return new LimitCount(lootItemConditions, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
