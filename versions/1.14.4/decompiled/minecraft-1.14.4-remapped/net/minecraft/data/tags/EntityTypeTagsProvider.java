package net.minecraft.data.tags;

import java.nio.file.Path;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTagsProvider extends TagsProvider {
   public EntityTypeTagsProvider(DataGenerator dataGenerator) {
      super(dataGenerator, Registry.ENTITY_TYPE);
   }

   protected void addTags() {
      this.tag(EntityTypeTags.SKELETONS).add((Object[])(new EntityType[]{EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON}));
      this.tag(EntityTypeTags.RAIDERS).add((Object[])(new EntityType[]{EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH}));
   }

   protected Path getPath(ResourceLocation resourceLocation) {
      return this.generator.getOutputFolder().resolve("data/" + resourceLocation.getNamespace() + "/tags/entity_types/" + resourceLocation.getPath() + ".json");
   }

   public String getName() {
      return "Entity Type Tags";
   }

   protected void useTags(TagCollection tagCollection) {
      EntityTypeTags.reset(tagCollection);
   }
}
