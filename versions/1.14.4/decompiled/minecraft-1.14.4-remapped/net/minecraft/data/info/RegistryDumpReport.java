package net.minecraft.data.info;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public class RegistryDumpReport implements DataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public RegistryDumpReport(DataGenerator generator) {
      this.generator = generator;
   }

   public void run(HashCache hashCache) throws IOException {
      JsonObject var2 = new JsonObject();
      Registry.REGISTRY.keySet().forEach((resourceLocation) -> {
         var2.add(resourceLocation.toString(), dumpRegistry((WritableRegistry)Registry.REGISTRY.get(resourceLocation)));
      });
      Path var3 = this.generator.getOutputFolder().resolve("reports/registries.json");
      DataProvider.save(GSON, hashCache, var2, var3);
   }

   private static JsonElement dumpRegistry(WritableRegistry writableRegistry) {
      JsonObject var1 = new JsonObject();
      if(writableRegistry instanceof DefaultedRegistry) {
         ResourceLocation var2 = ((DefaultedRegistry)writableRegistry).getDefaultKey();
         var1.addProperty("default", var2.toString());
      }

      int var2 = Registry.REGISTRY.getId(writableRegistry);
      var1.addProperty("protocol_id", Integer.valueOf(var2));
      JsonObject var3 = new JsonObject();

      for(ResourceLocation var5 : writableRegistry.keySet()) {
         T var6 = writableRegistry.get(var5);
         int var7 = writableRegistry.getId(var6);
         JsonObject var8 = new JsonObject();
         var8.addProperty("protocol_id", Integer.valueOf(var7));
         var3.add(var5.toString(), var8);
      }

      var1.add("entries", var3);
      return var1;
   }

   public String getName() {
      return "Registry Dump";
   }
}
