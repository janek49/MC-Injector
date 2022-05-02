package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public class HorseArmorLayer extends RenderLayer {
   private final HorseModel model = new HorseModel(0.1F);

   public HorseArmorLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Horse horse, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var9 = horse.getArmor();
      if(var9.getItem() instanceof HorseArmorItem) {
         HorseArmorItem var10 = (HorseArmorItem)var9.getItem();
         ((HorseModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.prepareMobModel((AbstractHorse)horse, var2, var3, var4);
         this.bindTexture(var10.getTexture());
         if(var10 instanceof DyeableHorseArmorItem) {
            int var11 = ((DyeableHorseArmorItem)var10).getColor(var9);
            float var12 = (float)(var11 >> 16 & 255) / 255.0F;
            float var13 = (float)(var11 >> 8 & 255) / 255.0F;
            float var14 = (float)(var11 & 255) / 255.0F;
            GlStateManager.color4f(var12, var13, var14, 1.0F);
            this.model.render((AbstractHorse)horse, var2, var3, var5, var6, var7, var8);
            return;
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.model.render((AbstractHorse)horse, var2, var3, var5, var6, var7, var8);
      }

   }

   public boolean colorsOnDamage() {
      return false;
   }
}
