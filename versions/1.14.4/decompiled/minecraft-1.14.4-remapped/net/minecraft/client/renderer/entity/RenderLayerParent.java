package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public interface RenderLayerParent {
   EntityModel getModel();

   void bindTexture(ResourceLocation var1);

   void setLightColor(Entity var1);
}
