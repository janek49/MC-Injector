package net.minecraft.client.renderer.debug;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

@ClientJarOnly
public class CaveDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map tunnelsList = Maps.newHashMap();
   private final Map thicknessMap = Maps.newHashMap();
   private final List startPoses = Lists.newArrayList();

   public CaveDebugRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void addTunnel(BlockPos blockPos, List var2, List var3) {
      for(int var4 = 0; var4 < var2.size(); ++var4) {
         this.tunnelsList.put(var2.get(var4), blockPos);
         this.thicknessMap.put(var2.get(var4), var3.get(var4));
      }

      this.startPoses.add(blockPos);
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
      BlockPos var10 = new BlockPos(var3.getPosition().x, 0.0D, var3.getPosition().z);
      Tesselator var11 = Tesselator.getInstance();
      BufferBuilder var12 = var11.getBuilder();
      var12.begin(5, DefaultVertexFormat.POSITION_COLOR);

      for(Entry<BlockPos, BlockPos> var14 : this.tunnelsList.entrySet()) {
         BlockPos var15 = (BlockPos)var14.getKey();
         BlockPos var16 = (BlockPos)var14.getValue();
         float var17 = (float)(var16.getX() * 128 % 256) / 256.0F;
         float var18 = (float)(var16.getY() * 128 % 256) / 256.0F;
         float var19 = (float)(var16.getZ() * 128 % 256) / 256.0F;
         float var20 = ((Float)this.thicknessMap.get(var15)).floatValue();
         if(var10.closerThan(var15, 160.0D)) {
            LevelRenderer.addChainedFilledBoxVertices(var12, (double)((float)var15.getX() + 0.5F) - var4 - (double)var20, (double)((float)var15.getY() + 0.5F) - var6 - (double)var20, (double)((float)var15.getZ() + 0.5F) - var8 - (double)var20, (double)((float)var15.getX() + 0.5F) - var4 + (double)var20, (double)((float)var15.getY() + 0.5F) - var6 + (double)var20, (double)((float)var15.getZ() + 0.5F) - var8 + (double)var20, var17, var18, var19, 0.5F);
         }
      }

      for(BlockPos var14 : this.startPoses) {
         if(var10.closerThan(var14, 160.0D)) {
            LevelRenderer.addChainedFilledBoxVertices(var12, (double)var14.getX() - var4, (double)var14.getY() - var6, (double)var14.getZ() - var8, (double)((float)var14.getX() + 1.0F) - var4, (double)((float)var14.getY() + 1.0F) - var6, (double)((float)var14.getZ() + 1.0F) - var8, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      var11.end();
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }
}
