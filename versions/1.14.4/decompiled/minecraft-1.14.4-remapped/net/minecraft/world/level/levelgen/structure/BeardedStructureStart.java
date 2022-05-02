package net.minecraft.world.level.levelgen.structure;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public abstract class BeardedStructureStart extends StructureStart {
   public BeardedStructureStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
      super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
   }

   protected void calculateBoundingBox() {
      super.calculateBoundingBox();
      int var1 = 12;
      this.boundingBox.x0 -= 12;
      this.boundingBox.y0 -= 12;
      this.boundingBox.z0 -= 12;
      this.boundingBox.x1 += 12;
      this.boundingBox.y1 += 12;
      this.boundingBox.z1 += 12;
   }
}
