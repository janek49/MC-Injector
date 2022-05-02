package net.minecraft.world.level.newbiome.context;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public interface BigContext extends Context {
   void initRandom(long var1, long var3);

   Area createResult(PixelTransformer var1);

   default Area createResult(PixelTransformer pixelTransformer, Area var2) {
      return this.createResult(pixelTransformer);
   }

   default Area createResult(PixelTransformer pixelTransformer, Area var2, Area var3) {
      return this.createResult(pixelTransformer);
   }

   default int random(int var1, int var2) {
      return this.nextRandom(2) == 0?var1:var2;
   }

   default int random(int var1, int var2, int var3, int var4) {
      int var5 = this.nextRandom(4);
      return var5 == 0?var1:(var5 == 1?var2:(var5 == 2?var3:var4));
   }
}
