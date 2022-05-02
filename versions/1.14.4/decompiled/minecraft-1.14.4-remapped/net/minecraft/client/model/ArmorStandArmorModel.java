package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.decoration.ArmorStand;

@ClientJarOnly
public class ArmorStandArmorModel extends HumanoidModel {
   public ArmorStandArmorModel() {
      this(0.0F);
   }

   public ArmorStandArmorModel(float f) {
      this(f, 64, 32);
   }

   protected ArmorStandArmorModel(float var1, int var2, int var3) {
      super(var1, 0.0F, var2, var3);
   }

   public void setupAnim(ArmorStand armorStand, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.head.xRot = 0.017453292F * armorStand.getHeadPose().getX();
      this.head.yRot = 0.017453292F * armorStand.getHeadPose().getY();
      this.head.zRot = 0.017453292F * armorStand.getHeadPose().getZ();
      this.head.setPos(0.0F, 1.0F, 0.0F);
      this.body.xRot = 0.017453292F * armorStand.getBodyPose().getX();
      this.body.yRot = 0.017453292F * armorStand.getBodyPose().getY();
      this.body.zRot = 0.017453292F * armorStand.getBodyPose().getZ();
      this.leftArm.xRot = 0.017453292F * armorStand.getLeftArmPose().getX();
      this.leftArm.yRot = 0.017453292F * armorStand.getLeftArmPose().getY();
      this.leftArm.zRot = 0.017453292F * armorStand.getLeftArmPose().getZ();
      this.rightArm.xRot = 0.017453292F * armorStand.getRightArmPose().getX();
      this.rightArm.yRot = 0.017453292F * armorStand.getRightArmPose().getY();
      this.rightArm.zRot = 0.017453292F * armorStand.getRightArmPose().getZ();
      this.leftLeg.xRot = 0.017453292F * armorStand.getLeftLegPose().getX();
      this.leftLeg.yRot = 0.017453292F * armorStand.getLeftLegPose().getY();
      this.leftLeg.zRot = 0.017453292F * armorStand.getLeftLegPose().getZ();
      this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
      this.rightLeg.xRot = 0.017453292F * armorStand.getRightLegPose().getX();
      this.rightLeg.yRot = 0.017453292F * armorStand.getRightLegPose().getY();
      this.rightLeg.zRot = 0.017453292F * armorStand.getRightLegPose().getZ();
      this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
      this.hat.copyFrom(this.head);
   }
}
