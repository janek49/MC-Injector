package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetItemDamageFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RandomValueBounds damage;

   private SetItemDamageFunction(LootItemCondition[] lootItemConditions, RandomValueBounds damage) {
      super(lootItemConditions);
      this.damage = damage;
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      if(var1.isDamageableItem()) {
         float var3 = 1.0F - this.damage.getFloat(lootContext.getRandom());
         var1.setDamageValue(Mth.floor(var3 * (float)var1.getMaxDamage()));
      } else {
         LOGGER.warn("Couldn\'t set damage of loot item {}", var1);
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder setDamage(RandomValueBounds damage) {
      return simpleBuilder((lootItemConditions) -> {
         return new SetItemDamageFunction(lootItemConditions, damage);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("set_damage"), SetItemDamageFunction.class);
      }

      public void serialize(JsonObject jsonObject, SetItemDamageFunction setItemDamageFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)setItemDamageFunction, jsonSerializationContext);
         jsonObject.add("damage", jsonSerializationContext.serialize(setItemDamageFunction.damage));
      }

      public SetItemDamageFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         return new SetItemDamageFunction(lootItemConditions, (RandomValueBounds)GsonHelper.getAsObject(jsonObject, "damage", jsonDeserializationContext, RandomValueBounds.class));
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
