package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemRandomChanceWithLootingCondition implements LootItemCondition {
   private final float percent;
   private final float lootingMultiplier;

   private LootItemRandomChanceWithLootingCondition(float percent, float lootingMultiplier) {
      this.percent = percent;
      this.lootingMultiplier = lootingMultiplier;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.KILLER_ENTITY);
   }

   public boolean test(LootContext lootContext) {
      Entity var2 = (Entity)lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
      int var3 = 0;
      if(var2 instanceof LivingEntity) {
         var3 = EnchantmentHelper.getMobLooting((LivingEntity)var2);
      }

      return lootContext.getRandom().nextFloat() < this.percent + (float)var3 * this.lootingMultiplier;
   }

   public static LootItemCondition.Builder randomChanceAndLootingBoost(float var0, float var1) {
      return () -> {
         return new LootItemRandomChanceWithLootingCondition(var0, var1);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      protected Serializer() {
         super(new ResourceLocation("random_chance_with_looting"), LootItemRandomChanceWithLootingCondition.class);
      }

      public void serialize(JsonObject jsonObject, LootItemRandomChanceWithLootingCondition lootItemRandomChanceWithLootingCondition, JsonSerializationContext jsonSerializationContext) {
         jsonObject.addProperty("chance", Float.valueOf(lootItemRandomChanceWithLootingCondition.percent));
         jsonObject.addProperty("looting_multiplier", Float.valueOf(lootItemRandomChanceWithLootingCondition.lootingMultiplier));
      }

      public LootItemRandomChanceWithLootingCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         return new LootItemRandomChanceWithLootingCondition(GsonHelper.getAsFloat(jsonObject, "chance"), GsonHelper.getAsFloat(jsonObject, "looting_multiplier"));
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
