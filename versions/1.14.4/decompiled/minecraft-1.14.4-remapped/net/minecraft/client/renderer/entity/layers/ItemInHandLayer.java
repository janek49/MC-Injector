package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ArmedModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public class ItemInHandLayer extends RenderLayer {
   public ItemInHandLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(LivingEntity livingEntity, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      boolean var9 = livingEntity.getMainArm() == HumanoidArm.RIGHT;
      ItemStack var10 = var9?livingEntity.getOffhandItem():livingEntity.getMainHandItem();
      ItemStack var11 = var9?livingEntity.getMainHandItem():livingEntity.getOffhandItem();
      if(!var10.isEmpty() || !var11.isEmpty()) {
         GlStateManager.pushMatrix();
         if(this.getParentModel().young) {
            float var12 = 0.5F;
            GlStateManager.translatef(0.0F, 0.75F, 0.0F);
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         }

         this.renderArmWithItem(livingEntity, var11, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT);
         this.renderArmWithItem(livingEntity, var10, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT);
         GlStateManager.popMatrix();
      }
   }

   private void renderArmWithItem(LivingEntity livingEntity, ItemStack itemStack, ItemTransforms.TransformType itemTransforms$TransformType, HumanoidArm humanoidArm) {
      if(!itemStack.isEmpty()) {
         GlStateManager.pushMatrix();
         this.translateToHand(humanoidArm);
         if(livingEntity.isVisuallySneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         boolean var5 = humanoidArm == HumanoidArm.LEFT;
         GlStateManager.translatef((float)(var5?-1:1) / 16.0F, 0.125F, -0.625F);
         Minecraft.getInstance().getItemInHandRenderer().renderItem(livingEntity, itemStack, itemTransforms$TransformType, var5);
         GlStateManager.popMatrix();
      }
   }

   protected void translateToHand(HumanoidArm humanoidArm) {
      ((ArmedModel)this.getParentModel()).translateToHand(0.0625F, humanoidArm);
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
