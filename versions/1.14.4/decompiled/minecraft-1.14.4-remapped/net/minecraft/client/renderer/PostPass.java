package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.server.packs.resources.ResourceManager;

@ClientJarOnly
public class PostPass implements AutoCloseable {
   private final EffectInstance effect;
   public final RenderTarget inTarget;
   public final RenderTarget outTarget;
   private final List auxAssets = Lists.newArrayList();
   private final List auxNames = Lists.newArrayList();
   private final List auxWidths = Lists.newArrayList();
   private final List auxHeights = Lists.newArrayList();
   private Matrix4f shaderOrthoMatrix;

   public PostPass(ResourceManager resourceManager, String string, RenderTarget inTarget, RenderTarget outTarget) throws IOException {
      this.effect = new EffectInstance(resourceManager, string);
      this.inTarget = inTarget;
      this.outTarget = outTarget;
   }

   public void close() {
      this.effect.close();
   }

   public void addAuxAsset(String string, Object object, int var3, int var4) {
      this.auxNames.add(this.auxNames.size(), string);
      this.auxAssets.add(this.auxAssets.size(), object);
      this.auxWidths.add(this.auxWidths.size(), Integer.valueOf(var3));
      this.auxHeights.add(this.auxHeights.size(), Integer.valueOf(var4));
   }

   private void prepareState() {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.disableDepthTest();
      GlStateManager.disableAlphaTest();
      GlStateManager.disableFog();
      GlStateManager.disableLighting();
      GlStateManager.disableColorMaterial();
      GlStateManager.enableTexture();
      GlStateManager.bindTexture(0);
   }

   public void setOrthoMatrix(Matrix4f orthoMatrix) {
      this.shaderOrthoMatrix = orthoMatrix;
   }

   public void process(float f) {
      this.prepareState();
      this.inTarget.unbindWrite();
      float var2 = (float)this.outTarget.width;
      float var3 = (float)this.outTarget.height;
      GlStateManager.viewport(0, 0, (int)var2, (int)var3);
      this.effect.setSampler("DiffuseSampler", this.inTarget);

      for(int var4 = 0; var4 < this.auxAssets.size(); ++var4) {
         this.effect.setSampler((String)this.auxNames.get(var4), this.auxAssets.get(var4));
         this.effect.safeGetUniform("AuxSize" + var4).set((float)((Integer)this.auxWidths.get(var4)).intValue(), (float)((Integer)this.auxHeights.get(var4)).intValue());
      }

      this.effect.safeGetUniform("ProjMat").set(this.shaderOrthoMatrix);
      this.effect.safeGetUniform("InSize").set((float)this.inTarget.width, (float)this.inTarget.height);
      this.effect.safeGetUniform("OutSize").set(var2, var3);
      this.effect.safeGetUniform("Time").set(f);
      Minecraft var4 = Minecraft.getInstance();
      this.effect.safeGetUniform("ScreenSize").set((float)var4.window.getWidth(), (float)var4.window.getHeight());
      this.effect.apply();
      this.outTarget.clear(Minecraft.ON_OSX);
      this.outTarget.bindWrite(false);
      GlStateManager.depthMask(false);
      GlStateManager.colorMask(true, true, true, true);
      Tesselator var5 = Tesselator.getInstance();
      BufferBuilder var6 = var5.getBuilder();
      var6.begin(7, DefaultVertexFormat.POSITION_COLOR);
      var6.vertex(0.0D, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
      var6.vertex((double)var2, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
      var6.vertex((double)var2, (double)var3, 500.0D).color(255, 255, 255, 255).endVertex();
      var6.vertex(0.0D, (double)var3, 500.0D).color(255, 255, 255, 255).endVertex();
      var5.end();
      GlStateManager.depthMask(true);
      GlStateManager.colorMask(true, true, true, true);
      this.effect.clear();
      this.outTarget.unbindWrite();
      this.inTarget.unbindRead();

      for(Object var8 : this.auxAssets) {
         if(var8 instanceof RenderTarget) {
            ((RenderTarget)var8).unbindRead();
         }
      }

   }

   public EffectInstance getEffect() {
      return this.effect;
   }
}
