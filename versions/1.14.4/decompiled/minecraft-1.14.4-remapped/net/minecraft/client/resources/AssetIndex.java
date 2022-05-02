package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class AssetIndex {
   protected static final Logger LOGGER = LogManager.getLogger();
   private final Map mapping = Maps.newHashMap();

   protected AssetIndex() {
   }

   public AssetIndex(File file, String string) {
      File file = new File(file, "objects");
      File var4 = new File(file, "indexes/" + string + ".json");
      BufferedReader var5 = null;

      try {
         var5 = Files.newReader(var4, StandardCharsets.UTF_8);
         JsonObject var6 = GsonHelper.parse((Reader)var5);
         JsonObject var7 = GsonHelper.getAsJsonObject(var6, "objects", (JsonObject)null);
         if(var7 != null) {
            for(Entry<String, JsonElement> var9 : var7.entrySet()) {
               JsonObject var10 = (JsonObject)var9.getValue();
               String var11 = (String)var9.getKey();
               String[] vars12 = var11.split("/", 2);
               String var13 = vars12.length == 1?vars12[0]:vars12[0] + ":" + vars12[1];
               String var14 = GsonHelper.getAsString(var10, "hash");
               File var15 = new File(file, var14.substring(0, 2) + "/" + var14);
               this.mapping.put(var13, var15);
            }
         }
      } catch (JsonParseException var20) {
         LOGGER.error("Unable to parse resource index file: {}", var4);
      } catch (FileNotFoundException var21) {
         LOGGER.error("Can\'t find the resource index file: {}", var4);
      } finally {
         IOUtils.closeQuietly(var5);
      }

   }

   @Nullable
   public File getFile(ResourceLocation resourceLocation) {
      return this.getFile(resourceLocation.toString());
   }

   @Nullable
   public File getFile(String string) {
      return (File)this.mapping.get(string);
   }

   public Collection getFiles(String string, int var2, Predicate predicate) {
      return (Collection)this.mapping.keySet().stream().filter((string) -> {
         return !string.endsWith(".mcmeta");
      }).map(ResourceLocation::<init>).map(ResourceLocation::getPath).filter((var1) -> {
         return var1.startsWith(string + "/");
      }).filter(predicate).collect(Collectors.toList());
   }
}
