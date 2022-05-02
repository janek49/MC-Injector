package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.DimensionTransformer;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public interface AreaTransformer2 extends DimensionTransformer {
   default AreaFactory run(BigContext bigContext, AreaFactory var2, AreaFactory var3) {
      return () -> {
         R area = var2.make();
         R var5 = var3.make();
         return bigContext.createResult((var4, var5x) -> {
            bigContext.initRandom((long)var4, (long)var5x);
            return this.applyPixel(bigContext, area, var5, var4, var5x);
         }, area, var5);
      };
   }

   int applyPixel(Context var1, Area var2, Area var3, int var4, int var5);
}
