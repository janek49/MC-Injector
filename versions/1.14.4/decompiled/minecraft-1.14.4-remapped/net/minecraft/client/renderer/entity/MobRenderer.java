package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.HangingEntity;

@ClientJarOnly
public abstract class MobRenderer extends LivingEntityRenderer {
   public MobRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float var3) {
      super(entityRenderDispatcher, entityModel, var3);
   }

   protected boolean shouldShowName(Mob mob) {
      return super.shouldShowName((LivingEntity)mob) && (mob.shouldShowName() || mob.hasCustomName() && mob == this.entityRenderDispatcher.crosshairPickEntity);
   }

   public boolean shouldRender(Mob mob, Culler culler, double var3, double var5, double var7) {
      if(super.shouldRender(mob, culler, var3, var5, var7)) {
         return true;
      } else {
         Entity var9 = mob.getLeashHolder();
         return var9 != null?culler.isVisible(var9.getBoundingBoxForCulling()):false;
      }
   }

   public void render(Mob mob, double var2, double var4, double var6, float var8, float var9) {
      super.render((LivingEntity)mob, var2, var4, var6, var8, var9);
      if(!this.solidRender) {
         this.renderLeash(mob, var2, var4, var6, var8, var9);
      }

   }

   protected void renderLeash(Mob mob, double var2, double var4, double var6, float var8, float var9) {
      Entity var10 = mob.getLeashHolder();
      if(var10 != null) {
         var4 = var4 - (1.6D - (double)mob.getBbHeight()) * 0.5D;
         Tesselator var11 = Tesselator.getInstance();
         BufferBuilder var12 = var11.getBuilder();
         double var13 = (double)(Mth.lerp(var9 * 0.5F, var10.yRot, var10.yRotO) * 0.017453292F);
         double var15 = (double)(Mth.lerp(var9 * 0.5F, var10.xRot, var10.xRotO) * 0.017453292F);
         double var17 = Math.cos(var13);
         double var19 = Math.sin(var13);
         double var21 = Math.sin(var15);
         if(var10 instanceof HangingEntity) {
            var17 = 0.0D;
            var19 = 0.0D;
            var21 = -1.0D;
         }

         double var23 = Math.cos(var15);
         double var25 = Mth.lerp((double)var9, var10.xo, var10.x) - var17 * 0.7D - var19 * 0.5D * var23;
         double var27 = Mth.lerp((double)var9, var10.yo + (double)var10.getEyeHeight() * 0.7D, var10.y + (double)var10.getEyeHeight() * 0.7D) - var21 * 0.5D - 0.25D;
         double var29 = Mth.lerp((double)var9, var10.zo, var10.z) - var19 * 0.7D + var17 * 0.5D * var23;
         double var31 = (double)(Mth.lerp(var9, mob.yBodyRot, mob.yBodyRotO) * 0.017453292F) + 1.5707963267948966D;
         var17 = Math.cos(var31) * (double)mob.getBbWidth() * 0.4D;
         var19 = Math.sin(var31) * (double)mob.getBbWidth() * 0.4D;
         double var33 = Mth.lerp((double)var9, mob.xo, mob.x) + var17;
         double var35 = Mth.lerp((double)var9, mob.yo, mob.y);
         double var37 = Mth.lerp((double)var9, mob.zo, mob.z) + var19;
         var2 = var2 + var17;
         var6 = var6 + var19;
         double var39 = (double)((float)(var25 - var33));
         double var41 = (double)((float)(var27 - var35));
         double var43 = (double)((float)(var29 - var37));
         GlStateManager.disableTexture();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         int var45 = 24;
         double var46 = 0.025D;
         var12.begin(5, DefaultVertexFormat.POSITION_COLOR);

         for(int var48 = 0; var48 <= 24; ++var48) {
            float var49 = 0.5F;
            float var50 = 0.4F;
            float var51 = 0.3F;
            if(var48 % 2 == 0) {
               var49 *= 0.7F;
               var50 *= 0.7F;
               var51 *= 0.7F;
            }

            float var52 = (float)var48 / 24.0F;
            var12.vertex(var2 + var39 * (double)var52 + 0.0D, var4 + var41 * (double)(var52 * var52 + var52) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F), var6 + var43 * (double)var52).color(var49, var50, var51, 1.0F).endVertex();
            var12.vertex(var2 + var39 * (double)var52 + 0.025D, var4 + var41 * (double)(var52 * var52 + var52) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F) + 0.025D, var6 + var43 * (double)var52).color(var49, var50, var51, 1.0F).endVertex();
         }

         var11.end();
         var12.begin(5, DefaultVertexFormat.POSITION_COLOR);

         for(int var48 = 0; var48 <= 24; ++var48) {
            float var49 = 0.5F;
            float var50 = 0.4F;
            float var51 = 0.3F;
            if(var48 % 2 == 0) {
               var49 *= 0.7F;
               var50 *= 0.7F;
               var51 *= 0.7F;
            }

            float var52 = (float)var48 / 24.0F;
            var12.vertex(var2 + var39 * (double)var52 + 0.0D, var4 + var41 * (double)(var52 * var52 + var52) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F) + 0.025D, var6 + var43 * (double)var52).color(var49, var50, var51, 1.0F).endVertex();
            var12.vertex(var2 + var39 * (double)var52 + 0.025D, var4 + var41 * (double)(var52 * var52 + var52) * 0.5D + (double)((24.0F - (float)var48) / 18.0F + 0.125F), var6 + var43 * (double)var52 + 0.025D).color(var49, var50, var51, 1.0F).endVertex();
         }

         var11.end();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture();
         GlStateManager.enableCull();
      }
   }

   // $FF: synthetic method
   protected boolean shouldShowName(LivingEntity var1) {
      return this.shouldShowName((Mob)var1);
   }
}
