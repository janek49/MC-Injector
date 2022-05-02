package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.AbstractArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;

@ClientJarOnly
public class HumanoidArmorLayer extends AbstractArmorLayer {
   public HumanoidArmorLayer(RenderLayerParent renderLayerParent, HumanoidModel var2, HumanoidModel var3) {
      super(renderLayerParent, var2, var3);
   }

   protected void setPartVisibility(HumanoidModel humanoidModel, EquipmentSlot equipmentSlot) {
      this.hideAllArmor(humanoidModel);
      switch(equipmentSlot) {
      case HEAD:
         humanoidModel.head.visible = true;
         humanoidModel.hat.visible = true;
         break;
      case CHEST:
         humanoidModel.body.visible = true;
         humanoidModel.rightArm.visible = true;
         humanoidModel.leftArm.visible = true;
         break;
      case LEGS:
         humanoidModel.body.visible = true;
         humanoidModel.rightLeg.visible = true;
         humanoidModel.leftLeg.visible = true;
         break;
      case FEET:
         humanoidModel.rightLeg.visible = true;
         humanoidModel.leftLeg.visible = true;
      }

   }

   protected void hideAllArmor(HumanoidModel humanoidModel) {
      humanoidModel.setAllVisible(false);
   }
}
