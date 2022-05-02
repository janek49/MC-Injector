package com.mojang.blaze3d.font;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.IntConsumer;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

@ClientJarOnly
public class TrueTypeGlyphProvider implements GlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final STBTTFontinfo font;
   private final float oversample;
   private final CharSet skip = new CharArraySet();
   private final float shiftX;
   private final float shiftY;
   private final float pointScale;
   private final float ascent;

   public TrueTypeGlyphProvider(STBTTFontinfo font, float var2, float oversample, float var4, float var5, String string) {
      this.font = font;
      this.oversample = oversample;
      string.chars().forEach((i) -> {
         this.skip.add((char)(i & '\uffff'));
      });
      this.shiftX = var4 * oversample;
      this.shiftY = var5 * oversample;
      this.pointScale = STBTruetype.stbtt_ScaleForPixelHeight(font, var2 * oversample);
      MemoryStack var7 = MemoryStack.stackPush();
      Throwable var8 = null;

      try {
         IntBuffer var9 = var7.mallocInt(1);
         IntBuffer var10 = var7.mallocInt(1);
         IntBuffer var11 = var7.mallocInt(1);
         STBTruetype.stbtt_GetFontVMetrics(font, var9, var10, var11);
         this.ascent = (float)var9.get(0) * this.pointScale;
      } catch (Throwable var19) {
         var8 = var19;
         throw var19;
      } finally {
         if(var7 != null) {
            if(var8 != null) {
               try {
                  var7.close();
               } catch (Throwable var18) {
                  var8.addSuppressed(var18);
               }
            } else {
               var7.close();
            }
         }

      }

   }

   @Nullable
   public TrueTypeGlyphProvider.Glyph getGlyph(char c) {
      if(this.skip.contains(c)) {
         return null;
      } else {
         MemoryStack var2 = MemoryStack.stackPush();
         Throwable var3 = null;

         Object var9;
         try {
            IntBuffer var4 = var2.mallocInt(1);
            IntBuffer var5 = var2.mallocInt(1);
            IntBuffer var6 = var2.mallocInt(1);
            IntBuffer var7 = var2.mallocInt(1);
            int var8 = STBTruetype.stbtt_FindGlyphIndex(this.font, c);
            if(var8 != 0) {
               STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(this.font, var8, this.pointScale, this.pointScale, this.shiftX, this.shiftY, var4, var5, var6, var7);
               int var9 = var6.get(0) - var4.get(0);
               int var10 = var7.get(0) - var5.get(0);
               if(var9 != 0 && var10 != 0) {
                  IntBuffer var11 = var2.mallocInt(1);
                  IntBuffer var12 = var2.mallocInt(1);
                  STBTruetype.stbtt_GetGlyphHMetrics(this.font, var8, var11, var12);
                  TrueTypeGlyphProvider.Glyph var13 = new TrueTypeGlyphProvider.Glyph(var4.get(0), var6.get(0), -var5.get(0), -var7.get(0), (float)var11.get(0) * this.pointScale, (float)var12.get(0) * this.pointScale, var8);
                  return var13;
               }

               Object var11 = null;
               return (TrueTypeGlyphProvider.Glyph)var11;
            }

            var9 = null;
         } catch (Throwable var24) {
            var3 = var24;
            throw var24;
         } finally {
            if(var2 != null) {
               if(var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var23) {
                     var3.addSuppressed(var23);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return (TrueTypeGlyphProvider.Glyph)var9;
      }
   }

   public static STBTTFontinfo getStbttFontinfo(ByteBuffer byteBuffer) throws IOException {
      STBTTFontinfo sTBTTFontinfo = STBTTFontinfo.create();
      if(!STBTruetype.stbtt_InitFont(sTBTTFontinfo, byteBuffer)) {
         throw new IOException("Invalid ttf");
      } else {
         return sTBTTFontinfo;
      }
   }

   // $FF: synthetic method
   @Nullable
   public RawGlyph getGlyph(char var1) {
      return this.getGlyph(var1);
   }

   @ClientJarOnly
   class Glyph implements RawGlyph {
      private final int width;
      private final int height;
      private final float bearingX;
      private final float bearingY;
      private final float advance;
      private final int index;

      private Glyph(int var2, int var3, int var4, int var5, float var6, float var7, int index) {
         this.width = var3 - var2;
         this.height = var4 - var5;
         this.advance = var6 / TrueTypeGlyphProvider.this.oversample;
         this.bearingX = (var7 + (float)var2 + TrueTypeGlyphProvider.this.shiftX) / TrueTypeGlyphProvider.this.oversample;
         this.bearingY = (TrueTypeGlyphProvider.this.ascent - (float)var4 + TrueTypeGlyphProvider.this.shiftY) / TrueTypeGlyphProvider.this.oversample;
         this.index = index;
      }

      public int getPixelWidth() {
         return this.width;
      }

      public int getPixelHeight() {
         return this.height;
      }

      public float getOversample() {
         return TrueTypeGlyphProvider.this.oversample;
      }

      public float getAdvance() {
         return this.advance;
      }

      public float getBearingX() {
         return this.bearingX;
      }

      public float getBearingY() {
         return this.bearingY;
      }

      public void upload(int var1, int var2) {
         NativeImage var3 = new NativeImage(NativeImage.Format.LUMINANCE, this.width, this.height, false);
         Throwable var4 = null;

         try {
            var3.copyFromFont(TrueTypeGlyphProvider.this.font, this.index, this.width, this.height, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.shiftX, TrueTypeGlyphProvider.this.shiftY, 0, 0);
            var3.upload(0, var1, var2, 0, 0, this.width, this.height, false);
         } catch (Throwable var13) {
            var4 = var13;
            throw var13;
         } finally {
            if(var3 != null) {
               if(var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var12) {
                     var4.addSuppressed(var12);
                  }
               } else {
                  var3.close();
               }
            }

         }

      }

      public boolean isColored() {
         return false;
      }
   }
}
