package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyBonusCount extends LootItemConditionalFunction {
   private static final Map FORMULAS = Maps.newHashMap();
   private final Enchantment enchantment;
   private final ApplyBonusCount.Formula formula;

   private ApplyBonusCount(LootItemCondition[] lootItemConditions, Enchantment enchantment, ApplyBonusCount.Formula formula) {
      super(lootItemConditions);
      this.enchantment = enchantment;
      this.formula = formula;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      ItemStack var3 = (ItemStack)lootContext.getParamOrNull(LootContextParams.TOOL);
      if(var3 != null) {
         int var4 = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, var3);
         int var5 = this.formula.calculateNewCount(lootContext.getRandom(), var1.getCount(), var4);
         var1.setCount(var5);
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder addBonusBinomialDistributionCount(Enchantment enchantment, float var1, int var2) {
      return simpleBuilder((lootItemConditions) -> {
         return new ApplyBonusCount(lootItemConditions, enchantment, new ApplyBonusCount.BinomialWithBonusCount(var2, var1));
      });
   }

   public static LootItemConditionalFunction.Builder addOreBonusCount(Enchantment enchantment) {
      return simpleBuilder((lootItemConditions) -> {
         return new ApplyBonusCount(lootItemConditions, enchantment, new ApplyBonusCount.OreDrops());
      });
   }

   public static LootItemConditionalFunction.Builder addUniformBonusCount(Enchantment enchantment) {
      return simpleBuilder((lootItemConditions) -> {
         return new ApplyBonusCount(lootItemConditions, enchantment, new ApplyBonusCount.UniformBonusCount(1));
      });
   }

   public static LootItemConditionalFunction.Builder addUniformBonusCount(Enchantment enchantment, int var1) {
      return simpleBuilder((lootItemConditions) -> {
         return new ApplyBonusCount(lootItemConditions, enchantment, new ApplyBonusCount.UniformBonusCount(var1));
      });
   }

   static {
      FORMULAS.put(ApplyBonusCount.BinomialWithBonusCount.TYPE, ApplyBonusCount.BinomialWithBonusCount::deserialize);
      FORMULAS.put(ApplyBonusCount.OreDrops.TYPE, ApplyBonusCount.OreDrops::deserialize);
      FORMULAS.put(ApplyBonusCount.UniformBonusCount.TYPE, ApplyBonusCount.UniformBonusCount::deserialize);
   }

   static final class BinomialWithBonusCount implements ApplyBonusCount.Formula {
      public static final ResourceLocation TYPE = new ResourceLocation("binomial_with_bonus_count");
      private final int extraRounds;
      private final float probability;

      public BinomialWithBonusCount(int extraRounds, float probability) {
         this.extraRounds = extraRounds;
         this.probability = probability;
      }

      public int calculateNewCount(Random random, int var2, int var3) {
         for(int var4 = 0; var4 < var3 + this.extraRounds; ++var4) {
            if(random.nextFloat() < this.probability) {
               ++var2;
            }
         }

         return var2;
      }

      public void serializeParams(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
         jsonObject.addProperty("extra", Integer.valueOf(this.extraRounds));
         jsonObject.addProperty("probability", Float.valueOf(this.probability));
      }

      public static ApplyBonusCount.Formula deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         int var2 = GsonHelper.getAsInt(jsonObject, "extra");
         float var3 = GsonHelper.getAsFloat(jsonObject, "probability");
         return new ApplyBonusCount.BinomialWithBonusCount(var2, var3);
      }

      public ResourceLocation getType() {
         return TYPE;
      }
   }

   interface Formula {
      int calculateNewCount(Random var1, int var2, int var3);

      void serializeParams(JsonObject var1, JsonSerializationContext var2);

      ResourceLocation getType();
   }

   interface FormulaDeserializer {
      ApplyBonusCount.Formula deserialize(JsonObject var1, JsonDeserializationContext var2);
   }

   static final class OreDrops implements ApplyBonusCount.Formula {
      public static final ResourceLocation TYPE = new ResourceLocation("ore_drops");

      private OreDrops() {
      }

      public int calculateNewCount(Random random, int var2, int var3) {
         if(var3 > 0) {
            int var4 = random.nextInt(var3 + 2) - 1;
            if(var4 < 0) {
               var4 = 0;
            }

            return var2 * (var4 + 1);
         } else {
            return var2;
         }
      }

      public void serializeParams(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
      }

      public static ApplyBonusCount.Formula deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         return new ApplyBonusCount.OreDrops();
      }

      public ResourceLocation getType() {
         return TYPE;
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("apply_bonus"), ApplyBonusCount.class);
      }

      public void serialize(JsonObject jsonObject, ApplyBonusCount applyBonusCount, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)applyBonusCount, jsonSerializationContext);
         jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getKey(applyBonusCount.enchantment).toString());
         jsonObject.addProperty("formula", applyBonusCount.formula.getType().toString());
         JsonObject jsonObject = new JsonObject();
         applyBonusCount.formula.serializeParams(jsonObject, jsonSerializationContext);
         if(jsonObject.size() > 0) {
            jsonObject.add("parameters", jsonObject);
         }

      }

      public ApplyBonusCount deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "enchantment"));
         Enchantment var5 = (Enchantment)Registry.ENCHANTMENT.getOptional(var4).orElseThrow(() -> {
            return new JsonParseException("Invalid enchantment id: " + var4);
         });
         ResourceLocation var6 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "formula"));
         ApplyBonusCount.FormulaDeserializer var7 = (ApplyBonusCount.FormulaDeserializer)ApplyBonusCount.FORMULAS.get(var6);
         if(var7 == null) {
            throw new JsonParseException("Invalid formula id: " + var6);
         } else {
            ApplyBonusCount.Formula var8;
            if(jsonObject.has("parameters")) {
               var8 = var7.deserialize(GsonHelper.getAsJsonObject(jsonObject, "parameters"), jsonDeserializationContext);
            } else {
               var8 = var7.deserialize(new JsonObject(), jsonDeserializationContext);
            }

            return new ApplyBonusCount(lootItemConditions, var5, var8);
         }
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }

   static final class UniformBonusCount implements ApplyBonusCount.Formula {
      public static final ResourceLocation TYPE = new ResourceLocation("uniform_bonus_count");
      private final int bonusMultiplier;

      public UniformBonusCount(int bonusMultiplier) {
         this.bonusMultiplier = bonusMultiplier;
      }

      public int calculateNewCount(Random random, int var2, int var3) {
         return var2 + random.nextInt(this.bonusMultiplier * var3 + 1);
      }

      public void serializeParams(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
         jsonObject.addProperty("bonusMultiplier", Integer.valueOf(this.bonusMultiplier));
      }

      public static ApplyBonusCount.Formula deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         int var2 = GsonHelper.getAsInt(jsonObject, "bonusMultiplier");
         return new ApplyBonusCount.UniformBonusCount(var2);
      }

      public ResourceLocation getType() {
         return TYPE;
      }
   }
}
