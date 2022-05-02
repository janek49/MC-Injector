package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cod;

@ClientJarOnly
public class CodRenderer extends MobRenderer {
   private static final ResourceLocation COD_LOCATION = new ResourceLocation("textures/entity/fish/cod.png");

   public CodRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new CodModel(), 0.3F);
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Cod cod) {
      return COD_LOCATION;
   }

   protected void setupRotations(Cod cod, float var2, float var3, float var4) {
      super.setupRotations(cod, var2, var3, var4);
      float var5 = 4.3F * Mth.sin(0.6F * var2);
      GlStateManager.rotatef(var5, 0.0F, 1.0F, 0.0F);
      if(!cod.isInWater()) {
         GlStateManager.translatef(0.1F, 0.1F, -0.1F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}
