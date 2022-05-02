package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomIntGenerators;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EnchantWithLevelsFunction extends LootItemConditionalFunction {
   private final RandomIntGenerator levels;
   private final boolean treasure;

   private EnchantWithLevelsFunction(LootItemCondition[] lootItemConditions, RandomIntGenerator levels, boolean treasure) {
      super(lootItemConditions);
      this.levels = levels;
      this.treasure = treasure;
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      Random var3 = lootContext.getRandom();
      return EnchantmentHelper.enchantItem(var3, var1, this.levels.getInt(var3), this.treasure);
   }

   public static EnchantWithLevelsFunction.Builder enchantWithLevels(RandomIntGenerator randomIntGenerator) {
      return new EnchantWithLevelsFunction.Builder(randomIntGenerator);
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final RandomIntGenerator levels;
      private boolean treasure;

      public Builder(RandomIntGenerator levels) {
         this.levels = levels;
      }

      protected EnchantWithLevelsFunction.Builder getThis() {
         return this;
      }

      public EnchantWithLevelsFunction.Builder allowTreasure() {
         this.treasure = true;
         return this;
      }

      public LootItemFunction build() {
         return new EnchantWithLevelsFunction(this.getConditions(), this.levels, this.treasure);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("enchant_with_levels"), EnchantWithLevelsFunction.class);
      }

      public void serialize(JsonObject jsonObject, EnchantWithLevelsFunction enchantWithLevelsFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)enchantWithLevelsFunction, jsonSerializationContext);
         jsonObject.add("levels", RandomIntGenerators.serialize(enchantWithLevelsFunction.levels, jsonSerializationContext));
         jsonObject.addProperty("treasure", Boolean.valueOf(enchantWithLevelsFunction.treasure));
      }

      public EnchantWithLevelsFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         RandomIntGenerator var4 = RandomIntGenerators.deserialize(jsonObject.get("levels"), jsonDeserializationContext);
         boolean var5 = GsonHelper.getAsBoolean(jsonObject, "treasure", false);
         return new EnchantWithLevelsFunction(lootItemConditions, var4, var5);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
