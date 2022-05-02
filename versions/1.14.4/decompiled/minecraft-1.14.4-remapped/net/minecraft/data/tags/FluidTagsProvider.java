package net.minecraft.data.tags;

import java.nio.file.Path;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidTagsProvider extends TagsProvider {
   public FluidTagsProvider(DataGenerator dataGenerator) {
      super(dataGenerator, Registry.FLUID);
   }

   protected void addTags() {
      this.tag(FluidTags.WATER).add((Object[])(new Fluid[]{Fluids.WATER, Fluids.FLOWING_WATER}));
      this.tag(FluidTags.LAVA).add((Object[])(new Fluid[]{Fluids.LAVA, Fluids.FLOWING_LAVA}));
   }

   protected Path getPath(ResourceLocation resourceLocation) {
      return this.generator.getOutputFolder().resolve("data/" + resourceLocation.getNamespace() + "/tags/fluids/" + resourceLocation.getPath() + ".json");
   }

   public String getName() {
      return "Fluid Tags";
   }

   protected void useTags(TagCollection tagCollection) {
      FluidTags.reset(tagCollection);
   }
}
