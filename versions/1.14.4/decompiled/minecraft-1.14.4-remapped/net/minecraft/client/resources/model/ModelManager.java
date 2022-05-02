package net.minecraft.client.resources.model;

import com.fox2code.repacker.ClientJarOnly;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

@ClientJarOnly
public class ModelManager extends SimplePreparableReloadListener {
   private Map bakedRegistry;
   private final TextureAtlas terrainAtlas;
   private final BlockModelShaper blockModelShaper;
   private final BlockColors blockColors;
   private BakedModel missingModel;
   private Object2IntMap modelGroups;

   public ModelManager(TextureAtlas terrainAtlas, BlockColors blockColors) {
      this.terrainAtlas = terrainAtlas;
      this.blockColors = blockColors;
      this.blockModelShaper = new BlockModelShaper(this);
   }

   public BakedModel getModel(ModelResourceLocation modelResourceLocation) {
      return (BakedModel)this.bakedRegistry.getOrDefault(modelResourceLocation, this.missingModel);
   }

   public BakedModel getMissingModel() {
      return this.missingModel;
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   protected ModelBakery prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      profilerFiller.startTick();
      ModelBakery modelBakery = new ModelBakery(resourceManager, this.terrainAtlas, this.blockColors, profilerFiller);
      profilerFiller.endTick();
      return modelBakery;
   }

   protected void apply(ModelBakery modelBakery, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      profilerFiller.startTick();
      profilerFiller.push("upload");
      modelBakery.uploadTextures(profilerFiller);
      this.bakedRegistry = modelBakery.getBakedTopLevelModels();
      this.modelGroups = modelBakery.getModelGroups();
      this.missingModel = (BakedModel)this.bakedRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
      profilerFiller.popPush("cache");
      this.blockModelShaper.rebuildCache();
      profilerFiller.pop();
      profilerFiller.endTick();
   }

   public boolean requiresRender(BlockState var1, BlockState var2) {
      if(var1 == var2) {
         return false;
      } else {
         int var3 = this.modelGroups.getInt(var1);
         if(var3 != -1) {
            int var4 = this.modelGroups.getInt(var2);
            if(var3 == var4) {
               FluidState var5 = var1.getFluidState();
               FluidState var6 = var2.getFluidState();
               return var5 != var6;
            }
         }

         return true;
      }
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
