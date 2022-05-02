package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public abstract class StructurePoolElement {
   @Nullable
   private volatile StructureTemplatePool.Projection projection;

   protected StructurePoolElement(StructureTemplatePool.Projection projection) {
      this.projection = projection;
   }

   protected StructurePoolElement(Dynamic dynamic) {
      this.projection = StructureTemplatePool.Projection.byName(dynamic.get("projection").asString(StructureTemplatePool.Projection.RIGID.getName()));
   }

   public abstract List getShuffledJigsawBlocks(StructureManager var1, BlockPos var2, Rotation var3, Random var4);

   public abstract BoundingBox getBoundingBox(StructureManager var1, BlockPos var2, Rotation var3);

   public abstract boolean place(StructureManager var1, LevelAccessor var2, BlockPos var3, Rotation var4, BoundingBox var5, Random var6);

   public abstract StructurePoolElementType getType();

   public void handleDataMarker(LevelAccessor levelAccessor, StructureTemplate.StructureBlockInfo structureTemplate$StructureBlockInfo, BlockPos blockPos, Rotation rotation, Random random, BoundingBox boundingBox) {
   }

   public StructurePoolElement setProjection(StructureTemplatePool.Projection projection) {
      this.projection = projection;
      return this;
   }

   public StructureTemplatePool.Projection getProjection() {
      StructureTemplatePool.Projection structureTemplatePool$Projection = this.projection;
      if(structureTemplatePool$Projection == null) {
         throw new IllegalStateException();
      } else {
         return structureTemplatePool$Projection;
      }
   }

   protected abstract Dynamic getDynamic(DynamicOps var1);

   public Dynamic serialize(DynamicOps dynamicOps) {
      T var2 = this.getDynamic(dynamicOps).getValue();
      T var3 = dynamicOps.mergeInto(var2, dynamicOps.createString("element_type"), dynamicOps.createString(Registry.STRUCTURE_POOL_ELEMENT.getKey(this.getType()).toString()));
      return new Dynamic(dynamicOps, dynamicOps.mergeInto(var3, dynamicOps.createString("projection"), dynamicOps.createString(this.projection.getName())));
   }

   public int getGroundLevelDelta() {
      return 1;
   }
}
