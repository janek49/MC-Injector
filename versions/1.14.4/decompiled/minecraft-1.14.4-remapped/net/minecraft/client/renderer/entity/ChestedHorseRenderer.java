package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Mule;

@ClientJarOnly
public class ChestedHorseRenderer extends AbstractHorseRenderer {
   private static final Map MAP = Maps.newHashMap(ImmutableMap.of(Donkey.class, new ResourceLocation("textures/entity/horse/donkey.png"), Mule.class, new ResourceLocation("textures/entity/horse/mule.png")));

   public ChestedHorseRenderer(EntityRenderDispatcher entityRenderDispatcher, float var2) {
      super(entityRenderDispatcher, new ChestedHorseModel(0.0F), var2);
   }

   protected ResourceLocation getTextureLocation(AbstractChestedHorse abstractChestedHorse) {
      return (ResourceLocation)MAP.get(abstractChestedHorse.getClass());
   }
}
