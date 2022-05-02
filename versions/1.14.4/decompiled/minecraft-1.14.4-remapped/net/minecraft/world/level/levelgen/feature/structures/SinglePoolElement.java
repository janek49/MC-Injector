package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Deserializer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.NopProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class SinglePoolElement extends StructurePoolElement {
   protected final ResourceLocation location;
   protected final ImmutableList processors;

   @Deprecated
   public SinglePoolElement(String string, List list) {
      this(string, list, StructureTemplatePool.Projection.RIGID);
   }

   public SinglePoolElement(String string, List list, StructureTemplatePool.Projection structureTemplatePool$Projection) {
      super(structureTemplatePool$Projection);
      this.location = new ResourceLocation(string);
      this.processors = ImmutableList.copyOf(list);
   }

   @Deprecated
   public SinglePoolElement(String string) {
      this(string, ImmutableList.of());
   }

   public SinglePoolElement(Dynamic dynamic) {
      super(dynamic);
      this.location = new ResourceLocation(dynamic.get("location").asString(""));
      this.processors = ImmutableList.copyOf(dynamic.get("processors").asList((dynamic) -> {
         return (StructureProcessor)Deserializer.deserialize(dynamic, Registry.STRUCTURE_PROCESSOR, "processor_type", NopProcessor.INSTANCE);
      }));
   }

   public List getDataMarkers(StructureManager structureManager, BlockPos blockPos, Rotation rotation, boolean var4) {
      StructureTemplate var5 = structureManager.getOrCreate(this.location);
      List<StructureTemplate.StructureBlockInfo> var6 = var5.filterBlocks(blockPos, (new StructurePlaceSettings()).setRotation(rotation), Blocks.STRUCTURE_BLOCK, var4);
      List<StructureTemplate.StructureBlockInfo> var7 = Lists.newArrayList();

      for(StructureTemplate.StructureBlockInfo var9 : var6) {
         if(var9.nbt != null) {
            StructureMode var10 = StructureMode.valueOf(var9.nbt.getString("mode"));
            if(var10 == StructureMode.DATA) {
               var7.add(var9);
            }
         }
      }

      return var7;
   }

   public List getShuffledJigsawBlocks(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random) {
      StructureTemplate var5 = structureManager.getOrCreate(this.location);
      List<StructureTemplate.StructureBlockInfo> var6 = var5.filterBlocks(blockPos, (new StructurePlaceSettings()).setRotation(rotation), Blocks.JIGSAW_BLOCK, true);
      Collections.shuffle(var6, random);
      return var6;
   }

   public BoundingBox getBoundingBox(StructureManager structureManager, BlockPos blockPos, Rotation rotation) {
      StructureTemplate var4 = structureManager.getOrCreate(this.location);
      return var4.getBoundingBox((new StructurePlaceSettings()).setRotation(rotation), blockPos);
   }

   public boolean place(StructureManager structureManager, LevelAccessor levelAccessor, BlockPos blockPos, Rotation rotation, BoundingBox boundingBox, Random random) {
      StructureTemplate var7 = structureManager.getOrCreate(this.location);
      StructurePlaceSettings var8 = this.getSettings(rotation, boundingBox);
      if(!var7.placeInWorld(levelAccessor, blockPos, var8, 18)) {
         return false;
      } else {
         for(StructureTemplate.StructureBlockInfo var11 : StructureTemplate.processBlockInfos(levelAccessor, blockPos, var8, this.getDataMarkers(structureManager, blockPos, rotation, false))) {
            this.handleDataMarker(levelAccessor, var11, blockPos, rotation, random, boundingBox);
         }

         return true;
      }
   }

   protected StructurePlaceSettings getSettings(Rotation rotation, BoundingBox boundingBox) {
      StructurePlaceSettings structurePlaceSettings = new StructurePlaceSettings();
      structurePlaceSettings.setBoundingBox(boundingBox);
      structurePlaceSettings.setRotation(rotation);
      structurePlaceSettings.setKnownShape(true);
      structurePlaceSettings.setIgnoreEntities(false);
      structurePlaceSettings.addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      structurePlaceSettings.addProcessor(JigsawReplacementProcessor.INSTANCE);
      this.processors.forEach(structurePlaceSettings::addProcessor);
      this.getProjection().getProcessors().forEach(structurePlaceSettings::addProcessor);
      return structurePlaceSettings;
   }

   public StructurePoolElementType getType() {
      return StructurePoolElementType.SINGLE;
   }

   public Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("location"), dynamicOps.createString(this.location.toString()), dynamicOps.createString("processors"), dynamicOps.createList(this.processors.stream().map((structureProcessor) -> {
         return structureProcessor.serialize(dynamicOps).getValue();
      })))));
   }

   public String toString() {
      return "Single[" + this.location + "]";
   }
}
