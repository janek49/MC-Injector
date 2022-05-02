package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class GravityProcessor extends StructureProcessor {
   private final Heightmap.Types heightmap;
   private final int offset;

   public GravityProcessor(Heightmap.Types heightmap, int offset) {
      this.heightmap = heightmap;
      this.offset = offset;
   }

   public GravityProcessor(Dynamic dynamic) {
      this(Heightmap.Types.getFromKey(dynamic.get("heightmap").asString(Heightmap.Types.WORLD_SURFACE_WG.getSerializationKey())), dynamic.get("offset").asInt(0));
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings structurePlaceSettings) {
      int var6 = levelReader.getHeight(this.heightmap, var4.pos.getX(), var4.pos.getZ()) + this.offset;
      int var7 = var3.pos.getY();
      return new StructureTemplate.StructureBlockInfo(new BlockPos(var4.pos.getX(), var6 + var7, var4.pos.getZ()), var4.state, var4.nbt);
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.GRAVITY;
   }

   protected Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("heightmap"), dynamicOps.createString(this.heightmap.getSerializationKey()), dynamicOps.createString("offset"), dynamicOps.createInt(this.offset))));
   }
}
