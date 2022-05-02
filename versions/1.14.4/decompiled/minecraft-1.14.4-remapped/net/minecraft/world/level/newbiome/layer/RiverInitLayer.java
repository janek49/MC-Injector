package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.C0Transformer;

public enum RiverInitLayer implements C0Transformer {
   INSTANCE;

   public int apply(Context context, int var2) {
      return Layers.isShallowOcean(var2)?var2:context.nextRandom(299999) + 2;
   }
}
