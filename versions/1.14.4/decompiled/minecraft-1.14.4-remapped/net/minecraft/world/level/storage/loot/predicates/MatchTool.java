package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class MatchTool implements LootItemCondition {
   private final ItemPredicate predicate;

   public MatchTool(ItemPredicate predicate) {
      this.predicate = predicate;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   public boolean test(LootContext lootContext) {
      ItemStack var2 = (ItemStack)lootContext.getParamOrNull(LootContextParams.TOOL);
      return var2 != null && this.predicate.matches(var2);
   }

   public static LootItemCondition.Builder toolMatches(ItemPredicate.Builder itemPredicate$Builder) {
      return () -> {
         return new MatchTool(itemPredicate$Builder.build());
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      protected Serializer() {
         super(new ResourceLocation("match_tool"), MatchTool.class);
      }

      public void serialize(JsonObject jsonObject, MatchTool matchTool, JsonSerializationContext jsonSerializationContext) {
         jsonObject.add("predicate", matchTool.predicate.serializeToJson());
      }

      public MatchTool deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         ItemPredicate var3 = ItemPredicate.fromJson(jsonObject.get("predicate"));
         return new MatchTool(var3);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
