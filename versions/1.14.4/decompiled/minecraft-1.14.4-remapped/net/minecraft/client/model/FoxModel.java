package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Fox;

@ClientJarOnly
public class FoxModel extends EntityModel {
   public final ModelPart head;
   private final ModelPart earL;
   private final ModelPart earR;
   private final ModelPart nose;
   private final ModelPart body;
   private final ModelPart leg0;
   private final ModelPart leg1;
   private final ModelPart leg2;
   private final ModelPart leg3;
   private final ModelPart tail;
   private float legMotionPos;

   public FoxModel() {
      this.texWidth = 48;
      this.texHeight = 32;
      this.head = new ModelPart(this, 1, 5);
      this.head.addBox(-3.0F, -2.0F, -5.0F, 8, 6, 6);
      this.head.setPos(-1.0F, 16.5F, -3.0F);
      this.earL = new ModelPart(this, 8, 1);
      this.earL.addBox(-3.0F, -4.0F, -4.0F, 2, 2, 1);
      this.earR = new ModelPart(this, 15, 1);
      this.earR.addBox(3.0F, -4.0F, -4.0F, 2, 2, 1);
      this.nose = new ModelPart(this, 6, 18);
      this.nose.addBox(-1.0F, 2.01F, -8.0F, 4, 2, 3);
      this.head.addChild(this.earL);
      this.head.addChild(this.earR);
      this.head.addChild(this.nose);
      this.body = new ModelPart(this, 24, 15);
      this.body.addBox(-3.0F, 3.999F, -3.5F, 6, 11, 6);
      this.body.setPos(0.0F, 16.0F, -6.0F);
      float var1 = 0.001F;
      this.leg0 = new ModelPart(this, 13, 24);
      this.leg0.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
      this.leg0.setPos(-5.0F, 17.5F, 7.0F);
      this.leg1 = new ModelPart(this, 4, 24);
      this.leg1.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
      this.leg1.setPos(-1.0F, 17.5F, 7.0F);
      this.leg2 = new ModelPart(this, 13, 24);
      this.leg2.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
      this.leg2.setPos(-5.0F, 17.5F, 0.0F);
      this.leg3 = new ModelPart(this, 4, 24);
      this.leg3.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
      this.leg3.setPos(-1.0F, 17.5F, 0.0F);
      this.tail = new ModelPart(this, 30, 0);
      this.tail.addBox(2.0F, 0.0F, -1.0F, 4, 9, 5);
      this.tail.setPos(-4.0F, 15.0F, -1.0F);
      this.body.addChild(this.tail);
   }

