package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

@ClientJarOnly
public class ChestedHorseModel extends HorseModel {
   private final ModelPart boxL = new ModelPart(this, 26, 21);
   private final ModelPart boxR;

   public ChestedHorseModel(float f) {
      super(f);
      this.boxL.addBox(-4.0F, 0.0F, -2.0F, 8, 8, 3);
      this.boxR = new ModelPart(this, 26, 21);
      this.boxR.addBox(-4.0F, 0.0F, -2.0F, 8, 8, 3);
      this.boxL.yRot = -1.5707964F;
      this.boxR.yRot = 1.5707964F;
      this.boxL.setPos(6.0F, -8.0F, 0.0F);
      this.boxR.setPos(-6.0F, -8.0F, 0.0F);
      this.body.addChild(this.boxL);
      this.body.addChild(this.boxR);
   }

   protected void addEarModels(ModelPart modelPart) {
      ModelPart modelPart = new ModelPart(this, 0, 12);
      modelPart.addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1);
      modelPart.setPos(1.25F, -10.0F, 4.0F);
      ModelPart var3 = new ModelPart(this, 0, 12);
      var3.addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1);
      var3.setPos(-1.25F, -10.0F, 4.0F);
      modelPart.xRot = 0.2617994F;
      modelPart.zRot = 0.2617994F;
      var3.xRot = 0.2617994F;
      var3.zRot = -0.2617994F;
      modelPart.addChild(modelPart);
      modelPart.addChild(var3);
   }

   public void render(AbstractChestedHorse abstractChestedHorse, float var2, float var3, float var4, float var5, float var6, float var7) {
      if(abstractChestedHorse.hasChest()) {
         this.boxL.visible = true;
         this.boxR.visible = true;
      } else {
         this.boxL.visible = false;
         this.boxR.visible = false;
      }

      super.render((AbstractHorse)abstractChestedHorse, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((AbstractChestedHorse)var1, var2, var3, var4, var5, var6, var7);
   }
}
