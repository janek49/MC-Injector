package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantRandomlyFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List enchantments;

   private EnchantRandomlyFunction(LootItemCondition[] lootItemConditions, Collection collection) {
      super(lootItemConditions);
      this.enchantments = ImmutableList.copyOf(collection);
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      Random var4 = lootContext.getRandom();
      Enchantment var3;
      if(this.enchantments.isEmpty()) {
         List<Enchantment> var5 = Lists.newArrayList();

         for(Enchantment var7 : Registry.ENCHANTMENT) {
            if(var1.getItem() == Items.BOOK || var7.canEnchant(var1)) {
               var5.add(var7);
            }
         }

         if(var5.isEmpty()) {
            LOGGER.warn("Couldn\'t find a compatible enchantment for {}", var1);
            return var1;
         }

         var3 = (Enchantment)var5.get(var4.nextInt(var5.size()));
      } else {
         var3 = (Enchantment)this.enchantments.get(var4.nextInt(this.enchantments.size()));
      }

      int var5 = Mth.nextInt(var4, var3.getMinLevel(), var3.getMaxLevel());
      if(var1.getItem() == Items.BOOK) {
         var1 = new ItemStack(Items.ENCHANTED_BOOK);
         EnchantedBookItem.addEnchantment(var1, new EnchantmentInstance(var3, var5));
      } else {
         var1.enchant(var3, var5);
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder randomApplicableEnchantment() {
      return simpleBuilder((lootItemConditions) -> {
         return new EnchantRandomlyFunction(lootItemConditions, ImmutableList.of());
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("enchant_randomly"), EnchantRandomlyFunction.class);
      }

      public void serialize(JsonObject jsonObject, EnchantRandomlyFunction enchantRandomlyFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)enchantRandomlyFunction, jsonSerializationContext);
         if(!enchantRandomlyFunction.enchantments.isEmpty()) {
            JsonArray var4 = new JsonArray();

            for(Enchantment var6 : enchantRandomlyFunction.enchantments) {
               ResourceLocation var7 = Registry.ENCHANTMENT.getKey(var6);
               if(var7 == null) {
                  throw new IllegalArgumentException("Don\'t know how to serialize enchantment " + var6);
               }

               var4.add(new JsonPrimitive(var7.toString()));
            }

            jsonObject.add("enchantments", var4);
         }

      }

      public EnchantRandomlyFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         List<Enchantment> var4 = Lists.newArrayList();
         if(jsonObject.has("enchantments")) {
            for(JsonElement var7 : GsonHelper.getAsJsonArray(jsonObject, "enchantments")) {
               String var8 = GsonHelper.convertToString(var7, "enchantment");
               Enchantment var9 = (Enchantment)Registry.ENCHANTMENT.getOptional(new ResourceLocation(var8)).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown enchantment \'" + var8 + "\'");
               });
               var4.add(var9);
            }
         }

         return new EnchantRandomlyFunction(lootItemConditions, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
