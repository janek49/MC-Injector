package net.minecraft.client.renderer.block.model.multipart;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.multipart.AndCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.KeyValueCondition;
import net.minecraft.client.renderer.block.model.multipart.OrCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.StateDefinition;

@ClientJarOnly
public class Selector {
   private final Condition condition;
   private final MultiVariant variant;

   public Selector(Condition condition, MultiVariant variant) {
      if(condition == null) {
         throw new IllegalArgumentException("Missing condition for selector");
      } else if(variant == null) {
         throw new IllegalArgumentException("Missing variant for selector");
      } else {
         this.condition = condition;
         this.variant = variant;
      }
   }

   public MultiVariant getVariant() {
      return this.variant;
   }

   public Predicate getPredicate(StateDefinition stateDefinition) {
      return this.condition.getPredicate(stateDefinition);
   }

   public boolean equals(Object object) {
      return this == object;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   @ClientJarOnly
   public static class Deserializer implements JsonDeserializer {
      public Selector deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = jsonElement.getAsJsonObject();
         return new Selector(this.getSelector(var4), (MultiVariant)jsonDeserializationContext.deserialize(var4.get("apply"), MultiVariant.class));
      }

      private Condition getSelector(JsonObject jsonObject) {
         return jsonObject.has("when")?getCondition(GsonHelper.getAsJsonObject(jsonObject, "when")):Condition.TRUE;
      }

      @VisibleForTesting
      static Condition getCondition(JsonObject jsonObject) {
         Set<Entry<String, JsonElement>> var1 = jsonObject.entrySet();
         if(var1.isEmpty()) {
            throw new JsonParseException("No elements found in selector");
         } else if(var1.size() == 1) {
            if(jsonObject.has("OR")) {
               List<Condition> var2 = (List)Streams.stream(GsonHelper.getAsJsonArray(jsonObject, "OR")).map((jsonElement) -> {
                  return getCondition(jsonElement.getAsJsonObject());
               }).collect(Collectors.toList());
               return new OrCondition(var2);
            } else if(jsonObject.has("AND")) {
               List<Condition> var2 = (List)Streams.stream(GsonHelper.getAsJsonArray(jsonObject, "AND")).map((jsonElement) -> {
                  return getCondition(jsonElement.getAsJsonObject());
               }).collect(Collectors.toList());
               return new AndCondition(var2);
            } else {
               return getKeyValueCondition((Entry)var1.iterator().next());
            }
         } else {
            return new AndCondition((Iterable)var1.stream().map(Selector.Deserializer::getKeyValueCondition).collect(Collectors.toList()));
         }
      }

      private static Condition getKeyValueCondition(Entry map$Entry) {
         return new KeyValueCondition((String)map$Entry.getKey(), ((JsonElement)map$Entry.getValue()).getAsString());
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
