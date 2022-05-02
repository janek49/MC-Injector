package net.minecraft.advancements.critereon;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

public abstract class EntityTypePredicate {
   public static final EntityTypePredicate ANY = new EntityTypePredicate() {
      public boolean matches(EntityType entityType) {
         return true;
      }

      public JsonElement serializeToJson() {
         return JsonNull.INSTANCE;
      }
   };
   private static final Joiner COMMA_JOINER = Joiner.on(", ");

   public abstract boolean matches(EntityType var1);

   public abstract JsonElement serializeToJson();

   public static EntityTypePredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         String var1 = GsonHelper.convertToString(json, "type");
         if(var1.startsWith("#")) {
            ResourceLocation var2 = new ResourceLocation(var1.substring(1));
            Tag<EntityType<?>> var3 = EntityTypeTags.getAllTags().getTagOrEmpty(var2);
            return new EntityTypePredicate.TagPredicate(var3);
         } else {
            ResourceLocation var2 = new ResourceLocation(var1);
            EntityType<?> var3 = (EntityType)Registry.ENTITY_TYPE.getOptional(var2).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown entity type \'" + var2 + "\', valid types are: " + COMMA_JOINER.join(Registry.ENTITY_TYPE.keySet()));
            });
            return new EntityTypePredicate.TypePredicate(var3);
         }
      } else {
         return ANY;
      }
   }

   public static EntityTypePredicate of(EntityType entityType) {
      return new EntityTypePredicate.TypePredicate(entityType);
   }

   public static EntityTypePredicate of(Tag tag) {
      return new EntityTypePredicate.TagPredicate(tag);
   }

   static class TagPredicate extends EntityTypePredicate {
      private final Tag tag;

      public TagPredicate(Tag tag) {
         this.tag = tag;
      }

      public boolean matches(EntityType entityType) {
         return this.tag.contains(entityType);
      }

      public JsonElement serializeToJson() {
         return new JsonPrimitive("#" + this.tag.getId().toString());
      }
   }

   static class TypePredicate extends EntityTypePredicate {
      private final EntityType type;

      public TypePredicate(EntityType type) {
         this.type = type;
      }

      public boolean matches(EntityType entityType) {
         return this.type == entityType;
      }

      public JsonElement serializeToJson() {
         return new JsonPrimitive(Registry.ENTITY_TYPE.getKey(this.type).toString());
      }
   }
}
