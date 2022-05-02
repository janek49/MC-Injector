package net.minecraft.data.advancements;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.advancements.AdventureAdvancements;
import net.minecraft.data.advancements.HusbandryAdvancements;
import net.minecraft.data.advancements.NetherAdvancements;
import net.minecraft.data.advancements.StoryAdvancements;
import net.minecraft.data.advancements.TheEndAdvancements;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementProvider implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;
   private final List tabs = ImmutableList.of(new TheEndAdvancements(), new HusbandryAdvancements(), new AdventureAdvancements(), new NetherAdvancements(), new StoryAdvancements());

   public AdvancementProvider(DataGenerator generator) {
      this.generator = generator;
   }

   public void run(HashCache hashCache) throws IOException {
      Path var2 = this.generator.getOutputFolder();
      Set<ResourceLocation> var3 = Sets.newHashSet();
      Consumer<Advancement> var4 = (advancement) -> {
         if(!var3.add(advancement.getId())) {
            throw new IllegalStateException("Duplicate advancement " + advancement.getId());
         } else {
            Path path = createPath(var2, advancement);

            try {
               DataProvider.save(GSON, hashCache, advancement.deconstruct().serializeToJson(), path);
            } catch (IOException var6) {
               LOGGER.error("Couldn\'t save advancement {}", path, var6);
            }

         }
      };

      for(Consumer<Consumer<Advancement>> var6 : this.tabs) {
         var6.accept(var4);
      }

   }

   private static Path createPath(Path var0, Advancement advancement) {
      return var0.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
   }

   public String getName() {
      return "Advancements";
   }
}
