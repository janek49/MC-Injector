package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

@ClientJarOnly
public abstract class AbstractHorseRenderer extends MobRenderer {
   private final float scale;

   public AbstractHorseRenderer(EntityRenderDispatcher entityRenderDispatcher, HorseModel horseModel, float scale) {
      super(entityRenderDispatcher, horseModel, 0.75F);
      this.scale = scale;
   }

   protected void scale(AbstractHorse abstractHorse, float var2) {
      GlStateManager.scalef(this.scale, this.scale, this.scale);
      super.scale(abstractHorse, var2);
   }
}
