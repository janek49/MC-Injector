package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.dragon.EndCrystalModel;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

@ClientJarOnly
public class EndCrystalRenderer extends EntityRenderer {
   private static final ResourceLocation END_CRYSTAL_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
   private final EntityModel model = new EndCrystalModel(0.0F, true);
   private final EntityModel modelWithoutBottom = new EndCrystalModel(0.0F, false);

   public EndCrystalRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
      this.shadowRadius = 0.5F;
   }

   public void render(EndCrystal endCrystal, double var2, double var4, double var6, float var8, float var9) {
      float var10 = (float)endCrystal.time + var9;
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      this.bindTexture(END_CRYSTAL_LOCATION);
      float var11 = Mth.sin(var10 * 0.2F) / 2.0F + 0.5F;
      var11 = var11 * var11 + var11;
      if(this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(endCrystal));
      }

      if(endCrystal.showsBottom()) {
         this.model.render(endCrystal, 0.0F, var10 * 3.0F, var11 * 0.2F, 0.0F, 0.0F, 0.0625F);
      } else {
         this.modelWithoutBottom.render(endCrystal, 0.0F, var10 * 3.0F, var11 * 0.2F, 0.0F, 0.0F, 0.0625F);
      }

      if(this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      BlockPos var12 = endCrystal.getBeamTarget();
      if(var12 != null) {
         this.bindTexture(EnderDragonRenderer.CRYSTAL_BEAM_LOCATION);
         float var13 = (float)var12.getX() + 0.5F;
         float var14 = (float)var12.getY() + 0.5F;
         float var15 = (float)var12.getZ() + 0.5F;
         double var16 = (double)var13 - endCrystal.x;
         double var18 = (double)var14 - endCrystal.y;
         double var20 = (double)var15 - endCrystal.z;
         EnderDragonRenderer.renderCrystalBeams(var2 + var16, var4 - 0.3D + (double)(var11 * 0.4F) + var18, var6 + var20, var9, (double)var13, (double)var14, (double)var15, endCrystal.time, endCrystal.x, endCrystal.y, endCrystal.z);
      }

      super.render(endCrystal, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(EndCrystal endCrystal) {
      return END_CRYSTAL_LOCATION;
   }

   public boolean shouldRender(EndCrystal endCrystal, Culler culler, double var3, double var5, double var7) {
      return super.shouldRender(endCrystal, culler, var3, var5, var7) || endCrystal.getBeamTarget() != null;
   }
}
