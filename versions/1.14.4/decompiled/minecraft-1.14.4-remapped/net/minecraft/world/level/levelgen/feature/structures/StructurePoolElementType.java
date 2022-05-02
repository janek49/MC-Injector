package net.minecraft.world.level.levelgen.feature.structures;

import net.minecraft.core.Registry;
import net.minecraft.util.Deserializer;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.FeaturePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.ListPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.SinglePoolElement;

public interface StructurePoolElementType extends Deserializer {
   StructurePoolElementType SINGLE = register("single_pool_element", SinglePoolElement::<init>);
   StructurePoolElementType LIST = register("list_pool_element", ListPoolElement::<init>);
   StructurePoolElementType FEATURE = register("feature_pool_element", FeaturePoolElement::<init>);
   StructurePoolElementType EMPTY = register("empty_pool_element", (dynamic) -> {
      return EmptyPoolElement.INSTANCE;
   });

   static default StructurePoolElementType register(String string, StructurePoolElementType var1) {
      return (StructurePoolElementType)Registry.register(Registry.STRUCTURE_POOL_ELEMENT, (String)string, var1);
   }
}
