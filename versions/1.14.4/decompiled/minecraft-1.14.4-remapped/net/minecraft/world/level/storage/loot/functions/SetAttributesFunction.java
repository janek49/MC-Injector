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
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetAttributesFunction extends LootItemConditionalFunction {
   private final List modifiers;

   private SetAttributesFunction(LootItemCondition[] lootItemConditions, List list) {
      super(lootItemConditions);
      this.modifiers = ImmutableList.copyOf(list);
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      Random var3 = lootContext.getRandom();

      for(SetAttributesFunction.Modifier var5 : this.modifiers) {
         UUID var6 = var5.id;
         if(var6 == null) {
            var6 = UUID.randomUUID();
         }

         EquipmentSlot var7 = var5.slots[var3.nextInt(var5.slots.length)];
         var1.addAttributeModifier(var5.attribute, new AttributeModifier(var6, var5.name, (double)var5.amount.getFloat(var3), var5.operation), var7);
      }

      return var1;
   }

   static class Modifier {
      private final String name;
      private final String attribute;
      private final AttributeModifier.Operation operation;
      private final RandomValueBounds amount;
      @Nullable
      private final UUID id;
      private final EquipmentSlot[] slots;

      private Modifier(String name, String attribute, AttributeModifier.Operation operation, RandomValueBounds amount, EquipmentSlot[] slots, @Nullable UUID id) {
         this.name = name;
         this.attribute = attribute;
         this.operation = operation;
         this.amount = amount;
         this.id = id;
         this.slots = slots;
      }

      public JsonObject serialize(JsonSerializationContext jsonSerializationContext) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("name", this.name);
         jsonObject.addProperty("attribute", this.attribute);
         jsonObject.addProperty("operation", operationToString(this.operation));
         jsonObject.add("amount", jsonSerializationContext.serialize(this.amount));
         if(this.id != null) {
            jsonObject.addProperty("id", this.id.toString());
         }

         if(this.slots.length == 1) {
            jsonObject.addProperty("slot", this.slots[0].getName());
         } else {
            JsonArray var3 = new JsonArray();

            for(EquipmentSlot var7 : this.slots) {
               var3.add(new JsonPrimitive(var7.getName()));
            }

            jsonObject.add("slot", var3);
         }

         return jsonObject;
      }

      public static SetAttributesFunction.Modifier deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         String var2 = GsonHelper.getAsString(jsonObject, "name");
         String var3 = GsonHelper.getAsString(jsonObject, "attribute");
         AttributeModifier.Operation var4 = operationFromString(GsonHelper.getAsString(jsonObject, "operation"));
         RandomValueBounds var5 = (RandomValueBounds)GsonHelper.getAsObject(jsonObject, "amount", jsonDeserializationContext, RandomValueBounds.class);
         UUID var7 = null;
         EquipmentSlot[] vars6;
         if(GsonHelper.isStringValue(jsonObject, "slot")) {
            vars6 = new EquipmentSlot[]{EquipmentSlot.byName(GsonHelper.getAsString(jsonObject, "slot"))};
         } else {
            if(!GsonHelper.isArrayNode(jsonObject, "slot")) {
               throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
            }

            JsonArray var8 = GsonHelper.getAsJsonArray(jsonObject, "slot");
            vars6 = new EquipmentSlot[var8.size()];
            int var9 = 0;

            for(JsonElement var11 : var8) {
               vars6[var9++] = EquipmentSlot.byName(GsonHelper.convertToString(var11, "slot"));
            }

            if(vars6.length == 0) {
               throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
            }
         }

         if(jsonObject.has("id")) {
            String var8 = GsonHelper.getAsString(jsonObject, "id");

            try {
               var7 = UUID.fromString(var8);
            } catch (IllegalArgumentException var12) {
               throw new JsonSyntaxException("Invalid attribute modifier id \'" + var8 + "\' (must be UUID format, with dashes)");
            }
         }

         return new SetAttributesFunction.Modifier(var2, var3, var4, var5, vars6, var7);
      }

      private static String operationToString(AttributeModifier.Operation attributeModifier$Operation) {
         switch(attributeModifier$Operation) {
         case ADDITION:
            return "addition";
         case MULTIPLY_BASE:
            return "multiply_base";
         case MULTIPLY_TOTAL:
            return "multiply_total";
         default:
            throw new IllegalArgumentException("Unknown operation " + attributeModifier$Operation);
         }
      }

      private static AttributeModifier.Operation operationFromString(String string) {
         byte var2 = -1;
         switch(string.hashCode()) {
         case -1226589444:
            if(string.equals("addition")) {
               var2 = 0;
            }
            break;
         case -78229492:
            if(string.equals("multiply_base")) {
               var2 = 1;
            }
            break;
         case 1886894441:
            if(string.equals("multiply_total")) {
               var2 = 2;
            }
         }

         switch(var2) {
         case 0:
            return AttributeModifier.Operation.ADDITION;
         case 1:
            return AttributeModifier.Operation.MULTIPLY_BASE;
         case 2:
            return AttributeModifier.Operation.MULTIPLY_TOTAL;
         default:
            throw new JsonSyntaxException("Unknown attribute modifier operation " + string);
         }
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("set_attributes"), SetAttributesFunction.class);
      }

      public void serialize(JsonObject jsonObject, SetAttributesFunction setAttributesFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)setAttributesFunction, jsonSerializationContext);
         JsonArray var4 = new JsonArray();

         for(SetAttributesFunction.Modifier var6 : setAttributesFunction.modifiers) {
            var4.add(var6.serialize(jsonSerializationContext));
         }

         jsonObject.add("modifiers", var4);
      }

      public SetAttributesFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         JsonArray var4 = GsonHelper.getAsJsonArray(jsonObject, "modifiers");
         List<SetAttributesFunction.Modifier> var5 = Lists.newArrayListWithExpectedSize(var4.size());

         for(JsonElement var7 : var4) {
            var5.add(SetAttributesFunction.Modifier.deserialize(GsonHelper.convertToJsonObject(var7, "modifier"), jsonDeserializationContext));
         }

         if(var5.isEmpty()) {
            throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
         } else {
            return new SetAttributesFunction(lootItemConditions, var5);
         }
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
