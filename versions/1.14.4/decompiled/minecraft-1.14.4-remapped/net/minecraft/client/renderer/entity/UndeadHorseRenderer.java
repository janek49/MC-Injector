package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;

@ClientJarOnly
public class UndeadHorseRenderer extends AbstractHorseRenderer {
   private static final Map MAP = Maps.newHashMap(ImmutableMap.of(ZombieHorse.class, new ResourceLocation("textures/entity/horse/horse_zombie.png"), SkeletonHorse.class, new ResourceLocation("textures/entity/horse/horse_skeleton.png")));

   public UndeadHorseRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new HorseModel(0.0F), 1.0F);
   }

   protected ResourceLocation getTextureLocation(AbstractHorse abstractHorse) {
      return (ResourceLocation)MAP.get(abstractHorse.getClass());
   }
}
