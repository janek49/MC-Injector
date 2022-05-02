package net.minecraft.client.gui.font.providers;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class BitmapProvider implements GlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final NativeImage image;
   private final Char2ObjectMap glyphs;

   public BitmapProvider(NativeImage image, Char2ObjectMap glyphs) {
      this.image = image;
      this.glyphs = glyphs;
   }

   public void close() {
      this.image.close();
   }

   @Nullable
   public RawGlyph getGlyph(char c) {
      return (RawGlyph)this.glyphs.get(c);
   }

   @ClientJarOnly
   public static class Builder implements GlyphProviderBuilder {
      private final ResourceLocation texture;
      private final List chars;
      private final int height;
      private final int ascent;

      public Builder(ResourceLocation resourceLocation, int height, int ascent, List chars) {
         this.texture = new ResourceLocation(resourceLocation.getNamespace(), "textures/" + resourceLocation.getPath());
         this.chars = chars;
         this.height = height;
         this.ascent = ascent;
      }

      public static BitmapProvider.Builder fromJson(JsonObject json) {
         int var1 = GsonHelper.getAsInt(json, "height", 8);
         int var2 = GsonHelper.getAsInt(json, "ascent");
         if(var2 > var1) {
            throw new JsonParseException("Ascent " + var2 + " higher than height " + var1);
         } else {
            List<String> var3 = Lists.newArrayList();
            JsonArray var4 = GsonHelper.getAsJsonArray(json, "chars");

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               String var6 = GsonHelper.convertToString(var4.get(var5), "chars[" + var5 + "]");
               if(var5 > 0) {
                  int var7 = var6.length();
                  int var8 = ((String)var3.get(0)).length();
                  if(var7 != var8) {
                     throw new JsonParseException("Elements of chars have to be the same length (found: " + var7 + ", expected: " + var8 + "), pad with space or \\u0000");
                  }
               }

               var3.add(var6);
            }

            if(!var3.isEmpty() && !((String)var3.get(0)).isEmpty()) {
               return new BitmapProvider.Builder(new ResourceLocation(GsonHelper.getAsString(json, "file")), var1, var2, var3);
            } else {
               throw new JsonParseException("Expected to find data in chars, found none.");
            }
         }
      }

      @Nullable
      public GlyphProvider create(ResourceManager resourceManager) {
         try {
            Resource var2 = resourceManager.getResource(this.texture);
            Throwable var3 = null;

            BitmapProvider var28;
            try {
               NativeImage var4 = NativeImage.read(NativeImage.Format.RGBA, var2.getInputStream());
               int var5 = var4.getWidth();
               int var6 = var4.getHeight();
               int var7 = var5 / ((String)this.chars.get(0)).length();
               int var8 = var6 / this.chars.size();
               float var9 = (float)this.height / (float)var8;
               Char2ObjectMap<BitmapProvider.Glyph> var10 = new Char2ObjectOpenHashMap();

               for(int var11 = 0; var11 < this.chars.size(); ++var11) {
                  String var12 = (String)this.chars.get(var11);

                  for(int var13 = 0; var13 < var12.length(); ++var13) {
                     char var14 = var12.charAt(var13);
                     if(var14 != 0 && var14 != 32) {
                        int var15 = this.getActualGlyphWidth(var4, var7, var8, var13, var11);
                        BitmapProvider.Glyph var16 = (BitmapProvider.Glyph)var10.put(var14, new BitmapProvider.Glyph(var9, var4, var13 * var7, var11 * var8, var7, var8, (int)(0.5D + (double)((float)var15 * var9)) + 1, this.ascent));
                        if(var16 != null) {
                           BitmapProvider.LOGGER.warn("Codepoint \'{}\' declared multiple times in {}", Integer.toHexString(var14), this.texture);
                        }
                     }
                  }
               }

               var28 = new BitmapProvider(var4, var10);
            } catch (Throwable var25) {
               var3 = var25;
               throw var25;
            } finally {
               if(var2 != null) {
                  if(var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var24) {
                        var3.addSuppressed(var24);
                     }
                  } else {
                     var2.close();
                  }
               }

            }

            return var28;
         } catch (IOException var27) {
            throw new RuntimeException(var27.getMessage());
         }
      }

      private int getActualGlyphWidth(NativeImage nativeImage, int var2, int var3, int var4, int var5) {
         int var6;
         for(var6 = var2 - 1; var6 >= 0; --var6) {
            int var7 = var4 * var2 + var6;

            for(int var8 = 0; var8 < var3; ++var8) {
               int var9 = var5 * var3 + var8;
               if(nativeImage.getLuminanceOrAlpha(var7, var9) != 0) {
                  return var6 + 1;
               }
            }
         }

         return var6 + 1;
      }
   }

   @ClientJarOnly
   static final class Glyph implements RawGlyph {
      private final float scale;
      private final NativeImage image;
      private final int offsetX;
      private final int offsetY;
      private final int width;
      private final int height;
      private final int advance;
      private final int ascent;

      private Glyph(float scale, NativeImage image, int offsetX, int offsetY, int width, int height, int advance, int ascent) {
         this.scale = scale;
         this.image = image;
         this.offsetX = offsetX;
         this.offsetY = offsetY;
         this.width = width;
         this.height = height;
         this.advance = advance;
         this.ascent = ascent;
      }

      public float getOversample() {
         return 1.0F / this.scale;
      }

      public int getPixelWidth() {
         return this.width;
      }

      public int getPixelHeight() {
         return this.height;
      }

      public float getAdvance() {
         return (float)this.advance;
      }

      public float getBearingY() {
         return super.getBearingY() + 7.0F - (float)this.ascent;
      }

      public void upload(int var1, int var2) {
         this.image.upload(0, var1, var2, this.offsetX, this.offsetY, this.width, this.height, false);
      }

      public boolean isColored() {
         return this.image.format().components() > 1;
      }
   }
}
