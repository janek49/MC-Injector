package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public class SpiderModel extends EntityModel {
   private final ModelPart head;
   private final ModelPart body0;
   private final ModelPart body1;
   private final ModelPart leg0;
   private final ModelPart leg1;
   private final ModelPart leg2;
   private final ModelPart leg3;
   private final ModelPart leg4;
   private final ModelPart leg5;
   private final ModelPart leg6;
   private final ModelPart leg7;

   public SpiderModel() {
      float var1 = 0.0F;
      int var2 = 15;
      this.head = new ModelPart(this, 32, 4);
      this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
      this.head.setPos(0.0F, 15.0F, -3.0F);
      this.body0 = new ModelPart(this, 0, 0);
      this.body0.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F);
      this.body0.setPos(0.0F, 15.0F, 0.0F);
      this.body1 = new ModelPart(this, 0, 12);
      this.body1.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, 0.0F);
      this.body1.setPos(0.0F, 15.0F, 9.0F);
      this.leg0 = new ModelPart(this, 18, 0);
      this.leg0.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.leg0.setPos(-4.0F, 15.0F, 2.0F);
      this.leg1 = new ModelPart(this, 18, 0);
      this.leg1.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.leg1.setPos(4.0F, 15.0F, 2.0F);
      this.leg2 = new ModelPart(this, 18, 0);
      this.leg2.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.leg2.setPos(-4.0F, 15.0F, 1.0F);
      this.leg3 = new ModelPart(this, 18, 0);
      this.leg3.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.leg3.setPos(4.0F, 15.0F, 1.0F);
      this.leg4 = new ModelPart(this, 18, 0);
      this.leg4.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.leg4.setPos(-4.0F, 15.0F, 0.0F);
      this.leg5 = new ModelPart(this, 18, 0);
      this.leg5.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.leg5.setPos(4.0F, 15.0F, 0.0F);
      this.leg6 = new ModelPart(this, 18, 0);
      this.leg6.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.leg6.setPos(-4.0F, 15.0F, -1.0F);
      this.leg7 = new ModelPart(this, 18, 0);
      this.leg7.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.leg7.setPos(4.0F, 15.0F, -1.0F);
   }

   public void render(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(entity, var2, var3, var4, var5, var6, var7);
      this.head.render(var7);
      this.body0.render(var7);
      this.body1.render(var7);
      this.leg0.render(var7);
      this.leg1.render(var7);
      this.leg2.render(var7);
      this.leg3.render(var7);
      this.leg4.render(var7);
      this.leg5.render(var7);
      this.leg6.render(var7);
      this.leg7.render(var7);
   }

   public void setupAnim(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      float var8 = 0.7853982F;
      this.leg0.zRot = -0.7853982F;
      this.leg1.zRot = 0.7853982F;
      this.leg2.zRot = -0.58119464F;
      this.leg3.zRot = 0.58119464F;
      this.leg4.zRot = -0.58119464F;
      this.leg5.zRot = 0.58119464F;
      this.leg6.zRot = -0.7853982F;
      this.leg7.zRot = 0.7853982F;
      float var9 = -0.0F;
      float var10 = 0.3926991F;
      this.leg0.yRot = 0.7853982F;
      this.leg1.yRot = -0.7853982F;
      this.leg2.yRot = 0.3926991F;
      this.leg3.yRot = -0.3926991F;
      this.leg4.yRot = -0.3926991F;
      this.leg5.yRot = 0.3926991F;
      this.leg6.yRot = -0.7853982F;
      this.leg7.yRot = 0.7853982F;
      float var11 = -(Mth.cos(var2 * 0.6662F * 2.0F + 0.0F) * 0.4F) * var3;
      float var12 = -(Mth.cos(var2 * 0.6662F * 2.0F + 3.1415927F) * 0.4F) * var3;
      float var13 = -(Mth.cos(var2 * 0.6662F * 2.0F + 1.5707964F) * 0.4F) * var3;
      float var14 = -(Mth.cos(var2 * 0.6662F * 2.0F + 4.712389F) * 0.4F) * var3;
      float var15 = Math.abs(Mth.sin(var2 * 0.6662F + 0.0F) * 0.4F) * var3;
      float var16 = Math.abs(Mth.sin(var2 * 0.6662F + 3.1415927F) * 0.4F) * var3;
      float var17 = Math.abs(Mth.sin(var2 * 0.6662F + 1.5707964F) * 0.4F) * var3;
      float var18 = Math.abs(Mth.sin(var2 * 0.6662F + 4.712389F) * 0.4F) * var3;
      this.leg0.yRot += var11;
      this.leg1.yRot += -var11;
      this.leg2.yRot += var12;
      this.leg3.yRot += -var12;
      this.leg4.yRot += var13;
      this.leg5.yRot += -var13;
      this.leg6.yRot += var14;
      this.leg7.yRot += -var14;
      this.leg0.zRot += var15;
      this.leg1.zRot += -var15;
      this.leg2.zRot += var16;
      this.leg3.zRot += -var16;
      this.leg4.zRot += var17;
      this.leg5.zRot += -var17;
      this.leg6.zRot += var18;
      this.leg7.zRot += -var18;
   }
}
