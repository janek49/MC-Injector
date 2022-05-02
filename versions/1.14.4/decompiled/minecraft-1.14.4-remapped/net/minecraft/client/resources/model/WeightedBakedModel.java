package net.minecraft.client.resources.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class WeightedBakedModel implements BakedModel {
   private final int totalWeight;
   private final List list;
   private final BakedModel wrapped;

   public WeightedBakedModel(List list) {
      this.list = list;
      this.totalWeight = WeighedRandom.getTotalWeight(list);
      this.wrapped = ((WeightedBakedModel.WeightedModel)list.get(0)).model;
   }

   public List getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
      return ((WeightedBakedModel.WeightedModel)WeighedRandom.getWeightedItem(this.list, Math.abs((int)random.nextLong()) % this.totalWeight)).model.getQuads(blockState, direction, random);
   }

   public boolean useAmbientOcclusion() {
      return this.wrapped.useAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.wrapped.isGui3d();
   }

   public boolean isCustomRenderer() {
      return this.wrapped.isCustomRenderer();
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.wrapped.getParticleIcon();
   }

   public ItemTransforms getTransforms() {
      return this.wrapped.getTransforms();
   }

   public ItemOverrides getOverrides() {
      return this.wrapped.getOverrides();
   }

   @ClientJarOnly
   public static class Builder {
      private final List list = Lists.newArrayList();

      public WeightedBakedModel.Builder add(@Nullable BakedModel bakedModel, int var2) {
         if(bakedModel != null) {
            this.list.add(new WeightedBakedModel.WeightedModel(bakedModel, var2));
         }

         return this;
      }

      @Nullable
      public BakedModel build() {
         return (BakedModel)(this.list.isEmpty()?null:(this.list.size() == 1?((WeightedBakedModel.WeightedModel)this.list.get(0)).model:new WeightedBakedModel(this.list)));
      }
   }

   @ClientJarOnly
   static class WeightedModel extends WeighedRandom.WeighedRandomItem {
      protected final BakedModel model;

      public WeightedModel(BakedModel model, int var2) {
         super(var2);
         this.model = model;
      }
   }
}
