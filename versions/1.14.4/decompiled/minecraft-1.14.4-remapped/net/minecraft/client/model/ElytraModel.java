package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public class ElytraModel extends EntityModel {
   private final ModelPart rightWing;
   private final ModelPart leftWing = new ModelPart(this, 22, 0);

   public ElytraModel() {
      this.leftWing.addBox(-10.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
      this.rightWing = new ModelPart(this, 22, 0);
      this.rightWing.mirror = true;
      this.rightWing.addBox(0.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
   }

   public void render(LivingEntity livingEntity, float var2, float var3, float var4, float var5, float var6, float var7) {
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableCull();
      if(livingEntity.isBaby()) {
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 1.5F, -0.1F);
         this.leftWing.render(var7);
         this.rightWing.render(var7);
         GlStateManager.popMatrix();
      } else {
         this.leftWing.render(var7);
         this.rightWing.render(var7);
      }

   }

   public void setupAnim(LivingEntity livingEntity, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(livingEntity, var2, var3, var4, var5, var6, var7);
      float var8 = 0.2617994F;
      float var9 = -0.2617994F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      if(livingEntity.isFallFlying()) {
         float var12 = 1.0F;
         Vec3 var13 = livingEntity.getDeltaMovement();
         if(var13.y < 0.0D) {
            Vec3 var14 = var13.normalize();
            var12 = 1.0F - (float)Math.pow(-var14.y, 1.5D);
         }

         var8 = var12 * 0.34906584F + (1.0F - var12) * var8;
         var9 = var12 * -1.5707964F + (1.0F - var12) * var9;
      } else if(livingEntity.isVisuallySneaking()) {
         var8 = 0.6981317F;
         var9 = -0.7853982F;
         var10 = 3.0F;
         var11 = 0.08726646F;
      }

      this.leftWing.x = 5.0F;
      this.leftWing.y = var10;
      if(livingEntity instanceof AbstractClientPlayer) {
         AbstractClientPlayer var12 = (AbstractClientPlayer)livingEntity;
         var12.elytraRotX = (float)((double)var12.elytraRotX + (double)(var8 - var12.elytraRotX) * 0.1D);
         var12.elytraRotY = (float)((double)var12.elytraRotY + (double)(var11 - var12.elytraRotY) * 0.1D);
         var12.elytraRotZ = (float)((double)var12.elytraRotZ + (double)(var9 - var12.elytraRotZ) * 0.1D);
         this.leftWing.xRot = var12.elytraRotX;
         this.leftWing.yRot = var12.elytraRotY;
         this.leftWing.zRot = var12.elytraRotZ;
      } else {
         this.leftWing.xRot = var8;
         this.leftWing.zRot = var9;
         this.leftWing.yRot = var11;
      }

      this.rightWing.x = -this.leftWing.x;
      this.rightWing.yRot = -this.leftWing.yRot;
      this.rightWing.y = this.leftWing.y;
      this.rightWing.xRot = this.leftWing.xRot;
      this.rightWing.zRot = -this.leftWing.zRot;
   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((LivingEntity)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((LivingEntity)var1, var2, var3, var4, var5, var6, var7);
   }
}
