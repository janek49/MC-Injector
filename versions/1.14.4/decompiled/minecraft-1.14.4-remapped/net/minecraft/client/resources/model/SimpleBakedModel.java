package net.minecraft.client.resources.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BreakingQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class SimpleBakedModel implements BakedModel {
   protected final List unculledFaces;
   protected final Map culledFaces;
   protected final boolean hasAmbientOcclusion;
   protected final boolean isGui3d;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemTransforms transforms;
   protected final ItemOverrides overrides;

   public SimpleBakedModel(List unculledFaces, Map culledFaces, boolean hasAmbientOcclusion, boolean isGui3d, TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides) {
      this.unculledFaces = unculledFaces;
      this.culledFaces = culledFaces;
      this.hasAmbientOcclusion = hasAmbientOcclusion;
      this.isGui3d = isGui3d;
      this.particleIcon = particleIcon;
      this.transforms = transforms;
      this.overrides = overrides;
   }

   public List getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
      return direction == null?this.unculledFaces:(List)this.culledFaces.get(direction);
   }

   public boolean useAmbientOcclusion() {
      return this.hasAmbientOcclusion;
   }

   public boolean isGui3d() {
      return this.isGui3d;
   }

   public boolean isCustomRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.particleIcon;
   }

   public ItemTransforms getTransforms() {
      return this.transforms;
   }

   public ItemOverrides getOverrides() {
      return this.overrides;
   }

   @ClientJarOnly
   public static class Builder {
      private final List unculledFaces;
      private final Map culledFaces;
      private final ItemOverrides overrides;
      private final boolean hasAmbientOcclusion;
      private TextureAtlasSprite particleIcon;
      private final boolean isGui3d;
      private final ItemTransforms transforms;

      public Builder(BlockModel blockModel, ItemOverrides itemOverrides) {
         this(blockModel.hasAmbientOcclusion(), blockModel.isGui3d(), blockModel.getTransforms(), itemOverrides);
      }

      public Builder(BlockState blockState, BakedModel bakedModel, TextureAtlasSprite textureAtlasSprite, Random random, long var5) {
         this(bakedModel.useAmbientOcclusion(), bakedModel.isGui3d(), bakedModel.getTransforms(), bakedModel.getOverrides());
         this.particleIcon = bakedModel.getParticleIcon();

         for(Direction var10 : Direction.values()) {
            random.setSeed(var5);

            for(BakedQuad var12 : bakedModel.getQuads(blockState, var10, random)) {
               this.addCulledFace(var10, new BreakingQuad(var12, textureAtlasSprite));
            }
         }

         random.setSeed(var5);

         for(BakedQuad var8 : bakedModel.getQuads(blockState, (Direction)null, random)) {
            this.addUnculledFace(new BreakingQuad(var8, textureAtlasSprite));
         }

      }

      private Builder(boolean hasAmbientOcclusion, boolean isGui3d, ItemTransforms transforms, ItemOverrides overrides) {
         this.unculledFaces = Lists.newArrayList();
         this.culledFaces = Maps.newEnumMap(Direction.class);

         for(Direction var8 : Direction.values()) {
            this.culledFaces.put(var8, Lists.newArrayList());
         }

         this.overrides = overrides;
         this.hasAmbientOcclusion = hasAmbientOcclusion;
         this.isGui3d = isGui3d;
         this.transforms = transforms;
      }

      public SimpleBakedModel.Builder addCulledFace(Direction direction, BakedQuad bakedQuad) {
         ((List)this.culledFaces.get(direction)).add(bakedQuad);
         return this;
      }

      public SimpleBakedModel.Builder addUnculledFace(BakedQuad bakedQuad) {
         this.unculledFaces.add(bakedQuad);
         return this;
      }

      public SimpleBakedModel.Builder particle(TextureAtlasSprite particleIcon) {
         this.particleIcon = particleIcon;
         return this;
      }

      public BakedModel build() {
         if(this.particleIcon == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.isGui3d, this.particleIcon, this.transforms, this.overrides);
         }
      }
   }
}
