package net.minecraft.client.renderer.debug;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

@ClientJarOnly
public class WorldGenAttemptRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final List toRender = Lists.newArrayList();
   private final List scales = Lists.newArrayList();
   private final List alphas = Lists.newArrayList();
   private final List reds = Lists.newArrayList();
   private final List greens = Lists.newArrayList();
   private final List blues = Lists.newArrayList();

   public WorldGenAttemptRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void addPos(BlockPos blockPos, float var2, float var3, float var4, float var5, float var6) {
      this.toRender.add(blockPos);
      this.scales.add(Float.valueOf(var2));
      this.alphas.add(Float.valueOf(var6));
      this.reds.add(Float.valueOf(var3));
      this.greens.add(Float.valueOf(var4));
      this.blues.add(Float.valueOf(var5));
   }

   public void render(long l) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      double var4 = var3.getPosition().x;
      double var6 = var3.getPosition().y;
      double var8 = var3.getPosition().z;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      Tesselator var10 = Tesselator.getInstance();
      BufferBuilder var11 = var10.getBuilder();
      var11.begin(5, DefaultVertexFormat.POSITION_COLOR);

      for(int var12 = 0; var12 < this.toRender.size(); ++var12) {
         BlockPos var13 = (BlockPos)this.toRender.get(var12);
         Float var14 = (Float)this.scales.get(var12);
         float var15 = var14.floatValue() / 2.0F;
         LevelRenderer.addChainedFilledBoxVertices(var11, (double)((float)var13.getX() + 0.5F - var15) - var4, (double)((float)var13.getY() + 0.5F - var15) - var6, (double)((float)var13.getZ() + 0.5F - var15) - var8, (double)((float)var13.getX() + 0.5F + var15) - var4, (double)((float)var13.getY() + 0.5F + var15) - var6, (double)((float)var13.getZ() + 0.5F + var15) - var8, ((Float)this.reds.get(var12)).floatValue(), ((Float)this.greens.get(var12)).floatValue(), ((Float)this.blues.get(var12)).floatValue(), ((Float)this.alphas.get(var12)).floatValue());
      }

      var10.end();
      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }
}
