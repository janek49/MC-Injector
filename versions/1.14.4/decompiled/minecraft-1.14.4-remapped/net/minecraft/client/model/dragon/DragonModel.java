package net.minecraft.client.model.dragon;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

@ClientJarOnly
public class DragonModel extends EntityModel {
   private final ModelPart head;
   private final ModelPart neck;
   private final ModelPart jaw;
   private final ModelPart body;
   private final ModelPart rearLeg;
   private final ModelPart frontLeg;
   private final ModelPart rearLegTip;
   private final ModelPart frontLegTip;
   private final ModelPart rearFoot;
   private final ModelPart frontFoot;
   private final ModelPart wing;
   private final ModelPart wingTip;
   private float a;

   public DragonModel(float f) {
      this.texWidth = 256;
      this.texHeight = 256;
      float var2 = -16.0F;
      this.head = new ModelPart(this, "head");
      this.head.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, f, 176, 44);
      this.head.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, f, 112, 30);
      this.head.mirror = true;
      this.head.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
      this.head.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
      this.head.mirror = false;
      this.head.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
      this.head.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
      this.jaw = new ModelPart(this, "jaw");
      this.jaw.setPos(0.0F, 4.0F, -8.0F);
      this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, f, 176, 65);
      this.head.addChild(this.jaw);
      this.neck = new ModelPart(this, "neck");
      this.neck.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, f, 192, 104);
      this.neck.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, f, 48, 0);
      this.body = new ModelPart(this, "body");
      this.body.setPos(0.0F, 4.0F, 8.0F);
      this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, f, 0, 0);
      this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, f, 220, 53);
      this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, f, 220, 53);
      this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, f, 220, 53);
      this.wing = new ModelPart(this, "wing");
      this.wing.setPos(-12.0F, 5.0F, 2.0F);
      this.wing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, f, 112, 88);
      this.wing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, f, -56, 88);
      this.wingTip = new ModelPart(this, "wingtip");
      this.wingTip.setPos(-56.0F, 0.0F, 0.0F);
      this.wingTip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, f, 112, 136);
      this.wingTip.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, f, -56, 144);
      this.wing.addChild(this.wingTip);
      this.frontLeg = new ModelPart(this, "frontleg");
      this.frontLeg.setPos(-12.0F, 20.0F, 2.0F);
      this.frontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, f, 112, 104);
      this.frontLegTip = new ModelPart(this, "frontlegtip");
      this.frontLegTip.setPos(0.0F, 20.0F, -1.0F);
      this.frontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, f, 226, 138);
      this.frontLeg.addChild(this.frontLegTip);
      this.frontFoot = new ModelPart(this, "frontfoot");
      this.frontFoot.setPos(0.0F, 23.0F, 0.0F);
      this.frontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, f, 144, 104);
      this.frontLegTip.addChild(this.frontFoot);
      this.rearLeg = new ModelPart(this, "rearleg");
      this.rearLeg.setPos(-16.0F, 16.0F, 42.0F);
      this.rearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, f, 0, 0);
      this.rearLegTip = new ModelPart(this, "rearlegtip");
      this.rearLegTip.setPos(0.0F, 32.0F, -4.0F);
      this.rearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, f, 196, 0);
      this.rearLeg.addChild(this.rearLegTip);
      this.rearFoot = new ModelPart(this, "rearfoot");
      this.rearFoot.setPos(0.0F, 31.0F, 4.0F);
      this.rearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, f, 112, 0);
      this.rearLegTip.addChild(this.rearFoot);
   }

   public void prepareMobModel(EnderDragon enderDragon, float var2, float var3, float a) {
      this.a = a;
   }

   public void render(EnderDragon enderDragon, float var2, float var3, float var4, float var5, float var6, float var7) {
      GlStateManager.pushMatrix();
      float var8 = Mth.lerp(this.a, enderDragon.oFlapTime, enderDragon.flapTime);
      this.jaw.xRot = (float)(Math.sin((double)(var8 * 6.2831855F)) + 1.0D) * 0.2F;
      float var9 = (float)(Math.sin((double)(var8 * 6.2831855F - 1.0F)) + 1.0D);
      var9 = (var9 * var9 + var9 * 2.0F) * 0.05F;
      GlStateManager.translatef(0.0F, var9 - 2.0F, -3.0F);
      GlStateManager.rotatef(var9 * 2.0F, 1.0F, 0.0F, 0.0F);
      float var10 = 0.0F;
      float var11 = 20.0F;
      float var12 = -12.0F;
      float var13 = 1.5F;
      double[] vars14 = enderDragon.getLatencyPos(6, this.a);
      float var15 = this.rotWrap(enderDragon.getLatencyPos(5, this.a)[0] - enderDragon.getLatencyPos(10, this.a)[0]);
      float var16 = this.rotWrap(enderDragon.getLatencyPos(5, this.a)[0] + (double)(var15 / 2.0F));
      float var17 = var8 * 6.2831855F;

      for(int var18 = 0; var18 < 5; ++var18) {
         double[] vars19 = enderDragon.getLatencyPos(5 - var18, this.a);
         float var20 = (float)Math.cos((double)((float)var18 * 0.45F + var17)) * 0.15F;
         this.neck.yRot = this.rotWrap(vars19[0] - vars14[0]) * 0.017453292F * 1.5F;
         this.neck.xRot = var20 + enderDragon.getHeadPartYOffset(var18, vars14, vars19) * 0.017453292F * 1.5F * 5.0F;
         this.neck.zRot = -this.rotWrap(vars19[0] - (double)var16) * 0.017453292F * 1.5F;
         this.neck.y = var11;
         this.neck.z = var12;
         this.neck.x = var10;
         var11 = (float)((double)var11 + Math.sin((double)this.neck.xRot) * 10.0D);
         var12 = (float)((double)var12 - Math.cos((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0D);
         var10 = (float)((double)var10 - Math.sin((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0D);
         this.neck.render(var7);
      }

      this.head.y = var11;
      this.head.z = var12;
      this.head.x = var10;
      double[] vars18 = enderDragon.getLatencyPos(0, this.a);
      this.head.yRot = this.rotWrap(vars18[0] - vars14[0]) * 0.017453292F;
      this.head.xRot = this.rotWrap((double)enderDragon.getHeadPartYOffset(6, vars14, vars18)) * 0.017453292F * 1.5F * 5.0F;
      this.head.zRot = -this.rotWrap(vars18[0] - (double)var16) * 0.017453292F;
      this.head.render(var7);
      GlStateManager.pushMatrix();
      GlStateManager.translatef(0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-var15 * 1.5F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(0.0F, -1.0F, 0.0F);
      this.body.zRot = 0.0F;
      this.body.render(var7);

      for(int var19 = 0; var19 < 2; ++var19) {
         GlStateManager.enableCull();
         float var20 = var8 * 6.2831855F;
         this.wing.xRot = 0.125F - (float)Math.cos((double)var20) * 0.2F;
         this.wing.yRot = 0.25F;
         this.wing.zRot = (float)(Math.sin((double)var20) + 0.125D) * 0.8F;
         this.wingTip.zRot = -((float)(Math.sin((double)(var20 + 2.0F)) + 0.5D)) * 0.75F;
         this.rearLeg.xRot = 1.0F + var9 * 0.1F;
         this.rearLegTip.xRot = 0.5F + var9 * 0.1F;
         this.rearFoot.xRot = 0.75F + var9 * 0.1F;
         this.frontLeg.xRot = 1.3F + var9 * 0.1F;
         this.frontLegTip.xRot = -0.5F - var9 * 0.1F;
         this.frontFoot.xRot = 0.75F + var9 * 0.1F;
         this.wing.render(var7);
         this.frontLeg.render(var7);
         this.rearLeg.render(var7);
         GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
         if(var19 == 0) {
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.cullFace(GlStateManager.CullFace.BACK);
      GlStateManager.disableCull();
      float var19 = -((float)Math.sin((double)(var8 * 6.2831855F))) * 0.0F;
      var17 = var8 * 6.2831855F;
      var11 = 10.0F;
      var12 = 60.0F;
      var10 = 0.0F;
      vars14 = enderDragon.getLatencyPos(11, this.a);

      for(int var20 = 0; var20 < 12; ++var20) {
         vars18 = enderDragon.getLatencyPos(12 + var20, this.a);
         var19 = (float)((double)var19 + Math.sin((double)((float)var20 * 0.45F + var17)) * 0.05000000074505806D);
         this.neck.yRot = (this.rotWrap(vars18[0] - vars14[0]) * 1.5F + 180.0F) * 0.017453292F;
         this.neck.xRot = var19 + (float)(vars18[1] - vars14[1]) * 0.017453292F * 1.5F * 5.0F;
         this.neck.zRot = this.rotWrap(vars18[0] - (double)var16) * 0.017453292F * 1.5F;
         this.neck.y = var11;
         this.neck.z = var12;
         this.neck.x = var10;
         var11 = (float)((double)var11 + Math.sin((double)this.neck.xRot) * 10.0D);
         var12 = (float)((double)var12 - Math.cos((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0D);
         var10 = (float)((double)var10 - Math.sin((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0D);
         this.neck.render(var7);
      }

      GlStateManager.popMatrix();
   }

   private float rotWrap(double d) {
      while(d >= 180.0D) {
         d -= 360.0D;
      }

      while(d < -180.0D) {
         d += 360.0D;
      }

      return (float)d;
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((EnderDragon)var1, var2, var3, var4, var5, var6, var7);
   }
}
