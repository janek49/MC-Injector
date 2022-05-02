package net.minecraft.client.renderer.blockentity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.List;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

@ClientJarOnly
public class BeaconRenderer extends BlockEntityRenderer {
   private static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");

   public void render(BeaconBlockEntity beaconBlockEntity, double var2, double var4, double var6, float var8, int var9) {
      this.renderBeaconBeam(var2, var4, var6, (double)var8, beaconBlockEntity.getBeamSections(), beaconBlockEntity.getLevel().getGameTime());
   }

   private void renderBeaconBeam(double var1, double var3, double var5, double var7, List list, long var10) {
      GlStateManager.alphaFunc(516, 0.1F);
      this.bindTexture(BEAM_LOCATION);
      GlStateManager.disableFog();
      int var12 = 0;

      for(int var13 = 0; var13 < list.size(); ++var13) {
         BeaconBlockEntity.BeaconBeamSection var14 = (BeaconBlockEntity.BeaconBeamSection)list.get(var13);
         renderBeaconBeam(var1, var3, var5, var7, var10, var12, var13 == list.size() - 1?1024:var14.getHeight(), var14.getColor());
         var12 += var14.getHeight();
      }

      GlStateManager.enableFog();
   }

   private static void renderBeaconBeam(double var0, double var2, double var4, double var6, long var8, int var10, int var11, float[] floats) {
      renderBeaconBeam(var0, var2, var4, var6, 1.0D, var8, var10, var11, floats, 0.2D, 0.25D);
   }

   public static void renderBeaconBeam(double var0, double var2, double var4, double var6, double var8, long var10, int var12, int var13, float[] floats, double var15, double var17) {
      int var19 = var12 + var13;
      GlStateManager.texParameter(3553, 10242, 10497);
      GlStateManager.texParameter(3553, 10243, 10497);
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.pushMatrix();
      GlStateManager.translated(var0 + 0.5D, var2, var4 + 0.5D);
      Tesselator var20 = Tesselator.getInstance();
      BufferBuilder var21 = var20.getBuilder();
      double var22 = (double)Math.floorMod(var10, 40L) + var6;
      double var24 = var13 < 0?var22:-var22;
      double var26 = Mth.frac(var24 * 0.2D - (double)Mth.floor(var24 * 0.1D));
      float var28 = floats[0];
      float var29 = floats[1];
      float var30 = floats[2];
      GlStateManager.pushMatrix();
      GlStateManager.rotated(var22 * 2.25D - 45.0D, 0.0D, 1.0D, 0.0D);
      double var31 = 0.0D;
      double var37 = 0.0D;
      double var39 = -var15;
      double var41 = 0.0D;
      double var43 = 0.0D;
      double var45 = -var15;
      double var47 = 0.0D;
      double var49 = 1.0D;
      double var51 = -1.0D + var26;
      double var53 = (double)var13 * var8 * (0.5D / var15) + var51;
      var21.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var21.vertex(0.0D, (double)var19, var15).uv(1.0D, var53).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(0.0D, (double)var12, var15).uv(1.0D, var51).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(var15, (double)var12, 0.0D).uv(0.0D, var51).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(var15, (double)var19, 0.0D).uv(0.0D, var53).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(0.0D, (double)var19, var45).uv(1.0D, var53).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(0.0D, (double)var12, var45).uv(1.0D, var51).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(var39, (double)var12, 0.0D).uv(0.0D, var51).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(var39, (double)var19, 0.0D).uv(0.0D, var53).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(var15, (double)var19, 0.0D).uv(1.0D, var53).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(var15, (double)var12, 0.0D).uv(1.0D, var51).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(0.0D, (double)var12, var45).uv(0.0D, var51).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(0.0D, (double)var19, var45).uv(0.0D, var53).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(var39, (double)var19, 0.0D).uv(1.0D, var53).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(var39, (double)var12, 0.0D).uv(1.0D, var51).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(0.0D, (double)var12, var15).uv(0.0D, var51).color(var28, var29, var30, 1.0F).endVertex();
      var21.vertex(0.0D, (double)var19, var15).uv(0.0D, var53).color(var28, var29, var30, 1.0F).endVertex();
      var20.end();
      GlStateManager.popMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.depthMask(false);
      var31 = -var17;
      double var33 = -var17;
      var37 = -var17;
      var39 = -var17;
      var47 = 0.0D;
      var49 = 1.0D;
      var51 = -1.0D + var26;
      var53 = (double)var13 * var8 + var51;
      var21.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var21.vertex(var31, (double)var19, var33).uv(1.0D, var53).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var31, (double)var12, var33).uv(1.0D, var51).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var17, (double)var12, var37).uv(0.0D, var51).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var17, (double)var19, var37).uv(0.0D, var53).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var17, (double)var19, var17).uv(1.0D, var53).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var17, (double)var12, var17).uv(1.0D, var51).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var39, (double)var12, var17).uv(0.0D, var51).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var39, (double)var19, var17).uv(0.0D, var53).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var17, (double)var19, var37).uv(1.0D, var53).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var17, (double)var12, var37).uv(1.0D, var51).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var17, (double)var12, var17).uv(0.0D, var51).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var17, (double)var19, var17).uv(0.0D, var53).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var39, (double)var19, var17).uv(1.0D, var53).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var39, (double)var12, var17).uv(1.0D, var51).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var31, (double)var12, var33).uv(0.0D, var51).color(var28, var29, var30, 0.125F).endVertex();
      var21.vertex(var31, (double)var19, var33).uv(0.0D, var53).color(var28, var29, var30, 0.125F).endVertex();
      var20.end();
      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture();
      GlStateManager.depthMask(true);
   }

   public boolean shouldRenderOffScreen(BeaconBlockEntity beaconBlockEntity) {
      return true;
   }
}
