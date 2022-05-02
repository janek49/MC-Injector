package net.minecraft.world.level.storage.loot.functions;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootItemConditionalFunction implements LootItemFunction {
   protected final LootItemCondition[] predicates;
   private final Predicate compositePredicates;

   protected LootItemConditionalFunction(LootItemCondition[] predicates) {
      this.predicates = predicates;
      this.compositePredicates = LootItemConditions.andConditions(predicates);
   }

   public final ItemStack apply(ItemStack var1, LootContext lootContext) {
      return this.compositePredicates.test(lootContext)?this.run(var1, lootContext):var1;
   }

   protected abstract ItemStack run(ItemStack var1, LootContext var2);

   public void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      super.validate(lootTableProblemCollector, function, set, lootContextParamSet);

      for(int var5 = 0; var5 < this.predicates.length; ++var5) {
         this.predicates[var5].validate(lootTableProblemCollector.forChild(".conditions[" + var5 + "]"), function, set, lootContextParamSet);
      }

   }

   protected static LootItemConditionalFunction.Builder simpleBuilder(Function function) {
      return new LootItemConditionalFunction.DummyBuilder(function);
   }

   // $FF: synthetic method
   public Object apply(Object var1, Object var2) {
      return this.apply((ItemStack)var1, (LootContext)var2);
   }

   public abstract static class Builder implements LootItemFunction.Builder, ConditionUserBuilder {
      private final List conditions = Lists.newArrayList();

      public LootItemConditionalFunction.Builder when(LootItemCondition.Builder lootItemCondition$Builder) {
         this.conditions.add(lootItemCondition$Builder.build());
         return this.getThis();
      }

      public final LootItemConditionalFunction.Builder unwrap() {
         return this.getThis();
      }

      protected abstract LootItemConditionalFunction.Builder getThis();

      protected LootItemCondition[] getConditions() {
         return (LootItemCondition[])this.conditions.toArray(new LootItemCondition[0]);
      }

      // $FF: synthetic method
      public Object unwrap() {
         return this.unwrap();
      }

      // $FF: synthetic method
      public Object when(LootItemCondition.Builder var1) {
         return this.when(var1);
      }
   }

   static final class DummyBuilder extends LootItemConditionalFunction.Builder {
      private final Function constructor;

      public DummyBuilder(Function constructor) {
         this.constructor = constructor;
      }

      protected LootItemConditionalFunction.DummyBuilder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return (LootItemFunction)this.constructor.apply(this.getConditions());
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public abstract static class Serializer extends LootItemFunction.Serializer {
      public Serializer(ResourceLocation resourceLocation, Class class) {
         super(resourceLocation, class);
      }

      public void serialize(JsonObject jsonObject, LootItemConditionalFunction lootItemConditionalFunction, JsonSerializationContext jsonSerializationContext) {
         if(!ArrayUtils.isEmpty(lootItemConditionalFunction.predicates)) {
            jsonObject.add("conditions", jsonSerializationContext.serialize(lootItemConditionalFunction.predicates));
         }

      }

      public final LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         LootItemCondition[] vars3 = (LootItemCondition[])GsonHelper.getAsObject(jsonObject, "conditions", new LootItemCondition[0], jsonDeserializationContext, LootItemCondition[].class);
         return this.deserialize(jsonObject, jsonDeserializationContext, vars3);
      }

      public abstract LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);

      // $FF: synthetic method
      public LootItemFunction deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
