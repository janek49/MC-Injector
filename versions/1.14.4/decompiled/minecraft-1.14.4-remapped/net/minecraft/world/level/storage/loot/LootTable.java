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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LootTable EMPTY = new LootTable(LootContextParamSets.EMPTY, new LootPool[0], new LootItemFunction[0]);
   public static final LootContextParamSet DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
   private final LootContextParamSet paramSet;
   private final LootPool[] pools;
   private final LootItemFunction[] functions;
   private final BiFunction compositeFunction;

   private LootTable(LootContextParamSet paramSet, LootPool[] pools, LootItemFunction[] functions) {
      this.paramSet = paramSet;
      this.pools = pools;
      this.functions = functions;
      this.compositeFunction = LootItemFunctions.compose(functions);
   }

   public static Consumer createStackSplitter(Consumer consumer) {
      return (itemStack) -> {
         if(itemStack.getCount() < itemStack.getMaxStackSize()) {
            consumer.accept(itemStack);
         } else {
            int var2 = itemStack.getCount();

            while(var2 > 0) {
               ItemStack var3 = itemStack.copy();
               var3.setCount(Math.min(itemStack.getMaxStackSize(), var2));
               var2 -= var3.getCount();
               consumer.accept(var3);
            }
         }

      };
   }

   public void getRandomItemsRaw(LootContext lootContext, Consumer consumer) {
      if(lootContext.addVisitedTable(this)) {
         Consumer<ItemStack> consumer = LootItemFunction.decorate(this.compositeFunction, consumer, lootContext);

         for(LootPool var7 : this.pools) {
            var7.addRandomItems(consumer, lootContext);
         }

         lootContext.removeVisitedTable(this);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }

   }

   public void getRandomItems(LootContext lootContext, Consumer consumer) {
      this.getRandomItemsRaw(lootContext, createStackSplitter(consumer));
   }

   public List getRandomItems(LootContext lootContext) {
      List<ItemStack> list = Lists.newArrayList();
      this.getRandomItems(lootContext, list::add);
      return list;
   }

   public LootContextParamSet getParamSet() {
      return this.paramSet;
   }

   public void validate(LootTableProblemCollector lootTableProblemCollector, Function function, Set set, LootContextParamSet lootContextParamSet) {
      for(int var5 = 0; var5 < this.pools.length; ++var5) {
         this.pools[var5].validate(lootTableProblemCollector.forChild(".pools[" + var5 + "]"), function, set, lootContextParamSet);
      }

      for(int var5 = 0; var5 < this.functions.length; ++var5) {
         this.functions[var5].validate(lootTableProblemCollector.forChild(".functions[" + var5 + "]"), function, set, lootContextParamSet);
      }

   }

   public void fill(Container container, LootContext lootContext) {
      List<ItemStack> var3 = this.getRandomItems(lootContext);
      Random var4 = lootContext.getRandom();
      List<Integer> var5 = this.getAvailableSlots(container, var4);
      this.shuffleAndSplitItems(var3, var5.size(), var4);

      for(ItemStack var7 : var3) {
         if(var5.isEmpty()) {
            LOGGER.warn("Tried to over-fill a container");
            return;
         }

         if(var7.isEmpty()) {
            container.setItem(((Integer)var5.remove(var5.size() - 1)).intValue(), ItemStack.EMPTY);
         } else {
            container.setItem(((Integer)var5.remove(var5.size() - 1)).intValue(), var7);
         }
      }

   }

   private void shuffleAndSplitItems(List list, int var2, Random random) {
      List<ItemStack> list = Lists.newArrayList();
      Iterator<ItemStack> var5 = list.iterator();

      while(var5.hasNext()) {
         ItemStack var6 = (ItemStack)var5.next();
         if(var6.isEmpty()) {
            var5.remove();
         } else if(var6.getCount() > 1) {
            list.add(var6);
            var5.remove();
         }
      }

      while(var2 - list.size() - ((List)list).size() > 0 && !((List)list).isEmpty()) {
         ItemStack var5 = (ItemStack)list.remove(Mth.nextInt(random, 0, list.size() - 1));
         int var6 = Mth.nextInt(random, 1, var5.getCount() / 2);
         ItemStack var7 = var5.split(var6);
         if(var5.getCount() > 1 && random.nextBoolean()) {
            list.add(var5);
         } else {
            list.add(var5);
         }

         if(var7.getCount() > 1 && random.nextBoolean()) {
            list.add(var7);
         } else {
            list.add(var7);
         }
      }

      list.addAll(list);
      Collections.shuffle(list, random);
   }

   private List getAvailableSlots(Container container, Random random) {
      List<Integer> list = Lists.newArrayList();

      for(int var4 = 0; var4 < container.getContainerSize(); ++var4) {
         if(container.getItem(var4).isEmpty()) {
            list.add(Integer.valueOf(var4));
         }
      }

      Collections.shuffle(list, random);
      return list;
   }

   public static LootTable.Builder lootTable() {
      return new LootTable.Builder();
   }

   public static class Builder implements FunctionUserBuilder {
      private final List pools = Lists.newArrayList();
      private final List functions = Lists.newArrayList();
      private LootContextParamSet paramSet = LootTable.DEFAULT_PARAM_SET;

      public LootTable.Builder withPool(LootPool.Builder lootPool$Builder) {
         this.pools.add(lootPool$Builder.build());
         return this;
      }

      public LootTable.Builder setParamSet(LootContextParamSet paramSet) {
         this.paramSet = paramSet;
         return this;
      }

      public LootTable.Builder apply(LootItemFunction.Builder lootItemFunction$Builder) {
         this.functions.add(lootItemFunction$Builder.build());
         return this;
      }

      public LootTable.Builder unwrap() {
         return this;
      }

      public LootTable build() {
         return new LootTable(this.paramSet, (LootPool[])this.pools.toArray(new LootPool[0]), (LootItemFunction[])this.functions.toArray(new LootItemFunction[0]));
      }

      // $FF: synthetic method
      public Object unwrap() {
         return this.unwrap();
      }

      // $FF: synthetic method
      public Object apply(LootItemFunction.Builder var1) {
         return this.apply(var1);
      }
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public LootTable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "loot table");
         LootPool[] vars5 = (LootPool[])GsonHelper.getAsObject(var4, "pools", new LootPool[0], jsonDeserializationContext, LootPool[].class);
         LootContextParamSet var6 = null;
         if(var4.has("type")) {
            String var7 = GsonHelper.getAsString(var4, "type");
            var6 = LootContextParamSets.get(new ResourceLocation(var7));
         }

         LootItemFunction[] vars7 = (LootItemFunction[])GsonHelper.getAsObject(var4, "functions", new LootItemFunction[0], jsonDeserializationContext, LootItemFunction[].class);
         return new LootTable(var6 != null?var6:LootContextParamSets.ALL_PARAMS, vars5, vars7);
      }

      public JsonElement serialize(LootTable lootTable, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         if(lootTable.paramSet != LootTable.DEFAULT_PARAM_SET) {
            ResourceLocation var5 = LootContextParamSets.getKey(lootTable.paramSet);
            if(var5 != null) {
               var4.addProperty("type", var5.toString());
            } else {
               LootTable.LOGGER.warn("Failed to find id for param set " + lootTable.paramSet);
            }
         }

         if(lootTable.pools.length > 0) {
            var4.add("pools", jsonSerializationContext.serialize(lootTable.pools));
         }

         if(!ArrayUtils.isEmpty(lootTable.functions)) {
            var4.add("functions", jsonSerializationContext.serialize(lootTable.functions));
         }

         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootTable)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
