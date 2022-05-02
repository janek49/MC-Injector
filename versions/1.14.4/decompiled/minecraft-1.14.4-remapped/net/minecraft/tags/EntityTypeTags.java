package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTags {
   private static TagCollection source = new TagCollection((resourceLocation) -> {
      return Optional.empty();
   }, "", false, "");
   private static int resetCount;
   public static final Tag SKELETONS = bind("skeletons");
   public static final Tag RAIDERS = bind("raiders");

   public static void reset(TagCollection tagCollection) {
      source = tagCollection;
      ++resetCount;
   }

   public static TagCollection getAllTags() {
      return source;
   }

   private static Tag bind(String string) {
      return new EntityTypeTags.Wrapper(new ResourceLocation(string));
   }

   public static class Wrapper extends Tag {
      private int check = -1;
      private Tag actual;

      public Wrapper(ResourceLocation resourceLocation) {
         super(resourceLocation);
      }

      public boolean contains(EntityType entityType) {
         if(this.check != EntityTypeTags.resetCount) {
            this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
            this.check = EntityTypeTags.resetCount;
         }

         return this.actual.contains(entityType);
      }

      public Collection getValues() {
         if(this.check != EntityTypeTags.resetCount) {
            this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
            this.check = EntityTypeTags.resetCount;
         }

         return this.actual.getValues();
      }

      public Collection getSource() {
         if(this.check != EntityTypeTags.resetCount) {
            this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
            this.check = EntityTypeTags.resetCount;
         }

         return this.actual.getSource();
      }
   }
}
