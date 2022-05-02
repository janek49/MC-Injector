package net.minecraft.client.renderer.block.model.multipart;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.level.block.state.StateDefinition;

@ClientJarOnly
public class MultiPart implements UnbakedModel {
   private final StateDefinition definition;
   private final List selectors;

   public MultiPart(StateDefinition definition, List selectors) {
      this.definition = definition;
      this.selectors = selectors;
   }

   public List getSelectors() {
      return this.selectors;
   }

   public Set getMultiVariants() {
      Set<MultiVariant> set = Sets.newHashSet();

      for(Selector var3 : this.selectors) {
         set.add(var3.getVariant());
      }

      return set;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof MultiPart)) {
         return false;
      } else {
         MultiPart var2 = (MultiPart)object;
         return Objects.equals(this.definition, var2.definition) && Objects.equals(this.selectors, var2.selectors);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.definition, this.selectors});
   }

   public Collection getDependencies() {
      return (Collection)this.getSelectors().stream().flatMap((selector) -> {
         return selector.getVariant().getDependencies().stream();
      }).collect(Collectors.toSet());
   }

   public Collection getTextures(Function function, Set set) {
      return (Collection)this.getSelectors().stream().flatMap((selector) -> {
         return selector.getVariant().getTextures(function, set).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public BakedModel bake(ModelBakery modelBakery, Function function, ModelState modelState) {
      MultiPartBakedModel.Builder var4 = new MultiPartBakedModel.Builder();

      for(Selector var6 : this.getSelectors()) {
         BakedModel var7 = var6.getVariant().bake(modelBakery, function, modelState);
         if(var7 != null) {
            var4.add(var6.getPredicate(this.definition), var7);
         }
      }

      return var4.build();
   }

   @ClientJarOnly
   public static class Deserializer implements JsonDeserializer {
      private final BlockModelDefinition.Context context;

      public Deserializer(BlockModelDefinition.Context context) {
         this.context = context;
      }

      public MultiPart deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         return new MultiPart(this.context.getDefinition(), this.getSelectors(jsonDeserializationContext, jsonElement.getAsJsonArray()));
      }

      private List getSelectors(JsonDeserializationContext jsonDeserializationContext, JsonArray jsonArray) {
         List<Selector> list = Lists.newArrayList();

         for(JsonElement var5 : jsonArray) {
            list.add(jsonDeserializationContext.deserialize(var5, Selector.class));
         }

         return list;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
