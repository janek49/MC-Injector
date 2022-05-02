package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootPoolSingletonContainer extends LootPoolEntryContainer {
   protected final int weight;
   protected final int quality;
   protected final LootItemFunction[] functions;
   private final BiFunction compositeFunction;
   private final LootPoolEntry entry = new LootPoolSingletonContainer.EntryBase() {
      public void createItemStack(Consumer consumer, LootContext lootContext) {
         LootPoolSingletonContainer.this.createItemStack(LootItemFunction.decorate(LootPoolSingletonContainer.this.compositeFunction, consumer, lootContext), lootContext);
      }
   };

   protected LootPoolSingletonContainer(int weight, int quality, LootItemCondition[] lootItemConditions, LootItemFunction[] functions) {
      super(lootItemConditions);
      this.weight = weight;
      this.quality = quality;
      this.functions = functions;
      this.compositeFunction = LootItemFunctions.compose(functions);
   }

   public void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      super.validate(lootTableProblemCollector, function, set, lootContextParamSet);

      for(int var5 = 0; var5 < this.functions.length; ++var5) {
         this.functions[var5].validate(lootTableProblemCollector.forChild(".functions[" + var5 + "]"), function, set, lootContextParamSet);
      }

   }

   protected abstract void createItemStack(Consumer var1, LootContext var2);

   public boolean expand(LootContext lootContext, Consumer consumer) {
      if(this.canRun(lootContext)) {
         consumer.accept(this.entry);
         return true;
      } else {
         return false;
      }
   }

   public static LootPoolSingletonContainer.Builder simpleBuilder(LootPoolSingletonContainer.EntryConstructor lootPoolSingletonContainer$EntryConstructor) {
      return new LootPoolSingletonContainer.DummyBuilder(lootPoolSingletonContainer$EntryConstructor);
   }

   public abstract static class Builder extends LootPoolEntryContainer.Builder implements FunctionUserBuilder {
      protected int weight = 1;
      protected int quality = 0;
      private final List functions = Lists.newArrayList();

      public LootPoolSingletonContainer.Builder apply(LootItemFunction.Builder lootItemFunction$Builder) {
         this.functions.add(lootItemFunction$Builder.build());
         return (LootPoolSingletonContainer.Builder)this.getThis();
      }

      protected LootItemFunction[] getFunctions() {
         return (LootItemFunction[])this.functions.toArray(new LootItemFunction[0]);
      }

      public LootPoolSingletonContainer.Builder setWeight(int weight) {
         this.weight = weight;
         return (LootPoolSingletonContainer.Builder)this.getThis();
      }

      public LootPoolSingletonContainer.Builder setQuality(int quality) {
         this.quality = quality;
         return (LootPoolSingletonContainer.Builder)this.getThis();
      }

      // $FF: synthetic method
      public Object apply(LootItemFunction.Builder var1) {
         return this.apply(var1);
      }
   }

   static class DummyBuilder extends LootPoolSingletonContainer.Builder {
      private final LootPoolSingletonContainer.EntryConstructor constructor;

      public DummyBuilder(LootPoolSingletonContainer.EntryConstructor constructor) {
         this.constructor = constructor;
      }

      protected LootPoolSingletonContainer.DummyBuilder getThis() {
         return this;
      }

      public LootPoolEntryContainer build() {
         return this.constructor.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
      }

      // $FF: synthetic method
      protected LootPoolEntryContainer.Builder getThis() {
         return this.getThis();
      }
   }

   public abstract class EntryBase implements LootPoolEntry {
      public int getWeight(float f) {
         return Math.max(Mth.floor((float)LootPoolSingletonContainer.this.weight + (float)LootPoolSingletonContainer.this.quality * f), 0);
      }
   }

   @FunctionalInterface
   public interface EntryConstructor {
      LootPoolSingletonContainer build(int var1, int var2, LootItemCondition[] var3, LootItemFunction[] var4);
   }

   public abstract static class Serializer extends LootPoolEntryContainer.Serializer {
      public Serializer(ResourceLocation resourceLocation, Class class) {
         super(resourceLocation, class);
      }

      public void serialize(JsonObject jsonObject, LootPoolSingletonContainer lootPoolSingletonContainer, JsonSerializationContext jsonSerializationContext) {
         if(lootPoolSingletonContainer.weight != 1) {
            jsonObject.addProperty("weight", Integer.valueOf(lootPoolSingletonContainer.weight));
         }

         if(lootPoolSingletonContainer.quality != 0) {
            jsonObject.addProperty("quality", Integer.valueOf(lootPoolSingletonContainer.quality));
         }

         if(!ArrayUtils.isEmpty(lootPoolSingletonContainer.functions)) {
            jsonObject.add("functions", jsonSerializationContext.serialize(lootPoolSingletonContainer.functions));
         }

      }

      public final LootPoolSingletonContainer deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         int var4 = GsonHelper.getAsInt(jsonObject, "weight", 1);
         int var5 = GsonHelper.getAsInt(jsonObject, "quality", 0);
         LootItemFunction[] vars6 = (LootItemFunction[])GsonHelper.getAsObject(jsonObject, "functions", new LootItemFunction[0], jsonDeserializationContext, LootItemFunction[].class);
         return this.deserialize(jsonObject, jsonDeserializationContext, var4, var5, lootItemConditions, vars6);
      }

      protected abstract LootPoolSingletonContainer deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6);

      // $FF: synthetic method
      public LootPoolEntryContainer deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
