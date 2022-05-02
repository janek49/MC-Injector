package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum SmoothLayer implements CastleTransformer {
   INSTANCE;

   public int apply(Context context, int var2, int var3, int var4, int var5, int var6) {
      boolean var7 = var3 == var5;
      boolean var8 = var2 == var4;
      return var7 == var8?(var7?(context.nextRandom(2) == 0?var5:var2):var6):(var7?var5:var2);
   }
}
