package net.minecraft.client.renderer.banner;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;

@ClientJarOnly
public class BannerTextures {
   public static final BannerTextures.TextureCache BANNER_CACHE = new BannerTextures.TextureCache("banner_", new ResourceLocation("textures/entity/banner_base.png"), "textures/entity/banner/");
   public static final BannerTextures.TextureCache SHIELD_CACHE = new BannerTextures.TextureCache("shield_", new ResourceLocation("textures/entity/shield_base.png"), "textures/entity/shield/");
   public static final ResourceLocation NO_PATTERN_SHIELD = new ResourceLocation("textures/entity/shield_base_nopattern.png");
   public static final ResourceLocation DEFAULT_PATTERN_BANNER = new ResourceLocation("textures/entity/banner/base.png");

   @ClientJarOnly
   public static class TextureCache {
      private final Map cache = Maps.newLinkedHashMap();
      private final ResourceLocation baseResource;
      private final String resourceNameBase;
      private final String hashPrefix;

      public TextureCache(String hashPrefix, ResourceLocation baseResource, String resourceNameBase) {
         this.hashPrefix = hashPrefix;
         this.baseResource = baseResource;
         this.resourceNameBase = resourceNameBase;
      }

      @Nullable
      public ResourceLocation getTextureLocation(String string, List var2, List var3) {
         if(string.isEmpty()) {
            return null;
         } else if(!var2.isEmpty() && !var3.isEmpty()) {
            string = this.hashPrefix + string;
            BannerTextures.TimestampedBannerTexture var4 = (BannerTextures.TimestampedBannerTexture)this.cache.get(string);
            if(var4 == null) {
               if(this.cache.size() >= 256 && !this.freeCacheSlot()) {
                  return BannerTextures.DEFAULT_PATTERN_BANNER;
               }

               List<String> var5 = Lists.newArrayList();

               for(BannerPattern var7 : var2) {
                  var5.add(this.resourceNameBase + var7.getFilename() + ".png");
               }

               var4 = new BannerTextures.TimestampedBannerTexture();
               var4.textureLocation = new ResourceLocation(string);
               Minecraft.getInstance().getTextureManager().register((ResourceLocation)var4.textureLocation, (TextureObject)(new LayeredColorMaskTexture(this.baseResource, var5, var3)));
               this.cache.put(string, var4);
            }

            var4.lastUseMilliseconds = Util.getMillis();
            return var4.textureLocation;
         } else {
            return MissingTextureAtlasSprite.getLocation();
         }
      }

      private boolean freeCacheSlot() {
         long var1 = Util.getMillis();
         Iterator<String> var3 = this.cache.keySet().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            BannerTextures.TimestampedBannerTexture var5 = (BannerTextures.TimestampedBannerTexture)this.cache.get(var4);
            if(var1 - var5.lastUseMilliseconds > 5000L) {
               Minecraft.getInstance().getTextureManager().release(var5.textureLocation);
               var3.remove();
               return true;
            }
         }

         return this.cache.size() < 256;
      }
   }

   @ClientJarOnly
   static class TimestampedBannerTexture {
      public long lastUseMilliseconds;
      public ResourceLocation textureLocation;

      private TimestampedBannerTexture() {
      }
   }
}
