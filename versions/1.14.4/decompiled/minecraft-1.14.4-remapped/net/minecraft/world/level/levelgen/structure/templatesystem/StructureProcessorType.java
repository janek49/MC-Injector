package net.minecraft.world.level.levelgen.structure.templatesystem;

import net.minecraft.core.Registry;
import net.minecraft.util.Deserializer;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.NopProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;

public interface StructureProcessorType extends Deserializer {
   StructureProcessorType BLOCK_IGNORE = register("block_ignore", BlockIgnoreProcessor::<init>);
   StructureProcessorType BLOCK_ROT = register("block_rot", BlockRotProcessor::<init>);
   StructureProcessorType GRAVITY = register("gravity", GravityProcessor::<init>);
   StructureProcessorType JIGSAW_REPLACEMENT = register("jigsaw_replacement", (dynamic) -> {
      return JigsawReplacementProcessor.INSTANCE;
   });
   StructureProcessorType RULE = register("rule", RuleProcessor::<init>);
   StructureProcessorType NOP = register("nop", (dynamic) -> {
      return NopProcessor.INSTANCE;
   });

   static default StructureProcessorType register(String string, StructureProcessorType var1) {
      return (StructureProcessorType)Registry.register(Registry.STRUCTURE_PROCESSOR, (String)string, var1);
   }
}
