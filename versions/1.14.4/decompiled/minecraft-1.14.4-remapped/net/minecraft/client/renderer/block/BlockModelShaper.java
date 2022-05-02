package net.minecraft.client.renderer.block;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

@ClientJarOnly
public class BlockModelShaper {
   private final Map modelByStateCache = Maps.newIdentityHashMap();
   private final ModelManager modelManager;

   public BlockModelShaper(ModelManager modelManager) {
      this.modelManager = modelManager;
   }

   public TextureAtlasSprite getParticleIcon(BlockState blockState) {
      return this.getBlockModel(blockState).getParticleIcon();
   }

   public BakedModel getBlockModel(BlockState blockState) {
      BakedModel bakedModel = (BakedModel)this.modelByStateCache.get(blockState);
      if(bakedModel == null) {
         bakedModel = this.modelManager.getMissingModel();
      }

      return bakedModel;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void rebuildCache() {
      this.modelByStateCache.clear();

      for(Block var2 : Registry.BLOCK) {
         var2.getStateDefinition().getPossibleStates().forEach((blockState) -> {
            BakedModel var10000 = (BakedModel)this.modelByStateCache.put(blockState, this.modelManager.getModel(stateToModelLocation(blockState)));
         });
      }

   }

   public static ModelResourceLocation stateToModelLocation(BlockState blockState) {
      return stateToModelLocation(Registry.BLOCK.getKey(blockState.getBlock()), blockState);
   }

   public static ModelResourceLocation stateToModelLocation(ResourceLocation resourceLocation, BlockState blockState) {
      return new ModelResourceLocation(resourceLocation, statePropertiesToString(blockState.getValues()));
   }

   public static String statePropertiesToString(Map map) {
      StringBuilder var1 = new StringBuilder();

      for(Entry<Property<?>, Comparable<?>> var3 : map.entrySet()) {
         if(var1.length() != 0) {
            var1.append(',');
         }

         Property<?> var4 = (Property)var3.getKey();
         var1.append(var4.getName());
         var1.append('=');
         var1.append(getValue(var4, (Comparable)var3.getValue()));
      }

      return var1.toString();
   }

   private static String getValue(Property property, Comparable comparable) {
      return property.getName(comparable);
   }
}
