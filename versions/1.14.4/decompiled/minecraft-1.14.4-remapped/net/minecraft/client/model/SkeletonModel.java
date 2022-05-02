package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@ClientJarOnly
public class SkeletonModel extends HumanoidModel {
   public SkeletonModel() {
      this(0.0F, false);
   }

   public SkeletonModel(float var1, boolean var2) {
      super(var1, 0.0F, 64, 32);
      if(!var2) {
         this.rightArm = new ModelPart(this, 40, 16);
         this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, var1);
         this.rightArm.setPos(-5.0F, 2.0F, 0.0F);
         this.leftArm = new ModelPart(this, 40, 16);
         this.leftArm.mirror = true;
         this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, var1);
         this.leftArm.setPos(5.0F, 2.0F, 0.0F);
         this.rightLeg = new ModelPart(this, 0, 16);
         this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, var1);
         this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
         this.leftLeg = new ModelPart(this, 0, 16);
         this.leftLeg.mirror = true;
         this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, var1);
         this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
      }

   }

   public void prepareMobModel(Mob mob, float var2, float var3, float var4) {
      this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
      this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
      ItemStack var5 = mob.getItemInHand(InteractionHand.MAIN_HAND);
      if(var5.getItem() == Items.BOW && mob.isAggressive()) {
         if(mob.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
         } else {
            this.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
         }
      }

      super.prepareMobModel((LivingEntity)mob, var2, var3, var4);
   }

   public void setupAnim(Mob mob, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim((LivingEntity)mob, var2, var3, var4, var5, var6, var7);
      ItemStack var8 = mob.getMainHandItem();
      if(mob.isAggressive() && (var8.isEmpty() || var8.getItem() != Items.BOW)) {
         float var9 = Mth.sin(this.attackTime * 3.1415927F);
         float var10 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * 3.1415927F);
         this.rightArm.zRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightArm.yRot = -(0.1F - var9 * 0.6F);
         this.leftArm.yRot = 0.1F - var9 * 0.6F;
         this.rightArm.xRot = -1.5707964F;
         this.leftArm.xRot = -1.5707964F;
         this.rightArm.xRot -= var9 * 1.2F - var10 * 0.4F;
         this.leftArm.xRot -= var9 * 1.2F - var10 * 0.4F;
         this.rightArm.zRot += Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
         this.leftArm.zRot -= Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
         this.rightArm.xRot += Mth.sin(var4 * 0.067F) * 0.05F;
         this.leftArm.xRot -= Mth.sin(var4 * 0.067F) * 0.05F;
      }

   }

   public void translateToHand(float var1, HumanoidArm humanoidArm) {
      float var3 = humanoidArm == HumanoidArm.RIGHT?1.0F:-1.0F;
      ModelPart var4 = this.getArm(humanoidArm);
      var4.x += var3;
      var4.translateTo(var1);
      var4.x -= var3;
   }
}
