package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Fox;

@ClientJarOnly
public class FoxRenderer extends MobRenderer {
   private static final ResourceLocation RED_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/fox.png");
   private static final ResourceLocation RED_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/fox_sleep.png");
   private static final ResourceLocation SNOW_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/snow_fox.png");
   private static final ResourceLocation SNOW_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/snow_fox_sleep.png");

   public FoxRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new FoxModel(), 0.4F);
      this.addLayer(new FoxHeldItemLayer(this));
   }

   protected void setupRotations(Fox fox, float var2, float var3, float var4) {
      super.setupRotations(fox, var2, var3, var4);
      if(fox.isPouncing() || fox.isFaceplanted()) {
         GlStateManager.rotatef(-Mth.lerp(var4, fox.xRotO, fox.xRot), 1.0F, 0.0F, 0.0F);
      }

   }

   @Nullable
   protected ResourceLocation getTextureLocation(Fox fox) {
      return fox.getFoxType() == Fox.Type.RED?(fox.isSleeping()?RED_FOX_SLEEP_TEXTURE:RED_FOX_TEXTURE):(fox.isSleeping()?SNOW_FOX_SLEEP_TEXTURE:SNOW_FOX_TEXTURE);
   }
}
