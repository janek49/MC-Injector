package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.StateDefinition;

@ClientJarOnly
public class BlockModelDefinition {
   private final Map variants = Maps.newLinkedHashMap();
   private MultiPart multiPart;

   public static BlockModelDefinition fromStream(BlockModelDefinition.Context blockModelDefinition$Context, Reader reader) {
      return (BlockModelDefinition)GsonHelper.fromJson(blockModelDefinition$Context.gson, reader, BlockModelDefinition.class);
   }

   public BlockModelDefinition(Map map, MultiPart multiPart) {
      this.multiPart = multiPart;
      this.variants.putAll(map);
   }

   public BlockModelDefinition(List list) {
      BlockModelDefinition var2 = null;

      for(BlockModelDefinition var4 : list) {
         if(var4.isMultiPart()) {
            this.variants.clear();
            var2 = var4;
         }

         this.variants.putAll(var4.variants);
      }

      if(var2 != null) {
         this.multiPart = var2.multiPart;
      }

   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else {
         if(object instanceof BlockModelDefinition) {
            BlockModelDefinition var2 = (BlockModelDefinition)object;
            if(this.variants.equals(var2.variants)) {
               return this.isMultiPart()?this.multiPart.equals(var2.multiPart):!var2.isMultiPart();
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return 31 * this.variants.hashCode() + (this.isMultiPart()?this.multiPart.hashCode():0);
   }

   public Map getVariants() {
      return this.variants;
   }

   public boolean isMultiPart() {
      return this.multiPart != null;
   }

   public MultiPart getMultiPart() {
      return this.multiPart;
   }

   @ClientJarOnly
   public static final class Context {
      protected final Gson gson = (new GsonBuilder()).registerTypeAdapter(BlockModelDefinition.class, new BlockModelDefinition.Deserializer()).registerTypeAdapter(Variant.class, new Variant.Deserializer()).registerTypeAdapter(MultiVariant.class, new MultiVariant.Deserializer()).registerTypeAdapter(MultiPart.class, new MultiPart.Deserializer(this)).registerTypeAdapter(Selector.class, new Selector.Deserializer()).create();
      private StateDefinition definition;

      public StateDefinition getDefinition() {
         return this.definition;
      }

      public void setDefinition(StateDefinition definition) {
         this.definition = definition;
      }
   }

   @ClientJarOnly
   public static class Deserializer implements JsonDeserializer {
      public BlockModelDefinition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = jsonElement.getAsJsonObject();
         Map<String, MultiVariant> var5 = this.getVariants(jsonDeserializationContext, var4);
         MultiPart var6 = this.getMultiPart(jsonDeserializationContext, var4);
         if(!var5.isEmpty() || var6 != null && !var6.getMultiVariants().isEmpty()) {
            return new BlockModelDefinition(var5, var6);
         } else {
            throw new JsonParseException("Neither \'variants\' nor \'multipart\' found");
         }
      }

      protected Map getVariants(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         Map<String, MultiVariant> map = Maps.newHashMap();
         if(jsonObject.has("variants")) {
            JsonObject var4 = GsonHelper.getAsJsonObject(jsonObject, "variants");

            for(Entry<String, JsonElement> var6 : var4.entrySet()) {
               map.put(var6.getKey(), jsonDeserializationContext.deserialize((JsonElement)var6.getValue(), MultiVariant.class));
            }
         }

         return map;
      }

      @Nullable
      protected MultiPart getMultiPart(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         if(!jsonObject.has("multipart")) {
            return null;
         } else {
            JsonArray var3 = GsonHelper.getAsJsonArray(jsonObject, "multipart");
            return (MultiPart)jsonDeserializationContext.deserialize(var3, MultiPart.class);
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