   public void prepareMobModel(Fox fox, float var2, float var3, float var4) {
      this.body.xRot = 1.5707964F;
      this.tail.xRot = -0.05235988F;
      this.leg0.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      this.leg1.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.leg2.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.leg3.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      this.head.setPos(-1.0F, 16.5F, -3.0F);
      this.head.yRot = 0.0F;
      this.head.zRot = fox.getHeadRollAngle(var4);
      this.leg0.visible = true;
      this.leg1.visible = true;
      this.leg2.visible = true;
      this.leg3.visible = true;
      this.body.setPos(0.0F, 16.0F, -6.0F);
      this.body.zRot = 0.0F;
      this.leg0.setPos(-5.0F, 17.5F, 7.0F);
      this.leg1.setPos(-1.0F, 17.5F, 7.0F);
      if(fox.isCrouching()) {
         this.body.xRot = 1.6755161F;
         float var5 = fox.getCrouchAmount(var4);
         this.body.setPos(0.0F, 16.0F + fox.getCrouchAmount(var4), -6.0F);
         this.head.setPos(-1.0F, 16.5F + var5, -3.0F);
         this.head.yRot = 0.0F;
      } else if(fox.isSleeping()) {
         this.body.zRot = -1.5707964F;
         this.body.setPos(0.0F, 21.0F, -6.0F);
         this.tail.xRot = -2.6179938F;
         if(this.young) {
            this.tail.xRot = -2.1816616F;
            this.body.setPos(0.0F, 21.0F, -2.0F);
         }

         this.head.setPos(1.0F, 19.49F, -3.0F);
         this.head.xRot = 0.0F;
         this.head.yRot = -2.0943952F;
         this.head.zRot = 0.0F;
         this.leg0.visible = false;
         this.leg1.visible = false;
         this.leg2.visible = false;
         this.leg3.visible = false;
      } else if(fox.isSitting()) {
         this.body.xRot = 0.5235988F;
         this.body.setPos(0.0F, 9.0F, -3.0F);
         this.tail.xRot = 0.7853982F;
         this.tail.setPos(-4.0F, 15.0F, -2.0F);
         this.head.setPos(-1.0F, 10.0F, -0.25F);
         this.head.xRot = 0.0F;
         this.head.yRot = 0.0F;
         if(this.young) {
            this.head.setPos(-1.0F, 13.0F, -3.75F);
         }

         this.leg0.xRot = -1.3089969F;
         this.leg0.setPos(-5.0F, 21.5F, 6.75F);
         this.leg1.xRot = -1.3089969F;
         this.leg1.setPos(-1.0F, 21.5F, 6.75F);
         this.leg2.xRot = -0.2617994F;
         this.leg3.xRot = -0.2617994F;
      }

   }

   public void render(Fox fox, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.render(fox, var2, var3, var4, var5, var6, var7);
      this.setupAnim(fox, var2, var3, var4, var5, var6, var7);
      if(this.young) {
         GlStateManager.pushMatrix();
         float var8 = 0.75F;
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.translatef(0.0F, 8.0F * var7, 3.35F * var7);
         this.head.render(var7);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         float var9 = 0.5F;
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * var7, 0.0F);
         this.body.render(var7);
         this.leg0.render(var7);
         this.leg1.render(var7);
         this.leg2.render(var7);
         this.leg3.render(var7);
         GlStateManager.popMatrix();
      } else {
         GlStateManager.pushMatrix();
         this.head.render(var7);
         this.body.render(var7);
         this.leg0.render(var7);
         this.leg1.render(var7);
         this.leg2.render(var7);
         this.leg3.render(var7);
         GlStateManager.popMatrix();
      }

   }

   public void setupAnim(Fox fox, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(fox, var2, var3, var4, var5, var6, var7);
      if(!fox.isSleeping() && !fox.isFaceplanted() && !fox.isCrouching()) {
         this.head.xRot = var6 * 0.017453292F;
         this.head.yRot = var5 * 0.017453292F;
      }

      if(fox.isSleeping()) {
         this.head.xRot = 0.0F;
         this.head.yRot = -2.0943952F;
         this.head.zRot = Mth.cos(var4 * 0.027F) / 22.0F;
      }

      if(fox.isCrouching()) {
         float var8 = Mth.cos(var4) * 0.01F;
         this.body.yRot = var8;
         this.leg0.zRot = var8;
         this.leg1.zRot = var8;
         this.leg2.zRot = var8 / 2.0F;
         this.leg3.zRot = var8 / 2.0F;
      }

      if(fox.isFaceplanted()) {
         float var8 = 0.1F;
         this.legMotionPos += 0.67F;
         this.leg0.xRot = Mth.cos(this.legMotionPos * 0.4662F) * 0.1F;
         this.leg1.xRot = Mth.cos(this.legMotionPos * 0.4662F + 3.1415927F) * 0.1F;
         this.leg2.xRot = Mth.cos(this.legMotionPos * 0.4662F + 3.1415927F) * 0.1F;
         this.leg3.xRot = Mth.cos(this.legMotionPos * 0.4662F) * 0.1F;
      }

   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((Fox)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((Fox)var1, var2, var3, var4, var5, var6, var7);
   }
}
