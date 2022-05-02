package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class BlockModel implements UnbakedModel {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final FaceBakery FACE_BAKERY = new FaceBakery();
   @VisibleForTesting
   static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(BlockModel.class, new BlockModel.Deserializer()).registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer()).registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer()).registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer()).create();
   private final List elements;
   private final boolean isGui3d;
   private final boolean hasAmbientOcclusion;
   private final ItemTransforms transforms;
   private final List overrides;
   public String name = "";
   @VisibleForTesting
   protected final Map textureMap;
   @Nullable
   protected BlockModel parent;
   @Nullable
   protected ResourceLocation parentLocation;

   public static BlockModel fromStream(Reader stream) {
      return (BlockModel)GsonHelper.fromJson(GSON, stream, BlockModel.class);
   }

   public static BlockModel fromString(String string) {
      return fromStream(new StringReader(string));
   }

   public BlockModel(@Nullable ResourceLocation parentLocation, List elements, Map textureMap, boolean hasAmbientOcclusion, boolean isGui3d, ItemTransforms transforms, List overrides) {
      this.elements = elements;
      this.hasAmbientOcclusion = hasAmbientOcclusion;
      this.isGui3d = isGui3d;
      this.textureMap = textureMap;
      this.parentLocation = parentLocation;
      this.transforms = transforms;
      this.overrides = overrides;
   }

   public List getElements() {
      return this.elements.isEmpty() && this.parent != null?this.parent.getElements():this.elements;
   }

   public boolean hasAmbientOcclusion() {
      return this.parent != null?this.parent.hasAmbientOcclusion():this.hasAmbientOcclusion;
   }

   public boolean isGui3d() {
      return this.isGui3d;
   }

   public List getOverrides() {
      return this.overrides;
   }

   private ItemOverrides getItemOverrides(ModelBakery modelBakery, BlockModel blockModel) {
      return this.overrides.isEmpty()?ItemOverrides.EMPTY:new ItemOverrides(modelBakery, blockModel, modelBakery::getModel, this.overrides);
   }

   public Collection getDependencies() {
      Set<ResourceLocation> var1 = Sets.newHashSet();

      for(ItemOverride var3 : this.overrides) {
         var1.add(var3.getModel());
      }

      if(this.parentLocation != null) {
         var1.add(this.parentLocation);
      }

      return var1;
   }

   public Collection getTextures(Function function, Set set) {
      Set<UnbakedModel> set = Sets.newLinkedHashSet();

      for(BlockModel var4 = this; var4.parentLocation != null && var4.parent == null; var4 = var4.parent) {
         set.add(var4);
         UnbakedModel var5 = (UnbakedModel)function.apply(var4.parentLocation);
         if(var5 == null) {
            LOGGER.warn("No parent \'{}\' while loading model \'{}\'", this.parentLocation, var4);
         }

         if(set.contains(var5)) {
            LOGGER.warn("Found \'parent\' loop while loading model \'{}\' in chain: {} -> {}", var4, set.stream().map(Object::toString).collect(Collectors.joining(" -> ")), this.parentLocation);
            var5 = null;
         }

         if(var5 == null) {
            var4.parentLocation = ModelBakery.MISSING_MODEL_LOCATION;
            var5 = (UnbakedModel)function.apply(var4.parentLocation);
         }

         if(!(var5 instanceof BlockModel)) {
            throw new IllegalStateException("BlockModel parent has to be a block model.");
         }

         var4.parent = (BlockModel)var5;
      }

      Set<ResourceLocation> var5 = Sets.newHashSet(new ResourceLocation[]{new ResourceLocation(this.getTexture("particle"))});

      for(BlockElement var7 : this.getElements()) {
         for(BlockElementFace var9 : var7.faces.values()) {
            String var10 = this.getTexture(var9.texture);
            if(Objects.equals(var10, MissingTextureAtlasSprite.getLocation().toString())) {
               set.add(String.format("%s in %s", new Object[]{var9.texture, this.name}));
            }

            var5.add(new ResourceLocation(var10));
         }
      }

      this.overrides.forEach((itemOverride) -> {
         UnbakedModel var5 = (UnbakedModel)function.apply(itemOverride.getModel());
         if(!Objects.equals(var5, this)) {
            var5.addAll(var5.getTextures(function, set));
         }
      });
      if(this.getRootModel() == ModelBakery.GENERATION_MARKER) {
         ItemModelGenerator.LAYERS.forEach((string) -> {
            var5.add(new ResourceLocation(this.getTexture(string)));
         });
      }

      return var5;
   }

   public BakedModel bake(ModelBakery modelBakery, Function function, ModelState modelState) {
      return this.bake(modelBakery, this, function, modelState);
   }

   public BakedModel bake(ModelBakery modelBakery, BlockModel blockModel, Function function, ModelState modelState) {
      TextureAtlasSprite var5 = (TextureAtlasSprite)function.apply(new ResourceLocation(this.getTexture("particle")));
      if(this.getRootModel() == ModelBakery.BLOCK_ENTITY_MARKER) {
         return new BuiltInModel(this.getTransforms(), this.getItemOverrides(modelBakery, blockModel), var5);
      } else {
         SimpleBakedModel.Builder var6 = (new SimpleBakedModel.Builder(this, this.getItemOverrides(modelBakery, blockModel))).particle(var5);

         for(BlockElement var8 : this.getElements()) {
            for(Direction var10 : var8.faces.keySet()) {
               BlockElementFace var11 = (BlockElementFace)var8.faces.get(var10);
               TextureAtlasSprite var12 = (TextureAtlasSprite)function.apply(new ResourceLocation(this.getTexture(var11.texture)));
               if(var11.cullForDirection == null) {
                  var6.addUnculledFace(bakeFace(var8, var11, var12, var10, modelState));
               } else {
                  var6.addCulledFace(modelState.getRotation().rotate(var11.cullForDirection), bakeFace(var8, var11, var12, var10, modelState));
               }
            }
         }

         return var6.build();
      }
   }

   private static BakedQuad bakeFace(BlockElement blockElement, BlockElementFace blockElementFace, TextureAtlasSprite textureAtlasSprite, Direction direction, ModelState modelState) {
      return FACE_BAKERY.bakeQuad(blockElement.from, blockElement.to, blockElementFace, textureAtlasSprite, direction, modelState, blockElement.rotation, blockElement.shade);
   }

   public boolean hasTexture(String string) {
      return !MissingTextureAtlasSprite.getLocation().toString().equals(this.getTexture(string));
   }

   public String getTexture(String string) {
      if(!this.isTextureReference(string)) {
         string = '#' + string;
      }

      return this.getTexture(string, new BlockModel.Bookkeep(this));
   }

   private String getTexture(String var1, BlockModel.Bookkeep blockModel$Bookkeep) {
      if(this.isTextureReference(var1)) {
         if(this == blockModel$Bookkeep.maxDepth) {
            LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", var1, this.name);
            return MissingTextureAtlasSprite.getLocation().toString();
         } else {
            String var3 = (String)this.textureMap.get(var1.substring(1));
            if(var3 == null && this.parent != null) {
               var3 = this.parent.getTexture(var1, blockModel$Bookkeep);
            }

            blockModel$Bookkeep.maxDepth = this;
            if(var3 != null && this.isTextureReference(var3)) {
               var3 = blockModel$Bookkeep.root.getTexture(var3, blockModel$Bookkeep);
            }

            return var3 != null && !this.isTextureReference(var3)?var3:MissingTextureAtlasSprite.getLocation().toString();
         }
      } else {
         return var1;
      }
   }

   private boolean isTextureReference(String string) {
      return string.charAt(0) == 35;
   }

   public BlockModel getRootModel() {
      return this.parent == null?this:this.parent.getRootModel();
   }

   public ItemTransforms getTransforms() {
      ItemTransform var1 = this.getTransform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
      ItemTransform var2 = this.getTransform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
      ItemTransform var3 = this.getTransform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
      ItemTransform var4 = this.getTransform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
      ItemTransform var5 = this.getTransform(ItemTransforms.TransformType.HEAD);
      ItemTransform var6 = this.getTransform(ItemTransforms.TransformType.GUI);
      ItemTransform var7 = this.getTransform(ItemTransforms.TransformType.GROUND);
      ItemTransform var8 = this.getTransform(ItemTransforms.TransformType.FIXED);
      return new ItemTransforms(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private ItemTransform getTransform(ItemTransforms.TransformType itemTransforms$TransformType) {
      return this.parent != null && !this.transforms.hasTransform(itemTransforms$TransformType)?this.parent.getTransform(itemTransforms$TransformType):this.transforms.getTransform(itemTransforms$TransformType);
   }

   public String toString() {
      return this.name;
   }

   @ClientJarOnly
   static final class Bookkeep {
      public final BlockModel root;
      public BlockModel maxDepth;

      private Bookkeep(BlockModel root) {
         this.root = root;
      }
   }

   @ClientJarOnly
   public static class Deserializer implements JsonDeserializer {
      public BlockModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = jsonElement.getAsJsonObject();
         List<BlockElement> var5 = this.getElements(jsonDeserializationContext, var4);
         String var6 = this.getParentName(var4);
         Map<String, String> var7 = this.getTextureMap(var4);
         boolean var8 = this.getAmbientOcclusion(var4);
         ItemTransforms var9 = ItemTransforms.NO_TRANSFORMS;
         if(var4.has("display")) {
            JsonObject var10 = GsonHelper.getAsJsonObject(var4, "display");
            var9 = (ItemTransforms)jsonDeserializationContext.deserialize(var10, ItemTransforms.class);
         }

         List<ItemOverride> var10 = this.getOverrides(jsonDeserializationContext, var4);
         ResourceLocation var11 = var6.isEmpty()?null:new ResourceLocation(var6);
         return new BlockModel(var11, var5, var7, var8, true, var9, var10);
      }

      protected List getOverrides(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         List<ItemOverride> list = Lists.newArrayList();
         if(jsonObject.has("overrides")) {
            for(JsonElement var6 : GsonHelper.getAsJsonArray(jsonObject, "overrides")) {
               list.add(jsonDeserializationContext.deserialize(var6, ItemOverride.class));
            }
         }

         return list;
      }

      private Map getTextureMap(JsonObject jsonObject) {
         Map<String, String> map = Maps.newHashMap();
         if(jsonObject.has("textures")) {
            JsonObject var3 = GsonHelper.getAsJsonObject(jsonObject, "textures");

            for(Entry<String, JsonElement> var5 : var3.entrySet()) {
               map.put(var5.getKey(), ((JsonElement)var5.getValue()).getAsString());
            }
         }

         return map;
      }

      private String getParentName(JsonObject jsonObject) {
         return GsonHelper.getAsString(jsonObject, "parent", "");
      }

      protected boolean getAmbientOcclusion(JsonObject jsonObject) {
         return GsonHelper.getAsBoolean(jsonObject, "ambientocclusion", true);
      }

      protected List getElements(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         List<BlockElement> list = Lists.newArrayList();
         if(jsonObject.has("elements")) {
            for(JsonElement var5 : GsonHelper.getAsJsonArray(jsonObject, "elements")) {
               list.add(jsonDeserializationContext.deserialize(var5, BlockElement.class));
            }
         }

         return list;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
