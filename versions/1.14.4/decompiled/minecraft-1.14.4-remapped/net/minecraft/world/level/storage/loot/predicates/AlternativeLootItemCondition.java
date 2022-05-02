package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public class AlternativeLootItemCondition implements LootItemCondition {
   private final LootItemCondition[] terms;
   private final Predicate composedPredicate;

   private AlternativeLootItemCondition(LootItemCondition[] terms) {
      this.terms = terms;
      this.composedPredicate = LootItemConditions.orConditions(terms);
   }

   public final boolean test(LootContext lootContext) {
      return this.composedPredicate.test(lootContext);
   }

   public void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      super.validate(lootTableProblemCollector, function, set, lootContextParamSet);

      for(int var5 = 0; var5 < this.terms.length; ++var5) {
         this.terms[var5].validate(lootTableProblemCollector.forChild(".term[" + var5 + "]"), function, set, lootContextParamSet);
      }

   }

   public static AlternativeLootItemCondition.Builder alternative(LootItemCondition.Builder... lootItemCondition$Builders) {
      return new AlternativeLootItemCondition.Builder(lootItemCondition$Builders);
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Builder implements LootItemCondition.Builder {
      private final List terms = Lists.newArrayList();

      public Builder(LootItemCondition.Builder... lootItemCondition$Builders) {
         for(LootItemCondition.Builder var5 : lootItemCondition$Builders) {
            this.terms.add(var5.build());
         }

      }

      public AlternativeLootItemCondition.Builder or(LootItemCondition.Builder lootItemCondition$Builder) {
         this.terms.add(lootItemCondition$Builder.build());
         return this;
      }

      public LootItemCondition build() {
         return new AlternativeLootItemCondition((LootItemCondition[])this.terms.toArray(new LootItemCondition[0]));
      }
   }

   public static class Serializer extends LootItemCondition.Serializer {
      public Serializer() {
         super(new ResourceLocation("alternative"), AlternativeLootItemCondition.class);
      }

      public void serialize(JsonObject jsonObject, AlternativeLootItemCondition alternativeLootItemCondition, JsonSerializationContext jsonSerializationContext) {
         jsonObject.add("terms", jsonSerializationContext.serialize(alternativeLootItemCondition.terms));
      }

      public AlternativeLootItemCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         LootItemCondition[] vars3 = (LootItemCondition[])GsonHelper.getAsObject(jsonObject, "terms", jsonDeserializationContext, LootItemCondition[].class);
         return new AlternativeLootItemCondition(vars3);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
