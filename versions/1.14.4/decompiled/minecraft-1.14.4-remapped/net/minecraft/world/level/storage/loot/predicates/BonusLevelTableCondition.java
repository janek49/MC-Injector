package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class BonusLevelTableCondition implements LootItemCondition {
   private final Enchantment enchantment;
   private final float[] values;

   private BonusLevelTableCondition(Enchantment enchantment, float[] values) {
      this.enchantment = enchantment;
      this.values = values;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   public boolean test(LootContext lootContext) {
      ItemStack var2 = (ItemStack)lootContext.getParamOrNull(LootContextParams.TOOL);
      int var3 = var2 != null?EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, var2):0;
      float var4 = this.values[Math.min(var3, this.values.length - 1)];
      return lootContext.getRandom().nextFloat() < var4;
   }

   public static LootItemCondition.Builder bonusLevelFlatChance(Enchantment enchantment, float... floats) {
      return () -> {
         return new BonusLevelTableCondition(enchantment, floats);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      public Serializer() {
         super(new ResourceLocation("table_bonus"), BonusLevelTableCondition.class);
      }

      public void serialize(JsonObject jsonObject, BonusLevelTableCondition bonusLevelTableCondition, JsonSerializationContext jsonSerializationContext) {
         jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getKey(bonusLevelTableCondition.enchantment).toString());
         jsonObject.add("chances", jsonSerializationContext.serialize(bonusLevelTableCondition.values));
      }

      public BonusLevelTableCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "enchantment"));
         Enchantment var4 = (Enchantment)Registry.ENCHANTMENT.getOptional(var3).orElseThrow(() -> {
            return new JsonParseException("Invalid enchantment id: " + var3);
         });
         float[] vars5 = (float[])GsonHelper.getAsObject(jsonObject, "chances", jsonDeserializationContext, float[].class);
         return new BonusLevelTableCondition(var4, vars5);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
