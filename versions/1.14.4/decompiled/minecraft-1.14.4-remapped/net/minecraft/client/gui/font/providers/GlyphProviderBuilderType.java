package net.minecraft.client.gui.font.providers;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.client.gui.font.providers.LegacyUnicodeBitmapsProvider;
import net.minecraft.client.gui.font.providers.TrueTypeGlyphProviderBuilder;

@ClientJarOnly
public enum GlyphProviderBuilderType {
   BITMAP("bitmap", BitmapProvider.Builder::fromJson),
   TTF("ttf", TrueTypeGlyphProviderBuilder::fromJson),
   LEGACY_UNICODE("legacy_unicode", LegacyUnicodeBitmapsProvider.Builder::fromJson);

   private static final Map BY_NAME = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      for(GlyphProviderBuilderType var4 : values()) {
         hashMap.put(var4.name, var4);
      }

   });
   private final String name;
   private final Function factory;

   private GlyphProviderBuilderType(String name, Function factory) {
      this.name = name;
      this.factory = factory;
   }

   public static GlyphProviderBuilderType byName(String name) {
      GlyphProviderBuilderType glyphProviderBuilderType = (GlyphProviderBuilderType)BY_NAME.get(name);
      if(glyphProviderBuilderType == null) {
         throw new IllegalArgumentException("Invalid type: " + name);
      } else {
         return glyphProviderBuilderType;
      }
   }

   public GlyphProviderBuilder create(JsonObject jsonObject) {
      return (GlyphProviderBuilder)this.factory.apply(jsonObject);
   }
}
