package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;

@ClientJarOnly
public class MobEffectTextureManager extends TextureAtlasHolder {
   public MobEffectTextureManager(TextureManager textureManager) {
      super(textureManager, TextureAtlas.LOCATION_MOB_EFFECTS, "textures/mob_effect");
   }

   protected Iterable getResourcesToLoad() {
      return Registry.MOB_EFFECT.keySet();
   }

   public TextureAtlasSprite get(MobEffect mobEffect) {
      return this.getSprite(Registry.MOB_EFFECT.getKey(mobEffect));
   }
}
