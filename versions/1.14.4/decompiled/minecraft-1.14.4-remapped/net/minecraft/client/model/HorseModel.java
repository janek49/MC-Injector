package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

@ClientJarOnly
public class HorseModel extends EntityModel {
   protected final ModelPart body;
   protected final ModelPart headParts;
   private final ModelPart leg1A;
   private final ModelPart leg2A;
   private final ModelPart leg3A;
   private final ModelPart leg4A;
   private final ModelPart tail;
   private final ModelPart[] saddleParts;
   private final ModelPart[] ridingParts;

   public HorseModel(float f) {
      this.texWidth = 64;
      this.texHeight = 64;
      this.body = new ModelPart(this, 0, 32);
      this.body.addBox(-5.0F, -8.0F, -17.0F, 10, 10, 22, 0.05F);
      this.body.setPos(0.0F, 11.0F, 5.0F);
      this.headParts = new ModelPart(this, 0, 35);
      this.headParts.addBox(-2.05F, -6.0F, -2.0F, 4, 12, 7);
      this.headParts.xRot = 0.5235988F;
      ModelPart var2 = new ModelPart(this, 0, 13);
      var2.addBox(-3.0F, -11.0F, -2.0F, 6, 5, 7, f);
      ModelPart var3 = new ModelPart(this, 56, 36);
      var3.addBox(-1.0F, -11.0F, 5.01F, 2, 16, 2, f);
      ModelPart var4 = new ModelPart(this, 0, 25);
      var4.addBox(-2.0F, -11.0F, -7.0F, 4, 5, 5, f);
      this.headParts.addChild(var2);
      this.headParts.addChild(var3);
      this.headParts.addChild(var4);
      this.addEarModels(this.headParts);
      this.leg1A = new ModelPart(this, 48, 21);
      this.leg1A.mirror = true;
      this.leg1A.addBox(-3.0F, -1.01F, -1.0F, 4, 11, 4, f);
      this.leg1A.setPos(4.0F, 14.0F, 7.0F);
      this.leg2A = new ModelPart(this, 48, 21);
      this.leg2A.addBox(-1.0F, -1.01F, -1.0F, 4, 11, 4, f);
      this.leg2A.setPos(-4.0F, 14.0F, 7.0F);
      this.leg3A = new ModelPart(this, 48, 21);
      this.leg3A.mirror = true;
      this.leg3A.addBox(-3.0F, -1.01F, -1.9F, 4, 11, 4, f);
      this.leg3A.setPos(4.0F, 6.0F, -12.0F);
      this.leg4A = new ModelPart(this, 48, 21);
      this.leg4A.addBox(-1.0F, -1.01F, -1.9F, 4, 11, 4, f);
      this.leg4A.setPos(-4.0F, 6.0F, -12.0F);
      this.tail = new ModelPart(this, 42, 36);
      this.tail.addBox(-1.5F, 0.0F, 0.0F, 3, 14, 4, f);
      this.tail.setPos(0.0F, -5.0F, 2.0F);
      this.tail.xRot = 0.5235988F;
      this.body.addChild(this.tail);
      ModelPart var5 = new ModelPart(this, 26, 0);
      var5.addBox(-5.0F, -8.0F, -9.0F, 10, 9, 9, 0.5F);
      this.body.addChild(var5);
      ModelPart var6 = new ModelPart(this, 29, 5);
      var6.addBox(2.0F, -9.0F, -6.0F, 1, 2, 2, f);
      this.headParts.addChild(var6);
      ModelPart var7 = new ModelPart(this, 29, 5);
      var7.addBox(-3.0F, -9.0F, -6.0F, 1, 2, 2, f);
      this.headParts.addChild(var7);
      ModelPart var8 = new ModelPart(this, 32, 2);
      var8.addBox(3.1F, -6.0F, -8.0F, 0, 3, 16, f);
      var8.xRot = -0.5235988F;
      this.headParts.addChild(var8);
      ModelPart var9 = new ModelPart(this, 32, 2);
      var9.addBox(-3.1F, -6.0F, -8.0F, 0, 3, 16, f);
      var9.xRot = -0.5235988F;
      this.headParts.addChild(var9);
      ModelPart var10 = new ModelPart(this, 1, 1);
      var10.addBox(-3.0F, -11.0F, -1.9F, 6, 5, 6, 0.2F);
      this.headParts.addChild(var10);
      ModelPart var11 = new ModelPart(this, 19, 0);
      var11.addBox(-2.0F, -11.0F, -4.0F, 4, 5, 2, 0.2F);
      this.headParts.addChild(var11);
      this.saddleParts = new ModelPart[]{var5, var6, var7, var10, var11};
      this.ridingParts = new ModelPart[]{var8, var9};
   }

