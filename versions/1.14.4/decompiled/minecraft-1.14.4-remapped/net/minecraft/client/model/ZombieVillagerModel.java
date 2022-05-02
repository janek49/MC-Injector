package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.VillagerHeadModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;

@ClientJarOnly
public class ZombieVillagerModel extends HumanoidModel implements VillagerHeadModel {
   private ModelPart hatRim;

   public ZombieVillagerModel() {
      this(0.0F, false);
   }

   public ZombieVillagerModel(float var1, boolean var2) {
      super(var1, 0.0F, 64, var2?32:64);
      if(var2) {
         this.head = new ModelPart(this, 0, 0);
         this.head.addBox(-4.0F, -10.0F, -4.0F, 8, 8, 8, var1);
         this.body = new ModelPart(this, 16, 16);
         this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, var1 + 0.1F);
         this.rightLeg = new ModelPart(this, 0, 16);
         this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
         this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1 + 0.1F);
         this.leftLeg = new ModelPart(this, 0, 16);
         this.leftLeg.mirror = true;
         this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
         this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1 + 0.1F);
      } else {
         this.head = new ModelPart(this, 0, 0);
         this.head.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, var1);
         this.head.texOffs(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2, 4, 2, var1);
         this.hat = new ModelPart(this, 32, 0);
         this.hat.addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, var1 + 0.5F);
         this.hatRim = new ModelPart(this);
         this.hatRim.texOffs(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16, 16, 1, var1);
         this.hatRim.xRot = -1.5707964F;
         this.hat.addChild(this.hatRim);
         this.body = new ModelPart(this, 16, 20);
         this.body.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, var1);
         this.body.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, var1 + 0.05F);
         this.rightArm = new ModelPart(this, 44, 22);
         this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1);
         this.rightArm.setPos(-5.0F, 2.0F, 0.0F);
         this.leftArm = new ModelPart(this, 44, 22);
         this.leftArm.mirror = true;
         this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1);
         this.leftArm.setPos(5.0F, 2.0F, 0.0F);
         this.rightLeg = new ModelPart(this, 0, 22);
         this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
         this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
         this.leftLeg = new ModelPart(this, 0, 22);
         this.leftLeg.mirror = true;
         this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
         this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      }

   }

   public void setupAnim(Zombie zombie, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim((LivingEntity)zombie, var2, var3, var4, var5, var6, var7);
      float var8 = Mth.sin(this.attackTime * 3.1415927F);
      float var9 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * 3.1415927F);
      this.rightArm.zRot = 0.0F;
      this.leftArm.zRot = 0.0F;
      this.rightArm.yRot = -(0.1F - var8 * 0.6F);
      this.leftArm.yRot = 0.1F - var8 * 0.6F;
      float var10 = -3.1415927F / (zombie.isAggressive()?1.5F:2.25F);
      this.rightArm.xRot = var10;
      this.leftArm.xRot = var10;
      this.rightArm.xRot += var8 * 1.2F - var9 * 0.4F;
      this.leftArm.xRot += var8 * 1.2F - var9 * 0.4F;
      this.rightArm.zRot += Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
      this.leftArm.zRot -= Mth.cos(var4 * 0.09F) * 0.05F + 0.05F;
      this.rightArm.xRot += Mth.sin(var4 * 0.067F) * 0.05F;
      this.leftArm.xRot -= Mth.sin(var4 * 0.067F) * 0.05F;
   }

   public void hatVisible(boolean b) {
      this.head.visible = b;
      this.hat.visible = b;
      this.hatRim.visible = b;
   }
}
