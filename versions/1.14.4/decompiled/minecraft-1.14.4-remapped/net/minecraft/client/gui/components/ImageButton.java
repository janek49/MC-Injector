package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class ImageButton extends Button {
   private final ResourceLocation resourceLocation;
   private final int xTexStart;
   private final int yTexStart;
   private final int yDiffTex;
   private final int textureWidth;
   private final int textureHeight;

   public ImageButton(int var1, int var2, int var3, int var4, int var5, int var6, int var7, ResourceLocation resourceLocation, Button.OnPress button$OnPress) {
      this(var1, var2, var3, var4, var5, var6, var7, resourceLocation, 256, 256, button$OnPress);
   }

   public ImageButton(int var1, int var2, int var3, int var4, int var5, int var6, int var7, ResourceLocation resourceLocation, int var9, int var10, Button.OnPress button$OnPress) {
      this(var1, var2, var3, var4, var5, var6, var7, resourceLocation, var9, var10, button$OnPress, "");
   }

   public ImageButton(int var1, int var2, int var3, int var4, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation resourceLocation, int textureWidth, int textureHeight, Button.OnPress button$OnPress, String string) {
      super(var1, var2, var3, var4, string, button$OnPress);
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.xTexStart = xTexStart;
      this.yTexStart = yTexStart;
      this.yDiffTex = yDiffTex;
      this.resourceLocation = resourceLocation;
   }

   public void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void renderButton(int var1, int var2, float var3) {
      Minecraft var4 = Minecraft.getInstance();
      var4.getTextureManager().bind(this.resourceLocation);
      GlStateManager.disableDepthTest();
      int var5 = this.yTexStart;
      if(this.isHovered()) {
         var5 += this.yDiffTex;
      }

      blit(this.x, this.y, (float)this.xTexStart, (float)var5, this.width, this.height, this.textureWidth, this.textureHeight);
      GlStateManager.enableDepthTest();
   }
}
