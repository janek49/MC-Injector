package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomIntGenerators;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetItemCountFunction extends LootItemConditionalFunction {
   private final RandomIntGenerator value;

   private SetItemCountFunction(LootItemCondition[] lootItemConditions, RandomIntGenerator value) {
      super(lootItemConditions);
      this.value = value;
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      var1.setCount(this.value.getInt(lootContext.getRandom()));
      return var1;
   }

   public static LootItemConditionalFunction.Builder setCount(RandomIntGenerator count) {
      return simpleBuilder((lootItemConditions) -> {
         return new SetItemCountFunction(lootItemConditions, count);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("set_count"), SetItemCountFunction.class);
      }

      public void serialize(JsonObject jsonObject, SetItemCountFunction setItemCountFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)setItemCountFunction, jsonSerializationContext);
         jsonObject.add("count", RandomIntGenerators.serialize(setItemCountFunction.value, jsonSerializationContext));
      }

      public SetItemCountFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         RandomIntGenerator var4 = RandomIntGenerators.deserialize(jsonObject.get("count"), jsonDeserializationContext);
         return new SetItemCountFunction(lootItemConditions, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
