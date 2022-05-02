package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BlockRotProcessor extends StructureProcessor {
   private final float integrity;

   public BlockRotProcessor(float integrity) {
      this.integrity = integrity;
   }

   public BlockRotProcessor(Dynamic dynamic) {
      this(dynamic.get("integrity").asFloat(1.0F));
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings structurePlaceSettings) {
      Random var6 = structurePlaceSettings.getRandom(var4.pos);
      return this.integrity < 1.0F && var6.nextFloat() > this.integrity?null:var4;
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.BLOCK_ROT;
   }

   protected Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("integrity"), dynamicOps.createFloat(this.integrity))));
   }
}
