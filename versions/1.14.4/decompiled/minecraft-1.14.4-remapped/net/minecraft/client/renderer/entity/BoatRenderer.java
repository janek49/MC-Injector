package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

@ClientJarOnly
public class BoatRenderer extends EntityRenderer {
   private static final ResourceLocation[] BOAT_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/boat/oak.png"), new ResourceLocation("textures/entity/boat/spruce.png"), new ResourceLocation("textures/entity/boat/birch.png"), new ResourceLocation("textures/entity/boat/jungle.png"), new ResourceLocation("textures/entity/boat/acacia.png"), new ResourceLocation("textures/entity/boat/dark_oak.png")};
   protected final BoatModel model = new BoatModel();

   public BoatRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
      this.shadowRadius = 0.8F;
   }

   public void render(Boat boat, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      this.setupTranslation(var2, var4, var6);
      this.setupRotation(boat, var8, var9);
      this.bindTexture(boat);
      if(this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(boat));
      }

      this.model.render(boat, var9, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      if(this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      super.render(boat, var2, var4, var6, var8, var9);
   }

   public void setupRotation(Boat boat, float var2, float var3) {
      GlStateManager.rotatef(180.0F - var2, 0.0F, 1.0F, 0.0F);
      float var4 = (float)boat.getHurtTime() - var3;
      float var5 = boat.getDamage() - var3;
      if(var5 < 0.0F) {
         var5 = 0.0F;
      }

      if(var4 > 0.0F) {
         GlStateManager.rotatef(Mth.sin(var4) * var4 * var5 / 10.0F * (float)boat.getHurtDir(), 1.0F, 0.0F, 0.0F);
      }

      float var6 = boat.getBubbleAngle(var3);
      if(!Mth.equal(var6, 0.0F)) {
         GlStateManager.rotatef(boat.getBubbleAngle(var3), 1.0F, 0.0F, 1.0F);
      }

      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
   }

   public void setupTranslation(double var1, double var3, double var5) {
      GlStateManager.translatef((float)var1, (float)var3 + 0.375F, (float)var5);
   }

   protected ResourceLocation getTextureLocation(Boat boat) {
      return BOAT_TEXTURE_LOCATIONS[boat.getBoatType().ordinal()];
   }

   public boolean hasSecondPass() {
      return true;
   }

   public void renderSecondPass(Boat boat, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      this.setupTranslation(var2, var4, var6);
      this.setupRotation(boat, var8, var9);
      this.bindTexture(boat);
      this.model.renderSecondPass(boat, var9, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
