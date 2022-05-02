package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.layer.traits.DimensionTransformer;

public interface DimensionOffset0Transformer extends DimensionTransformer {
   default int getParentX(int i) {
      return i;
   }

   default int getParentY(int i) {
      return i;
   }
}
