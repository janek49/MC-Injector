package net.minecraft.world.level.storage.loot.functions;

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
import java.util.function.BiFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.FillPlayerHead;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetAttributesFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.functions.SetContainerLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.SetLoreFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;

public class LootItemFunctions {
   private static final Map FUNCTIONS_BY_NAME = Maps.newHashMap();
   private static final Map FUNCTIONS_BY_CLASS = Maps.newHashMap();
   public static final BiFunction IDENTITY = (var0, lootContext) -> {
      return var0;
   };

   public static void register(LootItemFunction.Serializer lootItemFunction$Serializer) {
      ResourceLocation var1 = lootItemFunction$Serializer.getName();
      Class<T> var2 = lootItemFunction$Serializer.getFunctionClass();
      if(FUNCTIONS_BY_NAME.containsKey(var1)) {
         throw new IllegalArgumentException("Can\'t re-register item function name " + var1);
      } else if(FUNCTIONS_BY_CLASS.containsKey(var2)) {
         throw new IllegalArgumentException("Can\'t re-register item function class " + var2.getName());
      } else {
         FUNCTIONS_BY_NAME.put(var1, lootItemFunction$Serializer);
         FUNCTIONS_BY_CLASS.put(var2, lootItemFunction$Serializer);
      }
   }

   public static LootItemFunction.Serializer getSerializer(ResourceLocation resourceLocation) {
      LootItemFunction.Serializer<?> lootItemFunction$Serializer = (LootItemFunction.Serializer)FUNCTIONS_BY_NAME.get(resourceLocation);
      if(lootItemFunction$Serializer == null) {
         throw new IllegalArgumentException("Unknown loot item function \'" + resourceLocation + "\'");
      } else {
         return lootItemFunction$Serializer;
      }
   }

   public static LootItemFunction.Serializer getSerializer(LootItemFunction lootItemFunction) {
      LootItemFunction.Serializer<T> lootItemFunction$Serializer = (LootItemFunction.Serializer)FUNCTIONS_BY_CLASS.get(lootItemFunction.getClass());
      if(lootItemFunction$Serializer == null) {
         throw new IllegalArgumentException("Unknown loot item function " + lootItemFunction);
      } else {
         return lootItemFunction$Serializer;
      }
   }

   public static BiFunction compose(BiFunction[] biFunctions) {
      switch(biFunctions.length) {
      case 0:
         return IDENTITY;
      case 1:
         return biFunctions[0];
      case 2:
         BiFunction<ItemStack, LootContext, ItemStack> var1 = biFunctions[0];
         BiFunction<ItemStack, LootContext, ItemStack> var2 = biFunctions[1];
         return (var2x, lootContext) -> {
            return (ItemStack)var2.apply(var1.apply(var2x, lootContext), lootContext);
         };
      default:
         return (var1, lootContext) -> {
            for(BiFunction<ItemStack, LootContext, ItemStack> var6 : biFunctions) {
               var1 = (ItemStack)var6.apply(var1, lootContext);
            }

            return var1;
         };
      }
   }

   static {
      register(new SetItemCountFunction.Serializer());
      register(new EnchantWithLevelsFunction.Serializer());
      register(new EnchantRandomlyFunction.Serializer());
      register(new SetNbtFunction.Serializer());
      register(new SmeltItemFunction.Serializer());
      register(new LootingEnchantFunction.Serializer());
      register(new SetItemDamageFunction.Serializer());
      register(new SetAttributesFunction.Serializer());
      register(new SetNameFunction.Serializer());
      register(new ExplorationMapFunction.Serializer());
      register(new SetStewEffectFunction.Serializer());
      register(new CopyNameFunction.Serializer());
      register(new SetContainerContents.Serializer());
      register(new LimitCount.Serializer());
      register(new ApplyBonusCount.Serializer());
      register(new SetContainerLootTable.Serializer());
      register(new ApplyExplosionDecay.Serializer());
      register(new SetLoreFunction.Serializer());
      register(new FillPlayerHead.Serializer());
      register(new CopyNbtFunction.Serializer());
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public LootItemFunction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "function");
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var4, "function"));

         LootItemFunction.Serializer<?> var6;
         try {
            var6 = LootItemFunctions.getSerializer(var5);
         } catch (IllegalArgumentException var8) {
            throw new JsonSyntaxException("Unknown function \'" + var5 + "\'");
         }

         return var6.deserialize(var4, jsonDeserializationContext);
      }

      public JsonElement serialize(LootItemFunction lootItemFunction, Type type, JsonSerializationContext jsonSerializationContext) {
         LootItemFunction.Serializer<LootItemFunction> var4 = LootItemFunctions.getSerializer(lootItemFunction);
         JsonObject var5 = new JsonObject();
         var5.addProperty("function", var4.getName().toString());
         var4.serialize(var5, lootItemFunction, jsonSerializationContext);
         return var5;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootItemFunction)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
