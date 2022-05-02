package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootingEnchantFunction extends LootItemConditionalFunction {
   private final RandomValueBounds value;
   private final int limit;

   private LootingEnchantFunction(LootItemCondition[] lootItemConditions, RandomValueBounds value, int limit) {
      super(lootItemConditions);
      this.value = value;
      this.limit = limit;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.KILLER_ENTITY);
   }

   private boolean hasLimit() {
      return this.limit > 0;
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      Entity var3 = (Entity)lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
      if(var3 instanceof LivingEntity) {
         int var4 = EnchantmentHelper.getMobLooting((LivingEntity)var3);
         if(var4 == 0) {
            return var1;
         }

         float var5 = (float)var4 * this.value.getFloat(lootContext.getRandom());
         var1.grow(Math.round(var5));
         if(this.hasLimit() && var1.getCount() > this.limit) {
            var1.setCount(this.limit);
         }
      }

      return var1;
   }

   public static LootingEnchantFunction.Builder lootingMultiplier(RandomValueBounds randomValueBounds) {
      return new LootingEnchantFunction.Builder(randomValueBounds);
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final RandomValueBounds count;
      private int limit = 0;

      public Builder(RandomValueBounds count) {
         this.count = count;
      }

      protected LootingEnchantFunction.Builder getThis() {
         return this;
      }

      public LootingEnchantFunction.Builder setLimit(int limit) {
         this.limit = limit;
         return this;
      }

      public LootItemFunction build() {
         return new LootingEnchantFunction(this.getConditions(), this.count, this.limit);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("looting_enchant"), LootingEnchantFunction.class);
      }

      public void serialize(JsonObject jsonObject, LootingEnchantFunction lootingEnchantFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)lootingEnchantFunction, jsonSerializationContext);
         jsonObject.add("count", jsonSerializationContext.serialize(lootingEnchantFunction.value));
         if(lootingEnchantFunction.hasLimit()) {
            jsonObject.add("limit", jsonSerializationContext.serialize(Integer.valueOf(lootingEnchantFunction.limit)));
         }

      }

      public LootingEnchantFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         int var4 = GsonHelper.getAsInt(jsonObject, "limit", 0);
         return new LootingEnchantFunction(lootItemConditions, (RandomValueBounds)GsonHelper.getAsObject(jsonObject, "count", jsonDeserializationContext, RandomValueBounds.class), var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
