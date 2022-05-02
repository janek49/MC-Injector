package net.minecraft.world.level.newbiome.layer;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum RiverLayer implements CastleTransformer {
   INSTANCE;

   public static final int RIVER = Registry.BIOME.getId(Biomes.RIVER);

   public int apply(Context context, int var2, int var3, int var4, int var5, int var6) {
      int var7 = riverFilter(var6);
      return var7 == riverFilter(var5) && var7 == riverFilter(var2) && var7 == riverFilter(var3) && var7 == riverFilter(var4)?-1:RIVER;
   }

   private static int riverFilter(int i) {
      return i >= 2?2 + (i & 1):i;
   }
}
