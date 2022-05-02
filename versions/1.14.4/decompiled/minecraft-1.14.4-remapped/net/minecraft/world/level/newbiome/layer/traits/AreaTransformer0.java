package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public interface AreaTransformer0 {
   default AreaFactory run(BigContext bigContext) {
      return () -> {
         return bigContext.createResult((var2, var3) -> {
            bigContext.initRandom((long)var2, (long)var3);
            return this.applyPixel(bigContext, var2, var3);
         });
      };
   }

   int applyPixel(Context var1, int var2, int var3);
}
