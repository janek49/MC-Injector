package net.minecraft.client.gui.font.glyphs;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.BufferBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class BakedGlyph {
   private final ResourceLocation texture;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float left;
   private final float right;
   private final float up;
   private final float down;

   public BakedGlyph(ResourceLocation texture, float u0, float u1, float v0, float v1, float left, float right, float up, float down) {
      this.texture = texture;
      this.u0 = u0;
      this.u1 = u1;
      this.v0 = v0;
      this.v1 = v1;
      this.left = left;
      this.right = right;
      this.up = up;
      this.down = down;
   }

   public void render(TextureManager textureManager, boolean var2, float var3, float var4, BufferBuilder bufferBuilder, float var6, float var7, float var8, float var9) {
      int var10 = 3;
      float var11 = var3 + this.left;
      float var12 = var3 + this.right;
      float var13 = this.up - 3.0F;
      float var14 = this.down - 3.0F;
      float var15 = var4 + var13;
      float var16 = var4 + var14;
      float var17 = var2?1.0F - 0.25F * var13:0.0F;
      float var18 = var2?1.0F - 0.25F * var14:0.0F;
      bufferBuilder.vertex((double)(var11 + var17), (double)var15, 0.0D).uv((double)this.u0, (double)this.v0).color(var6, var7, var8, var9).endVertex();
      bufferBuilder.vertex((double)(var11 + var18), (double)var16, 0.0D).uv((double)this.u0, (double)this.v1).color(var6, var7, var8, var9).endVertex();
      bufferBuilder.vertex((double)(var12 + var18), (double)var16, 0.0D).uv((double)this.u1, (double)this.v1).color(var6, var7, var8, var9).endVertex();
      bufferBuilder.vertex((double)(var12 + var17), (double)var15, 0.0D).uv((double)this.u1, (double)this.v0).color(var6, var7, var8, var9).endVertex();
   }

   @Nullable
   public ResourceLocation getTexture() {
      return this.texture;
   }
}
