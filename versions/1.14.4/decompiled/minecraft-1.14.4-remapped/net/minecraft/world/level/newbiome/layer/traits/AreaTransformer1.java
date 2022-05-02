package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.layer.traits.DimensionTransformer;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public interface AreaTransformer1 extends DimensionTransformer {
   default AreaFactory run(BigContext bigContext, AreaFactory var2) {
      return () -> {
         R area = var2.make();
         return bigContext.createResult((var3, var4) -> {
            bigContext.initRandom((long)var3, (long)var4);
            return this.applyPixel(bigContext, area, var3, var4);
         }, area);
      };
   }

   int applyPixel(BigContext var1, Area var2, int var3, int var4);
}
