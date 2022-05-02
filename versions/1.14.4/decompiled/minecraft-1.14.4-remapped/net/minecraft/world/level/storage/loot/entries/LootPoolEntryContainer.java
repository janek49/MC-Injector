package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.ComposableEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public abstract class LootPoolEntryContainer implements ComposableEntryContainer {
   protected final LootItemCondition[] conditions;
   private final Predicate compositeCondition;

   protected LootPoolEntryContainer(LootItemCondition[] conditions) {
      this.conditions = conditions;
      this.compositeCondition = LootItemConditions.andConditions(conditions);
   }

   public void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      for(int var5 = 0; var5 < this.conditions.length; ++var5) {
         this.conditions[var5].validate(lootTableProblemCollector.forChild(".condition[" + var5 + "]"), function, set, lootContextParamSet);
      }

   }

   protected final boolean canRun(LootContext lootContext) {
      return this.compositeCondition.test(lootContext);
   }

   public abstract static class Builder implements ConditionUserBuilder {
      private final List conditions = Lists.newArrayList();

      protected abstract LootPoolEntryContainer.Builder getThis();

      public LootPoolEntryContainer.Builder when(LootItemCondition.Builder lootItemCondition$Builder) {
         this.conditions.add(lootItemCondition$Builder.build());
         return this.getThis();
      }

      public final LootPoolEntryContainer.Builder unwrap() {
         return this.getThis();
      }

      protected LootItemCondition[] getConditions() {
         return (LootItemCondition[])this.conditions.toArray(new LootItemCondition[0]);
      }

      public AlternativesEntry.Builder otherwise(LootPoolEntryContainer.Builder lootPoolEntryContainer$Builder) {
         return new AlternativesEntry.Builder(new LootPoolEntryContainer.Builder[]{this, lootPoolEntryContainer$Builder});
      }

      public abstract LootPoolEntryContainer build();

      // $FF: synthetic method
      public Object unwrap() {
         return this.unwrap();
      }

      // $FF: synthetic method
      public Object when(LootItemCondition.Builder var1) {
         return this.when(var1);
      }
   }

   public abstract static class Serializer {
      private final ResourceLocation name;
      private final Class clazz;

      protected Serializer(ResourceLocation name, Class clazz) {
         this.name = name;
         this.clazz = clazz;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      public Class getContainerClass() {
         return this.clazz;
      }

      public abstract void serialize(JsonObject var1, LootPoolEntryContainer var2, JsonSerializationContext var3);

      public abstract LootPoolEntryContainer deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);
   }
}
