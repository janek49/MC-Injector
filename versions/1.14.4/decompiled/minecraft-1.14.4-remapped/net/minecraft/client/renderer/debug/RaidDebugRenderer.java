package net.minecraft.client.renderer.debug;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

@ClientJarOnly
public class RaidDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private Collection raidCenters = Lists.newArrayList();

   public RaidDebugRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void setRaidCenters(Collection raidCenters) {
      this.raidCenters = raidCenters;
   }

   public void render(long l) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      this.doRender();
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }

   private void doRender() {
      BlockPos var1 = this.getCamera().getBlockPosition();

      for(BlockPos var3 : this.raidCenters) {
         if(var1.closerThan(var3, 160.0D)) {
            highlightRaidCenter(var3);
         }
      }

   }

   private static void highlightRaidCenter(BlockPos blockPos) {
      DebugRenderer.renderFilledBox(blockPos.offset(-0.5D, -0.5D, -0.5D), blockPos.offset(1.5D, 1.5D, 1.5D), 1.0F, 0.0F, 0.0F, 0.15F);
      int var1 = -65536;
      renderTextOverBlock("Raid center", blockPos, -65536);
   }

   private static void renderTextOverBlock(String string, BlockPos blockPos, int var2) {
      double var3 = (double)blockPos.getX() + 0.5D;
      double var5 = (double)blockPos.getY() + 1.3D;
      double var7 = (double)blockPos.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(string, var3, var5, var7, var2, 0.04F, true, 0.0F, true);
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }
}
