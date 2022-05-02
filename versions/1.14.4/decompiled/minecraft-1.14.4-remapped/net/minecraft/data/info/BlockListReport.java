package net.minecraft.data.info;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockListReport implements DataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public BlockListReport(DataGenerator generator) {
      this.generator = generator;
   }

   public void run(HashCache hashCache) throws IOException {
      JsonObject var2 = new JsonObject();

      for(Block var4 : Registry.BLOCK) {
         ResourceLocation var5 = Registry.BLOCK.getKey(var4);
         JsonObject var6 = new JsonObject();
         StateDefinition<Block, BlockState> var7 = var4.getStateDefinition();
         if(!var7.getProperties().isEmpty()) {
            JsonObject var8 = new JsonObject();

            for(Property<?> var10 : var7.getProperties()) {
               JsonArray var11 = new JsonArray();

               for(Comparable<?> var13 : var10.getPossibleValues()) {
                  var11.add(Util.getPropertyName(var10, var13));
               }

               var8.add(var10.getName(), var11);
            }

            var6.add("properties", var8);
         }

         JsonArray var8 = new JsonArray();

         JsonObject var11;
         for(UnmodifiableIterator var17 = var7.getPossibleStates().iterator(); var17.hasNext(); var8.add(var11)) {
            BlockState var10 = (BlockState)var17.next();
            var11 = new JsonObject();
            JsonObject var12 = new JsonObject();

            for(Property<?> var14 : var7.getProperties()) {
               var12.addProperty(var14.getName(), Util.getPropertyName(var14, var10.getValue(var14)));
            }

            if(var12.size() > 0) {
               var11.add("properties", var12);
            }

            var11.addProperty("id", Integer.valueOf(Block.getId(var10)));
            if(var10 == var4.defaultBlockState()) {
               var11.addProperty("default", Boolean.valueOf(true));
            }
         }

         var6.add("states", var8);
         var2.add(var5.toString(), var6);
      }

      Path var3 = this.generator.getOutputFolder().resolve("reports/blocks.json");
      DataProvider.save(GSON, hashCache, var2, var3);
   }

   public String getName() {
      return "Block List";
   }
}
