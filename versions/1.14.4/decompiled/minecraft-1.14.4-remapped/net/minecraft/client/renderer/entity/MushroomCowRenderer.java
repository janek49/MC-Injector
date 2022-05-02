package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.MushroomCowMushroomLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.MushroomCow;

@ClientJarOnly
public class MushroomCowRenderer extends MobRenderer {
   private static final Map TEXTURES = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put(MushroomCow.MushroomType.BROWN, new ResourceLocation("textures/entity/cow/brown_mooshroom.png"));
      hashMap.put(MushroomCow.MushroomType.RED, new ResourceLocation("textures/entity/cow/red_mooshroom.png"));
   });

   public MushroomCowRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new CowModel(), 0.7F);
      this.addLayer(new MushroomCowMushroomLayer(this));
   }

   protected ResourceLocation getTextureLocation(MushroomCow mushroomCow) {
      return (ResourceLocation)TEXTURES.get(mushroomCow.getMushroomType());
   }
}
