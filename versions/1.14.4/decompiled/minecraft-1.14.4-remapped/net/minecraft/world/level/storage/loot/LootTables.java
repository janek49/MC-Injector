package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.IntLimiter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTables extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(RandomValueBounds.class, new RandomValueBounds.Serializer()).registerTypeAdapter(BinomialDistributionGenerator.class, new BinomialDistributionGenerator.Serializer()).registerTypeAdapter(ConstantIntValue.class, new ConstantIntValue.Serializer()).registerTypeAdapter(IntLimiter.class, new IntLimiter.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootPoolEntryContainer.class, new LootPoolEntries.Serializer()).registerTypeHierarchyAdapter(LootItemFunction.class, new LootItemFunctions.Serializer()).registerTypeHierarchyAdapter(LootItemCondition.class, new LootItemConditions.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
   private Map tables = ImmutableMap.of();

   public LootTables() {
      super(GSON, "loot_tables");
   }

   public LootTable get(ResourceLocation resourceLocation) {
      return (LootTable)this.tables.getOrDefault(resourceLocation, LootTable.EMPTY);
   }

   protected void apply(Map map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      Builder<ResourceLocation, LootTable> var4 = ImmutableMap.builder();
      JsonObject var5 = (JsonObject)map.remove(BuiltInLootTables.EMPTY);
      if(var5 != null) {
         LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", BuiltInLootTables.EMPTY);
      }

      map.forEach((resourceLocation, jsonObject) -> {
         try {
            LootTable var3 = (LootTable)GSON.fromJson(jsonObject, LootTable.class);
            var4.put(resourceLocation, var3);
         } catch (Exception var4x) {
            LOGGER.error("Couldn\'t parse loot table {}", resourceLocation, var4x);
         }

      });
      var4.put(BuiltInLootTables.EMPTY, LootTable.EMPTY);
      ImmutableMap<ResourceLocation, LootTable> var6 = var4.build();
      LootTableProblemCollector var7 = new LootTableProblemCollector();
      var6.forEach((resourceLocation, lootTable) -> {
         validate(var7, resourceLocation, lootTable, var6::get);
      });
      var7.getProblems().forEach((var0, var1) -> {
         LOGGER.warn("Found validation problem in " + var0 + ": " + var1);
      });
      this.tables = var6;
   }

   public static void validate(LootTableProblemCollector lootTableProblemCollector, ResourceLocation resourceLocation, LootTable lootTable, Function function) {
      Set<ResourceLocation> var4 = ImmutableSet.of(resourceLocation);
      lootTable.validate(lootTableProblemCollector.forChild("{" + resourceLocation.toString() + "}"), function, var4, lootTable.getParamSet());
   }

   public static JsonElement serialize(LootTable lootTable) {
      return GSON.toJsonTree(lootTable);
   }

   public Set getIds() {
      return this.tables.keySet();
   }
}
