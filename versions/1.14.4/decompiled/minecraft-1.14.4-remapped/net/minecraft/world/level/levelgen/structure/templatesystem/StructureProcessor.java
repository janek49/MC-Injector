package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public abstract class StructureProcessor {
   @Nullable
   public abstract StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings var5);

   protected abstract StructureProcessorType getType();

   protected abstract Dynamic getDynamic(DynamicOps var1);

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.mergeInto(this.getDynamic(dynamicOps).getValue(), dynamicOps.createString("processor_type"), dynamicOps.createString(Registry.STRUCTURE_PROCESSOR.getKey(this.getType()).toString())));
   }
}
