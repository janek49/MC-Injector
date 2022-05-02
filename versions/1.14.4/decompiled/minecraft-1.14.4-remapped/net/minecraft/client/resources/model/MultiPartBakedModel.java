package net.minecraft.client.resources.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

@ClientJarOnly
public class MultiPartBakedModel implements BakedModel {
   private final List selectors;
   protected final boolean hasAmbientOcclusion;
   protected final boolean isGui3d;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemTransforms transforms;
   protected final ItemOverrides overrides;
   private final Map selectorCache = new Object2ObjectOpenCustomHashMap(Util.identityStrategy());

   public MultiPartBakedModel(List selectors) {
      this.selectors = selectors;
      BakedModel var2 = (BakedModel)((Pair)selectors.iterator().next()).getRight();
      this.hasAmbientOcclusion = var2.useAmbientOcclusion();
      this.isGui3d = var2.isGui3d();
      this.particleIcon = var2.getParticleIcon();
      this.transforms = var2.getTransforms();
      this.overrides = var2.getOverrides();
   }

   public List getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
      if(blockState == null) {
         return Collections.emptyList();
      } else {
         BitSet var4 = (BitSet)this.selectorCache.get(blockState);
         if(var4 == null) {
            var4 = new BitSet();

            for(int var5 = 0; var5 < this.selectors.size(); ++var5) {
               Pair<Predicate<BlockState>, BakedModel> var6 = (Pair)this.selectors.get(var5);
               if(((Predicate)var6.getLeft()).test(blockState)) {
                  var4.set(var5);
               }
            }

            this.selectorCache.put(blockState, var4);
         }

         List<BakedQuad> var5 = Lists.newArrayList();
         long var6 = random.nextLong();

         for(int var8 = 0; var8 < var4.length(); ++var8) {
            if(var4.get(var8)) {
               var5.addAll(((BakedModel)((Pair)this.selectors.get(var8)).getRight()).getQuads(blockState, direction, new Random(var6)));
            }
         }

         return var5;
      }
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
      private final List selectors = Lists.newArrayList();

      public void add(Predicate predicate, BakedModel bakedModel) {
         this.selectors.add(Pair.of(predicate, bakedModel));
      }

      public BakedModel build() {
         return new MultiPartBakedModel(this.selectors);
      }
   }
}
