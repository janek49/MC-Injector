package net.minecraft.client.resources.model;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class BuiltInModel implements BakedModel {
   private final ItemTransforms itemTransforms;
   private final ItemOverrides overrides;
   private final TextureAtlasSprite particleTexture;

   public BuiltInModel(ItemTransforms itemTransforms, ItemOverrides overrides, TextureAtlasSprite particleTexture) {
      this.itemTransforms = itemTransforms;
      this.overrides = overrides;
      this.particleTexture = particleTexture;
   }

   public List getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
      return Collections.emptyList();
   }

   public boolean useAmbientOcclusion() {
      return false;
   }

   public boolean isGui3d() {
      return true;
   }

   public boolean isCustomRenderer() {
      return true;
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.particleTexture;
   }

   public ItemTransforms getTransforms() {
      return this.itemTransforms;
   }

   public ItemOverrides getOverrides() {
      return this.overrides;
   }
}
