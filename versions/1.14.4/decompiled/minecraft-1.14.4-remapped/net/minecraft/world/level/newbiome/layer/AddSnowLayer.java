package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.C1Transformer;

public enum AddSnowLayer implements C1Transformer {
   INSTANCE;

   public int apply(Context context, int var2) {
      if(Layers.isShallowOcean(var2)) {
         return var2;
      } else {
         int var3 = context.nextRandom(6);
         return var3 == 0?4:(var3 == 1?3:1);
      }
   }
}
