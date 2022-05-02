package net.minecraft.world.level.newbiome.context;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.LazyArea;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public class LazyAreaContext implements BigContext {
   private final Long2IntLinkedOpenHashMap cache;
   private final int maxCache;
   protected long seedMixup;
   protected ImprovedNoise biomeNoise;
   private long seed;
   private long rval;

   public LazyAreaContext(int maxCache, long var2, long seedMixup) {
      this.seedMixup = seedMixup;
      this.seedMixup *= this.seedMixup * 6364136223846793005L + 1442695040888963407L;
      this.seedMixup += seedMixup;
      this.seedMixup *= this.seedMixup * 6364136223846793005L + 1442695040888963407L;
      this.seedMixup += seedMixup;
      this.seedMixup *= this.seedMixup * 6364136223846793005L + 1442695040888963407L;
      this.seedMixup += seedMixup;
      this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
      this.cache.defaultReturnValue(Integer.MIN_VALUE);
      this.maxCache = maxCache;
      this.init(var2);
   }

   public LazyArea createResult(PixelTransformer pixelTransformer) {
      return new LazyArea(this.cache, this.maxCache, pixelTransformer);
   }

   public LazyArea createResult(PixelTransformer pixelTransformer, LazyArea var2) {
      return new LazyArea(this.cache, Math.min(1024, var2.getMaxCache() * 4), pixelTransformer);
   }

   public LazyArea createResult(PixelTransformer pixelTransformer, LazyArea var2, LazyArea var3) {
      return new LazyArea(this.cache, Math.min(1024, Math.max(var2.getMaxCache(), var3.getMaxCache()) * 4), pixelTransformer);
   }

   public void init(long seed) {
      this.seed = seed;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedMixup;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedMixup;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedMixup;
      this.biomeNoise = new ImprovedNoise(new Random(seed));
   }

   public void initRandom(long var1, long var3) {
      this.rval = this.seed;
      this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
      this.rval += var1;
      this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
      this.rval += var3;
      this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
      this.rval += var1;
      this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
      this.rval += var3;
   }

   public int nextRandom(int i) {
      int var2 = (int)((this.rval >> 24) % (long)i);
      if(var2 < 0) {
         var2 += i;
      }

      this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
      this.rval += this.seed;
      return var2;
   }

   public ImprovedNoise getBiomeNoise() {
      return this.biomeNoise;
   }

   // $FF: synthetic method
   public Area createResult(PixelTransformer var1) {
      return this.createResult(var1);
   }
}
