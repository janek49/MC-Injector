package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

@ClientJarOnly
public class EndermanModel extends HumanoidModel {
   public boolean carrying;
   public boolean creepy;

   public EndermanModel(float f) {
      super(0.0F, -14.0F, 64, 32);
      float var2 = -14.0F;
      this.hat = new ModelPart(this, 0, 16);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f - 0.5F);
      this.hat.setPos(0.0F, -14.0F, 0.0F);
      this.body = new ModelPart(this, 32, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
      this.body.setPos(0.0F, -14.0F, 0.0F);
      this.rightArm = new ModelPart(this, 56, 0);
      this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
      this.rightArm.setPos(-3.0F, -12.0F, 0.0F);
      this.leftArm = new ModelPart(this, 56, 0);
      this.leftArm.mirror = true;
      this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
      this.leftArm.setPos(5.0F, -12.0F, 0.0F);
      this.rightLeg = new ModelPart(this, 56, 0);
      this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
      this.rightLeg.setPos(-2.0F, -2.0F, 0.0F);
      this.leftLeg = new ModelPart(this, 56, 0);
      this.leftLeg.mirror = true;
      this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
      this.leftLeg.setPos(2.0F, -2.0F, 0.0F);
   }

   public void setupAnim(LivingEntity livingEntity, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(livingEntity, var2, var3, var4, var5, var6, var7);
      this.head.visible = true;
      float var8 = -14.0F;
      this.body.xRot = 0.0F;
      this.body.y = -14.0F;
      this.body.z = -0.0F;
      this.rightLeg.xRot -= 0.0F;
      this.leftLeg.xRot -= 0.0F;
      this.rightArm.xRot = (float)((double)this.rightArm.xRot * 0.5D);
      this.leftArm.xRot = (float)((double)this.leftArm.xRot * 0.5D);
      this.rightLeg.xRot = (float)((double)this.rightLeg.xRot * 0.5D);
      this.leftLeg.xRot = (float)((double)this.leftLeg.xRot * 0.5D);
      float var9 = 0.4F;
      if(this.rightArm.xRot > 0.4F) {
         this.rightArm.xRot = 0.4F;
      }

      if(this.leftArm.xRot > 0.4F) {
         this.leftArm.xRot = 0.4F;
      }

      if(this.rightArm.xRot < -0.4F) {
         this.rightArm.xRot = -0.4F;
      }

      if(this.leftArm.xRot < -0.4F) {
         this.leftArm.xRot = -0.4F;
      }

      if(this.rightLeg.xRot > 0.4F) {
         this.rightLeg.xRot = 0.4F;
      }

      if(this.leftLeg.xRot > 0.4F) {
         this.leftLeg.xRot = 0.4F;
      }

      if(this.rightLeg.xRot < -0.4F) {
         this.rightLeg.xRot = -0.4F;
      }

      if(this.leftLeg.xRot < -0.4F) {
         this.leftLeg.xRot = -0.4F;
      }

      if(this.carrying) {
         this.rightArm.xRot = -0.5F;
         this.leftArm.xRot = -0.5F;
         this.rightArm.zRot = 0.05F;
         this.leftArm.zRot = -0.05F;
      }

      this.rightArm.z = 0.0F;
      this.leftArm.z = 0.0F;
      this.rightLeg.z = 0.0F;
      this.leftLeg.z = 0.0F;
      this.rightLeg.y = -5.0F;
      this.leftLeg.y = -5.0F;
      this.head.z = -0.0F;
      this.head.y = -13.0F;
      this.hat.x = this.head.x;
      this.hat.y = this.head.y;
      this.hat.z = this.head.z;
      this.hat.xRot = this.head.xRot;
      this.hat.yRot = this.head.yRot;
      this.hat.zRot = this.head.zRot;
      if(this.creepy) {
         float var10 = 1.0F;
         this.head.y -= 5.0F;
      }

   }
}
