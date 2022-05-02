package net.minecraft.client.renderer.texture;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;

@ClientJarOnly
public final class MissingTextureAtlasSprite extends TextureAtlasSprite {
   private static final ResourceLocation MISSING_TEXTURE_LOCATION = new ResourceLocation("missingno");
   @Nullable
   private static DynamicTexture missingTexture;
   private static final LazyLoadedValue MISSING_IMAGE_DATA = new LazyLoadedValue(() -> {
      NativeImage nativeImage = new NativeImage(16, 16, false);
      int var1 = -16777216;
      int var2 = -524040;

      for(int var3 = 0; var3 < 16; ++var3) {
         for(int var4 = 0; var4 < 16; ++var4) {
            if(var3 < 8 ^ var4 < 8) {
               nativeImage.setPixelRGBA(var4, var3, -524040);
            } else {
               nativeImage.setPixelRGBA(var4, var3, -16777216);
            }
         }
      }

      nativeImage.untrack();
      return nativeImage;
   });

   private MissingTextureAtlasSprite() {
      super(MISSING_TEXTURE_LOCATION, 16, 16);
      this.mainImage = new NativeImage[]{(NativeImage)MISSING_IMAGE_DATA.get()};
   }

   public static MissingTextureAtlasSprite newInstance() {
      return new MissingTextureAtlasSprite();
   }

   public static ResourceLocation getLocation() {
      return MISSING_TEXTURE_LOCATION;
   }

   public void wipeFrameData() {
      for(int var1 = 1; var1 < this.mainImage.length; ++var1) {
         this.mainImage[var1].close();
      }

      this.mainImage = new NativeImage[]{(NativeImage)MISSING_IMAGE_DATA.get()};
   }

   public static DynamicTexture getTexture() {
      if(missingTexture == null) {
         missingTexture = new DynamicTexture((NativeImage)MISSING_IMAGE_DATA.get());
         Minecraft.getInstance().getTextureManager().register((ResourceLocation)MISSING_TEXTURE_LOCATION, (TextureObject)missingTexture);
      }

      return missingTexture;
   }
}
