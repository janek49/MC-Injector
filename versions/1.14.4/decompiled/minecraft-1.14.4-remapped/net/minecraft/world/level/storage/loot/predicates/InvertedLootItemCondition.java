package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class InvertedLootItemCondition implements LootItemCondition {
   private final LootItemCondition term;

   private InvertedLootItemCondition(LootItemCondition term) {
      this.term = term;
   }

   public final boolean test(LootContext lootContext) {
      return !this.term.test(lootContext);
   }

   public Set getReferencedContextParams() {
      return this.term.getReferencedContextParams();
   }

   public void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      super.validate(lootTableProblemCollector, function, set, lootContextParamSet);
      this.term.validate(lootTableProblemCollector, function, set, lootContextParamSet);
   }

   public static LootItemCondition.Builder invert(LootItemCondition.Builder lootItemCondition$Builder) {
      InvertedLootItemCondition var1 = new InvertedLootItemCondition(lootItemCondition$Builder.build());
      return () -> {
         return var1;
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      public Serializer() {
         super(new ResourceLocation("inverted"), InvertedLootItemCondition.class);
      }

      public void serialize(JsonObject jsonObject, InvertedLootItemCondition invertedLootItemCondition, JsonSerializationContext jsonSerializationContext) {
         jsonObject.add("term", jsonSerializationContext.serialize(invertedLootItemCondition.term));
      }

      public InvertedLootItemCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         LootItemCondition var3 = (LootItemCondition)GsonHelper.getAsObject(jsonObject, "term", jsonDeserializationContext, LootItemCondition.class);
         return new InvertedLootItemCondition(var3);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
