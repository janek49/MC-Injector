package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public class MinecartRenderer extends EntityRenderer {
   private static final ResourceLocation MINECART_LOCATION = new ResourceLocation("textures/entity/minecart.png");
   protected final EntityModel model = new MinecartModel();

   public MinecartRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
      this.shadowRadius = 0.7F;
   }

   public void render(AbstractMinecart abstractMinecart, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      this.bindTexture(abstractMinecart);
      long var10 = (long)abstractMinecart.getId() * 493286711L;
      var10 = var10 * var10 * 4392167121L + var10 * 98761L;
      float var12 = (((float)(var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var13 = (((float)(var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var14 = (((float)(var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      GlStateManager.translatef(var12, var13, var14);
      double var15 = Mth.lerp((double)var9, abstractMinecart.xOld, abstractMinecart.x);
      double var17 = Mth.lerp((double)var9, abstractMinecart.yOld, abstractMinecart.y);
      double var19 = Mth.lerp((double)var9, abstractMinecart.zOld, abstractMinecart.z);
      double var21 = 0.30000001192092896D;
      Vec3 var23 = abstractMinecart.getPos(var15, var17, var19);
      float var24 = Mth.lerp(var9, abstractMinecart.xRotO, abstractMinecart.xRot);
      if(var23 != null) {
         Vec3 var25 = abstractMinecart.getPosOffs(var15, var17, var19, 0.30000001192092896D);
         Vec3 var26 = abstractMinecart.getPosOffs(var15, var17, var19, -0.30000001192092896D);
         if(var25 == null) {
            var25 = var23;
         }

         if(var26 == null) {
            var26 = var23;
         }

         var2 += var23.x - var15;
         var4 += (var25.y + var26.y) / 2.0D - var17;
         var6 += var23.z - var19;
         Vec3 var27 = var26.add(-var25.x, -var25.y, -var25.z);
         if(var27.length() != 0.0D) {
            var27 = var27.normalize();
            var8 = (float)(Math.atan2(var27.z, var27.x) * 180.0D / 3.141592653589793D);
            var24 = (float)(Math.atan(var27.y) * 73.0D);
         }
      }

      GlStateManager.translatef((float)var2, (float)var4 + 0.375F, (float)var6);
      GlStateManager.rotatef(180.0F - var8, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-var24, 0.0F, 0.0F, 1.0F);
      float var25 = (float)abstractMinecart.getHurtTime() - var9;
      float var26 = abstractMinecart.getDamage() - var9;
      if(var26 < 0.0F) {
         var26 = 0.0F;
      }

      if(var25 > 0.0F) {
         GlStateManager.rotatef(Mth.sin(var25) * var25 * var26 / 10.0F * (float)abstractMinecart.getHurtDir(), 1.0F, 0.0F, 0.0F);
      }

      int var27 = abstractMinecart.getDisplayOffset();
      if(this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(abstractMinecart));
      }

      BlockState var28 = abstractMinecart.getDisplayBlockState();
      if(var28.getRenderShape() != RenderShape.INVISIBLE) {
         GlStateManager.pushMatrix();
         this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
         float var29 = 0.75F;
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.translatef(-0.5F, (float)(var27 - 8) / 16.0F, 0.5F);
         this.renderMinecartContents(abstractMinecart, var9, var28);
         GlStateManager.popMatrix();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindTexture(abstractMinecart);
      }

      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.model.render(abstractMinecart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      if(this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      super.render(abstractMinecart, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(AbstractMinecart abstractMinecart) {
      return MINECART_LOCATION;
   }

   protected void renderMinecartContents(AbstractMinecart abstractMinecart, float var2, BlockState blockState) {
      GlStateManager.pushMatrix();
      Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, abstractMinecart.getBrightness());
      GlStateManager.popMatrix();
   }
}
