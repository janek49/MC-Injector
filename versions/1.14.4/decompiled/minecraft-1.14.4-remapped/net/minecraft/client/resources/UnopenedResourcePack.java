package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.UnopenedPack;

@ClientJarOnly
public class UnopenedResourcePack extends UnopenedPack {
   @Nullable
   private NativeImage icon;
   @Nullable
   private ResourceLocation iconLocation;

   public UnopenedResourcePack(String string, boolean var2, Supplier supplier, Pack pack, PackMetadataSection packMetadataSection, UnopenedPack.Position unopenedPack$Position) {
      super(string, var2, supplier, pack, packMetadataSection, unopenedPack$Position);
      NativeImage var7 = null;

      try {
         InputStream var8 = pack.getRootResource("pack.png");
         Throwable var9 = null;

         try {
            var7 = NativeImage.read(var8);
         } catch (Throwable var19) {
            var9 = var19;
            throw var19;
         } finally {
            if(var8 != null) {
               if(var9 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var18) {
                     var9.addSuppressed(var18);
                  }
               } else {
                  var8.close();
               }
            }

         }
      } catch (IllegalArgumentException | IOException var21) {
         ;
      }

      this.icon = var7;
   }

   public UnopenedResourcePack(String string, boolean var2, Supplier supplier, Component var4, Component var5, PackCompatibility packCompatibility, UnopenedPack.Position unopenedPack$Position, boolean var8, @Nullable NativeImage icon) {
      super(string, var2, supplier, var4, var5, packCompatibility, unopenedPack$Position, var8);
      this.icon = icon;
   }

   public void bindIcon(TextureManager textureManager) {
      if(this.iconLocation == null) {
         if(this.icon == null) {
            this.iconLocation = new ResourceLocation("textures/misc/unknown_pack.png");
         } else {
            this.iconLocation = textureManager.register("texturepackicon", new DynamicTexture(this.icon));
         }
      }

      textureManager.bind(this.iconLocation);
   }

   public void close() {
      super.close();
      if(this.icon != null) {
         this.icon.close();
         this.icon = null;
      }

   }
}
