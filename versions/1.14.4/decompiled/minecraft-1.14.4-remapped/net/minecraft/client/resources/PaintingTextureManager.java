package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Iterables;
import java.util.Collections;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.Motive;

@ClientJarOnly
public class PaintingTextureManager extends TextureAtlasHolder {
   private static final ResourceLocation BACK_SPRITE_LOCATION = new ResourceLocation("back");

   public PaintingTextureManager(TextureManager textureManager) {
      super(textureManager, TextureAtlas.LOCATION_PAINTINGS, "textures/painting");
   }

   protected Iterable getResourcesToLoad() {
      return Iterables.concat(Registry.MOTIVE.keySet(), Collections.singleton(BACK_SPRITE_LOCATION));
   }

   public TextureAtlasSprite get(Motive motive) {
      return this.getSprite(Registry.MOTIVE.getKey(motive));
   }

   public TextureAtlasSprite getBackSprite() {
      return this.getSprite(BACK_SPRITE_LOCATION);
   }
}
