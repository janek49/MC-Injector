package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class EmptyPoolElement extends StructurePoolElement {
   public static final EmptyPoolElement INSTANCE = new EmptyPoolElement();

   private EmptyPoolElement() {
      super(StructureTemplatePool.Projection.TERRAIN_MATCHING);
   }

   public List getShuffledJigsawBlocks(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random) {
      return Collections.emptyList();
   }

   public BoundingBox getBoundingBox(StructureManager structureManager, BlockPos blockPos, Rotation rotation) {
      return BoundingBox.getUnknownBox();
   }

   public boolean place(StructureManager structureManager, LevelAccessor levelAccessor, BlockPos blockPos, Rotation rotation, BoundingBox boundingBox, Random random) {
      return true;
   }

   public StructurePoolElementType getType() {
      return StructurePoolElementType.EMPTY;
   }

   public Dynamic getDynamic(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.emptyMap());
   }

   public String toString() {
      return "Empty";
   }
}
