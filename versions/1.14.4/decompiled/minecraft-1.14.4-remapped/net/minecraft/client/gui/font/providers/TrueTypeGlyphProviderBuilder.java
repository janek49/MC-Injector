package net.minecraft.client.gui.font.providers;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class TrueTypeGlyphProviderBuilder implements GlyphProviderBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ResourceLocation location;
   private final float size;
   private final float oversample;
   private final float shiftX;
   private final float shiftY;
   private final String skip;

   public TrueTypeGlyphProviderBuilder(ResourceLocation location, float size, float oversample, float shiftX, float shiftY, String skip) {
      this.location = location;
      this.size = size;
      this.oversample = oversample;
      this.shiftX = shiftX;
      this.shiftY = shiftY;
      this.skip = skip;
   }

   public static GlyphProviderBuilder fromJson(JsonObject json) {
      float var1 = 0.0F;
      float var2 = 0.0F;
      if(json.has("shift")) {
         JsonArray var3 = json.getAsJsonArray("shift");
         if(var3.size() != 2) {
            throw new JsonParseException("Expected 2 elements in \'shift\', found " + var3.size());
         }

         var1 = GsonHelper.convertToFloat(var3.get(0), "shift[0]");
         var2 = GsonHelper.convertToFloat(var3.get(1), "shift[1]");
      }

      StringBuilder var3 = new StringBuilder();
      if(json.has("skip")) {
         JsonElement var4 = json.get("skip");
         if(var4.isJsonArray()) {
            JsonArray var5 = GsonHelper.convertToJsonArray(var4, "skip");

            for(int var6 = 0; var6 < var5.size(); ++var6) {
               var3.append(GsonHelper.convertToString(var5.get(var6), "skip[" + var6 + "]"));
            }
         } else {
            var3.append(GsonHelper.convertToString(var4, "skip"));
         }
      }

      return new TrueTypeGlyphProviderBuilder(new ResourceLocation(GsonHelper.getAsString(json, "file")), GsonHelper.getAsFloat(json, "size", 11.0F), GsonHelper.getAsFloat(json, "oversample", 1.0F), var1, var2, var3.toString());
   }

   @Nullable
   public GlyphProvider create(ResourceManager resourceManager) {
      try {
         Resource var2 = resourceManager.getResource(new ResourceLocation(this.location.getNamespace(), "font/" + this.location.getPath()));
         Throwable var3 = null;

         TrueTypeGlyphProvider var5;
         try {
            LOGGER.info("Loading font");
            ByteBuffer var4 = TextureUtil.readResource(var2.getInputStream());
            var4.flip();
            LOGGER.info("Reading font");
            var5 = new TrueTypeGlyphProvider(TrueTypeGlyphProvider.getStbttFontinfo(var4), this.size, this.oversample, this.shiftX, this.shiftY, this.skip);
         } catch (Throwable var15) {
            var3 = var15;
            throw var15;
         } finally {
            if(var2 != null) {
               if(var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var14) {
                     var3.addSuppressed(var14);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var5;
      } catch (IOException var17) {
         LOGGER.error("Couldn\'t load truetype font {}", this.location, var17);
         return null;
      }
   }
}
