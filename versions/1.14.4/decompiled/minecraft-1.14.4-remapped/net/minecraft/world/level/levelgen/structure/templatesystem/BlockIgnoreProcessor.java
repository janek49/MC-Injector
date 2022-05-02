package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BlockIgnoreProcessor extends StructureProcessor {
   public static final BlockIgnoreProcessor STRUCTURE_BLOCK = new BlockIgnoreProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK));
   public static final BlockIgnoreProcessor AIR = new BlockIgnoreProcessor(ImmutableList.of(Blocks.AIR));
   public static final BlockIgnoreProcessor STRUCTURE_AND_AIR = new BlockIgnoreProcessor(ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK));
   private final ImmutableList toIgnore;

   public BlockIgnoreProcessor(List list) {
      this.toIgnore = ImmutableList.copyOf(list);
   }

   public BlockIgnoreProcessor(Dynamic dynamic) {
      this(dynamic.get("blocks").asList((dynamic) -> {
         return BlockState.deserialize(dynamic).getBlock();
      }));
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings structurePlaceSettings) {
      return this.toIgnore.contains(var4.state.getBlock())?null:var4;
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.BLOCK_IGNORE;
   }

   protected Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("blocks"), dynamicOps.createList(this.toIgnore.stream().map((block) -> {
         return BlockState.serialize(dynamicOps, block.defaultBlockState()).getValue();
      })))));
   }
}
