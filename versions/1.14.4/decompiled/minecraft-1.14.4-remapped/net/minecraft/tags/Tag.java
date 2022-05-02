package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Tag {
   private final ResourceLocation id;
   private final Set values;
   private final Collection source;

   public Tag(ResourceLocation id) {
      this.id = id;
      this.values = Collections.emptySet();
      this.source = Collections.emptyList();
   }

   public Tag(ResourceLocation id, Collection source, boolean var3) {
      this.id = id;
      this.values = (Set)(var3?Sets.newLinkedHashSet():Sets.newHashSet());
      this.source = source;

      for(Tag.Entry<T> var5 : source) {
         var5.build(this.values);
      }

   }

   public JsonObject serializeToJson(Function function) {
      JsonObject jsonObject = new JsonObject();
      JsonArray var3 = new JsonArray();

      for(Tag.Entry<T> var5 : this.source) {
         var5.serializeTo(var3, function);
      }

      jsonObject.addProperty("replace", Boolean.valueOf(false));
      jsonObject.add("values", var3);
      return jsonObject;
   }

   public boolean contains(Object object) {
      return this.values.contains(object);
   }

   public Collection getValues() {
      return this.values;
   }

   public Collection getSource() {
      return this.source;
   }

   public Object getRandomElement(Random random) {
      List<T> var2 = Lists.newArrayList(this.getValues());
      return var2.get(random.nextInt(var2.size()));
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public static class Builder {
      private final Set values = Sets.newLinkedHashSet();
      private boolean ordered;

      public static Tag.Builder tag() {
         return new Tag.Builder();
      }

      public Tag.Builder add(Tag.Entry tag$Entry) {
         this.values.add(tag$Entry);
         return this;
      }

      public Tag.Builder add(Object object) {
         this.values.add(new Tag.ValuesEntry(Collections.singleton(object)));
         return this;
      }

      @SafeVarargs
      public final Tag.Builder add(Object... objects) {
         this.values.add(new Tag.ValuesEntry(Lists.newArrayList(objects)));
         return this;
      }

      public Tag.Builder addTag(Tag tag) {
         this.values.add(new Tag.TagEntry(tag));
         return this;
      }

      public Tag.Builder keepOrder(boolean ordered) {
         this.ordered = ordered;
         return this;
      }

      public boolean canBuild(Function function) {
         for(Tag.Entry<T> var3 : this.values) {
            if(!var3.canBuild(function)) {
               return false;
            }
         }

         return true;
      }

      public Tag build(ResourceLocation resourceLocation) {
         return new Tag(resourceLocation, this.values, this.ordered);
      }

      public Tag.Builder addFromJson(Function function, JsonObject jsonObject) {
         JsonArray var3 = GsonHelper.getAsJsonArray(jsonObject, "values");
         List<Tag.Entry<T>> var4 = Lists.newArrayList();

         for(JsonElement var6 : var3) {
            String var7 = GsonHelper.convertToString(var6, "value");
            if(var7.startsWith("#")) {
               var4.add(new Tag.TagEntry(new ResourceLocation(var7.substring(1))));
            } else {
               ResourceLocation var8 = new ResourceLocation(var7);
               var4.add(new Tag.ValuesEntry(Collections.singleton(((Optional)function.apply(var8)).orElseThrow(() -> {
                  return new JsonParseException("Unknown value \'" + var8 + "\'");
               }))));
            }
         }

         if(GsonHelper.getAsBoolean(jsonObject, "replace", false)) {
            this.values.clear();
         }

         this.values.addAll(var4);
         return this;
      }
   }

   public interface Entry {
      default boolean canBuild(Function function) {
         return true;
      }

      void build(Collection var1);

      void serializeTo(JsonArray var1, Function var2);
   }

   public static class TagEntry implements Tag.Entry {
      @Nullable
      private final ResourceLocation id;
      @Nullable
      private Tag tag;

      public TagEntry(ResourceLocation id) {
         this.id = id;
      }

      public TagEntry(Tag tag) {
         this.id = tag.getId();
         this.tag = tag;
      }

      public boolean canBuild(Function function) {
         if(this.tag == null) {
            this.tag = (Tag)function.apply(this.id);
         }

         return this.tag != null;
      }

      public void build(Collection collection) {
         if(this.tag == null) {
            throw new IllegalStateException("Cannot build unresolved tag entry");
         } else {
            collection.addAll(this.tag.getValues());
         }
      }

      public ResourceLocation getId() {
         if(this.tag != null) {
            return this.tag.getId();
         } else if(this.id != null) {
            return this.id;
         } else {
            throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
         }
      }

      public void serializeTo(JsonArray jsonArray, Function function) {
         jsonArray.add("#" + this.getId());
      }
   }

   public static class ValuesEntry implements Tag.Entry {
      private final Collection values;

      public ValuesEntry(Collection values) {
         this.values = values;
      }

      public void build(Collection collection) {
         collection.addAll(this.values);
      }

      public void serializeTo(JsonArray jsonArray, Function function) {
         for(T var4 : this.values) {
            ResourceLocation var5 = (ResourceLocation)function.apply(var4);
            if(var5 == null) {
               throw new IllegalStateException("Unable to serialize an anonymous value to json!");
            }

            jsonArray.add(var5.toString());
         }

      }

      public Collection getValues() {
         return this.values;
      }
   }
}
