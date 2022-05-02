package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

@ClientJarOnly
public class ZombieRenderer extends AbstractZombieRenderer {
   public ZombieRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new ZombieModel(), new ZombieModel(0.5F, true), new ZombieModel(1.0F, true));
   }
}
