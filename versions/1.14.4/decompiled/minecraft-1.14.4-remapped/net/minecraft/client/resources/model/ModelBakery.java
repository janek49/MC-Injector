package net.minecraft.client.resources.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class ModelBakery {
   public static final ResourceLocation FIRE_0 = new ResourceLocation("block/fire_0");
   public static final ResourceLocation FIRE_1 = new ResourceLocation("block/fire_1");
   public static final ResourceLocation LAVA_FLOW = new ResourceLocation("block/lava_flow");
   public static final ResourceLocation WATER_FLOW = new ResourceLocation("block/water_flow");
   public static final ResourceLocation WATER_OVERLAY = new ResourceLocation("block/water_overlay");
   public static final ResourceLocation DESTROY_STAGE_0 = new ResourceLocation("block/destroy_stage_0");
   public static final ResourceLocation DESTROY_STAGE_1 = new ResourceLocation("block/destroy_stage_1");
   public static final ResourceLocation DESTROY_STAGE_2 = new ResourceLocation("block/destroy_stage_2");
   public static final ResourceLocation DESTROY_STAGE_3 = new ResourceLocation("block/destroy_stage_3");
   public static final ResourceLocation DESTROY_STAGE_4 = new ResourceLocation("block/destroy_stage_4");
   public static final ResourceLocation DESTROY_STAGE_5 = new ResourceLocation("block/destroy_stage_5");
   public static final ResourceLocation DESTROY_STAGE_6 = new ResourceLocation("block/destroy_stage_6");
   public static final ResourceLocation DESTROY_STAGE_7 = new ResourceLocation("block/destroy_stage_7");
   public static final ResourceLocation DESTROY_STAGE_8 = new ResourceLocation("block/destroy_stage_8");
   public static final ResourceLocation DESTROY_STAGE_9 = new ResourceLocation("block/destroy_stage_9");
   private static final Set UNREFERENCED_TEXTURES = Sets.newHashSet(new ResourceLocation[]{WATER_FLOW, LAVA_FLOW, WATER_OVERLAY, FIRE_0, FIRE_1, DESTROY_STAGE_0, DESTROY_STAGE_1, DESTROY_STAGE_2, DESTROY_STAGE_3, DESTROY_STAGE_4, DESTROY_STAGE_5, DESTROY_STAGE_6, DESTROY_STAGE_7, DESTROY_STAGE_8, DESTROY_STAGE_9, new ResourceLocation("item/empty_armor_slot_helmet"), new ResourceLocation("item/empty_armor_slot_chestplate"), new ResourceLocation("item/empty_armor_slot_leggings"), new ResourceLocation("item/empty_armor_slot_boots"), new ResourceLocation("item/empty_armor_slot_shield")});
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ModelResourceLocation MISSING_MODEL_LOCATION = new ModelResourceLocation("builtin/missing", "missing");
   @VisibleForTesting
   public static final String MISSING_MODEL_MESH = ("{    \'textures\': {       \'particle\': \'" + MissingTextureAtlasSprite.getLocation().getPath() + "\',       \'missingno\': \'" + MissingTextureAtlasSprite.getLocation().getPath() + "\'    },    \'elements\': [         {  \'from\': [ 0, 0, 0 ],            \'to\': [ 16, 16, 16 ],            \'faces\': {                \'down\':  { \'uv\': [ 0, 0, 16, 16 ], \'cullface\': \'down\',  \'texture\': \'#missingno\' },                \'up\':    { \'uv\': [ 0, 0, 16, 16 ], \'cullface\': \'up\',    \'texture\': \'#missingno\' },                \'north\': { \'uv\': [ 0, 0, 16, 16 ], \'cullface\': \'north\', \'texture\': \'#missingno\' },                \'south\': { \'uv\': [ 0, 0, 16, 16 ], \'cullface\': \'south\', \'texture\': \'#missingno\' },                \'west\':  { \'uv\': [ 0, 0, 16, 16 ], \'cullface\': \'west\',  \'texture\': \'#missingno\' },                \'east\':  { \'uv\': [ 0, 0, 16, 16 ], \'cullface\': \'east\',  \'texture\': \'#missingno\' }            }        }    ]}").replace('\'', '\"');
   private static final Map BUILTIN_MODELS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_MODEL_MESH));
   private static final Splitter COMMA_SPLITTER = Splitter.on(',');
   private static final Splitter EQUAL_SPLITTER = Splitter.on('=').limit(2);
   public static final BlockModel GENERATION_MARKER = (BlockModel)Util.make(BlockModel.fromString("{}"), (blockModel) -> {
      blockModel.name = "generation marker";
   });
   public static final BlockModel BLOCK_ENTITY_MARKER = (BlockModel)Util.make(BlockModel.fromString("{}"), (blockModel) -> {
      blockModel.name = "block entity marker";
   });
   private static final StateDefinition ITEM_FRAME_FAKE_DEFINITION = (new StateDefinition.Builder(Blocks.AIR)).add(new Property[]{BooleanProperty.create("map")}).create(BlockState::<init>);
   private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
   private static final Map STATIC_DEFINITIONS = ImmutableMap.of(new ResourceLocation("item_frame"), ITEM_FRAME_FAKE_DEFINITION);
   private final ResourceManager resourceManager;
   private final TextureAtlas blockAtlas;
   private final BlockColors blockColors;
   private final Set loadingStack = Sets.newHashSet();
   private final BlockModelDefinition.Context context = new BlockModelDefinition.Context();
   private final Map unbakedCache = Maps.newHashMap();
   private final Map bakedCache = Maps.newHashMap();
   private final Map topLevelModels = Maps.newHashMap();
   private final Map bakedTopLevelModels = Maps.newHashMap();
   private final TextureAtlas.Preparations atlasPreparations;
   private int nextModelGroup = 1;
   private final Object2IntMap modelGroups = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (object2IntOpenHashMap) -> {
      object2IntOpenHashMap.defaultReturnValue(-1);
   });

   public ModelBakery(ResourceManager resourceManager, TextureAtlas blockAtlas, BlockColors blockColors, ProfilerFiller profilerFiller) {
      this.resourceManager = resourceManager;
      this.blockAtlas = blockAtlas;
      this.blockColors = blockColors;
      profilerFiller.push("missing_model");

      try {
         this.unbakedCache.put(MISSING_MODEL_LOCATION, this.loadBlockModel(MISSING_MODEL_LOCATION));
         this.loadTopLevel(MISSING_MODEL_LOCATION);
      } catch (IOException var7) {
         LOGGER.error("Error loading missing model, should never happen :(", var7);
         throw new RuntimeException(var7);
      }

      profilerFiller.popPush("static_definitions");
      STATIC_DEFINITIONS.forEach((resourceLocation, stateDefinition) -> {
         stateDefinition.getPossibleStates().forEach((blockState) -> {
            this.loadTopLevel(BlockModelShaper.stateToModelLocation(resourceLocation, blockState));
         });
      });
      profilerFiller.popPush("blocks");

      for(Block var6 : Registry.BLOCK) {
         var6.getStateDefinition().getPossibleStates().forEach((blockState) -> {
            this.loadTopLevel(BlockModelShaper.stateToModelLocation(blockState));
         });
      }

      profilerFiller.popPush("items");

      for(ResourceLocation var6 : Registry.ITEM.keySet()) {
         this.loadTopLevel(new ModelResourceLocation(var6, "inventory"));
      }

      profilerFiller.popPush("special");
      this.loadTopLevel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      profilerFiller.popPush("textures");
      Set<String> var5 = Sets.newLinkedHashSet();
      Set<ResourceLocation> var6 = (Set)this.topLevelModels.values().stream().flatMap((unbakedModel) -> {
         return unbakedModel.getTextures(this::getModel, var9).stream();
      }).collect(Collectors.toSet());
      var6.addAll(UNREFERENCED_TEXTURES);
      var5.forEach((string) -> {
         LOGGER.warn("Unable to resolve texture reference: {}", string);
      });
      profilerFiller.popPush("stitching");
      this.atlasPreparations = this.blockAtlas.prepareToStitch(this.resourceManager, var6, profilerFiller);
      profilerFiller.pop();
   }

   public void uploadTextures(ProfilerFiller profilerFiller) {
      profilerFiller.push("atlas");
      this.blockAtlas.reload(this.atlasPreparations);
      profilerFiller.popPush("baking");
      this.topLevelModels.keySet().forEach((resourceLocation) -> {
         BakedModel var2 = null;

         try {
            var2 = this.bake(resourceLocation, BlockModelRotation.X0_Y0);
         } catch (Exception var4) {
            LOGGER.warn("Unable to bake model: \'{}\': {}", resourceLocation, var4);
         }

         if(var2 != null) {
            this.bakedTopLevelModels.put(resourceLocation, var2);
         }

      });
      profilerFiller.pop();
   }

   private static Predicate predicate(StateDefinition stateDefinition, String string) {
      Map<Property<?>, Comparable<?>> var2 = Maps.newHashMap();

      for(String var4 : COMMA_SPLITTER.split(string)) {
         Iterator<String> var5 = EQUAL_SPLITTER.split(var4).iterator();
         if(var5.hasNext()) {
            String var6 = (String)var5.next();
            Property<?> var7 = stateDefinition.getProperty(var6);
            if(var7 != null && var5.hasNext()) {
               String var8 = (String)var5.next();
               Comparable<?> var9 = getValueHelper(var7, var8);
               if(var9 == null) {
                  throw new RuntimeException("Unknown value: \'" + var8 + "\' for blockstate property: \'" + var6 + "\' " + var7.getPossibleValues());
               }

               var2.put(var7, var9);
            } else if(!var6.isEmpty()) {
               throw new RuntimeException("Unknown blockstate property: \'" + var6 + "\'");
            }
         }
      }

      Block var3 = (Block)stateDefinition.getOwner();
      return (blockState) -> {
         if(blockState != null && var10 == blockState.getBlock()) {
            for(Entry<Property<?>, Comparable<?>> var4 : var2.entrySet()) {
               if(!Objects.equals(blockState.getValue((Property)var4.getKey()), var4.getValue())) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      };
   }

   @Nullable
   static Comparable getValueHelper(Property property, String string) {
      return (Comparable)property.getValue(string).orElse((Object)null);
   }

   public UnbakedModel getModel(ResourceLocation resourceLocation) {
      if(this.unbakedCache.containsKey(resourceLocation)) {
         return (UnbakedModel)this.unbakedCache.get(resourceLocation);
      } else if(this.loadingStack.contains(resourceLocation)) {
         throw new IllegalStateException("Circular reference while loading " + resourceLocation);
      } else {
         this.loadingStack.add(resourceLocation);
         UnbakedModel unbakedModel = (UnbakedModel)this.unbakedCache.get(MISSING_MODEL_LOCATION);

         while(!this.loadingStack.isEmpty()) {
            ResourceLocation var3 = (ResourceLocation)this.loadingStack.iterator().next();

            try {
               if(!this.unbakedCache.containsKey(var3)) {
                  this.loadModel(var3);
               }
            } catch (ModelBakery.BlockStateDefinitionException var9) {
               LOGGER.warn(var9.getMessage());
               this.unbakedCache.put(var3, unbakedModel);
            } catch (Exception var10) {
               LOGGER.warn("Unable to load model: \'{}\' referenced from: {}: {}", var3, resourceLocation, var10);
               this.unbakedCache.put(var3, unbakedModel);
            } finally {
               this.loadingStack.remove(var3);
            }
         }

         return (UnbakedModel)this.unbakedCache.getOrDefault(resourceLocation, unbakedModel);
      }
   }

   private void loadModel(ResourceLocation resourceLocation) throws Exception {
      // $FF: Couldn't be decompiled
   }

   private void cacheAndQueueDependencies(ResourceLocation resourceLocation, UnbakedModel unbakedModel) {
      this.unbakedCache.put(resourceLocation, unbakedModel);
      this.loadingStack.addAll(unbakedModel.getDependencies());
   }

   private void loadTopLevel(ModelResourceLocation modelResourceLocation) {
      UnbakedModel var2 = this.getModel(modelResourceLocation);
      this.unbakedCache.put(modelResourceLocation, var2);
      this.topLevelModels.put(modelResourceLocation, var2);
   }

   private void registerModelGroup(Iterable iterable) {
      int var2 = this.nextModelGroup++;
      iterable.forEach((blockState) -> {
         this.modelGroups.put(blockState, var2);
      });
   }

   @Nullable
   public BakedModel bake(ResourceLocation resourceLocation, ModelState modelState) {
      Triple<ResourceLocation, BlockModelRotation, Boolean> var3 = Triple.of(resourceLocation, modelState.getRotation(), Boolean.valueOf(modelState.isUvLocked()));
      if(this.bakedCache.containsKey(var3)) {
         return (BakedModel)this.bakedCache.get(var3);
      } else {
         UnbakedModel var4 = this.getModel(resourceLocation);
         if(var4 instanceof BlockModel) {
            BlockModel var5 = (BlockModel)var4;
            if(var5.getRootModel() == GENERATION_MARKER) {
               ItemModelGenerator var10000 = ITEM_MODEL_GENERATOR;
               TextureAtlas var10001 = this.blockAtlas;
               this.blockAtlas.getClass();
               BlockModel var7 = var10000.generateBlockModel(var10001::getSprite, var5);
               TextureAtlas var10003 = this.blockAtlas;
               this.blockAtlas.getClass();
               return var7.bake(this, var5, var10003::getSprite, modelState);
            }
         }

         TextureAtlas var10002 = this.blockAtlas;
         this.blockAtlas.getClass();
         BakedModel var5 = var4.bake(this, var10002::getSprite, modelState);
         this.bakedCache.put(var3, var5);
         return var5;
      }
   }

   private BlockModel loadBlockModel(ResourceLocation resourceLocation) throws IOException {
      Reader var2 = null;
      Resource var3 = null;

      BlockModel var5;
      try {
         String var4 = resourceLocation.getPath();
         if(!"builtin/generated".equals(var4)) {
            if("builtin/entity".equals(var4)) {
               var5 = BLOCK_ENTITY_MARKER;
               return var5;
            }

            if(var4.startsWith("builtin/")) {
               String var5 = var4.substring("builtin/".length());
               String var6 = (String)BUILTIN_MODELS.get(var5);
               if(var6 == null) {
                  throw new FileNotFoundException(resourceLocation.toString());
               }

               var2 = new StringReader(var6);
            } else {
               var3 = this.resourceManager.getResource(new ResourceLocation(resourceLocation.getNamespace(), "models/" + resourceLocation.getPath() + ".json"));
               var2 = new InputStreamReader(var3.getInputStream(), StandardCharsets.UTF_8);
            }

            var5 = BlockModel.fromStream(var2);
            var5.name = resourceLocation.toString();
            BlockModel var13 = var5;
            return var13;
         }

         var5 = GENERATION_MARKER;
      } finally {
         IOUtils.closeQuietly(var2);
         IOUtils.closeQuietly(var3);
      }

      return var5;
   }

   public Map getBakedTopLevelModels() {
      return this.bakedTopLevelModels;
   }

   public Object2IntMap getModelGroups() {
      return this.modelGroups;
   }

   @ClientJarOnly
   static class BlockStateDefinitionException extends RuntimeException {
      public BlockStateDefinitionException(String string) {
         super(string);
      }
   }

   @ClientJarOnly
   static class ModelGroupKey {
      private final List models;
      private final List coloringValues;

      public ModelGroupKey(List models, List coloringValues) {
         this.models = models;
         this.coloringValues = coloringValues;
      }

      public boolean equals(Object object) {
         if(this == object) {
            return true;
         } else if(!(object instanceof ModelBakery.ModelGroupKey)) {
            return false;
         } else {
            ModelBakery.ModelGroupKey var2 = (ModelBakery.ModelGroupKey)object;
            return Objects.equals(this.models, var2.models) && Objects.equals(this.coloringValues, var2.coloringValues);
         }
      }

      public int hashCode() {
         return 31 * this.models.hashCode() + this.coloringValues.hashCode();
      }

      public static ModelBakery.ModelGroupKey create(BlockState blockState, MultiPart multiPart, Collection collection) {
         StateDefinition<Block, BlockState> var3 = blockState.getBlock().getStateDefinition();
         List<UnbakedModel> var4 = (List)multiPart.getSelectors().stream().filter((selector) -> {
            return selector.getPredicate(var3).test(blockState);
         }).map(Selector::getVariant).collect(ImmutableList.toImmutableList());
         List<Object> var5 = getColoringValues(blockState, collection);
         return new ModelBakery.ModelGroupKey(var4, var5);
      }

      public static ModelBakery.ModelGroupKey create(BlockState blockState, UnbakedModel unbakedModel, Collection collection) {
         List<Object> var3 = getColoringValues(blockState, collection);
         return new ModelBakery.ModelGroupKey(ImmutableList.of(unbakedModel), var3);
      }

      private static List getColoringValues(BlockState blockState, Collection collection) {
         Stream var10000 = collection.stream();
         blockState.getClass();
         return (List)var10000.map(blockState::getValue).collect(ImmutableList.toImmutableList());
      }
   }
}
