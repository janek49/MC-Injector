package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer0;

public enum IslandLayer implements AreaTransformer0 {
   INSTANCE;

   public int applyPixel(Context context, int var2, int var3) {
      return var2 == 0 && var3 == 0?1:(context.nextRandom(10) == 0?1:Layers.OCEAN);
   }
}
