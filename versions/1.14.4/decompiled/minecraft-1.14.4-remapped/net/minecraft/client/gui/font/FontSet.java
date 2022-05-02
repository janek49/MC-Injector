package net.minecraft.client.gui.font;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.gui.font.FontTexture;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.gui.font.glyphs.MissingGlyph;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class FontSet implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EmptyGlyph SPACE_GLYPH = new EmptyGlyph();
   private static final GlyphInfo SPACE_INFO = () -> {
      return 4.0F;
   };
   private static final Random RANDOM = new Random();
   private final TextureManager textureManager;
   private final ResourceLocation name;
   private BakedGlyph missingGlyph;
   private final List providers = Lists.newArrayList();
   private final Char2ObjectMap glyphs = new Char2ObjectOpenHashMap();
   private final Char2ObjectMap glyphInfos = new Char2ObjectOpenHashMap();
   private final Int2ObjectMap glyphsByWidth = new Int2ObjectOpenHashMap();
   private final List textures = Lists.newArrayList();

   public FontSet(TextureManager textureManager, ResourceLocation name) {
      this.textureManager = textureManager;
      this.name = name;
   }

   public void reload(List list) {
      for(GlyphProvider var3 : this.providers) {
         var3.close();
      }

      this.providers.clear();
      this.closeTextures();
      this.textures.clear();
      this.glyphs.clear();
      this.glyphInfos.clear();
      this.glyphsByWidth.clear();
      this.missingGlyph = this.stitch(MissingGlyph.INSTANCE);
      Set<GlyphProvider> var2 = Sets.newHashSet();

      for(char var3 = 0; var3 < '\uffff'; ++var3) {
         for(GlyphProvider var5 : list) {
            GlyphInfo var6 = (GlyphInfo)(var3 == 32?SPACE_INFO:var5.getGlyph(var3));
            if(var6 != null) {
               var2.add(var5);
               if(var6 != MissingGlyph.INSTANCE) {
                  ((CharList)this.glyphsByWidth.computeIfAbsent(Mth.ceil(var6.getAdvance(false)), (i) -> {
                     return new CharArrayList();
                  })).add(var3);
               }
               break;
            }
         }
      }

      Stream var10000 = list.stream();
      var2.getClass();
      var10000 = var10000.filter(var2::contains);
      List var10001 = this.providers;
      this.providers.getClass();
      var10000.forEach(var10001::add);
   }

   public void close() {
      this.closeTextures();
   }

   public void closeTextures() {
      for(FontTexture var2 : this.textures) {
         var2.close();
      }

   }

   public GlyphInfo getGlyphInfo(char c) {
      return (GlyphInfo)this.glyphInfos.computeIfAbsent(c, (i) -> {
         return (GlyphInfo)(i == 32?SPACE_INFO:this.getRaw((char)i));
      });
   }

   private RawGlyph getRaw(char c) {
      for(GlyphProvider var3 : this.providers) {
         RawGlyph var4 = var3.getGlyph(c);
         if(var4 != null) {
            return var4;
         }
      }

      return MissingGlyph.INSTANCE;
   }

   public BakedGlyph getGlyph(char c) {
      return (BakedGlyph)this.glyphs.computeIfAbsent(c, (i) -> {
         return (BakedGlyph)(i == 32?SPACE_GLYPH:this.stitch(this.getRaw((char)i)));
      });
   }

   private BakedGlyph stitch(RawGlyph rawGlyph) {
      for(FontTexture var3 : this.textures) {
         BakedGlyph var4 = var3.add(rawGlyph);
         if(var4 != null) {
            return var4;
         }
      }

      FontTexture var2 = new FontTexture(new ResourceLocation(this.name.getNamespace(), this.name.getPath() + "/" + this.textures.size()), rawGlyph.isColored());
      this.textures.add(var2);
      this.textureManager.register((ResourceLocation)var2.getName(), (TextureObject)var2);
      BakedGlyph var3 = var2.add(rawGlyph);
      return var3 == null?this.missingGlyph:var3;
   }

   public BakedGlyph getRandomGlyph(GlyphInfo glyphInfo) {
      CharList var2 = (CharList)this.glyphsByWidth.get(Mth.ceil(glyphInfo.getAdvance(false)));
      return var2 != null && !var2.isEmpty()?this.getGlyph(var2.get(RANDOM.nextInt(var2.size())).charValue()):this.missingGlyph;
   }
}
