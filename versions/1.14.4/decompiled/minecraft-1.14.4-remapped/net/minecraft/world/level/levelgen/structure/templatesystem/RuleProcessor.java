package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class RuleProcessor extends StructureProcessor {
   private final ImmutableList rules;

   public RuleProcessor(List list) {
      this.rules = ImmutableList.copyOf(list);
   }

   public RuleProcessor(Dynamic dynamic) {
      this(dynamic.get("rules").asList(ProcessorRule::deserialize));
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings structurePlaceSettings) {
      Random var6 = new Random(Mth.getSeed(var4.pos));
      BlockState var7 = levelReader.getBlockState(var4.pos);
      UnmodifiableIterator var8 = this.rules.iterator();

      while(var8.hasNext()) {
         ProcessorRule var9 = (ProcessorRule)var8.next();
         if(var9.test(var4.state, var7, var6)) {
            return new StructureTemplate.StructureBlockInfo(var4.pos, var9.getOutputState(), var9.getOutputTag());
         }
      }

      return var4;
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.RULE;
   }

   protected Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("rules"), dynamicOps.createList(this.rules.stream().map((processorRule) -> {
         return processorRule.serialize(dynamicOps).getValue();
      })))));
   }
}
