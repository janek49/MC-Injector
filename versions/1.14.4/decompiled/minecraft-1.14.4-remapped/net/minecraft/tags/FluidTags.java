package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.level.material.Fluid;

public class FluidTags {
   private static TagCollection source = new TagCollection((resourceLocation) -> {
      return Optional.empty();
   }, "", false, "");
   private static int resetCount;
   public static final Tag WATER = bind("water");
   public static final Tag LAVA = bind("lava");

   public static void reset(TagCollection tagCollection) {
      source = tagCollection;
      ++resetCount;
   }

   private static Tag bind(String string) {
      return new FluidTags.Wrapper(new ResourceLocation(string));
   }

   public static class Wrapper extends Tag {
      private int check = -1;
      private Tag actual;

      public Wrapper(ResourceLocation resourceLocation) {
         super(resourceLocation);
      }

      public boolean contains(Fluid fluid) {
         if(this.check != FluidTags.resetCount) {
            this.actual = FluidTags.source.getTagOrEmpty(this.getId());
            this.check = FluidTags.resetCount;
         }

         return this.actual.contains(fluid);
      }

      public Collection getValues() {
         if(this.check != FluidTags.resetCount) {
            this.actual = FluidTags.source.getTagOrEmpty(this.getId());
            this.check = FluidTags.resetCount;
         }

         return this.actual.getValues();
      }

      public Collection getSource() {
         if(this.check != FluidTags.resetCount) {
            this.actual = FluidTags.source.getTagOrEmpty(this.getId());
            this.check = FluidTags.resetCount;
         }

         return this.actual.getSource();
      }
   }
}
