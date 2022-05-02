package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomIntGenerators;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
   private final LootPoolEntryContainer[] entries;
   private final LootItemCondition[] conditions;
   private final Predicate compositeCondition;
   private final LootItemFunction[] functions;
   private final BiFunction compositeFunction;
   private final RandomIntGenerator rolls;
   private final RandomValueBounds bonusRolls;

   private LootPool(LootPoolEntryContainer[] entries, LootItemCondition[] conditions, LootItemFunction[] functions, RandomIntGenerator rolls, RandomValueBounds bonusRolls) {
      this.entries = entries;
      this.conditions = conditions;
      this.compositeCondition = LootItemConditions.andConditions(conditions);
      this.functions = functions;
      this.compositeFunction = LootItemFunctions.compose(functions);
      this.rolls = rolls;
      this.bonusRolls = bonusRolls;
   }

   private void addRandomItem(Consumer consumer, LootContext lootContext) {
      Random var3 = lootContext.getRandom();
      List<LootPoolEntry> var4 = Lists.newArrayList();
      MutableInt var5 = new MutableInt();

      for(LootPoolEntryContainer var9 : this.entries) {
         var9.expand(lootContext, (lootPoolEntry) -> {
            int var4 = lootPoolEntry.getWeight(lootContext.getLuck());
            if(var4 > 0) {
               var4.add(lootPoolEntry);
               var5.add(var4);
            }

         });
      }

      int var6 = var4.size();
      if(var5.intValue() != 0 && var6 != 0) {
         if(var6 == 1) {
            ((LootPoolEntry)var4.get(0)).createItemStack(consumer, lootContext);
         } else {
            int var7 = var3.nextInt(var5.intValue());

            for(LootPoolEntry var9 : var4) {
               var7 -= var9.getWeight(lootContext.getLuck());
               if(var7 < 0) {
                  var9.createItemStack(consumer, lootContext);
                  return;
               }
            }

         }
      }
   }

   public void addRandomItems(Consumer consumer, LootContext lootContext) {
      if(this.compositeCondition.test(lootContext)) {
         Consumer<ItemStack> consumer = LootItemFunction.decorate(this.compositeFunction, consumer, lootContext);
         Random var4 = lootContext.getRandom();
         int var5 = this.rolls.getInt(var4) + Mth.floor(this.bonusRolls.getFloat(var4) * lootContext.getLuck());

         for(int var6 = 0; var6 < var5; ++var6) {
            this.addRandomItem(consumer, lootContext);
         }

      }
   }

   public void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      for(int var5 = 0; var5 < this.conditions.length; ++var5) {
         this.conditions[var5].validate(lootTableProblemCollector.forChild(".condition[" + var5 + "]"), function, set, lootContextParamSet);
      }

      for(int var5 = 0; var5 < this.functions.length; ++var5) {
         this.functions[var5].validate(lootTableProblemCollector.forChild(".functions[" + var5 + "]"), function, set, lootContextParamSet);
      }

      for(int var5 = 0; var5 < this.entries.length; ++var5) {
         this.entries[var5].validate(lootTableProblemCollector.forChild(".entries[" + var5 + "]"), function, set, lootContextParamSet);
      }

   }

   public static LootPool.Builder lootPool() {
      return new LootPool.Builder();
   }

   public static class Builder implements FunctionUserBuilder, ConditionUserBuilder {
      private final List entries = Lists.newArrayList();
      private final List conditions = Lists.newArrayList();
      private final List functions = Lists.newArrayList();
      private RandomIntGenerator rolls = new RandomValueBounds(1.0F);
      private RandomValueBounds bonusRolls = new RandomValueBounds(0.0F, 0.0F);

      public LootPool.Builder setRolls(RandomIntGenerator rolls) {
         this.rolls = rolls;
         return this;
      }

      public LootPool.Builder unwrap() {
         return this;
      }

      public LootPool.Builder add(LootPoolEntryContainer.Builder lootPoolEntryContainer$Builder) {
         this.entries.add(lootPoolEntryContainer$Builder.build());
         return this;
      }

      public LootPool.Builder when(LootItemCondition.Builder lootItemCondition$Builder) {
         this.conditions.add(lootItemCondition$Builder.build());
         return this;
      }

      public LootPool.Builder apply(LootItemFunction.Builder lootItemFunction$Builder) {
         this.functions.add(lootItemFunction$Builder.build());
         return this;
      }

      public LootPool build() {
         if(this.rolls == null) {
            throw new IllegalArgumentException("Rolls not set");
         } else {
            return new LootPool((LootPoolEntryContainer[])this.entries.toArray(new LootPoolEntryContainer[0]), (LootItemCondition[])this.conditions.toArray(new LootItemCondition[0]), (LootItemFunction[])this.functions.toArray(new LootItemFunction[0]), this.rolls, this.bonusRolls);
         }
      }

      // $FF: synthetic method
      public Object unwrap() {
         return this.unwrap();
      }

      // $FF: synthetic method
      public Object apply(LootItemFunction.Builder var1) {
         return this.apply(var1);
      }

      // $FF: synthetic method
      public Object when(LootItemCondition.Builder var1) {
         return this.when(var1);
      }
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public LootPool deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "loot pool");
         LootPoolEntryContainer[] vars5 = (LootPoolEntryContainer[])GsonHelper.getAsObject(var4, "entries", jsonDeserializationContext, LootPoolEntryContainer[].class);
         LootItemCondition[] vars6 = (LootItemCondition[])GsonHelper.getAsObject(var4, "conditions", new LootItemCondition[0], jsonDeserializationContext, LootItemCondition[].class);
         LootItemFunction[] vars7 = (LootItemFunction[])GsonHelper.getAsObject(var4, "functions", new LootItemFunction[0], jsonDeserializationContext, LootItemFunction[].class);
         RandomIntGenerator var8 = RandomIntGenerators.deserialize(var4.get("rolls"), jsonDeserializationContext);
         RandomValueBounds var9 = (RandomValueBounds)GsonHelper.getAsObject(var4, "bonus_rolls", new RandomValueBounds(0.0F, 0.0F), jsonDeserializationContext, RandomValueBounds.class);
         return new LootPool(vars5, vars6, vars7, var8, var9);
      }

      public JsonElement serialize(LootPool lootPool, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         var4.add("rolls", RandomIntGenerators.serialize(lootPool.rolls, jsonSerializationContext));
         var4.add("entries", jsonSerializationContext.serialize(lootPool.entries));
         if(lootPool.bonusRolls.getMin() != 0.0F && lootPool.bonusRolls.getMax() != 0.0F) {
            var4.add("bonus_rolls", jsonSerializationContext.serialize(lootPool.bonusRolls));
         }

         if(!ArrayUtils.isEmpty(lootPool.conditions)) {
            var4.add("conditions", jsonSerializationContext.serialize(lootPool.conditions));
         }

         if(!ArrayUtils.isEmpty(lootPool.functions)) {
            var4.add("functions", jsonSerializationContext.serialize(lootPool.functions));
         }

         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootPool)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
