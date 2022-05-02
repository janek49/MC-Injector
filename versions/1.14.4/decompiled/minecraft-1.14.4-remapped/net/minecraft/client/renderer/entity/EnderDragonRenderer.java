package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.model.dragon.DragonModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EnderDragonDeathLayer;
import net.minecraft.client.renderer.entity.layers.EnderDragonEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

@ClientJarOnly
public class EnderDragonRenderer extends MobRenderer {
   public static final ResourceLocation CRYSTAL_BEAM_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal_beam.png");
   private static final ResourceLocation DRAGON_EXPLODING_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
   private static final ResourceLocation DRAGON_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon.png");

   public EnderDragonRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new DragonModel(0.0F), 0.5F);
      this.addLayer(new EnderDragonEyesLayer(this));
      this.addLayer(new EnderDragonDeathLayer(this));
   }

   protected void setupRotations(EnderDragon enderDragon, float var2, float var3, float var4) {
      float var5 = (float)enderDragon.getLatencyPos(7, var4)[0];
      float var6 = (float)(enderDragon.getLatencyPos(5, var4)[1] - enderDragon.getLatencyPos(10, var4)[1]);
      GlStateManager.rotatef(-var5, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var6 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.0F, 1.0F);
      if(enderDragon.deathTime > 0) {
         float var7 = ((float)enderDragon.deathTime + var4 - 1.0F) / 20.0F * 1.6F;
         var7 = Mth.sqrt(var7);
         if(var7 > 1.0F) {
            var7 = 1.0F;
         }

         GlStateManager.rotatef(var7 * this.getFlipDegrees(enderDragon), 0.0F, 0.0F, 1.0F);
      }

   }

   protected void renderModel(EnderDragon enderDragon, float var2, float var3, float var4, float var5, float var6, float var7) {
      if(enderDragon.dragonDeathTime > 0) {
         float var8 = (float)enderDragon.dragonDeathTime / 200.0F;
         GlStateManager.depthFunc(515);
         GlStateManager.enableAlphaTest();
         GlStateManager.alphaFunc(516, var8);
         this.bindTexture(DRAGON_EXPLODING_LOCATION);
         ((DragonModel)this.model).render(enderDragon, var2, var3, var4, var5, var6, var7);
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.depthFunc(514);
      }

      this.bindTexture(enderDragon);
      ((DragonModel)this.model).render(enderDragon, var2, var3, var4, var5, var6, var7);
      if(enderDragon.hurtTime > 0) {
         GlStateManager.depthFunc(514);
         GlStateManager.disableTexture();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.color4f(1.0F, 0.0F, 0.0F, 0.5F);
         ((DragonModel)this.model).render(enderDragon, var2, var3, var4, var5, var6, var7);
         GlStateManager.enableTexture();
         GlStateManager.disableBlend();
         GlStateManager.depthFunc(515);
      }

   }

   public void render(EnderDragon enderDragon, double var2, double var4, double var6, float var8, float var9) {
      super.render((Mob)enderDragon, var2, var4, var6, var8, var9);
      if(enderDragon.nearestCrystal != null) {
         this.bindTexture(CRYSTAL_BEAM_LOCATION);
         float var10 = Mth.sin(((float)enderDragon.nearestCrystal.tickCount + var9) * 0.2F) / 2.0F + 0.5F;
         var10 = (var10 * var10 + var10) * 0.2F;
         renderCrystalBeams(var2, var4, var6, var9, Mth.lerp((double)(1.0F - var9), enderDragon.x, enderDragon.xo), Mth.lerp((double)(1.0F - var9), enderDragon.y, enderDragon.yo), Mth.lerp((double)(1.0F - var9), enderDragon.z, enderDragon.zo), enderDragon.tickCount, enderDragon.nearestCrystal.x, (double)var10 + enderDragon.nearestCrystal.y, enderDragon.nearestCrystal.z);
      }

   }

   public static void renderCrystalBeams(double var0, double var2, double var4, float var6, double var7, double var9, double var11, int var13, double var14, double var16, double var18) {
      float var20 = (float)(var14 - var7);
      float var21 = (float)(var16 - 1.0D - var9);
      float var22 = (float)(var18 - var11);
      float var23 = Mth.sqrt(var20 * var20 + var22 * var22);
      float var24 = Mth.sqrt(var20 * var20 + var21 * var21 + var22 * var22);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var0, (float)var2 + 2.0F, (float)var4);
      GlStateManager.rotatef((float)(-Math.atan2((double)var22, (double)var20)) * 57.295776F - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef((float)(-Math.atan2((double)var23, (double)var21)) * 57.295776F - 90.0F, 1.0F, 0.0F, 0.0F);
      Tesselator var25 = Tesselator.getInstance();
      BufferBuilder var26 = var25.getBuilder();
      Lighting.turnOff();
      GlStateManager.disableCull();
      GlStateManager.shadeModel(7425);
      float var27 = 0.0F - ((float)var13 + var6) * 0.01F;
      float var28 = Mth.sqrt(var20 * var20 + var21 * var21 + var22 * var22) / 32.0F - ((float)var13 + var6) * 0.01F;
      var26.begin(5, DefaultVertexFormat.POSITION_TEX_COLOR);
      int var29 = 8;

      for(int var30 = 0; var30 <= 8; ++var30) {
         float var31 = Mth.sin((float)(var30 % 8) * 6.2831855F / 8.0F) * 0.75F;
         float var32 = Mth.cos((float)(var30 % 8) * 6.2831855F / 8.0F) * 0.75F;
         float var33 = (float)(var30 % 8) / 8.0F;
         var26.vertex((double)(var31 * 0.2F), (double)(var32 * 0.2F), 0.0D).uv((double)var33, (double)var27).color(0, 0, 0, 255).endVertex();
         var26.vertex((double)var31, (double)var32, (double)var24).uv((double)var33, (double)var28).color(255, 255, 255, 255).endVertex();
      }

      var25.end();
      GlStateManager.enableCull();
      GlStateManager.shadeModel(7424);
      Lighting.turnOn();
      GlStateManager.popMatrix();
   }

   protected ResourceLocation getTextureLocation(EnderDragon enderDragon) {
      return DRAGON_LOCATION;
   }
}
