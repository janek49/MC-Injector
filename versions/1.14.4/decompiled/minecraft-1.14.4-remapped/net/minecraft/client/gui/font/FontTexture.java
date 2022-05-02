package net.minecraft.client.gui.font;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.Closeable;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

@ClientJarOnly
public class FontTexture extends AbstractTexture implements Closeable {
   private final ResourceLocation name;
   private final boolean colored;
   private final FontTexture.Node root;

   public FontTexture(ResourceLocation name, boolean colored) {
      this.name = name;
      this.colored = colored;
      this.root = new FontTexture.Node(0, 0, 256, 256);
      TextureUtil.prepareImage(colored?NativeImage.InternalGlFormat.RGBA:NativeImage.InternalGlFormat.INTENSITY, this.getId(), 256, 256);
   }

   public void load(ResourceManager resourceManager) {
   }

   public void close() {
      this.releaseId();
   }

   @Nullable
   public BakedGlyph add(RawGlyph rawGlyph) {
      if(rawGlyph.isColored() != this.colored) {
         return null;
      } else {
         FontTexture.Node var2 = this.root.insert(rawGlyph);
         if(var2 != null) {
            this.bind();
            rawGlyph.upload(var2.x, var2.y);
            float var3 = 256.0F;
            float var4 = 256.0F;
            float var5 = 0.01F;
            return new BakedGlyph(this.name, ((float)var2.x + 0.01F) / 256.0F, ((float)var2.x - 0.01F + (float)rawGlyph.getPixelWidth()) / 256.0F, ((float)var2.y + 0.01F) / 256.0F, ((float)var2.y - 0.01F + (float)rawGlyph.getPixelHeight()) / 256.0F, rawGlyph.getLeft(), rawGlyph.getRight(), rawGlyph.getUp(), rawGlyph.getDown());
         } else {
            return null;
         }
      }
   }

   public ResourceLocation getName() {
      return this.name;
   }

   @ClientJarOnly
   static class Node {
      private final int x;
      private final int y;
      private final int width;
      private final int height;
      private FontTexture.Node left;
      private FontTexture.Node right;
      private boolean occupied;

      private Node(int x, int y, int width, int height) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }

      @Nullable
      FontTexture.Node insert(RawGlyph rawGlyph) {
         if(this.left != null && this.right != null) {
            FontTexture.Node fontTexture$Node = this.left.insert(rawGlyph);
            if(fontTexture$Node == null) {
               fontTexture$Node = this.right.insert(rawGlyph);
            }

            return fontTexture$Node;
         } else if(this.occupied) {
            return null;
         } else {
            int var2 = rawGlyph.getPixelWidth();
            int var3 = rawGlyph.getPixelHeight();
            if(var2 <= this.width && var3 <= this.height) {
               if(var2 == this.width && var3 == this.height) {
                  this.occupied = true;
                  return this;
               } else {
                  int var4 = this.width - var2;
                  int var5 = this.height - var3;
                  if(var4 > var5) {
                     this.left = new FontTexture.Node(this.x, this.y, var2, this.height);
                     this.right = new FontTexture.Node(this.x + var2 + 1, this.y, this.width - var2 - 1, this.height);
                  } else {
                     this.left = new FontTexture.Node(this.x, this.y, this.width, var3);
                     this.right = new FontTexture.Node(this.x, this.y + var3 + 1, this.width, this.height - var3 - 1);
                  }

                  return this.left.insert(rawGlyph);
               }
            } else {
               return null;
            }
         }
      }
   }
}
