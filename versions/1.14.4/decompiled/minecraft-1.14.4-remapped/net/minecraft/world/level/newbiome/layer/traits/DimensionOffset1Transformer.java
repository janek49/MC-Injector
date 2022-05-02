package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.layer.traits.DimensionTransformer;

public interface DimensionOffset1Transformer extends DimensionTransformer {
   default int getParentX(int i) {
      return i - 1;
   }

   default int getParentY(int i) {
      return i - 1;
   }
}
