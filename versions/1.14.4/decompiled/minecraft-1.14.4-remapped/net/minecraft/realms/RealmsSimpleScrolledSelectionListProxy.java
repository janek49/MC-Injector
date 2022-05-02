package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ScrolledSelectionList;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.util.Mth;

@ClientJarOnly
public class RealmsSimpleScrolledSelectionListProxy extends ScrolledSelectionList {
   private final RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList;

   public RealmsSimpleScrolledSelectionListProxy(RealmsSimpleScrolledSelectionList realmsSimpleScrolledSelectionList, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.getInstance(), var2, var3, var4, var5, var6);
      this.realmsSimpleScrolledSelectionList = realmsSimpleScrolledSelectionList;
   }

   public int getItemCount() {
      return this.realmsSimpleScrolledSelectionList.getItemCount();
   }

   public boolean selectItem(int var1, int var2, double var3, double var5) {
      return this.realmsSimpleScrolledSelectionList.selectItem(var1, var2, var3, var5);
   }

   public boolean isSelectedItem(int i) {
      return this.realmsSimpleScrolledSelectionList.isSelectedItem(i);
   }

   public void renderBackground() {
      this.realmsSimpleScrolledSelectionList.renderBackground();
   }

   public void renderItem(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
      this.realmsSimpleScrolledSelectionList.renderItem(var1, var2, var3, var4, var5, var6);
   }

   public int getWidth() {
      return this.width;
   }

   public int getMaxPosition() {
      return this.realmsSimpleScrolledSelectionList.getMaxPosition();
   }

   public int getScrollbarPosition() {
      return this.realmsSimpleScrolledSelectionList.getScrollbarPosition();
   }

   public void render(int var1, int var2, float var3) {
      if(this.visible) {
         this.renderBackground();
         int var4 = this.getScrollbarPosition();
         int var5 = var4 + 6;
         this.capYPosition();
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tesselator var6 = Tesselator.getInstance();
         BufferBuilder var7 = var6.getBuilder();
         int var8 = this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
         int var9 = this.y0 + 4 - (int)this.yo;
         if(this.renderHeader) {
            this.renderHeader(var8, var9, var6);
         }

         this.renderList(var8, var9, var1, var2, var3);
         GlStateManager.disableDepthTest();
         this.renderHoleBackground(0, this.y0, 255, 255);
         this.renderHoleBackground(this.y1, this.height, 255, 255);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         GlStateManager.disableAlphaTest();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture();
         int var10 = this.getMaxScroll();
         if(var10 > 0) {
            int var11 = (this.y1 - this.y0) * (this.y1 - this.y0) / this.getMaxPosition();
            var11 = Mth.clamp(var11, 32, this.y1 - this.y0 - 8);
            int var12 = (int)this.yo * (this.y1 - this.y0 - var11) / var10 + this.y0;
            if(var12 < this.y0) {
               var12 = this.y0;
            }

            var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            var7.vertex((double)var4, (double)this.y1, 0.0D).uv(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            var7.vertex((double)var5, (double)this.y1, 0.0D).uv(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            var7.vertex((double)var5, (double)this.y0, 0.0D).uv(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            var7.vertex((double)var4, (double)this.y0, 0.0D).uv(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            var6.end();
            var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            var7.vertex((double)var4, (double)(var12 + var11), 0.0D).uv(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            var7.vertex((double)var5, (double)(var12 + var11), 0.0D).uv(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            var7.vertex((double)var5, (double)var12, 0.0D).uv(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            var7.vertex((double)var4, (double)var12, 0.0D).uv(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            var6.end();
            var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            var7.vertex((double)var4, (double)(var12 + var11 - 1), 0.0D).uv(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            var7.vertex((double)(var5 - 1), (double)(var12 + var11 - 1), 0.0D).uv(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            var7.vertex((double)(var5 - 1), (double)var12, 0.0D).uv(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            var7.vertex((double)var4, (double)var12, 0.0D).uv(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            var6.end();
         }

         this.renderDecorations(var1, var2);
         GlStateManager.enableTexture();
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlphaTest();
         GlStateManager.disableBlend();
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.realmsSimpleScrolledSelectionList.mouseScrolled(var1, var3, var5)?true:super.mouseScrolled(var1, var3, var5);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.realmsSimpleScrolledSelectionList.mouseClicked(var1, var3, var5)?true:super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.realmsSimpleScrolledSelectionList.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.realmsSimpleScrolledSelectionList.mouseDragged(var1, var3, var5, var6, var8);
   }
}