   protected void addEarModels(ModelPart modelPart) {
      ModelPart modelPart = new ModelPart(this, 19, 16);
      modelPart.addBox(0.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
      ModelPart var3 = new ModelPart(this, 19, 16);
      var3.addBox(-2.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
      modelPart.addChild(modelPart);
      modelPart.addChild(var3);
   }

   public void render(AbstractHorse abstractHorse, float var2, float var3, float var4, float var5, float var6, float var7) {
      boolean var8 = abstractHorse.isBaby();
      float var9 = abstractHorse.getScale();
      boolean var10 = abstractHorse.isSaddled();
      boolean var11 = abstractHorse.isVehicle();

      for(ModelPart var15 : this.saddleParts) {
         var15.visible = var10;
      }

      for(ModelPart var15 : this.ridingParts) {
         var15.visible = var11 && var10;
      }

      if(var8) {
         GlStateManager.pushMatrix();
         GlStateManager.scalef(var9, 0.5F + var9 * 0.5F, var9);
         GlStateManager.translatef(0.0F, 0.95F * (1.0F - var9), 0.0F);
      }

      this.leg1A.render(var7);
      this.leg2A.render(var7);
      this.leg3A.render(var7);
      this.leg4A.render(var7);
      if(var8) {
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(var9, var9, var9);
         GlStateManager.translatef(0.0F, 2.3F * (1.0F - var9), 0.0F);
      }

      this.body.render(var7);
      if(var8) {
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         float var12 = var9 + 0.1F * var9;
         GlStateManager.scalef(var12, var12, var12);
         GlStateManager.translatef(0.0F, 2.25F * (1.0F - var12), 0.1F * (1.4F - var12));
      }

      this.headParts.render(var7);
      if(var8) {
         GlStateManager.popMatrix();
      }

   }

   public void prepareMobModel(AbstractHorse abstractHorse, float var2, float var3, float var4) {
      super.prepareMobModel(abstractHorse, var2, var3, var4);
      float var5 = this.rotlerp(abstractHorse.yBodyRotO, abstractHorse.yBodyRot, var4);
      float var6 = this.rotlerp(abstractHorse.yHeadRotO, abstractHorse.yHeadRot, var4);
      float var7 = Mth.lerp(var4, abstractHorse.xRotO, abstractHorse.xRot);
      float var8 = var6 - var5;
      float var9 = var7 * 0.017453292F;
      if(var8 > 20.0F) {
         var8 = 20.0F;
      }

      if(var8 < -20.0F) {
         var8 = -20.0F;
      }

      if(var3 > 0.2F) {
         var9 += Mth.cos(var2 * 0.4F) * 0.15F * var3;
      }

      float var10 = abstractHorse.getEatAnim(var4);
      float var11 = abstractHorse.getStandAnim(var4);
      float var12 = 1.0F - var11;
      float var13 = abstractHorse.getMouthAnim(var4);
      boolean var14 = abstractHorse.tailCounter != 0;
      float var15 = (float)abstractHorse.tickCount + var4;
      this.headParts.y = 4.0F;
      this.headParts.z = -12.0F;
      this.body.xRot = 0.0F;
      this.headParts.xRot = 0.5235988F + var9;
      this.headParts.yRot = var8 * 0.017453292F;
      float var16 = abstractHorse.isInWater()?0.2F:1.0F;
      float var17 = Mth.cos(var16 * var2 * 0.6662F + 3.1415927F);
      float var18 = var17 * 0.8F * var3;
      float var19 = (1.0F - Math.max(var11, var10)) * (0.5235988F + var9 + var13 * Mth.sin(var15) * 0.05F);
      this.headParts.xRot = var11 * (0.2617994F + var9) + var10 * (2.1816616F + Mth.sin(var15) * 0.05F) + var19;
      this.headParts.yRot = var11 * var8 * 0.017453292F + (1.0F - Math.max(var11, var10)) * this.headParts.yRot;
      this.headParts.y = var11 * -4.0F + var10 * 11.0F + (1.0F - Math.max(var11, var10)) * this.headParts.y;
      this.headParts.z = var11 * -4.0F + var10 * -12.0F + (1.0F - Math.max(var11, var10)) * this.headParts.z;
      this.body.xRot = var11 * -0.7853982F + var12 * this.body.xRot;
      float var20 = 0.2617994F * var11;
      float var21 = Mth.cos(var15 * 0.6F + 3.1415927F);
      this.leg3A.y = 2.0F * var11 + 14.0F * var12;
      this.leg3A.z = -6.0F * var11 - 10.0F * var12;
      this.leg4A.y = this.leg3A.y;
      this.leg4A.z = this.leg3A.z;
      float var22 = (-1.0471976F + var21) * var11 + var18 * var12;
      float var23 = (-1.0471976F - var21) * var11 - var18 * var12;
      this.leg1A.xRot = var20 - var17 * 0.5F * var3 * var12;
      this.leg2A.xRot = var20 + var17 * 0.5F * var3 * var12;
      this.leg3A.xRot = var22;
      this.leg4A.xRot = var23;
      this.tail.xRot = 0.5235988F + var3 * 0.75F;
      this.tail.y = -5.0F + var3;
      this.tail.z = 2.0F + var3 * 2.0F;
      if(var14) {
         this.tail.yRot = Mth.cos(var15 * 0.7F);
      } else {
         this.tail.yRot = 0.0F;
      }

   }

   private float rotlerp(float var1, float var2, float var3) {
      float var4;
      for(var4 = var2 - var1; var4 < -180.0F; var4 += 360.0F) {
         ;
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return var1 + var3 * var4;
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((AbstractHorse)var1, var2, var3, var4, var5, var6, var7);
   }
}
