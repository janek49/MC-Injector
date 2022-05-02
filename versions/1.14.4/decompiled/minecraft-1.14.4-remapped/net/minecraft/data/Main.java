package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.info.BlockListReport;
import net.minecraft.data.info.CommandsReport;
import net.minecraft.data.info.RegistryDumpReport;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.data.structures.StructureUpdater;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;

public class Main {
   public static void main(String[] args) throws IOException {
      OptionParser var1 = new OptionParser();
      OptionSpec<Void> var2 = var1.accepts("help", "Show the help menu").forHelp();
      OptionSpec<Void> var3 = var1.accepts("server", "Include server generators");
      OptionSpec<Void> var4 = var1.accepts("client", "Include client generators");
      OptionSpec<Void> var5 = var1.accepts("dev", "Include development tools");
      OptionSpec<Void> var6 = var1.accepts("reports", "Include data reports");
      OptionSpec<Void> var7 = var1.accepts("validate", "Validate inputs");
      OptionSpec<Void> var8 = var1.accepts("all", "Include all generators");
      OptionSpec<String> var9 = var1.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", new String[0]);
      OptionSpec<String> var10 = var1.accepts("input", "Input folder").withRequiredArg();
      OptionSet var11 = var1.parse(args);
      if(!var11.has(var2) && var11.hasOptions()) {
         Path var12 = Paths.get((String)var9.value(var11), new String[0]);
         boolean var13 = var11.has(var8);
         boolean var14 = var13 || var11.has(var4);
         boolean var15 = var13 || var11.has(var3);
         boolean var16 = var13 || var11.has(var5);
         boolean var17 = var13 || var11.has(var6);
         boolean var18 = var13 || var11.has(var7);
         DataGenerator var19 = createStandardGenerator(var12, (Collection)var11.valuesOf(var10).stream().map((string) -> {
            return Paths.get(string, new String[0]);
         }).collect(Collectors.toList()), var14, var15, var16, var17, var18);
         var19.run();
      } else {
         var1.printHelpOn(System.out);
      }
   }

   public static DataGenerator createStandardGenerator(Path path, Collection collection, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      DataGenerator dataGenerator = new DataGenerator(path, collection);
      if(var2 || var3) {
         dataGenerator.addProvider((new SnbtToNbt(dataGenerator)).addFilter(new StructureUpdater()));
      }

      if(var3) {
         dataGenerator.addProvider(new FluidTagsProvider(dataGenerator));
         dataGenerator.addProvider(new BlockTagsProvider(dataGenerator));
         dataGenerator.addProvider(new ItemTagsProvider(dataGenerator));
         dataGenerator.addProvider(new EntityTypeTagsProvider(dataGenerator));
         dataGenerator.addProvider(new RecipeProvider(dataGenerator));
         dataGenerator.addProvider(new AdvancementProvider(dataGenerator));
         dataGenerator.addProvider(new LootTableProvider(dataGenerator));
      }

      if(var4) {
         dataGenerator.addProvider(new NbtToSnbt(dataGenerator));
      }

      if(var5) {
         dataGenerator.addProvider(new BlockListReport(dataGenerator));
         dataGenerator.addProvider(new RegistryDumpReport(dataGenerator));
         dataGenerator.addProvider(new CommandsReport(dataGenerator));
      }

      return dataGenerator;
   }
}
