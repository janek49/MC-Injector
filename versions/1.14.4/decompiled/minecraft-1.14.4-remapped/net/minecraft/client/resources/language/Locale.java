package net.minecraft.client.resources.language;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class Locale {
   private static final Gson GSON = new Gson();
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   protected final Map storage = Maps.newHashMap();

   public synchronized void loadFrom(ResourceManager resourceManager, List list) {
      this.storage.clear();

      for(String var4 : list) {
         String var5 = String.format("lang/%s.json", new Object[]{var4});

         for(String var7 : resourceManager.getNamespaces()) {
            try {
               ResourceLocation var8 = new ResourceLocation(var7, var5);
               this.appendFrom(resourceManager.getResources(var8));
            } catch (FileNotFoundException var9) {
               ;
            } catch (Exception var10) {
               LOGGER.warn("Skipped language file: {}:{} ({})", var7, var5, var10.toString());
            }
         }
      }

   }

   private void appendFrom(List list) {
      for(Resource var3 : list) {
         InputStream var4 = var3.getInputStream();

         try {
            this.appendFrom(var4);
         } finally {
            IOUtils.closeQuietly(var4);
         }
      }

   }

   private void appendFrom(InputStream inputStream) {
      JsonElement var2 = (JsonElement)GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonElement.class);
      JsonObject var3 = GsonHelper.convertToJsonObject(var2, "strings");

      for(Entry<String, JsonElement> var5 : var3.entrySet()) {
         String var6 = UNSUPPORTED_FORMAT_PATTERN.matcher(GsonHelper.convertToString((JsonElement)var5.getValue(), (String)var5.getKey())).replaceAll("%$1s");
         this.storage.put(var5.getKey(), var6);
      }

   }

   private String getOrDefault(String string) {
      String var2 = (String)this.storage.get(string);
      return var2 == null?string:var2;
   }

   public String get(String var1, Object[] objects) {
      String var3 = this.getOrDefault(var1);

      try {
         return String.format(var3, objects);
      } catch (IllegalFormatException var5) {
         return "Format error: " + var3;
      }
   }

   public boolean has(String string) {
      return this.storage.containsKey(string);
   }
}
