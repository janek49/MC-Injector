package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

@ClientJarOnly
public class SheepFurLayer extends RenderLayer {
   private static final ResourceLocation SHEEP_FUR_LOCATION = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   private final SheepFurModel model = new SheepFurModel();

   public SheepFurLayer(RenderLayerParent renderLayerParent) {
      super(renderLayerParent);
   }

   public void render(Sheep sheep, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if(!sheep.isSheared() && !sheep.isInvisible()) {
         this.bindTexture(SHEEP_FUR_LOCATION);
         if(sheep.hasCustomName() && "jeb_".equals(sheep.getName().getContents())) {
            int var9 = 25;
            int var10 = sheep.tickCount / 25 + sheep.getId();
            int var11 = DyeColor.values().length;
            int var12 = var10 % var11;
            int var13 = (var10 + 1) % var11;
            float var14 = ((float)(sheep.tickCount % 25) + var4) / 25.0F;
            float[] vars15 = Sheep.getColorArray(DyeColor.byId(var12));
            float[] vars16 = Sheep.getColorArray(DyeColor.byId(var13));
            GlStateManager.color3f(vars15[0] * (1.0F - var14) + vars16[0] * var14, vars15[1] * (1.0F - var14) + vars16[1] * var14, vars15[2] * (1.0F - var14) + vars16[2] * var14);
         } else {
            float[] vars9 = Sheep.getColorArray(sheep.getColor());
            GlStateManager.color3f(vars9[0], vars9[1], vars9[2]);
         }

         ((SheepModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.prepareMobModel(sheep, var2, var3, var4);
         this.model.render(sheep, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
