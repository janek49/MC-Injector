package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class NopProcessor extends StructureProcessor {
   public static final NopProcessor INSTANCE = new NopProcessor();

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings structurePlaceSettings) {
      return var4;
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.NOP;
   }

   protected Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.emptyMap());
   }
}
