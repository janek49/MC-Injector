package net.minecraft.data.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.ChestLoot;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.data.loot.FishingLoot;
import net.minecraft.data.loot.GiftLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableProvider implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator generator;
   private final List subProviders = ImmutableList.of(Pair.of(FishingLoot::<init>, LootContextParamSets.FISHING), Pair.of(ChestLoot::<init>, LootContextParamSets.CHEST), Pair.of(EntityLoot::<init>, LootContextParamSets.ENTITY), Pair.of(BlockLoot::<init>, LootContextParamSets.BLOCK), Pair.of(GiftLoot::<init>, LootContextParamSets.GIFT));

   public LootTableProvider(DataGenerator generator) {
      this.generator = generator;
   }

   public void run(HashCache hashCache) {
      Path var2 = this.generator.getOutputFolder();
      Map<ResourceLocation, LootTable> var3 = Maps.newHashMap();
      this.subProviders.forEach((pair) -> {
         ((Consumer)((Supplier)pair.getFirst()).get()).accept((resourceLocation, lootTable$Builder) -> {
            if(var3.put(resourceLocation, lootTable$Builder.setParamSet((LootContextParamSet)pair.getSecond()).build()) != null) {
               throw new IllegalStateException("Duplicate loot table " + resourceLocation);
            }
         });
      });
      LootTableProblemCollector var4 = new LootTableProblemCollector();

      for(ResourceLocation var7 : Sets.difference(BuiltInLootTables.all(), var3.keySet())) {
         var4.reportProblem("Missing built-in table: " + var7);
      }

      var3.forEach((resourceLocation, lootTable) -> {
         LootTables.validate(var4, resourceLocation, lootTable, var3::get);
      });
      Multimap<String, String> var6 = var4.getProblems();
      if(!var6.isEmpty()) {
         var6.forEach((var0, var1) -> {
            LOGGER.warn("Found validation problem in " + var0 + ": " + var1);
         });
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         var3.forEach((resourceLocation, lootTable) -> {
            Path path = createPath(var2, resourceLocation);

            try {
               DataProvider.save(GSON, hashCache, LootTables.serialize(lootTable), path);
            } catch (IOException var6) {
               LOGGER.error("Couldn\'t save loot table {}", path, var6);
            }

         });
      }
   }

   private static Path createPath(Path var0, ResourceLocation resourceLocation) {
      return var0.resolve("data/" + resourceLocation.getNamespace() + "/loot_tables/" + resourceLocation.getPath() + ".json");
   }

   public String getName() {
      return "LootTables";
   }
}
