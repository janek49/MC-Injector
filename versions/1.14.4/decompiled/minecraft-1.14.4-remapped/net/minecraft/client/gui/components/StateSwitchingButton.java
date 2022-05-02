package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class StateSwitchingButton extends AbstractWidget {
   protected ResourceLocation resourceLocation;
   protected boolean isStateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public StateSwitchingButton(int var1, int var2, int var3, int var4, boolean isStateTriggered) {
      super(var1, var2, var3, var4, "");
      this.isStateTriggered = isStateTriggered;
   }

   public void initTextureValues(int xTexStart, int yTexStart, int xDiffTex, int yDiffTex, ResourceLocation resourceLocation) {
      this.xTexStart = xTexStart;
      this.yTexStart = yTexStart;
      this.xDiffTex = xDiffTex;
      this.yDiffTex = yDiffTex;
      this.resourceLocation = resourceLocation;
   }

   public void setStateTriggered(boolean stateTriggered) {
      this.isStateTriggered = stateTriggered;
   }

   public boolean isStateTriggered() {
      return this.isStateTriggered;
   }

   public void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void renderButton(int var1, int var2, float var3) {
      Minecraft var4 = Minecraft.getInstance();
      var4.getTextureManager().bind(this.resourceLocation);
      GlStateManager.disableDepthTest();
      int var5 = this.xTexStart;
      int var6 = this.yTexStart;
      if(this.isStateTriggered) {
         var5 += this.xDiffTex;
      }

      if(this.isHovered()) {
         var6 += this.yDiffTex;
      }

      this.blit(this.x, this.y, var5, var6, this.width, this.height);
      GlStateManager.enableDepthTest();
   }
}
