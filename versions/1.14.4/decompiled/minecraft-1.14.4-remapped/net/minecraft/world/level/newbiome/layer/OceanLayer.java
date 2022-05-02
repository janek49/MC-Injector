package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer0;

public enum OceanLayer implements AreaTransformer0 {
   INSTANCE;

   public int applyPixel(Context context, int var2, int var3) {
      ImprovedNoise var4 = context.getBiomeNoise();
      double var5 = var4.noise((double)var2 / 8.0D, (double)var3 / 8.0D, 0.0D, 0.0D, 0.0D);
      return var5 > 0.4D?Layers.WARM_OCEAN:(var5 > 0.2D?Layers.LUKEWARM_OCEAN:(var5 < -0.4D?Layers.FROZEN_OCEAN:(var5 < -0.2D?Layers.COLD_OCEAN:Layers.OCEAN)));
   }
}
