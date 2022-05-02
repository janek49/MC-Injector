package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;

@ClientJarOnly
public class MultiVariant implements UnbakedModel {
   private final List variants;

   public MultiVariant(List variants) {
      this.variants = variants;
   }

   public List getVariants() {
      return this.variants;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object instanceof MultiVariant) {
         MultiVariant var2 = (MultiVariant)object;
         return this.variants.equals(var2.variants);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.variants.hashCode();
   }

   public Collection getDependencies() {
      return (Collection)this.getVariants().stream().map(Variant::getModelLocation).collect(Collectors.toSet());
   }

   public Collection getTextures(Function function, Set set) {
      return (Collection)this.getVariants().stream().map(Variant::getModelLocation).distinct().flatMap((resourceLocation) -> {
         return ((UnbakedModel)function.apply(resourceLocation)).getTextures(function, set).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public BakedModel bake(ModelBakery modelBakery, Function function, ModelState modelState) {
      if(this.getVariants().isEmpty()) {
         return null;
      } else {
         WeightedBakedModel.Builder var4 = new WeightedBakedModel.Builder();

         for(Variant var6 : this.getVariants()) {
            BakedModel var7 = modelBakery.bake(var6.getModelLocation(), var6);
            var4.add(var7, var6.getWeight());
         }

         return var4.build();
      }
   }

   @ClientJarOnly
   public static class Deserializer implements JsonDeserializer {
      public MultiVariant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         List<Variant> var4 = Lists.newArrayList();
         if(jsonElement.isJsonArray()) {
            JsonArray var5 = jsonElement.getAsJsonArray();
            if(var5.size() == 0) {
               throw new JsonParseException("Empty variant array");
            }

            for(JsonElement var7 : var5) {
               var4.add(jsonDeserializationContext.deserialize(var7, Variant.class));
            }
         } else {
            var4.add(jsonDeserializationContext.deserialize(jsonElement, Variant.class));
         }

         return new MultiVariant(var4);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
