package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;

@ClientJarOnly
public final class HorseRenderer extends AbstractHorseRenderer {
   private static final Map LAYERED_LOCATION_CACHE = Maps.newHashMap();

   public HorseRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new HorseModel(0.0F), 1.1F);
      this.addLayer(new HorseArmorLayer(this));
   }

   protected ResourceLocation getTextureLocation(Horse horse) {
      String var2 = horse.getLayeredTextureHashName();
      ResourceLocation var3 = (ResourceLocation)LAYERED_LOCATION_CACHE.get(var2);
      if(var3 == null) {
         var3 = new ResourceLocation(var2);
         Minecraft.getInstance().getTextureManager().register((ResourceLocation)var3, (TextureObject)(new LayeredTexture(horse.getLayeredTextureLayers())));
         LAYERED_LOCATION_CACHE.put(var2, var3);
      }

      return var3;
   }
}
